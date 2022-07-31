package zgame.physics.material;

/** An object representing the attributes of the physics of an object */
public interface Material{
	
	/** @return The friction constant for this object */
	public double getFriction();
	
	/**
	 * @return A constant of velocity, determining how slippery this material is, zero being not slippery at all, and higher values meaning easier to slip on, negative for
	 *         infinite slipperiness.
	 *         It determines how fast things can slide down the sides of one another.
	 *         The maximum velocity an object can have while falling against another slippery object should be the speed from both of their getSlipperinessSpeed calls multiplied
	 */
	public double getSlipperinessSpeed();
	
	/**
	 * @return A constant of acceleration, determining how slippery this material is, zero being not slippery at all, and higher values meaning easier to slip on, negative for
	 *         infinite slipperiness.
	 *         It determines how fast things slow down when sliding down one another
	 *         The acceleration used for the force that makes the object slow down have while falling against another slippery object should be the acceleration from both of their
	 *         getSlipperinessAcceleration calls multiplied, and divided by the falling object's mass
	 */
	public double getSlipperinessAcceleration();
	
	/**
	 * @return When an object collides with something by moving to the left into it, it will bounce off it with ratio of the amount of velocity in the opposite direction.
	 *         0 means no bounce, 1 means bounce with the full ratio.
	 *         Should generally never be greater than 1
	 */
	public double getWallBounce();
	
	/**
	 * @return When an object collides with something by moving down into the ground, it will bounce off it with ratio of the amount of velocity in the opposite direction.
	 *         0 means no bounce, 1 means bounce with the full ratio.
	 *         Should generally never be greater than 1
	 */
	public double getFloorBounce();
	
	/**
	 * @return When an object collides with something by moving up into a ceiling, it will bounce off it with ratio of the amount of velocity in the opposite direction.
	 *         0 means no bounce, 1 means bounce with the full ratio.
	 *         Should generally never be greater than 1
	 */
	public double getCeilingBounce();
	
}
