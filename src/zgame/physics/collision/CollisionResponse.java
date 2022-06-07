package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;

/** An object containing values for what should happen to an object when it collides with something */
public class CollisionResponse{
	
	/** The amount to add to the x coordinate so that it no longer collides */
	private double x;
	/** The amount to add to the y coordinate so that it no longer collides */
	private double y;
	
	/** true if the collision was into a wall to the left, false otherwise */
	private boolean left;
	/** true if the collision was into a wall to the right, false otherwise */
	private boolean right;
	/** true if the collision was into a ceiling above, false otherwise */
	private boolean ceiling;
	/** true if the collision was into a floor below, false otherwise */
	private boolean floor;
	
	/** A response representing no collision occurring */
	public CollisionResponse(){
		this(0, 0);
	}
	
	/**
	 * Create a new {@link CollisionResponse} with the given amount of movement, where no walls were collided with
	 * 
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 */
	public CollisionResponse(double x, double y){
		this(x, y, false, false, false, false);
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
	 */
	public CollisionResponse(double x, double y, boolean left, boolean right, boolean ceiling, boolean floor){
		this.x = x;
		this.y = y;
		this.left = left;
		this.right = right;
		this.ceiling = ceiling;
		this.floor = floor;
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
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[CollisionResponse: x: ", this.x(), ", y: ", this.y(), ", left: ", this.left(), ", right: ", this.right(), ", ceiling: ", this.ceiling(),
				", floor: ", this.floor, "]");
	}
	
}
