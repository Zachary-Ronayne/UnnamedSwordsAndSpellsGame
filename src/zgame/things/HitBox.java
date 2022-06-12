package zgame.things;

import zgame.physics.collision.CollisionResponse;

/** An interface which defines an object that has a hit box */
public interface HitBox{
	
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
	 * Apply the given {@link CollisionResponse} to this object
	 * 
	 * @param r The {@link CollisionResponse} to use
	 */
	public void collide(CollisionResponse r);
	
	/** A method that defines what this object does when it touches a floor */
	public void touchFloor();
	
	/** A method that defines what this object does when it touches a ceiling */
	public void touchCeiling();
	
	/** A method that defines what this object does when it touches a wall */
	public void touchWall();
	
}
