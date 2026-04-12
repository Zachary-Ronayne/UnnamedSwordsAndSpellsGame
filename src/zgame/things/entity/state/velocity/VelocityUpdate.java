package zgame.things.entity.state.velocity;

import zgame.physics.ZVector;

/** Represents some action that will apply to velocity */
public interface VelocityUpdate<V extends ZVector<V>>{
	
	/**
	 * Given an existing velocity, perform some transformation to it
	 *
	 * @param existing The existing velocity
	 * @return The transformed velocity
	 */
	V apply(V existing);
	
	/** @return The relative priority that this update should take place over other updates. Smaller priorities are applied last */
	double priority();

}
