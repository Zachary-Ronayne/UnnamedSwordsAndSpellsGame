package zgame.things.entity.state;

import zgame.physics.ZVector;
import zgame.things.entity.EntityThing;
import zgame.things.entity.state.velocity.*;

import java.util.ArrayList;
import java.util.Comparator;

// TODO add docs
public class EntityState<V extends ZVector<V>>{
	
	/** The current velocity of the associated {@link EntityThing} */
	private V velocity;
	
	private final ArrayList<VelocityUpdate<V>> velocityUpdates;
	
	// TODO consider if this should be here or not, or maybe it should be in the generic state
	/** The amount of time a single tick will take */
	private double tickTime;
	
	// TODO use proper type parameters
	public EntityState(EntityThing<?, ? ,V, ?, ?> thing){
		this.velocity = thing.zeroVector();
		this.velocityUpdates = new ArrayList<>();
	}
	
	public void applyState(EntityState<V> updated){
		var newVelocity = this.velocity;
		var sortedUpdates = this.velocityUpdates.stream().sorted(Comparator.comparingDouble(VelocityUpdate::priority)).toList();
		for(var update : sortedUpdates){
			newVelocity = update.apply(newVelocity);
		}
		this.velocity = newVelocity;
		
		this.velocityUpdates.clear();
	}
	
	/** @param update A scheduled update to happen to velocity on the next tick */
	public void updateVelocity(VelocityUpdate<V> update){
		this.velocityUpdates.add(update);
	}
	
	/** @return See {@link #velocity} */
	public V getVelocity(){
		return this.velocity;
	}
	
	// TODO make docs
	public void addVelocity(V velocity){
		this.updateVelocity(new AddVelocity<>(velocity));
	}
	
	public void attemptSetVelocity(V velocity){
		this.updateVelocity(new ForceSetVelocity<>(velocity));
	}
	
	/** Schedule the velocity to be zero on the next update */
	public void clearVelocity(){
		this.updateVelocity(new ClearVelocity<>());
	}
	
	/** Schedule the vertical velocity to be zero on the next update */
	public void clearVelocityVertical(){
		this.updateVelocity(new ClearVelocityVertical<>());
	}
	
	public void scaleVelocity(double scalar){
		this.updateVelocity(new ScaleVelocity<>(scalar));
	}
	
	public void scaleVelocityVertical(double scalar){
		this.updateVelocity(new ScaleVelocityVertical<>(scalar));
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
