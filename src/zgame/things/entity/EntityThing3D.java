package zgame.things.entity;

import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.type.bounds.HitBox3D;
import zgame.things.type.bounds.HitboxType;
import zgame.world.Room3D;

/**
 * An {@link EntityThing} in 3D
 */
public abstract class EntityThing3D extends EntityThing<HitBox3D, EntityThing3D, ZVector3D, Room3D> implements HitBox3D{
	
	// TODO make a cylinder extension of entity thing 3D
	
	/** The x coordinate of the bottom center of this entity thing */
	private double x;
	/** The y coordinate of the bottom center of this entity thing */
	private double y;
	/** The z coordinate of the bottom center of this entity thing */
	private double z;
	
	/** The value of the x coordinate from the last tick */
	private double px;
	/** The value of the y coordinate from the last tick */
	private double py;
	/** The value of the z coordinate from the last tick */
	private double pz;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass See {@link #mass}
	 */
	public EntityThing3D(double mass){
		this(0, 0, 0, mass);
	}
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param mass See {@link #mass}
	 */
	public EntityThing3D(double x, double y, double z, double mass){
		super(mass);
		this.x = x;
		this.y = y;
		this.z = z;
		this.px = x;
		this.py = y;
		this.pz = z;
	}
	
	@Override
	public void moveEntity(ZVector3D distance){
		this.px = this.getX();
		this.py = this.getY();
		this.pz = this.getZ();
		this.addX(distance.getX());
		this.addY(distance.getY());
		this.addZ(distance.getZ());
	}
	
	/**
	 * Set the given force name with a force built from the given components.
	 * If the given name doesn't have a force mapped to it yet, then this method automatically adds it to the map
	 *
	 * @param name The name of the force to set
	 * @param x The x component
	 * @param y The y component
	 * @param z The z component
	 * @return The newly set vector object
	 */
	public ZVector3D setForce(String name, double x, double y, double z){
		return this.setForce(name, new ZVector3D(x, y, z));
	}
	
	@Override
	public ZVector3D setVerticalForce(String name, double f){
		return this.setForce(name, 0, f, 0);
	}
	
	@Override
	public double getHorizontalVel(){
		return this.getVelocity().getHorizontal();
	}
	
	@Override
	public void setHorizontalVel(double v){
		var oldVel = this.getVelocity();
		var oldAngle = oldVel.getAngleH();
		var newVel = new ZVector3D(Math.cos(oldAngle) * v, oldVel.getVertical(), Math.sin(oldAngle) * v, true);
		
		this.setVelocity(newVel);
	}
	
	@Override
	public double getVerticalVel(){
		return this.getVelocity().getY();
	}
	
	@Override
	public void setVerticalVel(double v){
		var vel = this.getVelocity();
		this.setVelocity(new ZVector3D(vel.getX(), v, vel.getZ()));
	}
	
	@Override
	public HitboxType getType(){
		// TODO implement
		return HitboxType.RECT;
	}
	
	@Override
	public boolean intersects(HitBox3D h){
		// TODO implement
		return false;
	}
	
	@Override
	public void collide(CollisionResponse r){
		// TODO implement
	}
	
	@Override
	public void touchWall(Material touched){
		// TODO somehow this needs to account for the angle the thing was at when it hit the wall and adjust the angle appropriately
		
		super.touchWall(touched);
	}
	
	@Override
	public double getPX(){
		return this.px;
	}
	
	@Override
	public double getPY(){
		return this.py;
	}
	
	@Override
	public double getPZ(){
		return this.pz;
	}
	
	/** @return See {@link #x} */
	@Override
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	@Override
	public void setX(double x){
		this.x = x;
	}
	
	/**
	 * Add the given value to the x coordinate
	 * @param x The amount to add
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
	@Override
	public void setY(double y){
		this.y = y;
	}
	
	/**
	 * Add the given value to the y coordinate
	 * @param y The amount to add
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
	@Override
	public void setZ(double z){
		this.z = z;
	}
	
	/**
	 * Add the given value to the z coordinate
	 * @param z The amount to add
	 */
	public void addZ(double z){
		this.setZ(this.getZ() + z);
	}
	
	@Override
	public ZVector3D zeroVector(){
		return new ZVector3D();
	}
	
	@Override
	public double getGravityAcceleration(){
		return -9.8;
	}
}
