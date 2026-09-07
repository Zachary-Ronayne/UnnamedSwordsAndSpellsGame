package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;
import zgame.physics.V2D;

/** An object containing values for what should happen to an object when it collides with something in 2D */
public non-sealed class Collision2D extends Collision<V2D>{
	
	/** true if the collision was into a wall to the left, false otherwise */
	private final boolean left;
	/** true if the collision was into a wall to the right, false otherwise */
	private final boolean right;
	
	// TODO update constructor docs
	/**
	 * Create a new {@link Collision2D} with the given amount of movement, where no walls were collided with
	 *
	 */
	public Collision2D(){
		this(new V2D(), false, false, false, false);
	}
	
	/**
	 * Create a new {@link Collision2D} with the given values
	 *
	 * @param left See {@link #left}
	 * @param right See {@link #right}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 */
	public Collision2D(V2D newPos, boolean left, boolean right, boolean ceiling, boolean floor){
		super(newPos, left || right, ceiling, floor);
		this.left = left;
		this.right = right;
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
	
	// TODO remove this method
	/**
	 * Get an identical copy of this {@link Collision2D}, but with the x and y values scaled by the given value
	 *
	 * @param s The scaling value
	 * @return The scaled response
	 */
	public Collision2D scale(double s){
		if(s < 0){
			var oppositeSide = !this.left() && !this.right();
			var oppositeTop = !this.ceiling() && !this.floor();
			return new Collision2D(this.x() * s, this.y() * s,
					this.left() == oppositeSide, this.right() == oppositeSide,
					this.ceiling() == oppositeTop, this.floor() == oppositeTop,
					this.material()
			);
		}
		return new Collision2D(s * this.x(), s * this.y(), this.left(), this.right(), this.ceiling(), this.floor());
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[CollisionResponse: newPos: ", this.newPos(), ", left: ", this.left(), ", right: ", this.right(), ", ceiling: ", this.ceiling(),
				", floor: ", this.floor(), "]");
	}
	
	// TODO probably do this in a better way to avoid casting
	@SuppressWarnings("unchecked")
	@Override
	public <C extends Collision<V2D>> C asCollision(Class<C> clazz){
		return (C)this;
	}
}
