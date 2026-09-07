package zgame.things.core;

import java.util.*;
import java.util.function.Supplier;

import zgame.core.GameTickable;
import zgame.core.utils.ZMath;
import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.core.state.GameThingState;
import zgame.things.core.state.StateHolder;
import zgame.things.core.state.StateList;
import zgame.things.core.state.Stateable;
import zgame.things.entity.projectile.Projectile;
import zgame.things.entity.state.EntityState;
import zgame.things.type.bounds.HitBox;

/**
 * A thing is an entity, i.e. an object which can regularly move around in space and exist at an arbitrary location.
 * This is for things like creatures, dropped items, projectiles, etc.
 *
 * @param <V> The vector implementation used by this entity
 */
public abstract class EntityThing<V extends ZVector<V>> extends GameThing implements GameTickable, HitBox<V>, Stateable{
	
	/** The uuid of this entity */
	private final String uuid;
	
	/** Tracks the current state of this entity */
	private final StateHolder<EntityState<V>> entityState;
	
	/** The state objects associated with this entity */
	private final StateList stateList;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass The initial mass of this thing
	 */
	public EntityThing(double mass){
		super();
		this.uuid = UUID.randomUUID().toString();
		
		this.stateList = new StateList();
		this.entityState = this.registerState(this::initState);
		
		// TODO update mass properly, also probably remove it from the constructor, just default the value to 1, and anything that needs to set mass can do it in its constructor
		this.entityStateCurrent().setMass(mass);
		this.entityStateNext().setMass(mass);
		
	}
	
	/**
	 * @param initState A state holder that should be managed by this entity
	 * @return The function used to initialize the state
	 * @param <T> The type of state to manage
	 */
	protected <T extends GameThingState> StateHolder<T> registerState(Supplier<T> initState){
		return this.stateList.register(initState);
	}
	
	/** @return The state object to read data from, never write */
	private EntityState<V> entityStateCurrent(){
		return this.entityState.getCurrent();
	}
	
	/** @return The state object to schedule updates to, never read */
	private EntityState<V> entityStateNext(){
		return this.entityState.getNext();
	}
	
	/** @return A state with the default values of an entity */
	private EntityState<V> initState(){
		return this.initEntityState(this.zeroVector(), this.getGravityAcceleration(), this.getClampVelocity());
	}
	
	// TODO maybe make a better way of doing this than making each child class have to pass along all of these fields, for now making initState final so that these values are passed to mobility entity
	protected abstract EntityState<V> initEntityState(V zeroVector, double gravityAcceleration, double clampVelocity);
	
	/** @return A new empty vector, representing no motion, for use with this entity. Should always return a new instance */
	public abstract V zeroVector();
	
	@Override
	public void tick(double dt){
		// TODO is this the way this should be done?
		this.entityStateCurrent().setTickTime(dt);
		this.entityStateNext().setTickTime(dt);
		
		// Account for drag going down for terminal velocity
		this.updateGravityDragForce(dt);
		
		// Account for sliding down walls
		this.updateWallSideForce(dt);
		
		// Account for frictional force based on current ground material, must be updated directly before applying any movement
		this.updateFrictionForce(dt);
	}
	
	@Override
	public void updateState(){
		this.stateList.updateStates();
	}
	
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
			if(currentFriction.getMagnitude() != 0) this.clearForce(EntityState.FORCE_FRICTION);
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
			this.clearForce(EntityState.FORCE_FRICTION);
			return;
		}
		
		// If velocity is in the opposite direction as gravity, apply no friction
		var currentVel = this.getVelocity();
		double velVert = currentVel.getVerticalValue();
		if(Math.abs(velVert) > clampVel && !ZMath.sameSign(velVert, gravityVert)){
			this.clearForce(EntityState.FORCE_FRICTION);
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
				this.addForce(EntityState.FORCE_FRICTION, currentFriction.inverse());
				// When friction exceeds current force, and on a surface, velocity must also be set to zero
				this.clearVelocity();
				return;
			}
			// With no horizontal velocity, there is no friction
			else if(!hasHorizontalVel){
				if(currentFriction.getMagnitude() != 0) this.clearForce(EntityState.FORCE_FRICTION);
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
			this.clearForce(EntityState.FORCE_FRICTION);
			this.clearVelocity();
			return;
		}
		
		// Otherwise, apply the full amount of friction
		this.attemptSetForce(EntityState.FORCE_FRICTION, newFriction);
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
			if(this.getGravityDragForce().getVertical() != gravityForce) this.attemptSetVerticalForce(EntityState.FORCE_GRAVITY_DRAG, gravityForce);
		}
		// Otherwise, remove the force
		else{
			// Only remove the force if it is not already zero
			if(this.getGravityDragForce().getVertical() != 0) this.clearForce(EntityState.FORCE_GRAVITY_DRAG);
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
		double mass = this.getMass();
		double slideStopForce = wall.getSlipperinessAcceleration() * mat.getSlipperinessAcceleration() / mass;
		
		// The slide force is always zero if the entity is not on a wall or is slower than the max slide velocity
		// or the max slide velocity is negative or the slideStopForce is negative
		double vy = this.getVerticalVel();
		if(maxSlideVel < 0 || slideStopForce < 0 || vy <= maxSlideVel || !this.isOnWall()){
			this.clearForce(EntityState.FORCE_WALL_SLIDE);
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
		this.attemptSetVerticalForce(EntityState.FORCE_WALL_SLIDE, slideForce);
	}
	
	/**
	 * @return The terminal velocity of this {@link EntityThing}. By default, based on the mass, the acceleration of gravity, the value of {@link HitBox#getGravityDragReferenceArea()},
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
	
	
	// TODO should this be in EntityThing? Probably should be defined as a global?
	
	/** @return The acceleration of gravity */
	public abstract double getGravityAcceleration();
	
	/** @return true if this {@link EntityThing} was on a ceiling in the past {@link #tick(double)}, false otherwise */
	public boolean isOnCeiling(){
		return this.getCeilingTime() == -1;
	}
	
	/** @return true if this {@link EntityThing} was touching a wall in the past {@link #tick(double)}, false otherwise */
	public boolean isOnWall(){
		return this.getWallTime() == -1;
	}
	
	// TODO abstract this into a better system than having projectiles be part of entity handling
	
	/**
	 * Called when this is hit by a projectile. Does nothing by default, implement to provide custom behavior
	 *
	 * @param p The projectile which hit this
	 */
	private void hitBy(Projectile<V> p){}
	
	/** A method that defines what this object does when it leaves the floor, i.e. it goes from touching the floor to not touching the floor */
	public void leaveFloor(){
		this.entityStateNext().leaveFloor();
	}
	
	// TODO make these methods consume collision udpates
	/**
	 * A method that defines what this object does when it touches a floor
	 *
	 * @param collision The collision resulting in the floor being touched
	 */
	public void touchFloor(Collision<V> collision){
		var touched = collision.material();
		this.entityStateNext().touchFloor(touched);
		
		// Bounce off the floor, or reset the y velocity to 0 if either material has no floor bounciness
		this.entityStateNext().scaleVelocityVertical(-1 * touched.getFloorBounce() * this.getMaterial().getFloorBounce());
	}
	
	/** A method that defines what this object does when it leaves a ceiling, i.e. it goes from touching a wall to not touching a ceiling */
	public void leaveCeiling(){
		this.entityStateNext().leaveCeiling();
	}
	
	/**
	 * A method that defines what this object does when it touches a ceiling
	 *
	 * @param collision The collision resulting in the ceiling being touched
	 */
	public void touchCeiling(Collision<V> collision){
		var touched = collision.material();
		this.entityStateNext().touchCeiling(touched);
		
		// Bounce off the ceiling, or reset the y velocity to 0 if either material has no ceiling bounciness
		this.entityStateNext().scaleVelocityVertical(-1 * touched.getCeilingBounce() * this.getMaterial().getCeilingBounce());
	}
	
	/** A method that defines what this object does when it leaves a wall, i.e. it goes from touching a wall to not touching a wall */
	public void leaveWall(){
		this.entityStateNext().leaveWall();
		
		// On leaving a wall, there is no more wall slide force
		this.entityStateNext().clearForce(EntityState.FORCE_WALL_SLIDE);
	}
	
	/**
	 * A method that defines what this object does when it touches a wall
	 *
	 * @param collision The collision resulting in the wall being touched
	 */
	public void touchWall(Collision<V> collision){
		var touched = collision.material();
		this.entityStateNext().touchWall(touched);
		// TODO potentially move wall bounce to here instead of having to have it in 2D/3D separately
	}
	
	// TODO how should this be called when collision results are applied?
//	@Override
//	public void collide(Collision<V> r){
//		if(r.wall()) this.touchWall(r);
//		if(r.ceiling()) this.touchCeiling(r);
//		if(r.floor()) this.touchFloor(r);
//	}
	
	/**
	 * Called each this {@link EntityThing} has its entity collision handled.
	 * Does nothing by default, override to add custom behavior
	 *
	 * @param entity The entity that was collided with this entity
	 * @param dt The amount of time, in seconds, which passed in the tick where this collision took place
	 */
	public void checkEntityCollision(EntityThing<V> entity, double dt){}
	
	
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
	
	/** @return true if this thing is ignoring collision, false otherwise */
	public boolean isNoClip(){
		return this.entityStateCurrent().isNoClip();
	}
	
	/** @param noClip The new value to set {@link #isNoClip()} to */
	public void setNoClip(boolean noClip){
		this.entityStateNext().scheduleNoClip(noClip);
	}
	
	@Override
	public String getUuid(){
		return this.uuid;
	}
	
	/** @return true if this thing can enter a rom, false otherwise, always returns true by default */
	public boolean canEnterRooms(){
		return true;
	}
	
	// Hitbox implementation
	
	// TODO add the rest of the methods from hitbox? Or grab the data from the state
	
	/** @return The current position of this entity */
	public V getPosition(){
		return this.entityStateNext().getPosition();
	}
	
	// Getters from state
	
	/** @return The mass of this entity based on current state */
	public double getMass(){
		return this.entityStateCurrent().getMass();
	}
	
	/** @return The material this entity is currently made of */
	public Material getMaterial(){
		return this.entityStateCurrent().getMaterial();
	}
	
	/** @return A {@link ZVector} representing the total of all forces on this object */
	public V getForce(){
		return this.entityStateCurrent().getForce();
	}
	
	/** @return The velocity of the current state of this entity */
	public V getVelocity(){
		return this.entityStateCurrent().getVelocity();
	}
	
	/** @return The current force of gravity on this entity */
	public V getGravity(){
		return this.entityStateCurrent().getForce(EntityState.FORCE_GRAVITY);
	}
	
	/** @return The current force of friction on this entity */
	public V getFriction(){
		return this.entityStateCurrent().getForce(EntityState.FORCE_FRICTION);
	}
	
	/** @return The current drag force acting against gravity */
	public V getGravityDragForce(){
		return this.entityStateCurrent().getForce(EntityState.FORCE_GRAVITY_DRAG);
	}
	
	/** @return The material of the ground that this thing is on, or {@link Materials#NONE} if not on ground */
	public Material getFloorMaterial(){
		return this.entityStateCurrent().getFloorMaterial();
	}
	
	/** @return The material of the ceiling that this thing is on, or {@link Materials#NONE} if not on ground */
	public Material getCeilingMaterial(){
		return this.entityStateCurrent().getCeilingMaterial();
	}
	
	/** @return The material of the wall that this thing is on, or {@link Materials#NONE} if not on ground */
	public Material getWallMaterial(){
		return this.entityStateCurrent().getWallMaterial();
	}
	
	/** @return true if this {@link EntityThing} was on the ground in the past {@link #tick(double)}, false otherwise */
	public boolean isOnGround(){
		return this.entityStateCurrent().isOnGround();
	}
	
	/** @return The number of seconds since this entity has been on the ground, -1 if currently on the ground */
	public double getGroundTime(){
		return this.entityStateCurrent().getGroundTime();
	}
	
	/** @return The number of seconds this entity has been on the ground, -1 if not on the ground */
	public double getOnGroundTime(){
		return this.entityStateCurrent().getOnGroundTime();
	}
	
	/** @return The number of seconds since this entity has touched a ceiling, -1 if currently touching a ceiling */
	public double getCeilingTime(){
		return this.entityStateCurrent().getCeilingTime();
	}
	
	/** @return The number of seconds since this entity has touched a wall, -1 if currently touching a wall */
	public double getWallTime(){
		return this.entityStateCurrent().getWallTime();
	}
	
	// Updates for state
	
	// TODO make docs and move this to an update system, and move this to setting next instead of current
	public void setMass(double mass){
		this.entityStateCurrent().setMass(mass);
	}
	
	/** @param name The name of the force that should be reset to zero */
	public void clearForce(String name){
		this.entityStateNext().clearForce(name);
	}
	
	/**
	 * @param name The name of the force that should be affected
	 * @param force The amount of force to add
	 */
	public void addForce(String name, V force){
		this.entityStateNext().addForce(name, force);
	}
	
	/**
	 * @param name The name of the force that should be affected
	 * @param force The new value of the given force
	 */
	public void attemptSetForce(String name, V force){
		this.entityStateNext().attemptSetForce(name, force);
	}
	
	/**
	 * @param name The name of the force that should be affected
	 * @param force The new value for the vertical force
	 */
	public void attemptSetVerticalForce(String name, double force){
		this.entityStateNext().attemptSetVerticalForce(name, force);
	}
	
	/**
	 * Add the given velocity to this entity
	 *
	 * @param vec The velocity to add
	 */
	public void addVelocity(V vec){
		this.entityStateNext().addVelocity(vec);
	}
	
	/**
	 * Set the velocity of this thing to zero on all axes and set the current applied for forces to 0
	 */
	public void clearMotion(){
		this.entityStateNext().clearMotion();
	}
	
	/** Instruct this entity that all of its velocity will be cleared on the next tick */
	public void clearVelocity(){
		this.entityStateNext().clearVelocity();
	}
	
	// TODO add docs
	public void attemptSetVelocity(V velocity){
		this.entityStateNext().attemptSetVelocity(velocity);
	}
	
	/**
	 * Initialize the position of this state directly to the given value
	 * @param pos The initial position to use
	 */
	public void initPosition(V pos){
		// TODO consider if this is how this should be set
		this.entityStateCurrent().initPosition(pos);
	}
	
	// TODO add docs or remove this
	public void scaleVelocity(double scalar){
		removeFrom();this.entityStateNext().scaleVelocity(scalar);
	}
	
	// TODO add docs
	public void collide(List<Collision<V>> c){
		this.entityStateNext().collide(c);
	}
	
}
