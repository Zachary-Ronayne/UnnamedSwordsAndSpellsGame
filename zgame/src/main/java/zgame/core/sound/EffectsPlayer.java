package zgame.core.sound;

import static org.lwjgl.openal.AL11.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link SoundPlayer} designed around playing short sound effects
 */
public class EffectsPlayer extends SoundPlayer<EffectSound>{
	
	/** A {@link Map} containing every {@link SoundMap} of each type of playing sounds */
	private final Map<String, SoundMap> effects;
	
	/** Create an empty {@link EffectsPlayer} with no currently playing sounds */
	public EffectsPlayer(){
		super();
		this.effects = new HashMap<>();
	}
	
	@Override
	protected void runSound(SoundSource source, EffectSound sound){
		// Add the sound to the map of separate maps
		this.addEffect(source, sound.getType());
		
		// Play the sound
		int sourceID = source.getId();
		alSourcei(sourceID, AL_BUFFER, sound.getId());
		alSourcePlay(sourceID);
	}
	
	/**
	 * Add the specified source to the map of different types of sound maps
	 *
	 * @param source The source to add
	 * @param type The type of the effect which the given source will play
	 */
	private void addEffect(SoundSource source, String type){
		// If the map doesn't exist yet, make a new map and add it to the list of effects
		this.addNewType(type);
		
		// Add the source to the new map
		this.effects.get(type).put(source);
	}
	
	/**
	 * If the given type of sounds does not yet exist in this {@link EffectsPlayer}, then create a new map for that type, otherwise do nothing
	 *
	 * @param type The type to add
	 */
	public void addNewType(String type){
		SoundMap map = this.effects.get(type);
		if(map == null) this.effects.put(type, new SoundMap());
	}
	
	/**
	 * Set the volume of a specific type of sounds. If the type does not exist, an empty map of that type is added to the player. Any calls to {@link #setVolume(double)} will
	 * set the volume of every sound in this {@link EffectsPlayer}, regardless of type. Use {@link #setVolume(double)} to set the volume of every sound
	 *
	 * @param type The type of sounds
	 * @param volume The new volume
	 */
	public void setTypeVolume(String type, double volume){
		this.addNewType(type);
		this.effects.get(type).setVolume(volume);
	}
	
	/**
	 * Like {@link #setTypeVolume(String, double)}, but add the specified volume instead of setting it
	 */
	public void addTypeVolume(String type, double volume){
		this.addNewType(type);
		this.effects.get(type).addVolume(volume);
	}
	
	@Override
	public void runUpdate(){
		this.removeFinishedSounds();
	}
	
	@Override
	public boolean removeFinishedSounds(){
		boolean found = false;
		for(SoundMap map : this.effects.values()) if(map.removeFinishedSounds()) found = true;
		return super.removeFinishedSounds() || found;
	}
	
}
