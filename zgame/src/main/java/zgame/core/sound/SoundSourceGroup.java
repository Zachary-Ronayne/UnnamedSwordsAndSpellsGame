package zgame.core.sound;

import zgame.core.graphics.Destroyable;

import java.util.HashMap;
import java.util.function.Consumer;

/** Represents multiple sound sources to be played as a part of the same thing. Manages all sounds collectively */
public class SoundSourceGroup implements Destroyable{

	/** Mapping of an id to differentiate each source, to the source */
	private final HashMap<String, ManagedSoundSource> sources;
	
	/** Create a new empty group */
	public SoundSourceGroup(){
		this.sources = new HashMap<>();
	}
	
	@Override
	public void destroy(){
		// Destroy all sounds and remove them from this group
		for(var s : this.sources.values()) s.destroy();
		this.sources.clear();
	}
	
	/**
	 * Add the given source to this group
	 * @param name The identifier for the source
	 * @param source The source to add
	 */
	public void add(String name, ManagedSoundSource source){
		this.sources.put(name, source);
	}
	
	/**
	 * @param name The name of the source to get
 	 * @return The source with the name, or null if none exists
	 */
	public ManagedSoundSource get(String name){
		return this.sources.get(name);
	}
	
	/**
	 * Play the sound for the given source
	 * @param name The name to play
	 */
	public void play(String name){
		var source = this.sources.get(name);
		if(source == null) return;
		
		source.playSound();
	}
	
	/**
	 * Apply the given function to the given source, then play the sound
	 * @param name The name to update and play
	 */
	public void updateAndPlay(String name, Consumer<ManagedSoundSource> updater){
		var source = this.sources.get(name);
		if(source == null) return;
		
		updater.accept(source);
		source.playSound();
	}

}
