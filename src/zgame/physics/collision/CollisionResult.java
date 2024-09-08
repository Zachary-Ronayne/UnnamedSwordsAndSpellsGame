package zgame.physics.collision;

import zgame.physics.material.Material;
import zgame.physics.material.Materials;

/** An object containing values for what should happen to an object when it collides with something */
public abstract class CollisionResult<C extends CollisionResult<C>>{
	
	/** The material which was hit during this collision, or null if no collision took place */
	private final Material material;
	
	/** A response representing no collision occurring */
	public CollisionResult(){
		this(null);
	}
	
	/**
	 * Create a new {@link CollisionResult} with the given material
	 *
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public CollisionResult(Material material){
		// Set the material to no material if none is given
		this.material = material == null ? Materials.NONE : material;
	}
	
	/** @return See {@link #material} */
	public Material material(){
		return this.material;
	}
	
	/** @return true if this {@link CollisionResult} represents a collision happening, false if no collision took place */
	public abstract boolean isCollided();
	
	/** @return true if the collision hit anything, i.e. a wall, ceiling, or floor, false otherwise */
	public abstract boolean hit();
	
	/**
	 * Get an identical copy of this {@link CollisionResult}, but with all quantity values scaled by the given value
	 *
	 * @param s The scaling value
	 * @return The scaled response
	 */
	public abstract C scale(double s);
	
}
