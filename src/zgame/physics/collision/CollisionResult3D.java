package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;

/** An object containing values for what should happen to an object when it collides with something in 3D */
public class CollisionResult3D extends CollisionResult<CollisionResult3D>{
	
	/** The amount to add to the x coordinate so that it no longer collides */
	private final double x;
	/** The amount to add to the y coordinate so that it no longer collides */
	private final double y;
	/** The amount to add to the z coordinate so that it no longer collides */
	private final double z;
	
	// TODO can wall/ceiling/floor be abstracted?
	/** true if the collision was into a wall, false otherwise */
	private final boolean wall;
	/** true if the collision was into a ceiling above, false otherwise */
	private final boolean ceiling;
	/** true if the collision was into a floor below, false otherwise */
	private final boolean floor;
	
	/** A response representing no collision occurring */
	public CollisionResult3D(){
		this(0, 0, 0, null);
	}
	
	/**
	 * Create a new {@link CollisionResult3D} with the given amount of movement, where no walls were collided with
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public CollisionResult3D(double x, double y, double z, Material material){
		this(x, y, z, false, false, false, material);
	}
	
	/**
	 * Create a new {@link CollisionResult3D} with the given values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public CollisionResult3D(double x, double y, double z, boolean wall, boolean ceiling, boolean floor, Material material){
		this.x = x;
		this.y = y;
		this.z = z;
		this.wall = wall;
		this.ceiling = ceiling;
		this.floor = floor;
	}
	
	/** @return true if this {@link CollisionResult3D} represents a collision happening, false if no collision took place */
	@Override
	public boolean isCollided(){
		return this.x() != 0 || this.y() != 0 || this.z() != 0;
	}
	
	/** @return See {@link #x} */
	public double x(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	public double y(){
		return this.y;
	}
	
	/** @return See {@link #y} */
	public double z(){
		return this.z;
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
	
	/** @return true if the collision hit anything, i.e. a wall, ceiling, or floor, false otherwise */
	@Override
	public boolean hit(){
		return this.wall() || this.ceiling() || this.floor();
	}
	
	/**
	 * Get an identical copy of this {@link CollisionResult3D}, but with the x and y values scaled by the given value
	 *
	 * @param s The scaling value
	 * @return The scaled response
	 */
	public CollisionResult3D scale(double s){
		if(s < 0){
			var oppositeTop = !this.ceiling() && !this.floor();
			return new CollisionResult3D(this.x() * s, this.y() * s, this.z() * s, this.wall(), this.ceiling() == oppositeTop, this.floor() == oppositeTop, this.material());
		}
		return new CollisionResult3D(s * this.x(), s * this.y(), s * this.z(), this.wall(), this.ceiling(), this.floor(), this.material());
	}
	
	/**
	 * Get an identical copy of this {@link CollisionResult3D}, but with the left and right sides and the ceiling and floor values swapped
	 * if one is true and the other is false
	 *
	 * @return The modified response
	 */
	public CollisionResult3D invertDirections(){
		var oppositeTop = !this.ceiling() && !this.floor();
		return new CollisionResult3D(this.x(), this.y(), this.z(), this.wall(), this.ceiling() == oppositeTop, this.floor() == oppositeTop, this.material());
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[CollisionResponse: x: ", this.x(), ", y: ", this.y(), ", z: ", this.z(), ", wall: ", this.wall(), ", ceiling: ", this.ceiling(),
				", floor: ", this.floor(), ", material: ", this.material(), "]");
	}
	
}
