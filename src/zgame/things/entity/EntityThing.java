package zgame.things.entity;

import java.util.*;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.utils.NotNullList;
import zgame.physics.ZVector;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
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
 */
public abstract class EntityThing<H extends HitBox<H>, E extends EntityThing<H, E, V, R>, V extends ZVector<V>, R extends Room<H, E, V, R>> extends GameThing implements GameTickable, HitBox<H>{
	
	/** The string used to identify the force of gravity in {@link #forces} */
	public static final String FORCE_NAME_GRAVITY = "gravity";
	/** The string used to identify the force of friction in {@link #forces} */
	public static final String FORCE_NAME_FRICTION = "friction";
	/** The string used to identify the force of friction in {@link #forces} */
	public static final String FORCE_NAME_GRAVITY_DRAG = "gravityDrag";
	/** The string used to identify the force of sticking to a wall in {@link #forces} */
	public static final String FORCE_NAME_WALL_SLIDE = "wallSlide";
	
	/** The uuid of this entity */
	private final String uuid;
	
	/** The current velocity of this {@link EntityThing} */
	private V velocity;
	
	/** The current force of gravity on this {@link EntityThing} */
	private V gravity;
	
	/** The percentage of gravity that applies to this {@link EntityThing}, defaults to 1, i.e. 100% */
	private double gravityLevel;
	
	/** The current force of friction on this {@link EntityThing}. */
	private V frictionForce;
	
	/** The current force of drag acting against gravity on this {@link EntityThing} */
	private V gravityDragForce;
	
	/** Every force currently acting on this {@link EntityThing}, mapped by a name */
	private final Map<String, V> forces;
	
	/** A set of all the uuids which are currently colliding with this entity */
	private final HashSet<String> collidingUuids;
	
	/** A {@link ZVector} representing the total force acting on this {@link EntityThing} */
	private V totalForce;
	
	/** The amount of time in seconds since this {@link EntityThing} last touched the ground, or -1 if it is currently on the ground */
	private double groundTime;
	
	/** The material which this {@link EntityThing} is standing on, or {@link Materials#NONE} if no material is being touched */
	private Material groundMaterial;
	
	/** The angle, in radians, that this thing is relative to the ground */
	private double groundAngle;
	
	/** The amount of time in seconds since this {@link EntityThing} last touched a ceiling, or -1 if it is currently touching a ceiling */
	private double ceilingTime;
	
	/** The material which this {@link EntityThing} is holding onto from the ceiling, or {@link Materials#NONE} if no ceiling is touched */
	private Material ceilingMaterial;
	
	/** The amount of time in seconds since this {@link EntityThing} last touched a wall, or -1 if it is currently touching a wall */
	private double wallTime;
	
	/** The material which this {@link EntityThing} is holding on a wall, or {@link Materials#NONE} if no wall is touched */
	private Material wallMaterial;
	
	/** The mass, i.e. weight, of this {@link EntityThing} */
	private double mass;
	
	/** The Material which this {@link EntityThing} is made of */
	private Material material;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass See {@link #mass}
	 */
	public EntityThing(double mass){
		this.uuid = UUID.randomUUID().toString();
		
		this.velocity = this.zeroVector();
		
		this.collidingUuids = new HashSet<>();
		this.forces = new HashMap<>();
		this.totalForce = this.zeroVector();
		
		this.gravity = this.zeroVector();
		this.setForce(FORCE_NAME_GRAVITY, gravity);
		this.setGravityLevel(1);
		this.setMass(mass);
		this.material = Materials.DEFAULT_ENTITY;
		
		this.frictionForce = this.zeroVector();
		this.setForce(FORCE_NAME_FRICTION, frictionForce);
		
		this.gravityDragForce = this.zeroVector();
		this.setForce(FORCE_NAME_GRAVITY_DRAG, this.gravityDragForce);
		
		this.setForce(FORCE_NAME_WALL_SLIDE, this.zeroVector());
		
		this.groundMaterial = Materials.NONE;
		this.groundTime = 0;
		this.groundAngle = 0;
		this.ceilingMaterial = Materials.NONE;
		this.ceilingTime = 0;
		this.wallMaterial = Materials.NONE;
		this.wallTime = 0;
	}
	
	/** @return A new empty vector, representing no motion, for use with this entity. Should always return a new instance */
	public abstract V zeroVector();
	
	@Override
	public void tick(Game game, double dt){
		// Update the amount of time the entity has been on the ground, walls, and ceiling
		if(this.groundTime != -1) this.groundTime += dt;
		if(this.ceilingTime != -1) this.ceilingTime += dt;
		if(this.wallTime != -1) this.wallTime += dt;
		
		// Account for frictional force based on current ground material
		this.updateFrictionForce(dt);
		
		// Account for drag going down for terminal velocity
		this.updateGravityDragForce(dt);
		
		// Account for sliding down walls
		this.updateWallSideForce(dt);
		
		// Check for entity collision, and apply appropriate forces based on what is currently colliding
		this.checkEntityCollisions(game, dt);
	}
	
	/**
	 * Update the position and velocity of this {@link EntityThing} based on its current forces and velocity
	 *
	 * @param game The {@link Game} where the update takes place
	 * @param dt The amount of time, in seconds, which passed in the tick where this update took place
	 */
	public void updatePosition(Game game, double dt){
		// Find the current acceleration
		var acceleration = this.getForce().scale(1.0 / this.getMass());
		
		// Add the acceleration to the current velocity
		this.addVelocity(acceleration.scale(dt));
		
		// Account for clamping the velocity
		if(Math.abs(this.getVelocity().getMagnitude()) < this.getClampVelocity()) this.setVelocity(this.zeroVector());
		
		// Apply the movement of the velocity
		this.moveEntity(this.getVelocity().scale(dt).add(acceleration.scale(dt * dt * 0.5)));
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
		// issue#36 allow friction to work on slopes? For now this only does proper friction on flat surfaces
		// TODO make slope collision in 3D and test this code
		
		double mass = this.getMass();
		var currentForce = this.getForce();
		var currentFriction = this.getFriction();
		var currentVel = this.getVelocity();
		
		// Find the total force for friction, i.e. the amount of acceleration from friction, based on the surface and the entity's friction
		double newFrictionForce = this.getFrictionConstant() * this.getGroundMaterial().getFriction() * Math.abs(this.getGravity().getVertical());
		
		double clampVel = this.getClampVelocity();
		// Depending on conditions, the friction force may be zero
		if(
				// With no velocity
				currentVel.getMagnitude() < clampVel ||
				// When not touching the ground or a wall
				(!this.isOnGround() && !this.isOnWall())
				// When the current force applied to the entity is less than the base force of friction
				|| currentForce.getMagnitude() > newFrictionForce
		){
			// Don't bother changing friction if it's already 0
			if(currentFriction.getMagnitude() == 0) return;
			
			this.frictionForce = this.setForce(FORCE_NAME_FRICTION, this.zeroVector());
			return;
		}
		
		// TODO somehow use the ground angle
		
		// TODO populate ground angle when appropriate, test with moving on spherical collision
//		double angle = this.getGroundAngle();
//		double frictionH = Math.cos(angle) * newFrictionForce;
//		double frictionV = Math.sin(angle) * newFrictionForce;
		
		// The new frictional force will always be in the opposite direction of current movement
		var newFriction = currentVel.inverse().modifyMagnitude(newFrictionForce);
		
		// Find the new force if friction is fully applied
		var forceWithoutFriction = currentForce.add(currentFriction.inverse());
		var newForce = forceWithoutFriction.add(newFriction);
		
		// If adding the new frictional force would result in moving in the opposite direction after accounting for the current velocity, stop movement entirely
		if(currentVel.isOpposite(currentVel.add(newForce.scale(dt / mass)))){
			this.frictionForce = this.setForce(FORCE_NAME_FRICTION, forceWithoutFriction.inverse());
			this.setVelocity(this.zeroVector());
			return;
		}
		
		// Otherwise, apply the full amount of friction
		this.frictionForce = this.setForce(FORCE_NAME_FRICTION, newFriction);
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
			double gravityForce = -this.getGravity().getVertical();
			if(this.getGravityDragForce().getVertical() != gravityForce) this.gravityDragForce = this.setVerticalForce(FORCE_NAME_GRAVITY_DRAG, gravityForce);
		}
		// Otherwise, remove the force
		else{
			// Only remove the force if it is not already zero
			if(this.getGravityDragForce().getVertical() != 0) this.gravityDragForce = this.setVerticalForce(FORCE_NAME_GRAVITY_DRAG, 0);
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
		double slideStopForce = wall.getSlipperinessAcceleration() * mat.getSlipperinessAcceleration() / mass;
		
		// The slide force is always zero if the entity is not on a wall or is slower than the max slide velocity
		// or the max slide velocity is negative or the slideStopForce is negative
		double vy = this.getVerticalVel();
		if(maxSlideVel < 0 || slideStopForce < 0 || vy <= maxSlideVel || !this.isOnWall()){
			this.setVerticalForce(FORCE_NAME_WALL_SLIDE, 0);
			return;
		}
		// The base amount of force to apply for sliding is the opposite of gravity
		double slideForce = -this.getGravity().getVertical();
		double mass = this.getMass();
		// If we get to this point, then we are falling faster than the maximum sliding speed, increase the slide force to slow the falling (slideForce will be a negative number)
		slideForce -= slideStopForce;
		
		// If the new slide force would put the velocity below the maximum sliding speed, adjust the force such that the next tick will put it on the sliding speed
		double newVel = vy + slideForce / mass * dt;
		if(newVel < maxSlideVel) slideForce = (maxSlideVel - vy) / dt * mass;
		// Set the force
		this.setVerticalForce(FORCE_NAME_WALL_SLIDE, slideForce);
	}
	
	/**
	 * @return The terminal velocity of this {@link EntityThing}. By default, based on the mass, the acceleration of gravity, the value of {@link #getSurfaceArea()},
	 * 		and the friction of the ground material, which is also the air material when this {@link EntityThing} is not on the ground.
	 * 		Returns 0 if this {@link EntityThing} is on the ground.
	 * 		If {@link #getSurfaceArea()} returns 0, or is negative, then the value is ignored in the calculation.
	 * 		If this method is made to return a negative value, terminal velocity is removed, i.e. the force of gravity will continue to accelerate
	 */
	public double getTerminalVelocity(){
		if(this.isOnGround()) return 0;
		
		Material m = this.getGroundMaterial();
		double s = this.getSurfaceArea();
		double surfaceArea = (s <= 0) ? 1 : s;
		
		// Multiplied by 2.0 because the internet says that constant is there for the equation is for terminal velocity
		// Multiplied by 0.01 as a placeholder for density. For now, everything entities fall through is considered to have that same constant density
		// TODO make a getDensity method, this would have to be a material the entities are current in
		return Math.sqrt(Math.abs((2.0 * this.getMass() * this.getGravityAcceleration()) / (m.getFriction() * surfaceArea * 0.01)));
	}
	
	/** @return The surface area of this {@link EntityThing} */
	public abstract double getSurfaceArea();
	
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
		return this.totalForce;
	}
	
	/**
	 * @param name The name of the force to get. This method assumes the force exists
	 * @return The {@link ZVector} representing the force on this object with the given name, or null if none exists for that force
	 */
	public V getForce(String name){
		return this.forces.get(name);
	}
	
	/** @return See {@link #velocity} */
	public V getVelocity(){
		return this.velocity;
	}
	
	/** @return See {@link #gravity} */
	public V getGravity(){
		return this.gravity;
	}
	
	/** Update the amount of gravitational force being applied to this {@link EntityThing} */
	private void updateGravity(){
		this.gravity = this.setVerticalForce(FORCE_NAME_GRAVITY, this.getGravityAcceleration() * this.getMass() * this.getGravityLevel());
	}
	
	/** @return See {@link #gravityLevel} */
	public double getGravityLevel(){
		return this.gravityLevel;
	}
	
	/** @return The acceleration of gravity */
	public abstract double getGravityAcceleration();
	
	/** @param gravityLevel See {@link #gravityLevel} */
	public void setGravityLevel(double gravityLevel){
		this.gravityLevel = gravityLevel;
		this.updateGravity();
	}
	
	/** @return See {@link #frictionForce} */
	public V getFriction(){
		return this.frictionForce;
	}
	
	/** @return SEE {@link #gravityDragForce} */
	public V getGravityDragForce(){
		return this.gravityDragForce;
	}
	
	/** @return See {@link #mass} */
	public double getMass(){
		return this.mass;
	}
	
	/** @param mass See {@link #mass} */
	public void setMass(double mass){
		this.mass = mass;
		this.updateGravity();
	}
	
	/** @return See {@link #groundMaterial} */
	public Material getGroundMaterial(){
		return this.groundMaterial;
	}
	
	/** @return See {@link #groundAngle} */
	public double getGroundAngle(){
		return this.groundAngle;
	}
	
	/** @return See {@link #ceilingMaterial} */
	public Material getCeilingMaterial(){
		return this.ceilingMaterial;
	}
	
	/** @return See {@link #wallMaterial} */
	public Material getWallMaterial(){
		return this.wallMaterial;
	}
	
	/** @return true if this {@link EntityThing} was on the ground in the past {@link #tick(Game, double)}, false otherwise */
	@Override
	public boolean isOnGround(){
		return this.groundTime == -1;
	}
	
	/** @return See {@link #groundTime} */
	public double getGroundTime(){
		return this.groundTime;
	}
	
	/** @return See {@link #ceilingTime} */
	public double getCeilingTime(){
		return this.ceilingTime;
	}
	
	/** @return See {@link #wallTime} */
	public double getWallTime(){
		return this.wallTime;
	}
	
	/** @return true if this {@link EntityThing} was on a ceiling in the past {@link #tick(Game, double)}, false otherwise */
	@Override
	public boolean isOnCeiling(){
		return this.ceilingTime == -1;
	}
	
	/** @return true if this {@link EntityThing} was touching a wall in the past {@link #tick(Game, double)}, false otherwise */
	@Override
	public boolean isOnWall(){
		return this.wallTime == -1;
	}
	
	@Override
	public void leaveFloor(){
		this.groundMaterial = Materials.NONE;
		this.groundTime = 0;
	}
	
	@Override
	public void touchFloor(Material touched){
		// Touching a floor means this entity is on the ground
		this.groundMaterial = touched;
		this.groundTime = -1;
		
		// Bounce off the floor, or reset the y velocity to 0 if either material has no floor bounciness
		this.setVerticalVel(-this.getVerticalVel() * touched.getFloorBounce() * this.getMaterial().getFloorBounce());
	}
	
	@Override
	public void leaveCeiling(){
		this.ceilingMaterial = Materials.NONE;
		this.ceilingTime = 0;
	}
	
	@Override
	public void touchCeiling(Material touched){
		this.ceilingMaterial = touched;
		this.ceilingTime = -1;
		
		// Bounce off the ceiling, or reset the y velocity to 0 if either material has no ceiling bounciness
		this.setVerticalVel(-this.getVerticalVel() * touched.getCeilingBounce() * this.getMaterial().getCeilingBounce());
	}
	
	@Override
	public void leaveWall(){
		this.wallMaterial = Materials.NONE;
		this.wallTime = 0;
		
		this.setVerticalForce(FORCE_NAME_WALL_SLIDE, 0);
	}
	
	@Override
	public void touchWall(Material touched){
		this.wallMaterial = touched;
		this.wallTime = -1;
		
		// Bounce off the wall based on the touched material and this entity thing
		this.setHorizontalVel(-this.getHorizontalVel() * touched.getWallBounce() * this.getMaterial().getWallBounce());
	}
	
	/**
	 * Called each this {@link EntityThing} has its entity collision handled.
	 * Does nothing by default, override to add custom behavior
	 *
	 * @param game The game the collision happened in
	 * @param entity The entity that was collided with this entity
	 * @param dt The amount of time, in seconds, which passed in the tick where this collision took place
	 */
	public void checkEntityCollision(Game game, E entity, double dt){}
	
	/**
	 * Collide this {@link EntityThing} with the entities in the given room. Can override this to perform custom collision
	 *
	 * @param game The game with the current room to collide with
	 * @param dt The amount of time, in seconds, which passed in the tick where this collision took place
	 */
	@SuppressWarnings("unchecked")
	public void checkEntityCollisions(Game game, double dt){
		Room<H, E, V, R> room = (Room<H, E, V, R>)game.getCurrentRoom();
		
		// issue#21 make this more efficient by reducing redundant checks, and not doing the same collision calculation for each pair of entities
		
		// Check any stored entities, and remove them if they are not intersecting or are not in the room
		ArrayList<String> toRemove = new ArrayList<>(this.collidingUuids.size());
		for(String eUuid : this.collidingUuids){
			E e = room.getEntity(eUuid);
			if(e == null || !this.intersects(e.get())) toRemove.add(eUuid);
		}
		for(String uuid : toRemove){
			this.collidingUuids.remove(uuid);
			
			// Set the velocity so that it will move this entity an amount to cancel out the current force applied on the next tick, then remove the force on the next tick
			var removed = this.removeForce(uuid);
			if(removed != null) this.addVelocity(removed.scale(-dt / this.getMass()));
		}
		// Get all entities
		NotNullList<E> entities = room.getEntities();
		
		// Iterate through all entities, ignoring this entity, and find the ones intersecting this entity
		for(int i = 0; i < entities.size(); i++){
			var e = entities.get(i);
			if(e == this || !this.intersects(e.get())) continue;
			this.checkEntityCollision(game, e, dt);

//			// If they intersect, determine the force they should have against each other, and apply it to both entities
//			String eUuid = e.getUuid();
//			ZPoint thisP = new ZPoint(this.centerX(), this.maxY());
//			ZPoint eP = new ZPoint(e.centerX(), e.maxY());
//			// Find the distance between the center bottom of the entities, to determine how much force should be applied
//			double dist = thisP.distance(eP);
//
//			// Find a distance where, if the bottom centers of the entities are further than this distance, they are definitely not intersecting
//			double maxDist = (this.getWidth() + this.getHeight() + e.getWidth() + e.getHeight()) * .5;
//			// The maximum amount of force that can be applied
//			double maxMag = (this.getForce().getMagnitude() + e.getForce().getMagnitude());
//
//			// In the equation f(x) = mx^2 + b, so that f(x) = 0 is the maximum amount of force, and 0 = mx^2 + b is the maximum distance to use
//			double b = maxMag;
//			double m = b / (maxDist * maxDist);
//
//			// Use that equation to find the force
//			double mag = m * dist * dist + b;
//			double angle = ZMath.lineAngle(eP.getX(), eP.getY(), thisP.getX(), thisP.getY());
//
//			// Find the initial amount of force to set
//			ZVector newForce = new ZVector(angle, mag, false);
//
//			// Apply most of the force as the x component, and less as the y component
//			newForce = new ZVector(newForce.getX(), newForce.getY() * 0.1);
//
//			//issue#21
//
//			// Try keeping track of the total velocity an entity collision has added to another entity, and then remove that much velocity when the entities stop colliding
//
//			// If that amount of force would move the entity too far away, set it so that the entities will only be touching on the next tick
//			// double xForce = newForce.getX();
//			// double xMoved = xForce / this.getMass() * dt * dt;
//			// double xDiff;
//			// if(this.getX() < e.getX()) xDiff = Math.abs(this.getX() + this.getWidth() - e.getX());
//			// else xDiff = Math.abs(e.getX() + e.getWidth() - this.getX());
//			// if(ZMath.sameSign(xMoved, xDiff) && Math.abs(xMoved) > xDiff){
//			// 	double newMoved = xMoved < 0 ? -xDiff : xDiff;
//			// 	newForce = new ZVector(newMoved / (dt * dt) * this.getMass(), newForce.getY());
//			// }
//
//			double limit = 10000;
//			if(newForce.getX() > limit) newForce = new ZVector(limit, newForce.getY());
//			else if(newForce.getX() < -limit) newForce = new ZVector(-limit, newForce.getY());
//
//			// Apply the force to both entities, not just this entity
//			this.setForce(eUuid, newForce);
//			this.collidingUuids.add(eUuid);
//			e.setForce(this.getUuid(), newForce.scale(-1));
//			e.collidingUuids.add(this.getUuid());
		}
	}
	
	/** @param velocity The new current velocity of this {@link EntityThing} */
	public void setVelocity(V velocity){
		this.velocity = velocity;
	}
	
	/**
	 * Add the given velocity to {@link #velocity}
	 *
	 * @param vec The velocity to add
	 */
	public void addVelocity(V vec){
		this.velocity = this.velocity.add(vec);
	}
	
	/**
	 * Determine if this {@link EntityThing} has the force object mapped to the given name
	 *
	 * @param name The object to check for
	 * @return true if this {@link EntityThing} has the given force, false otherwise
	 */
	public boolean hasForce(String name){
		return this.forces.containsKey(name);
	}
	
	/**
	 * @return A list of all forces acting on this thing. This returned list does not reflect actual the collection of forces applied to this thing
	 * 		and should be treated as read only
	 */
	public Collection<Map.Entry<String, V>> getForces(){
		return this.forces.entrySet().stream().toList();
	}
	
	/**
	 * Set the velocity of this thing to zero on all axes and set the current applied for forces to 0
	 */
	public void clearMotion(){
		this.setVelocity(this.zeroVector());
		for(var f : this.getForces()) this.setForce(f.getKey(), this.zeroVector());
	}
	
	/**
	 * Remove the {@link ZVector} with the specified name object from this {@link EntityThing}'s forces
	 *
	 * @param name The name of the force to remove
	 * @return The removed force vector, or null if the given force was not found
	 */
	public V removeForce(String name){
		var removed = this.forces.remove(name);
		if(removed == null) return null;
		this.totalForce = this.totalForce.add(removed.scale(-1));
		return removed;
	}
	
	/**
	 * Set the given force name to the given force. If the given name doesn't have a force mapped to it yet, then this method automatically adds it to the
	 * map
	 *
	 * @param name The name of the force to set
	 * @param force The force object to set
	 * @return force
	 */
	public V setForce(String name, V force){
		this.removeForce(name);
		this.forces.put(name, force);
		this.totalForce = this.totalForce.add(force);
		return force;
	}
	
	/**
	 * Set a force on the vertical, i.e. gravitational, axis.
	 *
	 * @param name The string identifying the force
	 * @param f The quantity of the force, positive means down and negative means up
	 * @return The vector representing the added force
	 */
	public abstract V setVerticalForce(String name, double f);
	
	/** @return The total horizontal velocity of this entity */
	public abstract double getHorizontalVel();
	
	/** @param v The new total horizontal velocity of this entity */
	public abstract void setHorizontalVel(double v);
	
	/** @return The total vertical velocity of this entity */
	public abstract double getVerticalVel();
	
	/** @param v The new total vertical velocity of this entity */
	public abstract void setVerticalVel(double v);
	
	/**
	 * @return If the absolute value of the magnitude of velocity ever reaches a value greater than zero and less than this value,
	 * 		velocity will be clamped to zero. Override if this value must be more restrictive
	 */
	public double getClampVelocity(){
		return 1E-13;
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
	 * @param game The {@link Game} where this thing entered the room
	 */
	public void enterRoom(R from, R to, Game game){
		if(from != null) from.removeThing(this);
		if(to != null) to.addThing(this);
	}
	
	/** @return true if this thing can enter a rom, false otherwise, always returns true by default */
	public boolean canEnterRooms(){
		return true;
	}
}
