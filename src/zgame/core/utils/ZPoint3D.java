package zgame.core.utils;

/** An object storing a 3D point */
public class ZPoint3D {
	
	/** The x coordinate of the point */
	private double x;
	/** The y coordinate of the point */
	private double y;
	/** The z coordinate of the point */
	private double z;
	
	/**
	 * Constructs and initializes a {@code ZPoint3D} with coordinates (0, 0, 0)
	 */
	public ZPoint3D(){
		this(0, 0, 0);
	}
	
	/**
	 * Constructs and initializes a {@code ZPoint3D} with the specified coordinates.
	 *3
	 * @param x the X coordinate of the newly constructed {@code ZPoint3D}
	 * @param y the Y coordinate of the newly constructed {@code ZPoint3D}
	 * @param z the Z coordinate of the newly constructed {@code ZPoint3D}
	 */
	public ZPoint3D(double x, double y, double z){
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	/** @return A point with the same x and y values as this point, but as a different object */
	public ZPoint3D copy(){
		return new ZPoint3D(this.getX(), this.getY(), this.getZ());
	}
	
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}
	
	/** @return See {@link #z} */
	public double getZ(){
		return this.z;
	}
	
	/** @param z See {@link #z} */
	public void setZ(double z){
		this.z = z;
	}
}
