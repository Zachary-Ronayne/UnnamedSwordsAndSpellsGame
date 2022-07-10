package zgame.physics.material;

/** An object representing the attributes of the physics of an object */
public interface Material{
	
	/** @return The acceleration of friction on this object */
	public double getFriction();
	
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
	
	/** @return See {@link #ceilingBounce} */
	/**
	 * @return When an object collides with something by moving up into a ceiling, it will bounce off it with ratio of the amount of velocity in the opposite direction.
	 *         0 means no bounce, 1 means bounce with the full ratio.
	 *         Should generally never be greater than 1
	 */
	public double getCeilingBounce();
	
}
