package zgame.core.sound;

import static org.lwjgl.openal.AL10.alSourcef;

/** Holds a single value used in an update to an OpenAL source */
public class SoundSourceValue{
	
	/** The type of value that this holds, i.e. gain, pitch, etc */
	private final int type;
	/** The sourceId who uses this vector */
	private final int sourceId;
	
	/** The value last sent to OpenAL */
	private float value;
	
	/**
	 * @param id See {@link #sourceId}
	 * @param type See {@link #type}
	 * @param value The default value
	 */
	public SoundSourceValue(int id, int type, double value){
		this.type = type;
		this.sourceId = id;
		
		this.updateUnchecked((float)value);
	}
	
	/**
	 * Update this value, skipping the OpenAL call if the given newValue is equal to the last sent value
	 * @param newValue The new value
	 */
	public void update(double newValue){
		float newFloat = (float)newValue;
		if(newFloat == this.value) return;
		this.updateUnchecked(newFloat);
	}
	
	/** Update the value in this source to its current value, if any changes are needed */
	public void update(){
		this.update(this.getValue());
	}
	
	/**
	 * Force update the OpenAL value stored in this object for the given id
	 * @param newValue The new value for {@link #value}
	 */
	private void updateUnchecked(float newValue){
		this.value = newValue;
		alSourcef(this.sourceId, this.getType(), this.value);
	}
	
	/** @return See {@link #type} */
	public int getType(){
		return this.type;
	}
	
	/** @return See {@link #value} */
	public float getValue(){
		return this.value;
	}
}
