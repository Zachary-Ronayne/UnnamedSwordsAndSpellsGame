package zgame.things.entity.state.vector;

import zgame.physics.ZVector;

/** A velocity update to add some amount of velocity */
public class AddVector<V extends ZVector<V>> implements VectorUpdate<V>{
	
	/** The amount of velocity to add for the update */
	private final V amount;
	
	/** @param amount See {@link #amount} */
	public AddVector(V amount){
		this.amount = amount;
	}
	
	@Override
	public V apply(V existing){
		return existing.add(this.amount);
	}
	
	@Override
	public double priority(){
		return 1000;
	}
}
