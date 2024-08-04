package zgame.physics;

/**
 * A Vector with an x and y component. The internal values of this object cannot be modified after the object is created outside of its extensions, i.e. this object is immutable
 *
 * @param <V> The type of vector used by this vector
 */
public abstract class ZVector<V extends ZVector<V>>{
	
	/** The magnitude, i.e. length or distance, of this {@link ZVector} */
	private double magnitude;
	
	/** Create a {@link ZVector} with a magnitude of 0 */
	public ZVector(){
		this(0);
	}
	
	/**
	 * Create a {@link ZVector} with the given magnitude
	 *
	 * @param magnitude See {@link #magnitude}
	 */
	public ZVector(double magnitude){
		this.magnitude = magnitude;
	}
	
	/** Update the internal component values based on the angles of this vector and {@link #magnitude} */
	public abstract void calcComponents();
	
	/** Update the internal angle and magnitude values based on the current component values */
	public abstract void calcAngleMag();
	
	/** @return See {@link #magnitude} */
	public double getMagnitude(){
		return this.magnitude;
	}
	
	/** @param magnitude See {@link #magnitude} */
	protected void setMagnitude(double magnitude){
		this.magnitude = magnitude;
	}
	
	/** @return The magnitude of the horizontal components of this vector */
	public abstract double getHorizontal();
	
	/** @return The magnitude of the vertical components of this vector */
	public abstract double getVertical();
	
	/**
	 * Add the given {@link ZVector} to this ZVector and return the result.
	 * This method does not modify either vector
	 *
	 * @param newV The ZVector to add
	 * @return The result of adding both vectors
	 */
	public abstract V add(V newV);
	
	/**
	 * Scale the vector based on the given value, i.e. multiply the x and y components by the given value.
	 * This method does not modify either vector
	 *
	 * @param scalar The value to scale by
	 * @return The result of scaling the vector
	 */
	public abstract V scale(double scalar);
	
	/**
	 * Create a new vector which has the same direction as this vector, but with the given magnitude
	 * @param magnitude The magnitude
	 * @return The new vector
	 */
	public abstract V modifyMagnitude(double magnitude);
	
	/**
	 * Create a new vector which has the same direction as this vector and the same vertical magnitude, but with the given horizontal magnitude
	 * @param magnitude The magnitude
	 * @return The new vector
	 */
	public abstract V modifyHorizontalMagnitude(double magnitude);
	
	/**
	 * Create a new vector which has the same direction as this vector and the same horizontal magnitude, but with the given vertical magnitude
	 * @param magnitude The magnitude
	 * @return The new vector
	 */
	public abstract V modifyVerticalMagnitude(double magnitude);
	
}
