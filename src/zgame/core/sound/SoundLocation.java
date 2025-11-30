package zgame.core.sound;

/**
 * A class that handles either a listener or source in OpenAL, which uses location information, and contains common functionality between the two
 */
public abstract class SoundLocation{
	
	/**
	 * Create a new empty {@link SoundLocation}
	 */
	public SoundLocation(){
	}
	
	/**
	 * Update the current position values of this {@link SoundLocation} based on the given values
	 *
	 * @param x The x coordinate of the sound location
	 * @param y The y coordinate of the sound location
	 * @param z The z coordinate of the sound location
	 */
	public abstract void updatePosition(double x, double y, double z);
	
	/**
	 * Update the direction which this sound is produced
	 * @param x The x vector component of the direction
	 * @param y The y vector component of the direction
	 * @param z The z vector component of the direction
	 */
	public abstract void updateDirection(double x, double y, double z);
	
	/**
	 * Update the orientation which this sound is produced
	 * @param yx The x vector component of the orientation for the yaw vector
	 * @param yy The y vector component of the orientation for the yaw vector
	 * @param yz The z vector component of the orientation for the yaw vector
	 * @param px The x vector component of the orientation for the pitch vector
	 * @param py The y vector component of the orientation for the pitch vector
	 * @param pz The z vector component of the orientation for the pitch vector
	 */
	public abstract void updateOrientation(double yx, double yy, double yz, double px, double py, double pz);
}
