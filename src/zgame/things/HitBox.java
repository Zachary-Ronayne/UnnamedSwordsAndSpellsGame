package zgame.things;

import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;

/** An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds */
public interface HitBox extends Materialable{
	
	/**
	 * Apply the given {@link CollisionResponse} to this object
	 * 
	 * @param r The {@link CollisionResponse} to use
	 */
	public void collide(CollisionResponse r);
	
	/**
	 * Determine a {@link CollisionResponse} from colliding this object with the given rectangular bounds. Essentially, move this object so that it no longer intersecting with the
	 * given bounds. This method should not change the state of this object, it should only return an object representing how the collision should happen.
	 * 
	 * @param x The x coordinate of the upper left hand corner of the bounds
	 * @param y The y coordinate of the upper left hand corner of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @param m The material which was collided with
	 * 
	 * @return The information about the collision
	 */
	public CollisionResponse calculateRectCollision(double x, double y, double w, double h, Material m);
	
	/**
	 * Reposition this object so that it is to the left of the given x coordinate.
	 * If the object is already to the left of the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being to the left of the given coordinate
	 * 
	 * @param x The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	public boolean keepLeft(double x);
	
	/**
	 * Reposition this object so that it is to the right of the given x coordinate.
	 * If the object is already to the right of the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being to the right of the given coordinate
	 * 
	 * @param x The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	public boolean keepRight(double x);
	
	/**
	 * Reposition this object so that it is above the given y coordinate.
	 * If the object is already above the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being above the given coordinate
	 * 
	 * @param y The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	public boolean keepAbove(double y);
	
	/**
	 * Reposition this object so that it is below the given y coordinate.
	 * If the object is already below the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being below the given coordinate
	 * 
	 * @param y The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	public boolean keepBelow(double y);
	
	/**
	 * A method that defines what this object does when it touches a floor
	 * 
	 * @param touched The Material which this {@link HitBox} touched
	 */
	public void touchFloor(Material touched);
	
	/** A method that defines what this object does when it leaves the floor, i.e. is not touching the floor */
	public void leaveFloor();
	
	/**
	 * A method that defines what this object does when it touches a ceiling
	 * 
	 * @param touched The Material which this {@link HitBox} touched
	 */
	public void touchCeiling(Material touched);
	
	/**
	 * A method that defines what this object does when it touches a wall
	 * 
	 * @param touched The Material which this {@link HitBox} touched
	 */
	public void touchWall(Material touched);

	/** @return true if this {@link HitBox} is on the ground, false otherwise i.e. it's in the air */
	public boolean isOnGround();
	
	// TODO maybe use these methods, or similar, when determining if something should render
	/** @return The minimum x coordinate of the bounds of this {@link HitBox} */
	public double getX();
	
	/** @return The minimum y coordinate of the bounds of this {@link HitBox} */
	public double getY();
	
	/** @return The previous value of {@link #getX()} before the last time it was moved with velocity */
	public double getPX();
	
	/** @return The previous value of {@link #getY()} before the last time it was moved with velocity */
	public double getPY();
	
	/** @return The maximum x coordinate of the bounds of this {@link HitBox} */
	public double getMX();
	
	/** @return The maximum y coordinate of the bounds of this {@link HitBox} */
	public double getMY();
	
}
