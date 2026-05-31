package zgame.physics.collision;

import zgame.physics.ZVector;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.type.bounds.HitBox;

import java.util.function.Function;

/**
 * An object containing values for what should happen to an object when it collides with something
 * @param <V> The type of vector storing information about the collision
 */
public sealed abstract class Collision<V extends ZVector<V>> permits Collision2D, Collision3D{
	
	/** The material which was hit during this collision, or null if no collision took place */
	private final Material material;
	
	/** true if the collision was into a wall, false otherwise */
	private final boolean wall;
	/** true if the collision was into a ceiling above, false otherwise */
	private final boolean ceiling;
	/** true if the collision was into a floor below, false otherwise */
	private final boolean floor;
	
	/** The change in position that this collision will result in from the associated hit box's initial state */
	private final V initialChange;
	
	/** A function that, given a hitbox to collide with, computes the new position it should move to */
	private final Function<HitBox<V>, V> computeNewPosition;
	
	/**
	 * Create a new {@link Collision} with the given material
	 *
	 * @param hitBox The object which collided
	 * @param computeNewPosition See {@link #computeNewPosition}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 */
	public Collision(HitBox<V> hitBox, Function<HitBox<V>, V> computeNewPosition, Material material, boolean wall, boolean ceiling, boolean floor){
		this.computeNewPosition = computeNewPosition;
		this.initialChange = hitBox.getPosition().add(this.newPos(hitBox));
		
		// Set the material to no material if none is given
		this.material = material == null ? Materials.NONE : material;
		this.wall = wall;
		this.ceiling = ceiling;
		this.floor = floor;
	}
	
	// TODO consider if it makes sense to do it this way with computing as needed
	/** @return The computed value of {@link #computeNewPosition} */
	public V newPos(HitBox<V> hitBox){
		return this.computeNewPosition.apply(hitBox);
	}
	
	/** @return See {@link #initialChange} */
	public V initialChange(){
		return this.initialChange;
	}
	
	/** @return See {@link #material} */
	public Material material(){
		return this.material;
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
		return this.initialChange().getMagnitude() != 0;
	}
	
	/** @return true if the collision hit anything, i.e. a wall, ceiling, or floor, false otherwise */
	public abstract boolean hit();
	
	/**
	 * Helper method for converting this collision to the correct type, implement as returning this casted to the correct type
	 * @param clazz The type of collision to convert to
	 * @return This, but as the correct type
	 * @param <C> The type to convert to
	 */
	public abstract <C extends Collision<V>> C asCollision(Class<C> clazz);
	
}
