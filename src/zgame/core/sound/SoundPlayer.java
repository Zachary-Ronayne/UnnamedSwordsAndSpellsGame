package zgame.core.sound;

import java.util.LinkedList;

import static org.lwjgl.openal.AL11.*;

/**
 * A class that handles and keeps track of a list of playing sounds
 * 
 * @param S The {@link Sound} type which this {@link SoundPlayer} can handle
 */
public abstract class SoundPlayer<S extends Sound>{
	
	/** A collection of every sound currently played by this {@link SoundPlayer} */
	private SoundMap playing;
	
	/** The queue of sounds which will begin playing on the next update */
	private LinkedList<SoundPair<S>> queue;
	
	/** true if this {@link SoundPlayer} should not make any sound, but sounds should continue to play, false otherwise */
	private boolean muted;
	
	/** true if this {@link SoundPlayer} is paused and should not play any sounds, false otherwise */
	private boolean paused;
	
	/** Create an empty {@link SoundPlayer} with no currently playing sounds */
	public SoundPlayer(){
		this.playing = new SoundMap();
		this.queue = new LinkedList<SoundPair<S>>();
		this.unmute();
		this.unpause();
	}
	
	/** Immediately stop playing and remove all currently played sounds, unmute the audio, and unpause the audio */
	public void reset(){
		this.unmute();
		this.unpause();
		
		this.playing.clearSound();
		this.queue.clear();
	}
	
	/**
	 * Queue the given {@link Sound} at the given {@link SoundSource} to play using this {@link SoundPlayer} on the next update
	 * 
	 * @param source The source which will play the sound
	 * @param sound The sound to play
	 */
	protected void playSound(SoundSource source, S sound){
		this.queue.push(new SoundPair<S>(source, sound));
	}
	
	/**
	 * Immediately use this {@link SoundPlayer} to play the given {@link Sound} at the given {@link SoundSource}
	 * This method should not be called outside of {@link #updateState()} or related method calls
	 * 
	 * @param source The source which will play the sound
	 * @param sound The sound to play
	 */
	private void playSoundNow(SoundSource source, S sound){
		// Keep track of the source that is now playing
		this.playing.put(source);
		// Use the source to keep track of the sound
		source.setCurrent(sound);
		
		// Ensure the source is appropriately muted, paused, and the correct volume, based on the current state of this sound player
		source.setMuted(this.isMuted());
		source.setPaused(this.isPaused());
		source.updateVolumeLevel();
		if(this.isPaused()) alSourcef(source.getId(), AL_GAIN, 0.0f);
		
		// Play the actual sound, playing it from the beginning
		alSourceRewind(source.getId());
		this.runSound(source, sound);
	}
	
	/**
	 * Implement this method so that this {@link SoundPlayer} will play the given {@link Sound} at the given {@link SoundSource}.
	 * Implementation will vary depending on if the entire sound should be loaded all at once, or buffered in sections.
	 * This method should not interact at all with volume, position of the sound in space, velocity, etc, this method
	 * is only responsible for setting when the sound will play
	 * 
	 * @param source The source which will play the sound
	 * @param sound The sound to play
	 */
	protected abstract void runSound(SoundSource source, S sound);
	
	/**
	 * A method to be called when this {@link SoundPlayer} needs to be updated.
	 * For example if a music track needs to loop
	 */
	public void updateState(){
		// Play all sounds and clear the queue
		for(SoundPair<S> sp : this.queue) this.playSoundNow(sp.getSource(), sp.getSound());
		this.queue.clear();
		
		// Update the states of the sounds
		this.playing.update();
		
		// Run the class defined update method
		this.runUpdate();
	}
	
	/**
	 * A method called each time {@link #updateState()} is called
	 */
	protected abstract void runUpdate();
	
	/** @return See {@link #muted} */
	public boolean isMuted(){
		return this.muted;
	}
	
	/** @param muted See {@link #muted} */
	public void setMuted(boolean muted){
		this.muted = muted;
		this.playing.setMuted(muted);
	}
	
	/** Shorthand for {@link #setMuted(boolean)} true */
	public void mute(){
		this.setMuted(true);
	}
	
	/** Shorthand for {@link #setMuted(boolean)} false */
	public void unmute(){
		this.setMuted(false);
	}
	
	/** If the player is currently muted, unmute it, otherwise, unmute it */
	public void toggleMuted(){
		this.setMuted(!this.isMuted());
	}
	
	/** @return See {@link #paused} */
	public boolean isPaused(){
		return this.paused;
	}
	
	/**
	 * Cause all sounds in this {@link SoundPlayer} to stop playing. If it was already paused, this method does nothing
	 */
	public void pause(){
		if(this.isPaused()) return;
		this.paused = true;
		this.playing.setPaused(true);
	}
	
	/** Unpause the sounds of this {@link SoundPlayer} and begin playing them. If it was already unpaused, this method does nothing */
	public void unpause(){
		if(!this.isPaused()) return;
		this.paused = false;
		this.playing.setPaused(false);
	}
	
	/** @param pause See {@link #paused} */
	public void setPaused(boolean pause){
		this.paused = pause;
		this.playing.setPaused(pause);
	}
	
	/** If the player is paused, unpause it, otherwise, pause it */
	public void togglePaused(){
		if(this.isPaused()) this.unpause();
		else this.pause();
	}
	
	/** @return The current volume set for every sound in this {@link SoundPlayer} */
	public double getVolume(){
		return this.playing.getVolume();
	}
	
	/** @param See The volume to set every sound in this {@link SoundPlayer} to play at */
	public void setVolume(double volume){
		this.playing.setVolume(volume);
	}
	
	/** Like {@link #setVolume(double)}, but add the volume instead of setting it */
	public void addVolume(double volume){
		this.playing.addVolume(volume);
	}
	
	/**
	 * Get an array of every {@link SoundSource} currently playing through this {@link SoundPlayer}
	 * This array is separate from the internal storage of this {@link SoundPlayer}, i.e. moving the elements of the returned array will not affect the player,
	 * however, modifying the individual objects in the array will affect the player
	 * 
	 * @return The array
	 */
	public SoundSource[] getPlaying(){
		return this.playing.toArray();
	}
	
	/**
	 * Remove any sounds from this {@link SoundPlayer} which are finished playing
	 * 
	 * @return true if at least one sound was removed, false otherwise
	 */
	public boolean removeFinishedSounds(){
		return this.playing.removeFinishedSounds();
	}
	
}
