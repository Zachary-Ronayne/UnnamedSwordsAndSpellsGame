package zgame.things.entity.state;

import zgame.physics.ZVector;
import zgame.things.entity.EntityThing;

import java.util.ArrayList;
import java.util.List;

// TODO add docs
public class EntityState<V extends ZVector<V>>{
	
	/** The current velocity of the associated {@link EntityThing} */
	private V velocity;
	
	/** The thing this state represents */
	private final EntityThing<?, ? ,V, ?, ?> thing;
	
	/*
	 TODO experiment with a more sophisticated system, allowing for a list of processes to update the velocity, or actions to take on the velocity, rather than just a boolean
	   this should allow for adding velocities
	   In this context, velocity is never directly updated, only ever scheduled to be updated
	 */
	private boolean clearVelocity;
	
	// TODO make docs and finalize design
	private final List<V> addVelocities;
	
	// TODO figure out if this makes any sense to use
	// TODO how will this work when multiple processes want to set the velocity? Need to use the more sophisticated system
	/** The velocity to force set the entity to */
	private V newVelocity;
	
	// TODO need some flag for inverting velocity, or maybe it's a generic way, for bouncing off walls
	
	// TODO consider if this should be here or not, or maybe it should be in the generic state
	/** The amount of time a single tick will take */
	private double tickTime;
	
	// TODO use proper type parameters
	public EntityState(EntityThing<?, ? ,V, ?, ?> thing){
		this.thing = thing;
		
		this.velocity = thing.zeroVector();
		this.clearVelocity = false;
		this.addVelocities = new ArrayList<>();
	}
	
	public void applyState(EntityState<V> updated){
		if(updated.clearVelocity) this.velocity = this.thing.zeroVector();
		else if(this.newVelocity != null){
			this.velocity = this.newVelocity;
		}
		else{
			var newVelocity = this.velocity;
			for(var v : addVelocities){
				newVelocity = newVelocity.add(v);
			}
			this.velocity = newVelocity;
		}
		this.clearVelocity = false;
		this.addVelocities.clear();
		this.newVelocity = null;
	}
	
	/** @return See {@link #velocity} */
	public V getVelocity(){
		return this.velocity;
	}
	
	// TODO make docs
	public void addVelocity(V velocity){
		this.addVelocities.add(velocity);
	}
	
	public void forceSetVelocity(V velocity){
		if(this.newVelocity != null) return;
		this.newVelocity = velocity;
	}
	
	/** Schedule the velocity to be zero on the next update */
	public void clearVelocity(){
		this.clearVelocity = true;
	}
	
	/** @return See {@link #tickTime} */
	public double getTickTime(){
		return this.tickTime;
	}
	
	/** @param tickTime See {@link #tickTime} */
	public void setTickTime(double tickTime){
		this.tickTime = tickTime;
	}
}
