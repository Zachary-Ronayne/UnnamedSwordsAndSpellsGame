package zgame.things.entity.state;

import zgame.physics.ZVector;
import zgame.things.entity.EntityThing;
import zgame.things.entity.state.vector.*;

import java.util.*;

// TODO add docs and update copied over docs
public class EntityState<V extends ZVector<V>>{
	
	/** The string used to identify the force of gravity in {@link #forces} */
	public static final String FORCE_NAME_GRAVITY = "gravity";
	/** The string used to identify the force of friction in {@link #forces} */
	public static final String FORCE_NAME_FRICTION = "friction";
	/** The string used to identify the force of friction in {@link #forces} */
	public static final String FORCE_NAME_GRAVITY_DRAG = "gravityDrag";
	/** The string used to identify the force of sticking to a wall in {@link #forces} */
	public static final String FORCE_NAME_WALL_SLIDE = "wallSlide";
	
	/** The current velocity of the associated {@link EntityThing} */
	private V velocity;
	
	private final VectorUpdateList<V> velocityUpdates;
	
	private final VectorUpdateMap<String, V> forceUpdates;
	
	/** Every force currently acting on this {@link EntityThing}, mapped by a name */
	private final HashMap<String, V> forces;
	
	// TODO consider if this should be here or not, or maybe it should be in the generic state
	/** The amount of time a single tick will take */
	private double tickTime;
	
	/** A {@link ZVector} representing the total force acting on this {@link EntityThing} */
	private V totalForce;
	
	/** The percentage of gravity that applies to this {@link EntityThing}, defaults to 1, i.e. 100% */
	private double gravityLevel;
	
	/** The mass, i.e. weight, of this {@link EntityThing} */
	private double mass;
	
	/** The current acceleration of gravity */
	private double gravityAcceleration;
	
	// TODO consider if this should be a variable like this
	private final double clampVelocity;
	
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
		this.setForce(FORCE_NAME_GRAVITY, zeroVector);
		this.setForce(FORCE_NAME_FRICTION, zeroVector);
		this.setForce(FORCE_NAME_GRAVITY_DRAG, zeroVector);
		this.setForce(FORCE_NAME_WALL_SLIDE, zeroVector);
	}
	
	// TODO is passing in the previous state needed? Where should it be used that it isn't being used?
	public void applyState(EntityState<V> updated){
		// TODO this should be removed and handled with an update system, copy individual fields
		this.tickTime = updated.getTickTime();
		this.mass = updated.getMass();
		this.gravityLevel = updated.getGravityLevel();
		this.gravityAcceleration = updated.getGravityAcceleration();
		
		// TODO only update gravity if something has changed with its computation
		this.setVerticalForce(FORCE_NAME_GRAVITY, this.getGravityAcceleration() * this.getMass() * this.getGravityLevel());
		
		// Compute updated forces
		this.forceUpdates.applyAll(this.forces);
		
		// Compute new force
		// No forces, there is no force
		// TODO only recompute force if it changes?
		if(this.forces.size() == 0) this.totalForce = this.totalForce.zero();
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
		
		// Add the acceleration to the current velocity
		this.velocity = velocity.add(acceleration.scale(this.getTickTime()));
		
		// Account for clamping the velocity
		double velMag = this.velocity.getMagnitude();
		if(velMag != 0 && velMag < this.clampVelocity) this.velocity = this.velocity.zero();
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
	
	/** @return See {@link #tickTime} */
	public double getTickTime(){
		return this.tickTime;
	}
	
	/** @param tickTime See {@link #tickTime} */
	public void setTickTime(double tickTime){
		this.tickTime = tickTime;
	}
	// TODO make docs and potentially better name
	public V setVerticalForce(String name, double f){
		var oldForce = this.getForce(name);
		return this.setForce(name, oldForce.modifyVerticalMagnitude(f));
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
	
	// TODO need to make all forces apply changes via udpate system, remove all of these methods
	public void setFrictionForce(V newForce){
		this.setForce(FORCE_NAME_FRICTION, newForce);
	}
	
	/**
	 * Remove the {@link ZVector} with the specified name object from this {@link EntityThing}'s forces
	 *
	 * @param name The name of the force to remove
	 * @return The removed force vector, or null if the given force was not found
	 */
	public V removeForce(String name){
		var removed = this.forces.remove(name);
		return removed;
	}
	
	/**
	 * @param name The name of the force to get. This method assumes the force exists
	 * @return The {@link ZVector} representing the force on this object with the given name, or null if none exists for that force
	 */
	public V getForce(String name){
		return this.forces.get(name);
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
		return force;
	}
	
	/**
	 * @return A list of all forces acting on this thing. This returned list does not reflect actual the collection of forces applied to this thing and should be treated as immutable
	 * 		and should be treated as read only
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
		for(var f : this.getForces()) this.setForce(f.getKey(), this.totalForce.zero());
	}
	
}
