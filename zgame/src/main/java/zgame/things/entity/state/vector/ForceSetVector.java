package zgame.things.entity.state.vector;

import zgame.physics.ZVector;

// TODO does it make sense to have a set update? May want to rethink how these updates will work
/** A vector update to be some amount */
public class ForceSetVector<V extends ZVector<V>> implements VectorUpdate<V>{
	
	/** The vector to set to. Priority will be dictated by the magnitude, higher magnitude means being applied last */
	private final V newVector;
	
	/** @param newVector See {@link #newVector} */
	public ForceSetVector(V newVector){
		this.newVector = newVector;
	}
	
	@Override
	public V apply(V existing){
		return this.newVector;
	}
	
	@Override
	public double priority(){
		return 10 + (1.0 / this.newVector.getMagnitude());
	}
}
