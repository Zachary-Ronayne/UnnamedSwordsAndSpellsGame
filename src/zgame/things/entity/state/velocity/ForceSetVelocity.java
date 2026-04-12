package zgame.things.entity.state.velocity;

import zgame.physics.ZVector;

/** A velocity update to force velocity to be some amount */
public class ForceSetVelocity<V extends ZVector<V>> implements VelocityUpdate<V>{
	
	/** The velocity to set to. Priority will be dictated by the magnitude, higher magnitude means being applied last */
	private final V newVelocity;
	
	/** @param newVelocity See {@link #newVelocity} */
	public ForceSetVelocity(V newVelocity){
		this.newVelocity = newVelocity;
	}
	
	@Override
	public V apply(V existing){
		return this.newVelocity;
	}
	
	@Override
	public double priority(){
		return 10 + (1.0 / this.newVelocity.getMagnitude());
	}
}
