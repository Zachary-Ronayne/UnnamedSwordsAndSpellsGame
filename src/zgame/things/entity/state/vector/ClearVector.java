package zgame.things.entity.state.vector;

import zgame.physics.ZVector;

/** A velocity update to clear all velocity to zero */
public class ClearVector<V extends ZVector<V>> implements VectorUpdate<V>{
	
	@Override
	public V apply(V existing){
		return existing.zero();
	}
	
	@Override
	public double priority(){
		return 500;
	}
}
