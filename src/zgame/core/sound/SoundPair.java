package zgame.core.sound;

/**
 * A utility class for holding a {@link SoundSource} and a {@link Sound} together
 */
public class SoundPair{
	
	/** The {@link SoundSource} of this {@link SoundPair} */
	private SoundSource source;
	
	/** The {@link Sound} of this {@link SoundPair} */
	private Sound sound;

	/**
	 * Create a {@link SoundPair} with the given parameters
	 * 
	 * @param source See {@link #source}
	 * @param sound See {@link #sound}
	 */
	public SoundPair(SoundSource source, Sound sound){
		this.source = source;
		this.sound = sound;
	}

	/** @return See {@link #source} */
	public SoundSource getSource(){
		return this.source;
	}
	
	/** @param source See {@link #source} */
	public void setSource(SoundSource source){
		this.source = source;
	}
	
	/** @return See {@link #sound} */
	public Sound getSound(){
		return this.sound;
	}
	
	/** @param sound See {@link #sound} */
	public void setSound(Sound sound){
		this.sound = sound;
	}

	
	
}
