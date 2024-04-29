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
	private double angleH;
	
	/** The angle, in radians, of this {@link ZVector3D}, along the vertical axis, i.e. the y axis */
	private double angleV;
	
	/** The magnitude on the horizontal axis, i.e. x z plane */
	private double horizontalMag;
	
	/** Create a {@link ZVector3D} with a magnitude of 0 */
	public ZVector3D(){
		this(0, 0, 0);
	}
	
	/**
	 * Create a new ZVector with the given angle and magnitude values
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
	 * @param a If comps is true, see {@link #x}, otherwise see {@link #angleH}
	 * @param b If comps is true, See {@link #y}, otherwise see {@link #angleV}
	 * @param c If comps is true, See {@link #y}, otherwise see {@link #magnitude}
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
			this.angleH = a;
			this.angleV = b;
			this.setMagnitude(c);
			this.calcComponents();
		}
	}
	
	/** Update the internal x, y, and z values based on the current values of {@link #angleH}, {@link #angleV} */
	@Override
	public void calcComponents(){
		// TODO explain where these equations come from, and figure out if they are even correct
		this.horizontalMag = Math.cos(angleV) * this.getMagnitude();
		this.x = Math.sin(this.angleH) * this.horizontalMag;
		this.y = Math.cos(this.angleV) * this.getMagnitude();
		this.z = Math.sin(this.angleH) * this.horizontalMag;
	}
	
	/** Update the internal angle and magnitude values based on the current values of {@link #x}, {@link #y}, and {@link #z} */
	@Override
	public void calcAngleMag(){
		// TODO maybe abstract out some of these equations and make this more readable
		// TODO explain where these equations come from, and figure out if they are even correct
		this.horizontalMag = Math.sqrt(this.x * this.x + this.z * this.z);
		this.angleH = (Math.atan2(this.z, this.x) + ZMath.TAU) % ZMath.TAU;
		this.angleV = (Math.atan2(this.y, this.horizontalMag) + ZMath.TAU) % ZMath.TAU;
		this.setMagnitude(Math.sqrt(this.y * this.y + this.horizontalMag * this.horizontalMag));
	}
	
	@Override
	public double getHorizontal(){
		return this.horizontalMag;
	}
	
	@Override
	public double getVertical(){
		return this.y;
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
	
	/** @return See {@link #angleH} */
	public double getAngleH(){
		return this.angleH;
	}
	
	/** @return See {@link #angleV} */
	public double getAngleV(){
		return this.angleV;
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
		StringBuilder sb = new StringBuilder("[ZVector3D | x: ");
		sb.append(this.getX());
		sb.append(", y: ");
		sb.append(this.getY());
		sb.append(", z: ");
		sb.append(this.getZ());
		sb.append(", angleH: ");
		sb.append(this.getAngleH());
		sb.append(", angleV: ");
		sb.append(this.getAngleV());
		sb.append(", mag: ");
		sb.append(this.getMagnitude());
		sb.append("]");
		return sb.toString();
	}
	
}
