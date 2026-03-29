package zgame.things.entity.state;

import zgame.physics.ZVector;
import zgame.things.entity.EntityThing;

// TODO add docs
public class EntityState<V extends ZVector<V>>{
	
	/** The current velocity of the associated {@link EntityThing} */
	private V velocity;
	
	// TODO use proper type parameters
	public EntityState(EntityThing<?, ? ,V, ?, ?> thing){
		this.velocity = thing.zeroVector();
	}
	
	/** @return See {@link #velocity} */
	public V getVelocity(){
		return this.velocity;
	}
	
	/** @param velocity See {@link #velocity} */
	public void setVelocity(V velocity){
		this.velocity = velocity;
	}
}
