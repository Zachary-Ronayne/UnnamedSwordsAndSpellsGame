package zgame.things.entity.state.velocity;

import zgame.physics.ZVector;

/** A velocity update to add some amount of velocity */
public class AddVelocity<V extends ZVector<V>> implements VelocityUpdate<V>{
	
	/** The amount of velocity to add for the update */
	private final V amount;
	
	/** @param amount See {@link #amount} */
	public AddVelocity(V amount){
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
