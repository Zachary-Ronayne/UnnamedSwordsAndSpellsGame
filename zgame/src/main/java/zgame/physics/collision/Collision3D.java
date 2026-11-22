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
	public Collision3D(double originalX, double originalY, double originalZ){
		this(originalX, originalY, originalZ, 0, 0, 0, false, false, false, 0);
	}
	
	public Collision3D(V3D original){
		this(original, new V3D(), false, false, false, 0);
	}
	
	/**
	 * Create a new {@link Collision3D} with the given values
	 *
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 * @param wallAngle See {@link #wallAngle}
	 */
	public Collision3D(double originalX, double originalY, double originalZ, double dx, double dy, double dz, boolean wall, boolean ceiling, boolean floor, double wallAngle){
		this(new V3D(originalX, originalY, originalZ), new V3D(dx, dy, dz), wall, ceiling, floor, wallAngle);
	}
	
	/**
	 * Create a new {@link Collision3D} with the given values
	 *
	 * @param wall See {@link #wall}
	 * @param ceiling See {@link #ceiling}
	 * @param floor See {@link #floor}
	 * @param wallAngle See {@link #wallAngle}
	 */
	public Collision3D(V3D original, V3D change, boolean wall, boolean ceiling, boolean floor, double wallAngle){
		super(original, change, wall, ceiling, floor);
		this.wallAngle = wallAngle;
	}
	
	/** @return See {@link #wallAngle} */
	public double wallAngle(){
		return this.wallAngle;
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[Collision3D: original: ", this.originalPos(), "change: ", this.changePos(), ", wall: ", this.wall(), ", ceiling: ", this.ceiling(),
				", floor: ", this.floor(), ", wallAngle: ", this.wallAngle(), "]");
	}
	
	// TODO probably do this in a better way to avoid casting
	@SuppressWarnings("unchecked")
	@Override
	public <C extends Collision<V3D>> C asCollision(Class<C> clazz){
		return (C)this;
	}
}
