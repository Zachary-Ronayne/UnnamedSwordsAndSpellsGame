package zgame.things.entity.state.vector;

import zgame.physics.ZVector;

/** Represents some action that will apply to vector */
public interface VectorUpdate<V extends ZVector<V>>{
	
	/**
	 * Given an existing vector, perform some transformation to it
	 *
	 * @param existing The existing vector
	 * @return The transformed vector
	 */
	V apply(V existing);
	
	/** @return The relative priority that this update should take place over other updates. Smaller priorities are applied last */
	double priority();

}
