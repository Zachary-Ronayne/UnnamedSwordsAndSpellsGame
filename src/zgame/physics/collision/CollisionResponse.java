package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;

/** An object containing values for what should happen to an object when it collides with something */
public class CollisionResponse{
	// TODO abstract this to 2D and 3D
	// TODO rename this to CollisionResult
	
	/** The amount to add to the x coordinate so that it no longer collides */
	private final double x;
	/** The amount to add to the y coordinate so that it no longer collides */
	private final double y;
	
	/** true if the collision was into a wall to the left, false otherwise */
	private final boolean left;
	/** true if the collision was into a wall to the right, false otherwise */
	private final boolean right;
	/** true if the collision was into a ceiling above, false otherwise */
	private final boolean ceiling;
	/** true if the collision was into a floor below, false otherwise */
	private final boolean floor;
	/** The material which was hit during this collision, or null if no collision took place */
	private final Material material;
	
	/** A response representing no collision occurring */
	public CollisionResponse(){
		this(0, 0, null);
	}
	
	/**
	 * Create a new {@link CollisionResponse} with the given amount of movement, where no walls were collided with
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public CollisionResponse(double x, double y, Material material){
		this(x, y, false, false, false, false, material);
	}
	
	/**
	 * Create a new {@link CollisionResponse} with the given values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param left See {@link #left}
	 * @param right See {@link #right}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public CollisionResponse(double x, double y, boolean left, boolean right, boolean ceiling, boolean floor, Material material){
		this.x = x;
		this.y = y;
		this.left = left;
		this.right = right;
		this.ceiling = ceiling;
		this.floor = floor;
		// Set the material to no material if none is given
		this.material = (material == null) ? Materials.NONE : material;
	}
	
	/** @return true if this {@link CollisionResponse} represents a collision happening, false if no collision took place */
	public boolean isCollided(){
		return this.x() != 0 || this.y() != 0;
	}
	
	/** @return See {@link #x} */
	public double x(){
		return x;
	}
	
	/** @return See {@link #y} */
	public double y(){
		return y;
	}
	
	/** @return See {@link #left} */
	public boolean left(){
		return left;
	}
	
	/** @return See {@link #right} */
	public boolean right(){
		return right;
	}
	
	/** @return See {@link #ceiling} */
	public boolean ceiling(){
		return ceiling;
	}
	
	/** @return See {@link #floor} */
	public boolean floor(){
		return floor;
	}
	
	/** @return true if the collision hit a wall, either on the left or right, false otherwise */
	public boolean wall(){
		return this.left() || this.right();
	}
	
	/** @return true if the collision hit anything, i.e. a wall, ceiling, or floor, false otherwise */
	public boolean hit(){
		return this.wall() || this.ceiling() || this.floor();
	}
	
	/** @return See {@link #material} */
	public Material material(){
		return this.material;
	}
	
	/**
	 * Get an identical copy of this {@link CollisionResponse}, but with the x and y values scaled by the given value
	 *
	 * @param s The scaling value
	 * @return The scaled response
	 */
	public CollisionResponse scale(double s){
		if(s < 0){
			var oppositeSide = !this.left() && !this.right();
			var oppositeTop = !this.ceiling() && !this.floor();
			return new CollisionResponse(this.x() * s, this.y() * s,
					this.left() == oppositeSide, this.right() == oppositeSide,
					this.ceiling() == oppositeTop, this.floor() == oppositeTop,
					this.material()
			);
		}
		return new CollisionResponse(s * this.x(), s * this.y(), this.left(), this.right(), this.ceiling(), this.floor(), this.material());
	}
	
	/**
	 * Get an identical copy of this {@link CollisionResponse}, but with the left and right sides and the ceiling and floor values swapped
	 * if one is true and the other is false
	 *
	 * @return The modified response
	 */
	public CollisionResponse invertDirections(){
		var oppositeSide = !this.left() && !this.right();
		var oppositeTop = !this.ceiling() && !this.floor();
		return new CollisionResponse(this.x(), this.y(),
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
