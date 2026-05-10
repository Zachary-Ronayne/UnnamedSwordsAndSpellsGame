package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.ZVector2D;
import zgame.physics.collision.CollisionResult2D;
import zgame.things.entity.state.vector.ForceSetElement;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room2D;

/**
 * An {@link EntityThing} in 2D
 */
public abstract class EntityThing2D extends EntityThing<HitBox2D, EntityThing2D, ZVector2D, Room2D, CollisionResult2D> implements HitBox2D{
	
	// issue#21 allow for multiple hitboxes, so a hitbox for collision and one for rendering, and one for hit detection
	
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
	 * @param mass The initial mass of the entity
	 */
	public EntityThing2D(double x, double y, double mass){
		super(mass);
		this.setX(x);
		this.setY(y);
	}
	
	/** @return Current x coordinate of this thing */
	@Override
	public double getX(){
		return this.getPosition().getX();
	}
	
	/** @return Current y coordinate of this thing */
	@Override
	public double getY(){
		return this.getPosition().getY();
	}
	
	@Override
	public void setX(double x){
		this.getNext().attemptSetSingleCoord(new ForceSetElement.X2D(x));
	}
	
	/** @param y New y coordinate of this thing */
	@Override
	public void setY(double y){
		this.getNext().attemptSetSingleCoord(new ForceSetElement.Y2D(y));
	}
	
	/**
	 * Add the given value to the x coordinate
	 * @param x The amount to add
	 */
	public void addX(double x){
		this.getCurrent().addPosition(new ZVector2D(x, 0));
	}
	
	/**
	 * Add the given value to the y coordinate
	 * @param y The amount to add
	 */
	public void addY(double y){
		this.getCurrent().addPosition(new ZVector2D(0, y));
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
		// TODO test this formally and make sure this new approach makes sense
//		this.setHorizontalVel(-this.getHorizontalVel() * result.material().getWallBounce() * this.getMaterial().getWallBounce());
		this.getNext().scaleVelocity(-1 * result.material().getWallBounce() * this.getMaterial().getWallBounce());
	}
	
	@Override
	public ZVector2D zeroVector(){
		return new ZVector2D();
	}
	
	@Override
	public void moveEntity(ZVector2D distance){
		// Move the entity based on the current velocity and acceleration
		this.addX(distance.getX());
		this.addY(distance.getY());
	}
	
	/** @return The velocity of this {@link EntityThing} on the x axis */
	public double getVX(){
		return this.getVelocity().getX();
	}
	
	/** @return The velocity of this {@link EntityThing} on the y axis */
	public double getVY(){
		return this.getVelocity().getY();
	}
	
	// TODO consider if these should exist or not
//	/**
//	 * Add the given amount of velocity to the x component
//	 *
//	 * @param x The velocity to add
//	 */
//	public void addVX(double x){
//		this.addVelocity(new ZVector2D(x, 0));
//	}
//
//	/**
//	 * Add the given amount of velocity to the y component
//	 *
//	 * @param y The velocity to add
//	 */
//	public void addVY(double y){
//		this.addVelocity(new ZVector2D(0, y));
//	}
	
	@Override
	public double getGravityDragReferenceArea(){
		return this.getWidth();
	}
	
	// TODO for now just returning x and y, should these be used?
	/** @return The x coordinate of this {@link EntityThing2D} where it was in the previous instance of time, based on its current velocity */
	public double getPX(){
		return this.getX();
	}
	
	/** @return The y coordinate of this {@link EntityThing2D} where it was in the previous instance of time, based on its current velocity */
	public double getPY(){
		return this.getY();
	}
	
	@Override
	public double getHorizontalVel(){
		return this.getVX();
	}
	
	@Override
	public double getVerticalVel(){
		return this.getVY();
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
