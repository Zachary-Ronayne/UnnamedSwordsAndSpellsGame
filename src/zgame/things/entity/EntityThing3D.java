package zgame.things.entity;

import zgame.core.GameTickable;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResponse;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.things.type.bounds.HitboxType;
import zgame.world.Room3D;

/**
 * An {@link EntityThing} in 3D
 */
public abstract class EntityThing3D extends EntityThing<HitBox3D, EntityThing3D, ZVector3D, Room3D> implements GameTickable, HitBox3D, Bounds3D{
	
	// TODO implement
	
	// TODO make a cylinder extension of entity thing 3D
	
	/** The x coordinate of the bottom center of this entity thing */
	private double x;
	/** The y coordinate of the bottom center of this entity thing */
	private double y;
	/** The z coordinate of the bottom center of this entity thing */
	private double z;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass See {@link #mass}
	 */
	public EntityThing3D(double mass){
		super(mass);
		// TODO make a better constructor
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	@Override
	public void moveEntity(ZVector3D moveVec, double dt){
		// TODO implement
	}
	
	@Override
	public ZVector3D setHorizontalForce(String name, double f){
		// TODO implement
		return zeroVector();
	}
	
	@Override
	public ZVector3D setVerticalForce(String name, double f){
		// TODO implement
		return zeroVector();
	}
	
	@Override
	public double getHorizontalVel(){
		// TODO implement
		return 0;
	}
	
	@Override
	public void setHorizontalVel(double v){
		// TODO implement
	}
	
	@Override
	public double getVerticalVel(){
		// TODO implement
		return 0;
	}
	
	@Override
	public void setVerticalVel(double v){
		// TODO implement
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
	
	}
	
	@Override
	public double getPX(){
		// TODO implement
		return 0;
	}
	
	@Override
	public double getPY(){
		// TODO implement
		return 0;
	}
	
	@Override
	public double getPZ(){
		// TODO implement
		return 0;
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
}
