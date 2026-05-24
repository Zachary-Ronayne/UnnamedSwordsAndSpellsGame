package zgame.physics.collision;

import zgame.physics.ZVector;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;

/**
 * An object containing values for what should happen to an object when it collides with something
 * @param <V> The type of vector storing information about the collision
 */
public abstract class Collision<V extends ZVector<V>>{
	
	/** The material which was hit during this collision, or null if no collision took place */
	private final Material material;
	
	/** true if the collision was into a wall, false otherwise */
	private final boolean wall;
	/** true if the collision was into a ceiling above, false otherwise */
	private final boolean ceiling;
	/** true if the collision was into a floor below, false otherwise */
	private final boolean floor;
	
	/** The change in position that this collision will result in */
	private final V change;
	
	/** The new position that this collision will move the colling hitbox to */
	private final V newPos;
	
	// TODO probably add a hitbox object stored here for later reference
	
	/**
	 * Create a new {@link Collision} with the given material
	 *
	 * @param originalPos The position of the hitbox before the collision
	 * @param newPos The new position the hitbox should have after the collision
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 */
	public Collision(V originalPos, V newPos, Material material, boolean wall, boolean ceiling, boolean floor){
		this.newPos = newPos;
		this.change = originalPos.add(newPos.inverse());
		
		// Set the material to no material if none is given
		this.material = material == null ? Materials.NONE : material;
		this.wall = wall;
		this.ceiling = ceiling;
		this.floor = floor;
	}
	
	/** @return See {@link #newPos} */
	public V newPos(){
		return this.newPos;
	}
	
	/** @return See {@link #change} */
	public V change(){
		return this.change;
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
		return this.change().getMagnitude() != 0;
	}
	
	/** @return true if the collision hit anything, i.e. a wall, ceiling, or floor, false otherwise */
	public abstract boolean hit();
	
}
