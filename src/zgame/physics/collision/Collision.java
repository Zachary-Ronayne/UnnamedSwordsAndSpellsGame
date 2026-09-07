package zgame.physics.collision;

import zgame.physics.ZVector;

/**
 * An object containing values for what should happen to an object when it collides with something
 * @param <V> The type of vector storing information about the collision
 */
public sealed abstract class Collision<V extends ZVector<V>> permits Collision2D, Collision3D{
	
	/** true if the collision was into a wall, false otherwise */
	private final boolean wall;
	/** true if the collision was into a ceiling above, false otherwise */
	private final boolean ceiling;
	/** true if the collision was into a floor below, false otherwise */
	private final boolean floor;
	
	/** The new position that the collided object should be at to no longer collide */
	private final V newPos;
	
	// TODO update docs
	/**
	 * Create a new {@link Collision} with the given material
	 *
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 */
	public Collision(V newPos, boolean wall, boolean ceiling, boolean floor){
		this.newPos = newPos;
		this.wall = wall;
		this.ceiling = ceiling;
		this.floor = floor;
	}
	
	// TODO setup auto docs for getters, setters, etc
	public V newPos(){
		return newPos;
	}
	
	/** @return See {@link #wall} */
	public boolean wall(){
		return this.wall;
	}
	
	/** @return See {@link #ceiling} */
	public boolean ceiling(){
		return ceiling;
	}
	
	/** @return See {@link #floor} */
	public boolean floor(){
		return floor;
	}
	
	/** @return true if this {@link Collision} represents a collision happening, false if no collision took place */
	public boolean isCollided(){
		return this.wall() || this.ceiling() || this.floor();
	}
	
	/** @return true if the collision hit anything, i.e. a wall, ceiling, or floor, false otherwise */
	public abstract boolean hit();
	
	/**
	 * Helper method for converting this collision to the correct type, implement as returning this cast to the correct type
	 * @param clazz The type of collision to convert to
	 * @return This, but as the correct type
	 * @param <C> The type to convert to
	 */
	public abstract <C extends Collision<V>> C asCollision(Class<C> clazz);
	
}
