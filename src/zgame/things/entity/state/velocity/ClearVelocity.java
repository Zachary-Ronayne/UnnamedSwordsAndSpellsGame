package zgame.things.entity.state.velocity;

import zgame.physics.ZVector;

/** A velocity update to clear all velocity to zero */
public class ClearVelocity<V extends ZVector<V>> implements VelocityUpdate<V>{
	
	@Override
	public V apply(V existing){
		return existing.zero();
	}
	
	@Override
	public double priority(){
		return 500;
	}
}
