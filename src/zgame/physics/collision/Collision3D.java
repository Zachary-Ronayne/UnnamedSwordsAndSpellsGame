package zgame.physics.collision;

import zgame.core.utils.ZStringUtils;
import zgame.physics.V3D;

/** An object containing values for what should happen to an object when it collides with something in 3D */
public non-sealed class Collision3D extends Collision<V3D>{
	
	/** The angle of the wall collided with in the range [0, PI) */
	private final double wallAngle;
	
	// TODO update constructor docs
	/**
	 */
	public Collision3D(){
		this(new V3D());
	}
	
	/**
	 * Create a new {@link Collision3D} with the given amount of movement, where no walls were collided with
	 */
	public Collision3D(V3D newPos){
		this(newPos, false, false, false);
	}
	
	/**
	 * Create a new {@link Collision3D} with the given values
	 *
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 * @param wallAngle See {@link #wallAngle}
	 */
	public Collision3D(V3D newPos, boolean wall, boolean ceiling, boolean floor, double wallAngle){
		super(newPos, wall, ceiling, floor);
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
				", floor: ", this.floor(), ", wallAngle: ", this.wallAngle(), "]");
	}
	
	// TODO probably do this in a better way to avoid casting
	@SuppressWarnings("unchecked")
	@Override
	public <C extends Collision<V3D>> C asCollision(Class<C> clazz){
		return (C)this;
	}
}
