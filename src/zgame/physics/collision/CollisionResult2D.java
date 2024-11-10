package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;

/** An object containing values for what should happen to an object when it collides with something in 2D */
public class CollisionResult2D extends CollisionResult<CollisionResult2D>{
	
	/** The amount to add to the x coordinate so that it no longer collides */
	private final double x;
	/** The amount to add to the y coordinate so that it no longer collides */
	private final double y;
	
	/** true if the collision was into a wall to the left, false otherwise */
	private final boolean left;
	/** true if the collision was into a wall to the right, false otherwise */
	private final boolean right;
	
	/** A response representing no collision occurring */
	public CollisionResult2D(){
		this(0, 0, Materials.NONE);
	}
	
	/**
	 * Create a new {@link CollisionResult2D} with the given amount of movement, where no walls were collided with
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public CollisionResult2D(double x, double y, Material material){
		this(x, y, false, false, false, false, material);
	}
	
	/**
	 * Create a new {@link CollisionResult2D} with the given values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param left See {@link #left}
	 * @param right See {@link #right}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public CollisionResult2D(double x, double y, boolean left, boolean right, boolean ceiling, boolean floor, Material material){
		super(material, left || right, ceiling, floor);
		this.x = x;
		this.y = y;
		this.left = left;
		this.right = right;
	}
	
	/** @return true if this {@link CollisionResult2D} represents a collision happening, false if no collision took place */
	@Override
	public boolean isCollided(){
		return this.x() != 0 || this.y() != 0;
	}
	
	/** @return See {@link #x} */
	public double x(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	public double y(){
		return this.y;
	}
	
	/** @return See {@link #left} */
	public boolean left(){
		return this.left;
	}
	
	/** @return See {@link #right} */
	public boolean right(){
		return this.right;
	}
	
	/** @return true if the collision hit a wall, either on the left or right, false otherwise */
	public boolean wall(){
		return this.left() || this.right();
	}
	
	/** @return true if the collision hit anything, i.e. a wall, ceiling, or floor, false otherwise */
	@Override
	public boolean hit(){
		return this.wall() || this.ceiling() || this.floor();
	}
	
	/**
	 * Get an identical copy of this {@link CollisionResult2D}, but with the x and y values scaled by the given value
	 *
	 * @param s The scaling value
	 * @return The scaled response
	 */
	public CollisionResult2D scale(double s){
		if(s < 0){
			var oppositeSide = !this.left() && !this.right();
			var oppositeTop = !this.ceiling() && !this.floor();
			return new CollisionResult2D(this.x() * s, this.y() * s,
					this.left() == oppositeSide, this.right() == oppositeSide,
					this.ceiling() == oppositeTop, this.floor() == oppositeTop,
					this.material()
			);
		}
		return new CollisionResult2D(s * this.x(), s * this.y(), this.left(), this.right(), this.ceiling(), this.floor(), this.material());
	}
	
	/**
	 * Get an identical copy of this {@link CollisionResult2D}, but with the left and right sides and the ceiling and floor values swapped
	 * if one is true and the other is false
	 *
	 * @return The modified response
	 */
	public CollisionResult2D invertDirections(){
		var oppositeSide = !this.left() && !this.right();
		var oppositeTop = !this.ceiling() && !this.floor();
		return new CollisionResult2D(this.x(), this.y(),
				this.left() == oppositeSide, this.right() == oppositeSide,
				this.ceiling() == oppositeTop, this.floor() == oppositeTop,
				this.material()
		);
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[CollisionResponse: x: ", this.x(), ", y: ", this.y(), ", left: ", this.left(), ", right: ", this.right(), ", ceiling: ", this.ceiling(),
				", floor: ", this.floor(), ", material: ", this.material(), "]");
	}
	
}
