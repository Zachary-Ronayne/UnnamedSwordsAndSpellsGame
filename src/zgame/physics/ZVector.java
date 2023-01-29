package zgame.physics;

import zgame.core.utils.ZMath;

/**
 * A Vector with an x and y component. The internal values of this object cannot be modified after the object is created, i.e. this object is immutable
 */
public class ZVector{
	
	/** The x component of this {@link ZVector} */
	private double x;
	/** The y component of this {@link ZVector} */
	private double y;
	
	/** The angle, in radians, of this {@link ZVector} */
	private double angle;
	/** The magnitude, i.e. length or distance, of this {@link ZVector} */
	private double magnitude;
	
	/** Create a {@link ZVector} with a magnitude of 0 */
	public ZVector(){
		this(0, 0);
	}
	
	/**
	 * Create a new ZVector with the given component values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 */
	public ZVector(double x, double y){
		this(x, y, true);
	}
	
	/**
	 * Create a new ZVector with the given component values
	 *
	 * @param a If comps is true, see {@link #x}, otherwise see {@link #angle}
	 * @param b If comps is true, See {@link #y}, otherwise see {@link #magnitude}
	 * @param comps true if a and b represent the x and y components of this {@link ZVector}, otherwise, they represent angle and magnitude
	 */
	public ZVector(double a, double b, boolean comps){
		if(comps){
			this.x = a;
			this.y = b;
			this.calcAngleMag();
		}
		else{
			this.angle = a;
			this.magnitude = b;
			this.calcComponents();
		}
	}
	
	/** Update the internal x and y values based on the current values of {@link #angle} and {@link #magnitude} */
	private void calcComponents(){
		this.x = Math.cos(this.getAngle()) * this.getMagnitude();
		this.y = Math.sin(this.getAngle()) * this.getMagnitude();
	}
	
	/** Update the internal angle and magnitude values based on the current values of {@link #x} and {@link #y} */
	private void calcAngleMag(){
		this.angle = (Math.atan2(this.getY(), this.getX()) + ZMath.TAU) % ZMath.TAU;
		this.magnitude = Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));
	}
	
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
	/** @return See {@link #angle} */
	public double getAngle(){
		return this.angle;
	}
	
	/** @return The value of {@link #angle} in degrees */
	public double getAngleDeg(){
		return this.angle * 180.0 / Math.PI;
	}
	
	/** @return See {@link #magnitude} */
	public double getMagnitude(){
		return this.magnitude;
	}
	
	/**
	 * Add the given {@link ZVector} to this ZVector and return the result.
	 * This method does not modify either vector
	 *
	 * @param newV The ZVector to add
	 * @return The result of adding both vectors
	 */
	public ZVector add(ZVector newV){
		return new ZVector(this.getX() + newV.getX(), this.getY() + newV.getY());
	}
	
	/**
	 * Scale the vector based on the given value, i.e. multiply the x and y components by the given value.
	 * This method does not modify either vector
	 *
	 * @param scalar The value to scale by
	 * @return The result of scaling the vector
	 */
	public ZVector scale(double scalar){
		return new ZVector(this.getX() * scalar, this.getY() * scalar);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("[ZVector | x: ");
		sb.append(this.getX());
		sb.append(", y: ");
		sb.append(this.getY());
		sb.append(", angle: ");
		sb.append(this.getAngle());
		sb.append(", mag: ");
		sb.append(this.getMagnitude());
		sb.append("]");
		return sb.toString();
	}
	
}
