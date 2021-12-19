package zgame.core.sound;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;

import static org.lwjgl.openal.AL11.*;

/**
 * A class that handles and keeps track of a list of playing sounds
 */
public abstract class SoundPlayer{
	
	/** A list of every sound currently played by this {@link SoundPlayer} */
	private List<SoundSource> playing;
	
	/** The queue of sounds which will begin playing on the next update */
	private LinkedList<SoundPair> queue;
	
	/** true if this {@link SoundPlayer} should not make any sound, but sounds should continue to play, false otherwise */
	private boolean muted;
	
	/** true if this {@link SoundPlayer} is paused and should not play any sounds, false otherwise */
	private boolean paused;
	
	/** Create an empty {@link SoundPlayer} with no currently playing sounds */
	public SoundPlayer(){
		this.playing = new ArrayList<SoundSource>();
		this.queue = new LinkedList<SoundPair>();
		this.unmute();
		this.unpause();
	}

	/** Immediately stop playinig and remove all currently played sounds, unmute the audio, and unpause the audio */
	public void reset(){
		this.unmute();
		this.unpause();

		for(SoundSource s : this.playing) alSourceStop(s.getId());
		this.playing.clear();
		this.queue.clear();
	}
	
	/**
	 * Queue the given {@link Sound} at the given {@link SoundSource} to play using this {@link SoundPlayer} on the next update
	 * 
	 * @param source The source which will play the sound
	 * @param sound The sound to play
	 */
	protected void playSound(SoundSource source, Sound sound){
		this.queue.push(new SoundPair(source, sound));
	}
	
	/**
	 * Immediately use this {@link SoundPlayer} to play the given {@link Sound} at the given {@link SoundSource}
	 * This method should not be called outside of {@link #updateState()} or related method calls
	 * 
	 * @param source The source which will play the sound
	 * @param sound The sound to play
	 */
	private void playSoundNow(SoundSource source, Sound sound){
		// Keep track of the source that is now player
		this.playing.add(source);
		// Use the source to keep track of the sound
		source.setCurrent(sound);
		
		// Ensure the source is appropriately mute and paused based on the current state of this sound player
		source.setMuted(this.isMuted());
		source.setPaused(this.isPaused());
		if(this.isMuted() || this.isPaused()) alSourcef(source.getId(), AL_GAIN, 0);

		// Play the actual sound, playing it from the beginning
		alSourceRewind(source.getId());
		this.runSound(source, sound);
	}
	
	/**
	 * Implement this method so that this {@link SoundPlayer} will play the given {@link Sound} at the given {@link SoundSource}.
	 * Implementation will varry depending on if the entire sound should be loaded all at once, or buffered in sections. 
	 * This method should not interact at all with volume, position of the sound in space, velocity, ect, this method 
	 * is only responsible for setting when the sound will play
	 * 
	 * @param source The source which will play the sound
	 * @param sound The sound to play
	 */
	protected abstract void runSound(SoundSource source, Sound sound);
	
	/**
	 * A method to be called when this {@link SoundPlayer} needs to be updated.
	 * For example if a music track needs to loop
	 */
	public void updateState(){
		// Play all sounds and clear the queue
		for(SoundPair sp : this.queue) this.playSoundNow(sp.getSource(), sp.getSound());
		this.queue.clear();
		
		// Update the states of the sounds
		for(SoundSource s : this.playing) s.update();
		
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
		for(SoundSource s : this.playing) s.setMuted(muted);
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
		for(SoundSource s : this.playing) s.pause();
	}
	
	/** Unpause the sounds of this {@link SoundPlayer} and begin playing them. If it was already unpaused, this method does nothing */
	public void unpause(){
		if(!this.isPaused()) return;
		this.paused = false;
		for(SoundSource s : this.playing) s.unpause();
	}

	/** If the player is paused, unpause it, otherwise, pause it */
	public void togglePaused(){
		if(this.isPaused()) this.unpause();
		else this.pause();
	}
	
	/**
	 * Get an array of every {@link SoundSource} currently playing through this {@link SoundPlayer}
	 * This array is separate from the internal storage of this SoundPlayer, i.e. moving the elements of the returned array will not affect the player,
	 * however, modifying the individual objects in the array will affect the player
	 * 
	 * @return The array
	 */
	public SoundSource[] getPlaying(){
		SoundSource[] s = new SoundSource[this.playing.size()];
		for(int i = 0; i < this.playing.size(); i++) s[i] = this.playing.get(i);
		return s;
	}
	
	/** Remove any sounds from this {@link SoundPlayer} which are finished playing */
	public void removeFinishedSounds(){
		for(int i = 0; i < this.playing.size(); i++){
			SoundSource s = this.playing.get(i);
			IntBuffer buf = BufferUtils.createIntBuffer(1);
			alGetSourcei(s.getId(), AL_SOURCE_STATE, buf);
			int state = buf.get(0);
			if(state == AL_STOPPED){
				this.playing.remove(s);
				i--;
			}
		}
	}
	
}
