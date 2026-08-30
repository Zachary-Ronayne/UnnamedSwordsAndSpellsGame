package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;
import zgame.physics.V2D;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.type.Position;
import zgame.things.type.bounds.HitBox;

import java.util.function.Function;

/** An object containing values for what should happen to an object when it collides with something in 2D */
public non-sealed class Collision2D extends Collision<V2D>{
	
	/** true if the collision was into a wall to the left, false otherwise */
	private final boolean left;
	/** true if the collision was into a wall to the right, false otherwise */
	private final boolean right;
	
	// TODO update constructor docs
	/** A response representing no collision occurring */
	public Collision2D(HitBox<V2D> hitBox){
		this(hitBox, Position::getPosition, Materials.NONE);
	}
	
	/**
	 * Create a new {@link Collision2D} with the given amount of movement, where no walls were collided with
	 *
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public Collision2D(HitBox<V2D> hitBox, Function<HitBox<V2D>, V2D> computeNewPosition, Material material){
		this(hitBox, computeNewPosition, false, false, false, false, material);
	}
	
	/**
	 * Create a new {@link Collision2D} with the given values
	 *
	 * @param left See {@link #left}
	 * @param right See {@link #right}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public Collision2D(HitBox<V2D> hitBox, Function<HitBox<V2D>, V2D> computeNewPosition, boolean left, boolean right, boolean ceiling, boolean floor, Material material){
		super(hitBox, computeNewPosition, material, left || right, ceiling, floor);
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
		return new Collision2D(s * this.x(), s * this.y(), this.left(), this.right(), this.ceiling(), this.floor(), this.material());
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[CollisionResponse: newPos: ", this.newPos(), ", left: ", this.left(), ", right: ", this.right(), ", ceiling: ", this.ceiling(),
				", floor: ", this.floor(), ", material: ", this.material(), "]");
	}
	
	// TODO probably do this in a better way to avoid casting
	@SuppressWarnings("unchecked")
	@Override
	public <C extends Collision<V2D>> C asCollision(Class<C> clazz){
		return (C)this;
	}
}
