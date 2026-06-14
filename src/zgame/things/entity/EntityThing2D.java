package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.V2D;
import zgame.physics.collision.Collision;
import zgame.things.entity.state.vector.EntityState2D;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room;
import zgame.world.Room2D;

/**
 * An {@link EntityThing} in 2D
 */
public abstract class EntityThing2D extends EntityThing<V2D> implements HitBox2D{
	
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
		this.getCurrent().initPosition(new V2D(x, y));
	}
	
	@Override
	protected EntityState2D initEntityState(V2D zeroVector, double gravityAcceleration, double clampVelocity){
		return new EntityState2D(this, zeroVector, gravityAcceleration, clampVelocity);
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
	
	// TODO remove/rename this to reflect that it's effectively for teleporting
	/**
	 * @param x New x coordinate of this thing
	 * @param y New y coordinate of this thing
	 */
	public void setPos(double x, double y){
		this.getNext().attemptSetPosition(new V2D(x, y));
	}
	
	@Override
	public void touchWall(Collision<V2D> result){
		super.touchWall(result);
		// TODO test this formally and make sure this new approach makes sense
//		this.setHorizontalVel(-this.getHorizontalVel() * result.material().getWallBounce() * this.getMaterial().getWallBounce());
		this.getNext().scaleVelocity(-1 * result.material().getWallBounce() * this.getMaterial().getWallBounce());
	}
	
	@Override
	public V2D zeroVector(){
		return new V2D();
	}
	
	/** @return The velocity of this {@link EntityThing} on the x axis */
	public double getVX(){
		return this.getVelocity().getX();
	}
	
	/** @return The velocity of this {@link EntityThing} on the y axis */
	public double getVY(){
		return this.getVelocity().getY();
	}
	
	@Override
	public double getGravityDragReferenceArea(){
		return this.getWidth();
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
	
	/** See {@link #enterRoom(Room, Room)} */
	public void enterRoom(Room2D from, Room2D to){
		super.enterRoom(from, to);
	}
}
