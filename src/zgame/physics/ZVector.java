package zgame.physics;

/**
 * A Vector with an x and y component. The internal values of this object cannot be modified after the object is created outside of its extensions, i.e. this object is immutable
 */
public abstract class ZVector{
	
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
	public void setMagnitude(double magnitude){
		this.magnitude = magnitude;
	}
	
	// TODO is this the best way to do this?
	/** @return The magnitude of the horizontal components of this vector */
	public abstract double getHorizontalForce();
	
	/** @return The magnitude of the vertical components of this vector */
	public abstract double getVerticalForce();
	
	/**
	 * Add the given {@link ZVector} to this ZVector and return the result.
	 * This method does not modify either vector
	 *
	 * @param newV The ZVector to add
	 * @return The result of adding both vectors
	 */
	public abstract ZVector add(ZVector newV);
	
	/**
	 * Scale the vector based on the given value, i.e. multiply the x and y components by the given value.
	 * This method does not modify either vector
	 *
	 * @param scalar The value to scale by
	 * @return The result of scaling the vector
	 */
	public abstract ZVector scale(double scalar);
	
}
