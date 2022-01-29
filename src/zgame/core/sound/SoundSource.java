package zgame.core.sound;

import static org.lwjgl.openal.AL11.*;

import zgame.core.utils.ZConfig;

/**
 * A class that tracks a single source in OpenAL
 */
public class SoundSource extends SoundLocation{
	
	/** The id used by OpenAL to track this source */
	private int id;
	
	/**
	 * The current level of loudness of this {@link SoundSource}. 0 = muted, 1 = maximum volume without peaking. Value can be higher than 1, but could result in unintentionally
	 * distorted audio
	 */
	private double volume;
	
	/**
	 * The volume of this sound before being modified by other sounds.
	 * So, if this {@link SoundSource} is part of a {@link SoundPlayer}, then the end result volume will be this value multiplied by the volume of the player.
	 * This value cannot go below zero
	 */
	private double baseVolume;
	
	/** true if this {@link SoundSource} should make no sound, regardless of {@link #volume}, false otherwise */
	private boolean muted;
	
	/** true if this {@link SoundSource} is should pause on the next update and should stop playing, false otherwise */
	private boolean paused;
	
	/** true if this {@link SoundSource} is currently paused and not making any sound, false otherwise */
	private boolean currentPaused;
	
	/** The sample position which the sound was at when it was initially paused, or -1 if the sound does not need to be stopped */
	private float pausedSample;
	
	/** The sound which this {@link SoundSource} is currently playing, can be null if no sound is playing */
	private Sound current;
	
	/**
	 * Create and initialize a new {@link SoundSource} at (0, 0)
	 */
	public SoundSource(){
		this(0, 0);
	}
	
	/**
	 * Create and initialize a new {@link SoundSource}
	 * 
	 * @param x The current x position of the sound
	 * @param y The current y position of the sound
	 */
	public SoundSource(double x, double y){
		this(x, y, 1);
	}
	
	/**
	 * Create and initialize a new {@link SoundSource}
	 * 
	 * @param x The current x position of the sound
	 * @param y The current y position of the sound
	 * @param baseVolume See {@link #baseVolume}
	 */
	public SoundSource(double x, double y, double baseVolume){
		super();
		this.id = alGenSources();
		this.muted = false;
		this.paused = false;
		this.currentPaused = false;
		this.setBaseVolume(baseVolume);
		this.setVolume(1);
		this.pausedSample = -1;
		alSourcef(this.getId(), AL_PITCH, 1);
		this.updatePosition(x, y);
		this.current = null;
	}
	
	@Override
	public void updatePosition(double x, double y){
		alSource3f(this.getId(), AL_POSITION, (float)x, (float)y, 0);
	}
	
	/** Should call this method when the state of a sound needs to be updated, i.e. each game loop */
	public void update(){
		// If the sound is currently paused and should unpause on this update, then unpause it
		if(this.currentPaused){
			if(!this.isPaused()){
				alSourcef(this.getId(), AL_SAMPLE_OFFSET, this.pausedSample);
				alSourcePlay(this.getId());
				if(!this.isMuted()) alSourcef(id, AL_GAIN, (float)this.getVolume());
				this.currentPaused = false;
			}
		}
		// If the sound is currently unpaused, and enough time has passed since it was set to pause on this update, then pause it
		else{
			float change = this.getSamplePos() - this.pausedSample;
			if(this.pausedSample >= 0 && change >= ZConfig.soundPauseDelay()){
				alSourcePause(this.getId());
				this.pausedSample = -1;
				this.currentPaused = true;
			}
		}
	}
	
	/** Free any resources used by this {@link SoundSource} */
	public void end(){
		alDeleteSources(this.getId());
	}
	
	/** @return See {@link #volume} */
	public double getVolume(){
		return this.volume;
	}
	
	/** @param volume See {@link #volume} */
	public void setVolume(double volume){
		this.volume = volume;
		this.updateVolumeLevel();
	}
	
	/** @param volume The amount to add to {@link #volume} */
	public void addVolume(double volume){
		this.setVolume(this.getVolume() + volume);
	}
	
	/** Based on the current state of the source, i.e. base volume, current volume, muted, set the correct volume level in OpenAL */
	public void updateVolumeLevel(){
		alSourcef(id, AL_GAIN, (float)this.getTotalVolume());
	}
	
	/** @return See {@link #baseVolume} */
	public double getBaseVolume(){
		return this.baseVolume;
	}
	
	/** @param baseVolume See {@link #baseVolume} */
	public void setBaseVolume(double baseVolume){
		this.baseVolume = Math.max(0, baseVolume);
	}
	
	/** @param volume The amount to add to {@link #baseVolume} */
	public void addBaseVolume(double volume){
		this.setBaseVolume(this.getBaseVolume() + volume);
		this.updateVolumeLevel();
	}
	
	/** @return The volume of the sound source based on the current state of the source. This accounts for base volume, current volume, and if the sound is muted */
	public double getTotalVolume(){
		return this.isMuted() ? 0 : (this.getVolume() * this.getBaseVolume());
	}
	
	/** @return See {@link #muted} */
	public boolean isMuted(){
		return this.muted;
	}
	
	/** @param muted See {@link #muted} */
	public void setMuted(boolean muted){
		this.muted = muted;
		this.updateVolumeLevel();
	}
	
	/** Shorthand for {@link #setMuted(boolean)} true */
	public void mute(){
		this.setMuted(true);
	}
	
	/** Shorthand for {@link #setMuted(boolean)} false */
	public void unmute(){
		this.setMuted(false);
	}
	
	/** @return See {@link #paused} */
	public boolean isPaused(){
		return this.paused;
	}
	
	/**
	 * @param paused See {@link #paused}
	 */
	public void setPaused(boolean paused){
		if(paused) this.pause();
		else this.unpause();
	}
	
	/**
	 * Cause {@link #current} to stop playing on the next update.
	 * This is done by setting the OpenAL volume to 0, waiting a brief time, then pausing the sound on the next update.
	 * If the sound was already paused, this method does nothing
	 */
	public void pause(){
		if(this.isPaused()) return;
		this.paused = true;
		
		// Track the sample position and then mute the sound immediately
		this.pausedSample = this.getSamplePos();
		alSourcef(this.getId(), AL_GAIN, 0);
	}
	
	/**
	 * Cause {@link #current} to begin playing again on the next update.
	 */
	public void unpause(){
		if(!this.isPaused()) return;
		this.paused = false;
	}
	
	/** @return The sample position of the sound currently playing in this source */
	public float getSamplePos(){
		return alGetSourcef(this.getId(), AL_SAMPLE_OFFSET);
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
	/** @return See {@link #current} */
	public Sound getCurrent(){
		return this.current;
	}
	
	/** @param current See {@link #current} */
	public void setCurrent(Sound current){
		this.current = current;
	}
	
}
