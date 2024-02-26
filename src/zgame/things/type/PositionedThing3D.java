package zgame.things.type;

import zgame.things.entity.Entity;

/** A {@link GameThing} which uses x, y, and z coordinates */
public abstract class PositionedThing3D extends PositionedThing implements Position3D{
	
	/** The x coordinate of this {@link PositionedThing}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link Entity} */
	private double x;
	/** The y coordinate of this {@link PositionedThing}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link Entity} */
	private double y;
	/** The z coordinate of this {@link PositionedThing}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link Entity} */
	private double z;
	
	/** Create a new {@link PositionedThing} at (0, 0, 0) */
	public PositionedThing3D(){
		this(0, 0, 0);
	}
	
	/**
	 * Create a new {@link PositionedThing2D}
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 */
	public PositionedThing3D(double x, double y, double z){
		super();
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	/** @return See {@link #x} */
	@Override
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/**
	 * Add the given value to {@link #x}
	 *
	 * @param x The value to add
	 */
	public void addX(double x){
		this.setX(this.getX() + x);
	}
	
	/** @return See {@link #y} */
	@Override
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}
	
	/**
	 * Add the given value to {@link #y}
	 *
	 * @param y The value to add
	 */
	public void addY(double y){
		this.setY(this.getY() + y);
	}
	
	/** @return See {@link #z} */
	@Override
	public double getZ(){
		return this.z;
	}
	
	/** @param z See {@link #z} */
	public void setZ(double z){
		this.z = z;
	}
	
	/**
	 * Add the given value to {@link #z}
	 *
	 * @param z The value to add
	 */
	public void addZ(double z){
		this.setZ(this.getZ() + z);
	}
	
}
