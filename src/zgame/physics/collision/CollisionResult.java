package zgame.physics.collision;

import zgame.physics.material.Material;
import zgame.physics.material.Materials;

/** An object containing values for what should happen to an object when it collides with something */
public abstract class CollisionResult<C extends CollisionResult<C>>{
	
	/** The material which was hit during this collision, or null if no collision took place */
	private final Material material;
	
	/** true if the collision was into a wall, false otherwise */
	private final boolean wall;
	/** true if the collision was into a ceiling above, false otherwise */
	private final boolean ceiling;
	/** true if the collision was into a floor below, false otherwise */
	private final boolean floor;
	
	/** A response representing no collision occurring */
	public CollisionResult(){
		this(null, false, false, false);
	}
	
	/**
	 * Create a new {@link CollisionResult} with the given material
	 *
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 */
	public CollisionResult(Material material, boolean wall, boolean ceiling, boolean floor){
		// Set the material to no material if none is given
		this.material = material == null ? Materials.NONE : material;
		this.wall = wall;
		this.ceiling = ceiling;
		this.floor = floor;
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
