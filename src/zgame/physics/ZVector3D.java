package zgame.physics;

import zgame.core.utils.ZMath;

/**
 * A Vector with an x, y, and z component. The internal values of this object cannot be modified after the object is created, i.e. this object is immutable
 */
public class ZVector3D extends ZVector<ZVector3D>{
	
	// TODO implement as 3D
	
	/** The x component of this {@link ZVector3D} */
	private double x;
	/** The y component of this {@link ZVector3D} */
	private double y;
	/** The z component of this {@link ZVector3D} */
	private double z;
	
	/** The angle, in radians, of this {@link ZVector3D} */
	private double angle;
	
	/** Create a {@link ZVector3D} with a magnitude of 0 */
	public ZVector3D(){
		this(0, 0, 0);
	}
	
	/**
	 * Create a new ZVector with the given component values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 */
	public ZVector3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/** Update the internal x and y values based on the current values of {@link #angle} and {@link #magnitude} */
	@Override
	public void calcComponents(){
		this.x = Math.cos(this.getAngle()) * this.getMagnitude();
		this.y = Math.sin(this.getAngle()) * this.getMagnitude();
	}
	
	/** Update the internal angle and magnitude values based on the current values of {@link #x} and {@link #y} */
	@Override
	public void calcAngleMag(){
		this.angle = (Math.atan2(this.getY(), this.getX()) + ZMath.TAU) % ZMath.TAU;
		this.setMagnitude(Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)));
	}
	
	@Override
	public double getHorizontalForce(){
		return 0;
	}
	
	@Override
	public double getVerticalForce(){
		return 0;
	}
	
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
	/** @return See {@link #z} */
	public double getZ(){
		return this.z;
	}
	
	/** @return See {@link #angle} */
	public double getAngle(){
		return this.angle;
	}
	
	/** @return The value of {@link #angle} in degrees */
	public double getAngleDeg(){
		return this.angle * 180.0 / Math.PI;
	}
	
	/**
	 * Add the given {@link ZVector2D} to this ZVector and return the result.
	 * This method does not modify either vector
	 *
	 * @param newV The ZVector to add
	 * @return The result of adding both vectors
	 */
	@Override
	public ZVector3D add(ZVector3D newV){
		return new ZVector3D(this.getX() + newV.getX(), this.getY() + newV.getY(), this.getZ() + newV.getZ());
	}
	
	/**
	 * Scale the vector based on the given value, i.e. multiply the x and y components by the given value.
	 * This method does not modify either vector
	 *
	 * @param scalar The value to scale by
	 * @return The result of scaling the vector
	 */
	@Override
	public ZVector3D scale(double scalar){
		return new ZVector3D(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("[ZVector | x: ");
		sb.append(this.getX());
		sb.append(", y: ");
		sb.append(this.getY());
		sb.append(", z: ");
		sb.append(this.getZ());
		sb.append(", angle: ");
		sb.append(this.getAngle());
		sb.append(", mag: ");
		sb.append(this.getMagnitude());
		sb.append("]");
		return sb.toString();
	}
	
}
