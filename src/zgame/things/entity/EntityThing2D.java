package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.ZVector2D;
import zgame.physics.collision.CollisionResult2D;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room2D;

/**
 * An {@link EntityThing} in 2D
 */
public abstract class EntityThing2D extends EntityThing<HitBox2D, EntityThing2D, ZVector2D, Room2D, CollisionResult2D> implements HitBox2D{
	
	// issue#21 allow for multiple hitboxes, so a hitbox for collision and one for rendering, and one for hit detection
	
	/** The x coordinate of this {@link EntityThing2D}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link EntityThing2D} */
	private double x;
	/** The y coordinate of this {@link EntityThing2D}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link EntityThing2D} */
	private double y;
	
	/** The value of the x coordinate from the last tick */
	private double px;
	/** The value of the y coordinate from the last tick */
	private double py;
	
	/**
	 * Create a new empty entity at (0, 0) with a mass of 100
	 */
	public EntityThing2D(){
		this(0, 0);
	}
	
	/**
	 * Create a new empty entity with a mass of 100
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 */
	public EntityThing2D(double x, double y){
		this(x, y, 100);
	}
	
	/**
	 * Create a new empty entity
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 * @param mass See {@link EntityThing#mass}
	 */
	public EntityThing2D(double x, double y, double mass){
		super(mass);
		this.setX(x);
		this.setY(y);
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
	
	@Override
	public void collide(CollisionResult2D r){
		super.collide(r);
		this.addX(r.x());
		this.addY(r.y());
	}
	
	@Override
	public void touchWall(CollisionResult2D result){
		super.touchWall(result);
		this.setHorizontalVel(-this.getHorizontalVel() * result.material().getWallBounce() * this.getMaterial().getWallBounce());
	}
	
	@Override
	public ZVector2D zeroVector(){
		return new ZVector2D();
	}
	
	@Override
	public void moveEntity(ZVector2D distance){
		// Move the entity based on the current velocity and acceleration
		this.px = this.getX();
		this.py = this.getY();
		this.addX(distance.getX());
		this.addY(distance.getY());
	}
	
	/**
	 * Set the given force name with a force built from the given components.
	 * If the given name doesn't have a force mapped to it yet, then this method automatically adds it to the map
	 *
	 * @param name The name of the force to set
	 * @param x The x component
	 * @param y The y component
	 * @return The newly set vector object
	 */
	public ZVector2D setForce(String name, double x, double y){
		return this.setForce(name, new ZVector2D(x, y));
	}
	
	/**
	 * @param x The new x velocity of this {@link EntityThing}
	 * @param y The new y velocity of this {@link EntityThing}
	 */
	public void setVelocity(double x, double y){
		this.setVelocity(new ZVector2D(x, y));
	}
	
	/** @return The velocity of this {@link EntityThing} on the x axis */
	public double getVX(){
		return this.getVelocity().getX();
	}
	
	/** @return The velocity of this {@link EntityThing} on the y axis */
	public double getVY(){
		return this.getVelocity().getY();
	}
	
	/** @param x the new x velocity of this {@link EntityThing} */
	public void setVX(double x){
		this.setVelocity(x, this.getVY());
	}
	
	/** @param y the new y velocity of this {@link EntityThing} */
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
	public double getGravityDragReferenceArea(){
		return this.getWidth();
	}
	
	/** @return The x coordinate of this {@link EntityThing2D} where it was in the previous instance of time, based on its current velocity */
	public double getPX(){
		return px;
	}
	
	/** @return The y coordinate of this {@link EntityThing2D} where it was in the previous instance of time, based on its current velocity */
	public double getPY(){
		return py;
	}
	
	/**
	 * Set a frictional force on the horizontal, i.e. non-gravitational, axis
	 *
	 * @param name The string identifying the force
	 * @param f The quantity of the force, negative or positive to use direction
	 * @return The vector representing the added force
	 */
	public ZVector2D setHorizontalForce(String name, double f){
		return this.setForce(name, f, 0);
	}
	
	@Override
	public ZVector2D setVerticalForce(String name, double f){
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
	public double getGravityAcceleration(){
		return 800;
	}
	
	@Override
	public boolean shouldRender(Renderer r){
		return Game.get().getWindow().gameBoundsInScreen(this.getBounds());
	}
	
	/**
	 * Center the camera of the given {@link Game} to the center of this object
	 */
	public void centerCamera(){
		Game.get().centerCamera(this.centerX(), this.centerY());
	}
	
}
