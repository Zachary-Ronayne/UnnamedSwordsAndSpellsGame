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
}
