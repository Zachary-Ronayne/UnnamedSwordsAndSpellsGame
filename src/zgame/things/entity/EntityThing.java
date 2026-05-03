package zgame.things.entity;

import java.util.*;

import zgame.core.GameTickable;
import zgame.core.utils.ZMath;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResult;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.entity.state.EntityState;
import zgame.things.type.GameThing;
import zgame.things.type.bounds.HitBox;
import zgame.world.Room;

/**
 * A thing is an entity, i.e. an object which can regularly move around in space and exist at an arbitrary location.
 * This is for things like creatures, dropped items, projectiles, etc.
 *
 * @param <H> The hitbox implementation used by this entity
 * @param <E> The entity implementation of this entity
 * @param <V> The vector implementation used by this entity
 * @param <R> The room implementation which this entity can exist in
 * @param <C> The type of collisions used by this entity
 */
// issue#50 find a way to avoid having to do this comical amount of type parameters without having to resort to weird type casting or instanceof checks
public abstract class EntityThing<
		H extends HitBox<H, C>,
		E extends EntityThing<H, E, V, R, C>,
		V extends ZVector<V>,
		R extends Room<H, E, V, R, C>,
		C extends CollisionResult<C>
		// TODO consider if this is the best way to handle the game thing type parameter
		> extends GameThing<EntityState<V>> implements GameTickable, HitBox<H, C>{
	
	/** The uuid of this entity */
	private final String uuid;

	/** The amount of time in seconds since this {@link EntityThing} last touched the ground, or -1 if it is currently on the ground */
	private double groundTime;
	
	/** The amount of time in seconds this {@link EntityThing} has been on the ground, or -1 if it is not on the ground */
	private double onGroundTime;
	
	/** The material which this {@link EntityThing} is standing on, or {@link Materials#NONE} if no material is being touched */
	private Material floorMaterial;
	
	/** The amount of time in seconds since this {@link EntityThing} last touched a ceiling, or -1 if it is currently touching a ceiling */
	private double ceilingTime;
	
	/** The material which this {@link EntityThing} is holding onto from the ceiling, or {@link Materials#NONE} if no ceiling is touched */
	private Material ceilingMaterial;
	
	/** The amount of time in seconds since this {@link EntityThing} last touched a wall, or -1 if it is currently touching a wall */
	private double wallTime;
	
	/** The material which this {@link EntityThing} is holding on a wall, or {@link Materials#NONE} if no wall is touched */
	private Material wallMaterial;
	
	/** The Material which this {@link EntityThing} is made of */
	private Material material;
	
	/** true if collision should be disabled, false otherwise */
	private boolean noClip;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass The initial mass of this thing
	 */
	public EntityThing(double mass){
		super();
		this.uuid = UUID.randomUUID().toString();
		
		// TODO update mass properly, also probably remove it from the constructor
		this.getCurrent().setMass(mass);
		this.getNext().setMass(mass);
		this.material = Materials.DEFAULT_ENTITY;
		
		this.floorMaterial = Materials.NONE;
		this.groundTime = 0;
		this.onGroundTime = 0;
		this.ceilingMaterial = Materials.NONE;
		this.ceilingTime = 0;
		this.wallMaterial = Materials.NONE;
		this.wallTime = 0;
		
		this.noClip = false;
	}
	
	// TODO make proper docs explaining the stages of updating state in each section
	
	@Override
	public EntityState<V> initState(){
		return new EntityState<>(this.zeroVector(), this.getGravityAcceleration(), this.getClampVelocity());
	}
	
	@Override
	public void updateState(){
		// TODO does doing it this way make sense?
		
		// Update state
		super.updateState();
		
		// After finding the new state, move the entity
		this.moveEntity(this.getVelocity());
	}
	
	@Override
	public EntityState<V> copyState(EntityState<V> current, EntityState<V> next){
		current.applyState(next);
		
		return current;
	}
	
	/** @return A new empty vector, representing no motion, for use with this entity. Should always return a new instance */
	public abstract V zeroVector();
	
	@Override
	public void tick(double dt){
		// TODO is this the way this should be done?
		this.getCurrent().setTickTime(dt);
		this.getNext().setTickTime(dt);
		
		// Update the amount of time the entity has been on the ground, walls, and ceiling
		if(this.groundTime != -1) this.groundTime += dt;
		if(this.onGroundTime != -1) this.onGroundTime += dt;
		if(this.ceilingTime != -1) this.ceilingTime += dt;
		if(this.wallTime != -1) this.wallTime += dt;
		
		// Account for drag going down for terminal velocity
		this.updateGravityDragForce(dt);
		
		// Account for sliding down walls
		this.updateWallSideForce(dt);
		
		// Account for frictional force based on current ground material, must be updated directly before applying any movement
		this.updateFrictionForce(dt);
	}
	
	/**
	 * Move the entity by the given amount
	 *
	 * @param distance The distance to move this entity by
	 */
	public abstract void moveEntity(V distance);
	
	/**
	 * Determine the current amount of friction on this {@link EntityThing} and update the force
	 *
	 * @param dt The amount of time, in seconds, that will pass the next time the frictional force is applied
	 */
	public void updateFrictionForce(double dt){
		// When not on a surface, there is no friction
		boolean onSurface = this.isOnGround() || this.isOnWall() || this.isOnCeiling();
		var currentFriction = this.getFriction();
		if(!onSurface){
			// Don't bother changing friction if it's already 0
			if(currentFriction.getMagnitude() != 0) this.getNext().clearForce(EntityState.FORCE_FRICTION);
			return;
		}
		double clampVel = this.getClampVelocity();
		
		var currentForce = this.getForce();
		var gravity = this.getGravity();
		double gravityVert = gravity.getVerticalValue();
		double forceNoGravityVert = currentForce.getVerticalValue() - gravityVert;

		/*
		 If the vertical component of force without gravity is greater than or equal to the force of gravity,
		 and the forces are in opposite directions or equal,
		 then there will be no friction
		 */
		if((!ZMath.sameSign(gravityVert, forceNoGravityVert) || gravityVert == forceNoGravityVert) && Math.abs(forceNoGravityVert) > clampVel){
			this.getNext().clearForce(EntityState.FORCE_FRICTION);
			return;
		}
		
		// If velocity is in the opposite direction as gravity, apply no friction
		var currentVel = this.getVelocity();
		double velVert = currentVel.getVerticalValue();
		if(Math.abs(velVert) > clampVel && !ZMath.sameSign(velVert, gravityVert)){
			this.getNext().clearForce(EntityState.FORCE_FRICTION);
			return;
		}
		
		// Find the total force for friction, i.e. the amount of acceleration from friction, based on the surface and the entity's friction
		double newFrictionForce = this.getFrictionConstant() * this.getFloorMaterial().getFriction() * this.getForce().getVertical();
		
		var hasHorizontalVel = currentVel.getHorizontal() >= clampVel;
		boolean currentForceExceedsFriction = currentForce.getMagnitude() > newFrictionForce;
		// When there is no horizontal velocity, or the current force applied to the entity is less than the base force of friction, the friction force may be zero
		if(!hasHorizontalVel || currentForceExceedsFriction){
			// If the frictional force exceeds the current force, and velocity is moving opposite of gravity, then there will be equal and opposite frictional force
			if(!currentForceExceedsFriction && currentVel.isOpposite(gravity)){
				this.getNext().addForce(EntityState.FORCE_FRICTION, currentFriction.inverse());
				// When friction exceeds current force, and on a surface, velocity must also be set to zero
				this.clearVelocity();
				return;
			}
			// With no horizontal velocity, there is no friction
			else if(!hasHorizontalVel){
				if(currentFriction.getMagnitude() != 0) this.getNext().clearForce(EntityState.FORCE_FRICTION);
				return;
			}
		}
		
		// The new frictional force will always be in the opposite direction of current movement
		var newFriction = currentVel.inverse().modifyMagnitude(newFrictionForce);
		
		// Find the new force if friction is fully applied
		var forceWithoutFriction = currentForce.sub(currentFriction);
		var newForce = forceWithoutFriction.add(newFriction);
		
		/*
		 If adding the new frictional force would result in moving in the opposite direction after accounting for the current velocity,
		 stop movement entirely if no other forces are acting
		 */
		double mass = this.getMass();
		if(currentVel.getMagnitude() < clampVel || currentVel.isOpposite(currentVel.add(newForce.scale(dt / mass)))){
			this.getNext().clearForce(EntityState.FORCE_FRICTION);
			this.clearVelocity();
			return;
		}
		
		// Otherwise, apply the full amount of friction
		this.getNext().attemptSetForce(EntityState.FORCE_FRICTION, newFriction);
	}
	
	/**
	 * Update the current amount of drag on this {@link EntityThing} counteracting the force of gravity
	 *
	 * @param dt The amount of time, in seconds, that will pass the next time the drag force is applied
	 */
	public void updateGravityDragForce(double dt){
		// Determine terminal velocity
		double terminalVelocity = this.getTerminalVelocity();
		
		// If downward velocity exceeds terminal velocity, and terminal velocity is not negative, set the vector to be equal and opposite to gravity
		if(this.getVerticalVel() >= terminalVelocity && terminalVelocity > 0){
			// Only set the value if it is not equal and opposite to gravity
			double gravityForce = -this.getGravity().getVerticalValue();
			if(this.getGravityDragForce().getVertical() != gravityForce) this.setVerticalForce(EntityState.FORCE_GRAVITY_DRAG, gravityForce);
		}
		// Otherwise, remove the force
		else{
			// Only remove the force if it is not already zero
			if(this.getGravityDragForce().getVertical() != 0) this.setVerticalForce(EntityState.FORCE_GRAVITY_DRAG, 0);
		}
	}
	
	/**
	 * Update the amount of force applied against gravity on this {@link EntityThing} from sliding down walls
	 *
	 * @param dt The amount of time, in seconds, that will pass the next time the wall slide force is applied
	 */
	public void updateWallSideForce(double dt){
		Material wall = this.getWallMaterial();
		Material mat = this.getMaterial();
		
		// The maximum speed that can be slid down the wall
		double maxSlideVel = wall.getSlipperinessSpeed() * mat.getSlipperinessSpeed();
		// The amount of force used to slow down
		double mass = this.getCurrent().getMass();
		double slideStopForce = wall.getSlipperinessAcceleration() * mat.getSlipperinessAcceleration() / mass;
		
		// The slide force is always zero if the entity is not on a wall or is slower than the max slide velocity
		// or the max slide velocity is negative or the slideStopForce is negative
		double vy = this.getVerticalVel();
		if(maxSlideVel < 0 || slideStopForce < 0 || vy <= maxSlideVel || !this.isOnWall()){
			this.setVerticalForce(EntityState.FORCE_WALL_SLIDE, 0);
			return;
		}
		// The base amount of force to apply for sliding is the opposite of gravity
		double slideForce = -this.getGravity().getVerticalValue();
		// If we get to this point, then we are falling faster than the maximum sliding speed, increase the slide force to slow the falling (slideForce will be a negative number)
		slideForce -= slideStopForce;
		
		// If the new slide force would put the velocity below the maximum sliding speed, adjust the force such that the next tick will put it on the sliding speed
		double newVel = vy + slideForce / mass * dt;
		if(newVel < maxSlideVel) slideForce = (maxSlideVel - vy) / dt * mass;
		// Set the force
		this.setVerticalForce(EntityState.FORCE_WALL_SLIDE, slideForce);
	}
	
	/**
	 * @return The terminal velocity of this {@link EntityThing}. By default, based on the mass, the acceleration of gravity, the value of {@link #getGravityDragReferenceArea()},
	 * 		and the friction of the ground material, which is also the air material when this {@link EntityThing} is not on the ground.
	 * 		Returns 0 if this {@link EntityThing} is on the ground.
	 * 		If {@link #getGravityDragReferenceArea()} returns 0, or is negative, then the value is ignored in the calculation.
	 * 		If this method is made to return a negative value, terminal velocity is removed, i.e. the force of gravity will continue to accelerate
	 */
	public double getTerminalVelocity(){
		if(this.isOnGround()) return 0;
		
		Material m = this.getFloorMaterial();
		double s = this.getGravityDragReferenceArea();
		double surfaceArea = (s <= 0) ? 1 : s;
		
		// Multiplied by 2.0 because the internet says that constant is there for the equation is for terminal velocity
		// Multiplied by 0.01 as a placeholder for density. For now, everything entities fall through is considered to have that same constant density
		// issue#45 make a getDensity method, this would have to be a material the entities are current in
		// issue#45 this should also be how swimming works, so you are swimming if you are denser than the density of the material you are in
		return Math.sqrt(Math.abs((2.0 * this.getMass() * this.getGravityAcceleration()) / (m.getFriction() * surfaceArea * 0.01)));
	}
	
	/**
	 * @return The number determining how much friction applies to this {@link EntityThing}.
	 * 		Higher values mean more friction, lower values mean less friction, 0 means no friction.
	 * 		Behavior is undefined for negative return values
	 */
	public abstract double getFrictionConstant();
	
	@Override
	public Material getMaterial(){
		return this.material;
	}
	
	/** @param material See {@link #material} */
	public void setMaterial(Material material){
		this.material = material;
	}
	
	/** @return A {@link ZVector} representing the total of all forces on this object */
	public V getForce(){
		return this.getCurrent().getForce();
	}
	
	/** @return The velocity of the current state of this entity */
	public V getVelocity(){
		return this.getCurrent().getVelocity();
	}
	
	/** @return The current force of gravity on this entity */
	public V getGravity(){
		return this.getCurrent().getForce(EntityState.FORCE_GRAVITY);
	}
	
	// TODO should this be in EntityThing? Probably should be defined as a global?
	/** @return The acceleration of gravity */
	public abstract double getGravityAcceleration();
	
	
	/** @return The current force of friction on this entity */
	public V getFriction(){
		return this.getCurrent().getForce(EntityState.FORCE_FRICTION);
	}
	
	/** @return The current drag force acting against gravity */
	public V getGravityDragForce(){
		return this.getCurrent().getForce(EntityState.FORCE_GRAVITY_DRAG);
	}
	
	public double getMass(){
		return this.getCurrent().getMass();
	}
	
	// TODO make docs and move this to an update system
	public void setMass(double mass){
		this.getCurrent().setMass(mass);
	}
	
	/** @return See {@link #floorMaterial} */
	@Override
	public Material getFloorMaterial(){
		return this.floorMaterial;
	}
	
	/** @return See {@link #ceilingMaterial} */
	@Override
	public Material getCeilingMaterial(){
		return this.ceilingMaterial;
	}
	
	/** @return See {@link #wallMaterial} */
	@Override
	public Material getWallMaterial(){
		return this.wallMaterial;
	}
	
	/** @return true if this {@link EntityThing} was on the ground in the past {@link #tick(double)}, false otherwise */
	@Override
	public boolean isOnGround(){
		return this.groundTime == -1;
	}
	
	/** @return See {@link #groundTime} */
	public double getGroundTime(){
		return this.groundTime;
	}
	
	/** @return See {@link #onGroundTime} */
	public double getOnGroundTime(){
		return this.onGroundTime;
	}
	
	/** @return See {@link #ceilingTime} */
	public double getCeilingTime(){
		return this.ceilingTime;
	}
	
	/** @return See {@link #wallTime} */
	public double getWallTime(){
		return this.wallTime;
	}
	
	/** @return true if this {@link EntityThing} was on a ceiling in the past {@link #tick(double)}, false otherwise */
	@Override
	public boolean isOnCeiling(){
		return this.ceilingTime == -1;
	}
	
	/** @return true if this {@link EntityThing} was touching a wall in the past {@link #tick(double)}, false otherwise */
	@Override
	public boolean isOnWall(){
		return this.wallTime == -1;
	}
	
	@Override
	public void leaveFloor(){
		this.floorMaterial = Materials.NONE;
		this.groundTime = 0;
		this.onGroundTime = -1;
	}
	
	@Override
	public void touchFloor(C collision){
		// Touching a floor means this entity is on the ground
		var touched = collision.material();
		this.floorMaterial = touched;
		this.groundTime = -1;
		if(onGroundTime < 0) this.onGroundTime = 0;
		
		// Bounce off the floor, or reset the y velocity to 0 if either material has no floor bounciness
		this.getNext().scaleVelocityVertical(-1 * touched.getFloorBounce() * this.getMaterial().getFloorBounce());
	}
	
	@Override
	public void leaveCeiling(){
		this.ceilingMaterial = Materials.NONE;
		this.ceilingTime = 0;
	}
	
	@Override
	public void touchCeiling(C collision){
		var touched = collision.material();
		this.ceilingMaterial = touched;
		this.ceilingTime = -1;
		
		// Bounce off the ceiling, or reset the y velocity to 0 if either material has no ceiling bounciness
		this.getNext().scaleVelocityVertical(-1 * touched.getCeilingBounce() * this.getMaterial().getCeilingBounce());
	}
	
	@Override
	public void leaveWall(){
		this.wallMaterial = Materials.NONE;
		this.wallTime = 0;
		
		// TODO handle this with an update system
		this.getCurrent().setVerticalForce(EntityState.FORCE_WALL_SLIDE, 0);
	}
	
	@Override
	public void touchWall(C collision){
		this.wallMaterial = collision.material();
		this.wallTime = -1;
	}
	
	@Override
	public void collide(C r){
		if(r.wall()) this.touchWall(r);
		if(r.ceiling()) this.touchCeiling(r);
		if(r.floor()) this.touchFloor(r);
	}
	
	/**
	 * Called each this {@link EntityThing} has its entity collision handled.
	 * Does nothing by default, override to add custom behavior
	 *
	 * @param entity The entity that was collided with this entity
	 * @param dt The amount of time, in seconds, which passed in the tick where this collision took place
	 */
	public void checkEntityCollision(E entity, double dt){}
	
	/**
	 * Add the given velocity to this entity
	 *
	 * @param vec The velocity to add
	 */
	public void addVelocity(V vec){
		this.getNext().addVelocity(vec);
	}
	
	// TODO make doc, potentially remove
	public void attemptSetVelocity(V velocity){
		this.getNext().attemptSetVelocity(velocity);
	}
	
	// TODO handle this using an update system
	/**
	 * Set the velocity of this thing to zero on all axes and set the current applied for forces to 0
	 */
	public void clearMotion(){
		this.getCurrent().clearMotion();
	}
	
	/** Instruct this entity that all of its velocity will be cleared on the next tick */
	public void clearVelocity(){
		this.getNext().clearVelocity();
	}
	
	// TODO handle this using an update system
	/**
	 * Set a force on the vertical, i.e. gravitational, axis.
	 *
	 * @param name The string identifying the force
	 * @param f The quantity of the force, positive means down and negative means up
	 * @return The vector representing the added force
	 */
	public V setVerticalForce(String name, double f){
		return this.getCurrent().setVerticalForce(name, f);
	}
	
	/** @return The total magnitude of horizontal velocity of this entity */
	public abstract double getHorizontalVel();
	
	/** @return The total vertical velocity of this entity */
	public abstract double getVerticalVel();
	
	/**
	 * @return If the absolute value of the magnitude of velocity ever reaches a value greater than zero and less than this value,
	 * 		velocity will be clamped to zero. Override if this value must be more restrictive
	 */
	public double getClampVelocity(){
		return 1E-13;
	}
	
	/** @return See {@link #noClip} */
	public boolean isNoClip(){
		return this.noClip;
	}
	
	/** @param noClip See {@link #noClip} */
	public void setNoClip(boolean noClip){
		this.noClip = noClip;
	}
	
	@Override
	public String getUuid(){
		return this.uuid;
	}
	
	/**
	 * Take this {@link EntityThing} from the given room, and place it in the other given room
	 *
	 * @param from The room to move the thing from, i.e. the thing was in this room. Can be null if the thing didn't come from a room
	 * @param to The room to move the thing to, i.e. the thing is now in this room. Can be null if the thing isn't going to a room
	 */
	public void enterRoom(R from, R to){
		if(from != null) from.removeThing(this);
		if(to != null) to.addThing(this);
	}
	
	/** @return true if this thing can enter a rom, false otherwise, always returns true by default */
	public boolean canEnterRooms(){
		return true;
	}
}
