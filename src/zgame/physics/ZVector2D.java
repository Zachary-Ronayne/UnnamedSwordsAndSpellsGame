package zgame.physics;

import zgame.core.utils.ZMath;

/**
 * A Vector with an x and y component. The internal values of this object cannot be modified after the object is created, i.e. this object is immutable
 */
public class ZVector2D extends ZVector<ZVector2D>{
	
	/** The x component of this {@link ZVector2D} */
	private double x;
	/** The y component of this {@link ZVector2D} */
	private double y;
	
	/** The angle, in radians, of this {@link ZVector2D} */
	private double angle;
	
	/** Create a {@link ZVector2D} with a magnitude of 0 */
	public ZVector2D(){
		this(0, 0);
	}
	
	/**
	 * Create a new ZVector with the given component values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 */
	public ZVector2D(double x, double y){
		this(x, y, true);
	}
	
	/**
	 * Create a new ZVector with the given component values
	 *
	 * @param a If comps is true, see {@link #x}, otherwise see {@link #angle}
	 * @param b If comps is true, See {@link #y}, otherwise see {@link #magnitude}
	 * @param comps true if a and b represent the x and y components of this {@link ZVector2D}, otherwise, they represent angle and magnitude
	 */
	public ZVector2D(double a, double b, boolean comps){
		super();
		if(comps){
			this.x = a;
			this.y = b;
			this.calcAngleMag();
		}
		else{
			this.angle = a;
			this.setMagnitude(b);
			this.calcComponents();
		}
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
	public double getHorizontal(){
		return this.getX();
	}
	
	@Override
	public double getVertical(){
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
	public ZVector2D add(ZVector2D newV){
		return new ZVector2D(this.getX() + newV.getX(), this.getY() + newV.getY());
	}
	
	/**
	 * Scale the vector based on the given value, i.e. multiply the x and y components by the given value.
	 * This method does not modify either vector
	 *
	 * @param scalar The value to scale by
	 * @return The result of scaling the vector
	 */
	@Override
	public ZVector2D scale(double scalar){
		return new ZVector2D(this.getX() * scalar, this.getY() * scalar);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("[ZVector2D | x: ");
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
