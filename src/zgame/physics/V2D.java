package zgame.physics;

import zgame.core.utils.ZMath;

/**
 * A Vector with an x and y component. The internal values of this object cannot be modified after the object is created, i.e. this object is immutable
 */
public final class V2D extends ZVector<V2D>{
	
	/** The x component of this {@link V2D} */
	private double x;
	/** The y component of this {@link V2D} */
	private double y;
	
	/** The angle, in radians, of this {@link V2D} */
	private double angle;
	
	/** Create a {@link V2D} with a magnitude of 0 */
	public V2D(){
		this(0, 0);
	}
	
	/**
	 * Create a new ZVector with the given component values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 */
	public V2D(double x, double y){
		this(x, y, true);
	}
	
	/**
	 * Create a new ZVector with the given component values
	 *
	 * @param a If comps is true, see {@link #x}, otherwise see {@link #angle}
	 * @param b If comps is true, See {@link #y}, otherwise see {@link #magnitude}
	 * @param comps true if a and b represent the x and y components of this {@link V2D}, otherwise, they represent angle and magnitude
	 */
	public V2D(double a, double b, boolean comps){
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
	
	@Override
	public V2D zero(){
		return new V2D();
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
		if(Double.isNaN(this.angle)) this.angle = 0;
		this.setMagnitude(Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)));
	}
	
	@Override
	public double getHorizontal(){
		return Math.abs(this.getX());
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
	
	/** @return See {@link #angle} */
	public double getAngle(){
		return this.angle;
	}
	
	/** @return The value of {@link #angle} in degrees */
	public double getAngleDeg(){
		return this.angle * 180.0 / Math.PI;
	}
	
	/**
	 * Add the given {@link V2D} to this ZVector and return the result.
	 * This method does not modify either vector
	 *
	 * @param newV The ZVector to add
	 * @return The result of adding both vectors
	 */
	@Override
	public V2D add(V2D newV){
		return new V2D(this.getX() + newV.getX(), this.getY() + newV.getY());
	}
	
	/**
	 * Scale the vector based on the given value, i.e. multiply the x and y components by the given value.
	 * This method does not modify either vector
	 *
	 * @param scalar The value to scale by
	 * @return The result of scaling the vector
	 */
	@Override
	public V2D scale(double scalar){
		return new V2D(this.getX() * scalar, this.getY() * scalar);
	}
	
	@Override
	public V2D modifyMagnitude(double magnitude){
		return new V2D(this.getAngle(), magnitude, false);
	}
	
	@Override
	public V2D modifyHorizontalMagnitude(double magnitude){
		if(Math.cos(this.getAngle()) < 0) magnitude = -magnitude;
		return new V2D(magnitude, this.getY());
	}
	
	@Override
	public V2D modifyVerticalMagnitude(double magnitude){
		if(Math.sin(this.getAngle()) < 0) magnitude = -magnitude;
		return new V2D(this.getX(), magnitude);
	}
	
	@Override
	public V2D modifyVerticalValue(double value){
		return new V2D(this.getX(), value);
	}
	
	@Override
	public boolean isOpposite(V2D vector){
		return ZVector.isOpposite(this.getAngle(), vector.getAngle());
	}
	
	/** @return A vector which is the same as this one, but with the y axis inverted */
	public V2D invertY(){
		return new V2D(this.getX(), -this.getY());
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
