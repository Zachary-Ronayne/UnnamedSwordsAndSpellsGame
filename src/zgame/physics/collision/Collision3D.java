package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;
import zgame.physics.V3D;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;

/** An object containing values for what should happen to an object when it collides with something in 3D */
public class Collision3D extends Collision<V3D>{
	
	/** The angle of the wall collided with in the range [0, PI) */
	private final double wallAngle;
	
	/**
	 * A response representing no collision occurring
	 * @param originalPos The existing position of the colliding hitbox
	 */
	public Collision3D(V3D originalPos){
		this(originalPos, originalPos, null);
	}
	
	/**
	 * Create a new {@link Collision3D} with the given amount of movement, where no walls were collided with
	 *
	 * @param originalPos The position of the hitbox before the collision
	 * @param newPos The new position the hitbox should have after the collision
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public Collision3D(V3D originalPos, V3D newPos, Material material){
		this(originalPos, newPos, false, false, false, material, 0);
	}
	
	/**
	 * Create a new {@link Collision3D} with the given values
	 *
	 * @param originalPos The position of the hitbox before the collision
	 * @param newPos The new position the hitbox should have after the collision
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 * @param wallAngle See {@link #wallAngle}
	 */
	public Collision3D(V3D originalPos, V3D newPos, boolean wall, boolean ceiling, boolean floor, Material material, double wallAngle){
		super(originalPos, newPos, material, wall, ceiling, floor);
		this.wallAngle = wallAngle;
	}
	
	/** @return See {@link #wallAngle} */
	public double wallAngle(){
		return this.wallAngle;
	}
	
	/** @return true if the collision hit anything, i.e. a wall, ceiling, or floor, false otherwise */
	@Override
	public boolean hit(){
		return this.wall() || this.ceiling() || this.floor();
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[CollisionResponse: newPos: ", this.newPos(), ", change: ", this.change(), ", wall: ", this.wall(), ", ceiling: ", this.ceiling(),
				", floor: ", this.floor(), ", material: ", this.material(), ", wallAngle: ", this.wallAngle(), "]");
	}
	
}
