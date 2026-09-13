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
	 */
	public Collision2D(double originalX, double originalY){
		this(originalX, originalY, 0, 0, false, false, false, false);
	}
	
	/**
	 * Create a new {@link Collision2D} with the given amount of movement, where no walls were collided with
	 */
	public Collision2D(V2D original){
		this(original, new V2D(), false, false, false, false);
	}
	
	/**
	 * Create a new {@link Collision2D} with the given values
	 *
	 * @param left See {@link #left}
	 * @param right See {@link #right}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 */
	public Collision2D(double originalX, double originalY, double dx, double dy, boolean left, boolean right, boolean ceiling, boolean floor){
		this(new V2D(originalX, originalY), new V2D(dx, dy), left, right, ceiling, floor);
	}
	
	/**
	 * Create a new {@link Collision2D} with the given values
	 *
	 * @param left See {@link #left}
	 * @param right See {@link #right}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 */
	public Collision2D(V2D original, V2D change, boolean left, boolean right, boolean ceiling, boolean floor){
		super(original, change, left || right, ceiling, floor);
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
	
	/**
	 * Get an identical copy of this {@link Collision2D}, but with the x and y values scaled by the given value
	 *
	 * @param s The scaling value
	 * @return The scaled response
	 */
	public Collision2D scale(double s){
		var scaled = this.changePos().scale(s);
		if(s < 0){
			var oppositeSide = !this.left() && !this.right();
			var oppositeTop = !this.ceiling() && !this.floor();
			return new Collision2D(this.originalPos(), scaled,
					this.left() == oppositeSide, this.right() == oppositeSide,
					this.ceiling() == oppositeTop, this.floor() == oppositeTop
			);
		}
		return new Collision2D(this.originalPos(), scaled, this.left(), this.right(), this.ceiling(), this.floor());
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[Collision2D: original: ", this.originalPos(), "change: ", this.changePos(), ", left: ", this.left(), ", right: ", this.right(), ", ceiling: ", this.ceiling(), ", floor: ", this.floor(), "]");
	}
	
	// TODO probably do this in a better way to avoid casting
	@SuppressWarnings("unchecked")
	@Override
	public <C extends Collision<V2D>> C asCollision(Class<C> clazz){
		return (C)this;
	}
}
