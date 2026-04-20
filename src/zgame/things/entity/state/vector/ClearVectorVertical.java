package zgame.things.entity.state.vector;

import zgame.physics.ZVector;

/** A velocity update to clear all vertical velocity to zero */
public class ClearVectorVertical<V extends ZVector<V>> implements VectorUpdate<V>{
	
	@Override
	public V apply(V existing){
		return existing.modifyVerticalMagnitude(0);
	}
	
	@Override
	public double priority(){
		return 501;
	}
}
