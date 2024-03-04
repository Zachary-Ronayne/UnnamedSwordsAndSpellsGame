package zgame.things.entity;

import zgame.physics.ZVector;
import zgame.physics.ZVector2D;
import zgame.physics.collision.CollisionResponse;
import zgame.things.type.bounds.HitBox;
import zgame.things.type.bounds.HitBox2D;
import zgame.things.type.bounds.HitboxType;

/**
 * A thing which keeps track of an entity in 2D space, i.e. an object which can regularly move around in space and exist at an arbitrary location.
 * This is for things like creatures, dropped items, projectiles, etc.
 */
public class Entity2D extends Entity<HitBox2D>{
	
	// TODO make a 3D version of this
	
	/** The thing using this entity */
	private final EntityThing2D thing;
	
	/** The value of the x coordinate from the last tick */
	private double px;
	/** The value of the y coordinate from the last tick */
	private double py;
	
	/**
	 * Create a new empty entity with the given mass for the given {@link EntityThing2D}
	 *
	 * @param mass See {@link #mass}
	 */
	public Entity2D(EntityThing2D thing, double mass){
		super(mass);
		this.thing = thing;
	}
	
	@Override
	public void collide(CollisionResponse r){
		// TODO implement properly and abstract out some of this logic
		this.thing.addX(r.x());
		this.thing.addY(r.y());
		if(r.wall()) this.touchWall(r.material());
		if(r.ceiling()) this.touchCeiling(r.material());
		if(r.floor()) this.touchFloor(r.material());
	}
	
	@Override
	public ZVector2D zeroVector(){
		return new ZVector2D();
	}
	
	@Override
	public void moveEntity(ZVector acceleration, double dt){
		// Move the entity based on the current velocity and acceleration
		this.px = this.getThing().getX();
		this.py = this.getThing().getY();
		// TODO is casting the best way to do this?
		var moveVec = (ZVector2D)this.getVelocity().scale(dt).add(acceleration.scale(dt * dt * 0.5));
		this.getThing().addX(moveVec.getX());
		this.getThing().addY(moveVec.getY());
	}
	
	// TODO abstract out
	/**
	 * Set the given force name with a force built from the given components. If the given name doesn't have a force mapped to it yet, then this method automatically adds it to the
	 * map
	 *
	 * @param name The name of the force to set
	 * @param x The x component
	 * @param y The y component
	 * @return The newly set vector object
	 */
	public ZVector setForce(String name, double x, double y){
		return setForce(name, new ZVector2D(x, y));
	}
	
	/**
	 * @param x The new x velocity of this {@link Entity}
	 * @param y The new y velocity of this {@link Entity}
	 */
	public void setVelocity(double x, double y){
		this.setVelocity(new ZVector2D(x, y));
	}
	
	// TODO implement these properly
	/** @return The velocity of this {@link Entity} on the x axis */
	public double getVX(){
		return this.getVelocity().getHorizontalForce();
	}
	
	/** @return The velocity of this {@link Entity} on the y axis */
	public double getVY(){
		return this.getVelocity().getVerticalForce();
	}
	
	/** @param x the new x velocity of this {@link Entity} */
	public void setVX(double x){
		this.setVelocity(x, this.getVY());
	}
	
	/** @param y the new y velocity of this {@link Entity} */
	public void setVY(double y){
		this.setVelocity(this.getVX(), y);
	}
	
	/**
	 * Add the given amount of velocity to the x component
	 *
	 * @param x The velocity to add
	 */
	public void addVX(double x){
		this.addVelocity(new ZVector2D(x, 0));
	}
	
	/**
	 * Add the given amount of velocity to the y component
	 *
	 * @param y The velocity to add
	 */
	public void addVY(double y){
		this.addVelocity(new ZVector2D(0, y));
	}
	
	@Override
	public double getSurfaceArea(){
		return this.getThing().getWidth();
	}
	
	/** @return The x coordinate of this {@link Entity2D} where it was in the previous instance of time, based on its current velocity */
	public double getPX(){
		return px;
	}
	
	/** @return The y coordinate of this {@link Entity2D} where it was in the previous instance of time, based on its current velocity */
	public double getPY(){
		return py;
	}
	
	/** @return See {@link #thing} */
	public EntityThing2D getThing(){
		return this.thing;
	}
	
	@Override
	public double getFrictionConstant(){
		return this.getThing().getFrictionConstant();
	}
	
	@Override
	public ZVector setHorizontalForce(String name, double f){
		return this.setForce(name, f, 0);
	}
	
	@Override
	public ZVector setVerticalForce(String name, double f){
		return this.setForce(name, 0, f);
	}
	
	@Override
	public double getHorizontalVel(){
		return this.getVX();
	}
	
	@Override
	public void setHorizontalVel(double v){
		this.setVX(v);
	}
	
	@Override
	public double getVerticalVel(){
		return this.getVY();
	}
	
	@Override
	public void setVerticalVel(double v){
		this.setVY(v);
	}
	
	@Override
	public HitboxType getType(){
		return this.getThing().getType();
	}
	
	@Override
	public boolean intersects(HitBox<HitBox2D> h){
		return this.getThing().intersects(h);
	}
	
	@Override
	public HitBox2D get(){
		return this.getThing();
	}
}
