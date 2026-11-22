package zgame.core.sound;

import static org.lwjgl.openal.AL10.alSource3f;

/** Same as {@link SoundSourceValue}, but holds 3 values for a vector, i.e. position, velocity, etc */
public class SoundSourceVector{
	
	/** The type of value that this holds, i.e. gain, pitch, etc */
	private final int type;
	/** The sourceId who uses this vector */
	private final int sourceId;
	
	/** The x vector value last sent to OpenAL */
	private float x;
	/** The y vector value last sent to OpenAL */
	private float y;
	/** The z vector value last sent to OpenAL */
	private float z;
	
	/**
	 * @param id See {@link #sourceId}
	 * @param type See {@link #type}
	 * @param x The default x value of the vector
	 * @param y The default y value of the vector
	 * @param z The default x value of the vector
	 */
	public SoundSourceVector(int id, int type, double x, double y, double z){
		this.type = type;
		this.sourceId = id;
		this.updateUnchecked((float)x, (float)y, (float)z);
	}
	
	/**
	 * Update this value, skipping the OpenAL call if the given newValue is equal to the last sent value
	 * @param x The new x value of the vector
	 * @param y The new y value of the vector
	 * @param z The new x value of the vector
	 */
	public void update(double x, double y, double z){
		float newX = (float)x;
		float newY = (float)y;
		float newZ = (float)z;
		
		if(newX == this.x && newY == this.y && newZ == this.z) return;

		this.updateUnchecked(newX, newY, newZ);
	}
	
	/**
	 * Force update the OpenAL value stored in this object for the given id
	 * @param newX The new value for {@link #x}
	 * @param newY The new value for {@link #y}
	 * @param newZ The new value for {@link #z}
	 */
	private void updateUnchecked(float newX, float newY, float newZ){
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		alSource3f(this.sourceId, this.getType(), this.x, this.y, this.z);
	}
	
	/** @return See {@link #type} */
	public int getType(){
		return this.type;
	}
	
	/** @return See {@link #x} */
	public float getX(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	public float getY(){
		return this.y;
	}
	
	/** @return See {@link #z} */
	public float getZ(){
		return this.z;
	}
}
