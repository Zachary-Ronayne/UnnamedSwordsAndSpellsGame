package zgame.things.entity.state.velocity;

import zgame.physics.ZVector;

/** A velocity update to clear all vertical velocity to zero */
public class ClearVelocityVertical<V extends ZVector<V>> implements VelocityUpdate<V>{
	
	@Override
	public V apply(V existing){
		return existing.modifyVerticalMagnitude(0);
	}
	
	@Override
	public double priority(){
		return 501;
	}
}
