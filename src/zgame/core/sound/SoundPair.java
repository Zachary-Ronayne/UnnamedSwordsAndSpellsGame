package zgame.core.sound;

/**
 * A utility class for holding a {@link SoundSource} and a {@link Sound} together
 * 
 * @param S The type of the sound held by this pair
 */
public class SoundPair<S extends Sound>{
	
	/** The {@link SoundSource} of this {@link SoundPair} */
	private SoundSource source;
	
	/** The {@link Sound} of this {@link SoundPair} */
	private S sound;
	
	/**
	 * Create a {@link SoundPair} with the given parameters
	 * 
	 * @param source See {@link #source}
	 * @param sound See {@link #sound}
	 */
	public SoundPair(SoundSource source, S sound){
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
	public S getSound(){
		return this.sound;
	}
	
	/** @param sound See {@link #sound} */
	public void setSound(S sound){
		this.sound = sound;
	}
	
}
