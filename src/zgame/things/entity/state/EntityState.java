package zgame.things.entity.state;

import zgame.physics.ZVector;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.entity.EntityThing;
import zgame.things.entity.state.vector.*;

import java.util.*;

/**
 * The state of an entity, accounting for position, velocity, and forces
 *
 * @param <V> The type of vector that this state uses
 */
public class EntityState<V extends ZVector<V>>{
	
	/** The string used to identify the force of gravity in {@link #forces} */
	public static final String FORCE_GRAVITY = "gravity";
	/** The string used to identify the force of friction in {@link #forces} */
	public static final String FORCE_FRICTION = "friction";
	/** The string used to identify the force of friction in {@link #forces} */
	public static final String FORCE_GRAVITY_DRAG = "gravityDrag";
	/** The string used to identify the force of sticking to a wall in {@link #forces} */
	public static final String FORCE_WALL_SLIDE = "wallSlide";
	
	/** The current velocity of this state */
	private V velocity;
	
	/** Pending updates that must occur to the current velocity */
	private final VectorUpdateList<V> velocityUpdates;
	
	/** Pending updates that must occur to specific forces, mapped by their names */
	private final VectorUpdateMap<String, V> forceUpdates;
	
	/** Every force currently acting on this, mapped by its name */
	private final HashMap<String, V> forces;
	
	// TODO consider if this should be here or not, or maybe it should be in the generic state
	/** The amount of time a single tick will take */
	private double tickTime;
	
	/** A {@link ZVector} representing the total force acting on this */
	private V totalForce;
	
	/** The percentage of gravity that applies to this, defaults to 1, i.e. 100% */
	private double gravityLevel;
	
	/** The mass, i.e. weight, of this */
	private double mass;
	
	/** The current acceleration of gravity */
	private double gravityAcceleration;
	
	/** The amount of time in seconds since this last touched the ground, or -1 if it is currently on the ground */
	private double groundTime;
	/** The amount of time in seconds this has been on the ground, or -1 if it is not on the ground */
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
	// TODO implement updates for allowing material to change
	/** The Material which this {@link EntityThing} is made of */
	private final Material material;
	/** true if collision should be disabled, false otherwise */
	private boolean noClip;
	
	/** All updates that must occur on the next state update */
	private final ArrayList<Runnable> stateUpdates;
	
	// TODO consider if this should be a variable like this, or a method like it was with entities
	private final double clampVelocity;
	
	// TODO probably avoid having to recompute all vector values every time for position, for now just using a simple vector
	/** Current game position of this */
	private V position;
	/** All updates to apply to the position */
	private final VectorUpdateList<V> positionUpdates;
	
	public EntityState(V zeroVector, double gravityAcceleration, double clampVelocity){
		this.clampVelocity = clampVelocity;
		
		// General misc fields
		this.setGravityLevel(1);
		this.setGravityAcceleration(gravityAcceleration);
		
		// Init main tracked velocity
		this.velocity = zeroVector;
		this.velocityUpdates = new VectorUpdateList<>();
		this.forceUpdates = new VectorUpdateMap<>();
		
		// Init tracker for total force and main set of forces
		this.totalForce = zeroVector;
		this.forces = new HashMap<>();
		
		// Init individual forces
		this.initForce(FORCE_GRAVITY);
		this.initForce(FORCE_FRICTION);
		this.initForce(FORCE_GRAVITY_DRAG);
		this.initForce(FORCE_WALL_SLIDE);
		
		// Init state for environment interaction
		this.material = Materials.DEFAULT_ENTITY;
		this.floorMaterial = Materials.NONE;
		this.groundTime = 0;
		this.onGroundTime = 0;
		this.ceilingMaterial = Materials.NONE;
		this.ceilingTime = 0;
		this.wallMaterial = Materials.NONE;
		this.wallTime = 0;
		this.noClip = false;
		
		this.stateUpdates = new ArrayList<>();
		
		this.position = zeroVector;
		this.positionUpdates = new VectorUpdateList<>();
	}
	
	/** @return A zero vector for this entity state */
	private V zeroVec(){
		return this.velocity.zero();
	}
	
	/**
	 * Initialize the position of this state directly to the given value
	 * @param pos The initial position to use
	 */
	public void initPosition(V pos){
		this.position = pos;
	}
	
	// TODO is passing in the previous state needed? Where should it be used that it isn't being used?
	public void applyState(EntityState<V> updated){
		// TODO avoid having to copy these every time if nothing changes
		this.tickTime = updated.getTickTime();
		this.mass = updated.getMass();
		this.gravityLevel = updated.getGravityLevel();
		this.gravityAcceleration = updated.getGravityAcceleration();
		
		// Update general state
		for(var update : this.stateUpdates) update.run();
		this.stateUpdates.clear();
		
		// TODO only update gravity if something has changed with its computation
		this.attemptSetVerticalForce(FORCE_GRAVITY, this.getGravityAcceleration() * this.getMass() * this.getGravityLevel());
		
		// Compute updated forces
		this.forceUpdates.applyAll(this.forces);
		
		// Compute new force
		// No forces, there is no force
		// TODO only recompute force if it changes?
		if(this.forces.size() == 0) this.totalForce = this.zeroVec();
		// Sum all forces
		else{
			var allForces = this.getForces();
			this.totalForce = allForces.get(0).getValue();
			for(int i = 1; i < allForces.size(); i++){
				this.totalForce = this.totalForce.add(allForces.get(i).getValue());
			}
		}
		
		// Compute new velocity
		this.velocity = this.velocityUpdates.apply(this.velocity);
		
		// Find the current acceleration
		var acceleration = this.getForce().scale(1.0 / this.getMass());
		
		// TODO should this part be in applyState? Maybe it should be in the same place position is updated
		// Add the acceleration to the current velocity
		this.velocity = this.velocity.add(acceleration.scale(this.getTickTime()));
		
		// TODO should this be in applyState?
		// Update the amount of time the entity has been on the ground, walls, and ceiling
		var dt = this.getTickTime();
		if(this.groundTime != -1) this.groundTime += dt;
		if(this.onGroundTime != -1) this.onGroundTime += dt;
		if(this.ceilingTime != -1) this.ceilingTime += dt;
		if(this.wallTime != -1) this.wallTime += dt;
		
		// TODO when should position be updated?
		this.position = this.positionUpdates.apply(updated.position);
		
		// Account for clamping the velocity
		double velMag = this.velocity.getMagnitude();
		if(velMag != 0 && velMag < this.clampVelocity) this.velocity = this.zeroVec();
	}
	
	/**
	 * Initialize the given force to a zero vector
	 *
	 * @param name The name of the force to init
	 * @return The initialized force
	 */
	public V initForce(String name){
		return this.forces.put(name, this.zeroVec());
	}
	
	/** @return See {@link #position} */
	public V getPosition(){
		return this.position;
	}
	
	/** @param update A scheduled update to happen to velocity on the next tick */
	public void updateVelocity(VectorUpdate<V> update){
		this.velocityUpdates.update(update);
	}
	
	/** @return See {@link #velocity} */
	public V getVelocity(){
		return this.velocity;
	}
	
	/** @return See {@link #totalForce} */
	public V getForce(){
		return this.totalForce;
	}
	
	// TODO make docs
	public void addVelocity(V velocity){
		this.updateVelocity(new AddVector<>(velocity));
	}
	
	public void attemptSetVelocity(V velocity){
		this.updateVelocity(new ForceSetVector<>(velocity));
	}
	
	/** Schedule the velocity to be zero on the next update */
	public void clearVelocity(){
		this.updateVelocity(new ClearVector<>());
	}
	
	/** Schedule the vertical velocity to be zero on the next update */
	public void clearVelocityVertical(){
		this.updateVelocity(new ClearVectorVertical<>());
	}
	
	public void scaleVelocity(double scalar){
		this.updateVelocity(new ScaleVector<>(scalar));
	}
	
	public void scaleVelocityVertical(double scalar){
		this.updateVelocity(new ScaleVectorVertical<>(scalar));
	}
	
	/** @return See {@link #clampVelocity} */
	public double getClampVelocity(){
		return this.clampVelocity;
	}
	
	/** @return See {@link #tickTime} */
	public double getTickTime(){
		return this.tickTime;
	}
	
	/** @param tickTime See {@link #tickTime} */
	public void setTickTime(double tickTime){
		this.tickTime = tickTime;
	}
	
	/** @return See {@link #gravityLevel} */
	public double getGravityLevel(){
		return this.gravityLevel;
	}
	
	// TODO handle with an update system
	
	/** @param gravityLevel See {@link #gravityLevel} */
	public void setGravityLevel(double gravityLevel){
		this.gravityLevel = gravityLevel;
	}
	
	/** @return See {@link #mass} */
	public double getMass(){
		return this.mass;
	}
	
	// TODO handle with an update system
	
	/** @param mass See {@link #mass} */
	public void setMass(double mass){
		this.mass = mass;
	}
	
	/** @return See {@link #gravityAcceleration} */
	public double getGravityAcceleration(){
		return this.gravityAcceleration;
	}
	
	/** @param gravityAcceleration See {@link #gravityAcceleration} */
	public void setGravityAcceleration(double gravityAcceleration){
		this.gravityAcceleration = gravityAcceleration;
	}
	
	/** @param name The name of the force that should be reset to zero */
	public void clearForce(String name){
		this.forceUpdates.update(name, new ClearVector<>());
	}
	
	/**
	 * @param name The name of the force that should be affected
	 * @param force The amount of force to add
	 */
	public void addForce(String name, V force){
		this.forceUpdates.update(name, new AddVector<>(force));
	}
	
	/**
	 * @param name The name of the force that should be affected
	 * @param force The new value of the given force
	 */
	public void attemptSetForce(String name, V force){
		this.forceUpdates.update(name, new ForceSetVector<>(force));
	}
	
	/**
	 * @param name The name of the force that should be affected
	 * @param force The new value for the vertical force
	 */
	public void attemptSetVerticalForce(String name, double force){
		this.forceUpdates.update(name, new ForceSetVector<>(this.zeroVec().modifyVerticalValue(force)));
	}
	
	/**
	 * @param name The name of the force to get. This method assumes the force exists
	 * @return The {@link ZVector} representing the force on this object with the given name, or null if none exists for that force
	 */
	public V getForce(String name){
		return this.forces.get(name);
	}
	
	/**
	 * @return A list of all forces acting on this thing. This returned list does not reflect actual the collection of forces applied to this thing,
	 * 		and should be treated as immutable and read only
	 */
	public List<Map.Entry<String, V>> getForces(){
		return this.forces.entrySet().stream().toList();
	}
	
	// TODO handle this with an update system
	
	/**
	 * Set the velocity of this thing to zero on all axes and set the current applied for forces to 0
	 */
	public void clearMotion(){
		this.clearVelocity();
		for(var f : this.getForces()) this.clearForce(f.getKey());
	}
	
	/** @return See {@link #material} */
	public Material getMaterial(){
		return this.material;
	}
	
	/** @return See {@link #floorMaterial} */
	public Material getFloorMaterial(){
		return this.floorMaterial;
	}
	
	/** @return See {@link #ceilingMaterial} */
	public Material getCeilingMaterial(){
		return this.ceilingMaterial;
	}
	
	/** @return See {@link #wallMaterial} */
	public Material getWallMaterial(){
		return this.wallMaterial;
	}
	
	// TODO potentially consolidate or rename, or remove some of these fields
	
	/** @return See {@link #groundTime} */
	public double getGroundTime(){
		return this.groundTime;
	}
	
	/** @return See {@link #onGroundTime} */
	public double getOnGroundTime(){
		return this.onGroundTime;
	}
	
	/** @return true if this was on the ground in the past tick, false otherwise */
	public boolean isOnGround(){
		return this.getGroundTime() == -1;
	}
	
	/** @return See {@link #ceilingTime} */
	public double getCeilingTime(){
		return this.ceilingTime;
	}
	
	/** @return See {@link #wallTime} */
	public double getWallTime(){
		return this.wallTime;
	}
	
	/** @return See {@link #noClip} */
	public boolean isNoClip(){
		return this.noClip;
	}
	
	/** @param update An update to schedule to run on the next state update */
	private void scheduleUpdate(Runnable update){
		this.stateUpdates.add(update);
	}
	
	// TODO should these updates go directly to this state, or accept the next state to set it to?
	// TODO need to have some priority system for which update to set if leave/touch methods are called in the same tick
	
	/** Schedule that this has left a floor */
	public void leaveFloor(){
		this.scheduleUpdate(() -> {
			this.floorMaterial = Materials.NONE;
			this.groundTime = 0;
			this.onGroundTime = -1;
		});
	}
	
	/**
	 * Schedule that this touched a floor
	 *
	 * @param touched The material the floor is made of
	 */
	public void touchFloor(Material touched){
		this.scheduleUpdate(() -> {
			// Touching a floor means this entity is on the ground
			this.floorMaterial = touched;
			this.groundTime = -1;
			if(onGroundTime < 0) this.onGroundTime = 0;
		});
	}
	
	/** Schedule that this has left a ceiling */
	public void leaveCeiling(){
		this.scheduleUpdate(() -> {
			this.ceilingMaterial = Materials.NONE;
			this.ceilingTime = 0;
		});
	}
	
	/**
	 * Schedule that this touched a ceiling
	 *
	 * @param touched The material the floor is made of
	 */
	public void touchCeiling(Material touched){
		this.scheduleUpdate(() -> {
			this.ceilingMaterial = touched;
			this.ceilingTime = -1;
		});
	}
	
	/** Schedule that this has left a wall */
	public void leaveWall(){
		this.scheduleUpdate(() -> {
			this.wallMaterial = Materials.NONE;
			this.wallTime = 0;
		});
	}
	
	/**
	 * Schedule that this touchWall a wall
	 *
	 * @param touched The material the floor is made of
	 */
	public void touchWall(Material touched){
		this.scheduleUpdate(() -> {
			this.wallMaterial = touched;
			this.wallTime = -1;
		});
	}
	
	// TODO this will need to be deterministic, probably just always prefer setting no clip as false by default, only set it to true if nothing else sets it to false
	/** @param noClip The new value to set {@link #noClip} */
	public void scheduleNoClip(boolean noClip){
		this.scheduleUpdate(() -> {
			this.noClip = noClip;
		});
	}
	
	public void schedulePosition(VectorUpdate<V> update){
		this.positionUpdates.update(update);
	}
	
	public void attemptSetPosition(V position){
		this.schedulePosition(new ForceSetVector<>(position));
	}
	
	public void addPosition(V delta){
		this.schedulePosition(new AddVector<>(delta));
	}
	
}
