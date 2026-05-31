package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;
import zgame.physics.V3D;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.type.bounds.HitBox;

import java.util.function.Function;

/** An object containing values for what should happen to an object when it collides with something in 3D */
public non-sealed class Collision3D extends Collision<V3D>{
	
	/** The angle of the wall collided with in the range [0, PI) */
	private final double wallAngle;
	
	// TODO update constructor docs
	/**
	 * A response representing no collision occurring
	 * @param originalPos The existing position of the colliding hitbox
	 */
	public Collision3D(HitBox<V3D> hitBox, Function<HitBox<V3D>, V3D> computeNewPosition){
		this(hitBox, computeNewPosition, null);
	}
	
	/**
	 * Create a new {@link Collision3D} with the given amount of movement, where no walls were collided with
	 *
	 * @param originalPos The position of the hitbox before the collision
	 * @param newPos The new position the hitbox should have after the collision
	 * @param material See {@link #material}. Can use null to set to {@link Materials#NONE}
	 */
	public Collision3D(HitBox<V3D> hitBox, Function<HitBox<V3D>, V3D> computeNewPosition, Material material){
		this(hitBox, computeNewPosition, false, false, false, material, 0);
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
	public Collision3D(HitBox<V3D> hitBox, Function<HitBox<V3D>, V3D> computeNewPosition, boolean wall, boolean ceiling, boolean floor, Material material, double wallAngle){
		super(hitBox, computeNewPosition, material, wall, ceiling, floor);
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
		return ZStringUtils.concat("[CollisionResponse: change: ", this.initialChange(), ", wall: ", this.wall(), ", ceiling: ", this.ceiling(),
				", floor: ", this.floor(), ", material: ", this.material(), ", wallAngle: ", this.wallAngle(), "]");
	}
	
	// TODO probably do this in a better way to avoid casting
	@SuppressWarnings("unchecked")
	@Override
	public <C extends Collision<V3D>> C asCollision(Class<C> clazz){
		return (C)this;
	}
}
