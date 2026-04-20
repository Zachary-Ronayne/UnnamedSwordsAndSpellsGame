package zgame.things.entity.state.vector;

import zgame.physics.ZVector;

/** A velocity update to scale the existing velocity by some amount, can include inverting */
public class ScaleVector<V extends ZVector<V>> implements VectorUpdate<V>{
	
	/** The amount to scale the velocity by */
	private final double scalar;
	
	/** @param scalar See {@link #scalar} */
	public ScaleVector(double scalar){
		this.scalar = scalar;
	}
	
	@Override
	public V apply(V existing){
		return existing.scale(this.scalar);
	}
	
	@Override
	public double priority(){
		return 2000;
	}
}
