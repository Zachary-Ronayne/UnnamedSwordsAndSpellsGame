package zgame.physics;

import zgame.core.utils.ZMath;

/**
 * A Vector with an x, y, and z component. The internal values of this object cannot be modified after the object is created, i.e. this object is immutable
 */
public class ZVector3D extends ZVector<ZVector3D>{
	
	/** The x component of this {@link ZVector3D} */
	private double x;
	/** The y component of this {@link ZVector3D} */
	private double y;
	/** The z component of this {@link ZVector3D} */
	private double z;
	
	/** The angle, in radians, of this {@link ZVector3D}, along the horizontal axis, i.e. the x z plane */
	private double yaw;
	
	/** The angle, in radians, of this {@link ZVector3D}, along the vertical axis, i.e. the y axis */
	private double pitch;
	
	/** The magnitude on the horizontal axis, i.e. x z plane */
	private double horizontalMag;
	
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
		this(x, y, z, true);
	}
	
	/**
	 * Create a new ZVector with the given component values
	 *
	 * @param a If comps is true, see {@link #x}, otherwise see {@link #yaw}
	 * @param b If comps is true, See {@link #y}, otherwise see {@link #pitch}
	 * @param c If comps is true, See {@link #z}, otherwise see {@link #magnitude}
	 * @param comps true if a, b, and c represent the x, y, and z components of this {@link ZVector2D}, otherwise, they represent the angles and magnitude
	 */
	public ZVector3D(double a, double b, double c, boolean comps){
		super();
		if(comps){
			this.x = a;
			this.y = b;
			this.z = c;
			this.calcAngleMag();
		}
		else{
			this.yaw = a;
			this.pitch = b;
			this.setMagnitude(c);
			this.calcComponents();
		}
	}
	
	/** Update the internal x, y, and z values based on the current values of {@link #yaw}, {@link #pitch} */
	@Override
	public void calcComponents(){
		// Ensure magnitude is positive
		double mag = this.getMagnitude();
		boolean inverted = mag < 0;
		if(inverted) this.setMagnitude(-mag);
		
		// Horizontal magnitude will be the cos, i.e. horizontal, value of the pitch and the total magnitude
		this.horizontalMag = Math.abs(Math.cos(this.pitch) * this.getMagnitude());
		// The x component will be the cos value on the horizontal axis with the total horizontal magnitude
		this.x = Math.cos(this.yaw) * this.horizontalMag;
		// The vertical magnitude will be the sin value of the pitch and the total magnitude
		this.y = Math.sin(this.pitch) * this.getMagnitude();
		// The z component will be the sin value on the horizontal axis with the total horizontal magnitude
		this.z = Math.sin(this.yaw) * this.horizontalMag;
		if(inverted) {
			this.x = -this.x;
			this.y = -this.y;
			this.z = -this.z;
		}
	}
	
	/** Update the internal angle and magnitude values based on the current values of {@link #x}, {@link #y}, and {@link #z} */
	@Override
	public void calcAngleMag(){
		// The horizontal magnitude is just the distance formula for the 2 horizontal axes
		this.horizontalMag = ZMath.hypot(this.x, this.z);
		// The yaw is the angle between the two horizontal axes, modded to be between [0, 2PI]
		this.yaw = ZMath.atan2Normalized(this.z, this.x);
		if(Double.isNaN(this.yaw)) this.yaw = 0;
		// The pitch angle is the angle between the horizontal magnitude and the y magnitude, modded to be between [0, 2PI]
		this.pitch = ZMath.atan2Normalized(this.y, this.horizontalMag);
		if(Double.isNaN(this.pitch)) this.pitch = 0;
		// The total magnitude is just the distance formula for the vertical and horizontal axes
		this.setMagnitude(ZMath.hypot(this.y, this.horizontalMag));
	}
	
	@Override
	public double getHorizontal(){
		return this.horizontalMag;
	}
	
	@Override
	public double getVertical(){
		return Math.abs(this.getY());
	}
	
	@Override
	public double getVerticalValue(){
		return this.getY();
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
	
	/** @return See {@link #yaw} */
	public double getYaw(){
		return this.yaw;
	}
	
	/** @return See {@link #pitch} */
	public double getPitch(){
		return this.pitch;
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
	 * Scale the vector based on the given value, i.e. multiply the x, y, and z components by the given value.
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
	public ZVector3D modifyMagnitude(double magnitude){
		return new ZVector3D(this.getYaw(), this.getPitch(), magnitude, false);
	}
	
	@Override
	public ZVector3D modifyHorizontalMagnitude(double magnitude){
		return new ZVector3D(Math.cos(this.getYaw()) * magnitude, this.getY(), Math.sin(this.getYaw()) * magnitude);
	}
	
	@Override
	public ZVector3D modifyVerticalMagnitude(double magnitude){
		return new ZVector3D(this.getX(), this.getY() > 0 ? magnitude : -magnitude, this.getZ());
	}
	
	@Override
	public ZVector3D modifyVerticalValue(double value){
		return new ZVector3D(this.getX(), value, this.getZ());
	}
	
	@Override
	public boolean isOpposite(ZVector3D vector){
		return ZVector.isOpposite(this.getYaw(), vector.getYaw()) || ZVector.isOpposite(this.getPitch(), vector.getPitch());
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("[ZVector3D | x: ");
		sb.append(this.getX());
		sb.append(", y: ");
		sb.append(this.getY());
		sb.append(", z: ");
		sb.append(this.getZ());
		sb.append(", yaw: ");
		sb.append(this.getYaw());
		sb.append(", pitch: ");
		sb.append(this.getPitch());
		sb.append(", horizontal: ");
		sb.append(this.getHorizontal());
		sb.append(", mag: ");
		sb.append(this.getMagnitude());
		sb.append("]");
		return sb.toString();
	}
	
}
