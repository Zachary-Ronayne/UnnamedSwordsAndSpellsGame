package zgame.core.sound;

import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import static org.lwjgl.openal.AL11.*;

/** A {@link HashMap} that manages a collection of {@link SoundSource} objects, their playing state, and relative volume */
public class SoundMap extends HashMap<Integer, SoundSource>{
	
	/** The current volume of this {@link SoundMap}. This value will never be negative */
	private double volume;
	
	/** Create a new empty {@link SoundMap} */
	public SoundMap(){
		super();
		this.volume = 1;
	}
	
	/** Cause every source in this map to stop playing, and then remove them from the map */
	public void clearSound(){
		for(SoundSource s : this.values()) alSourceStop(s.getId());
		this.clear();
	}
	
	/**
	 * Add the given source to the map
	 *
	 * @param s The source to add
	 */
	public void put(SoundSource s){
		this.put(s.getId(), s);
	}
	
	/** Update the state of every {@link SoundSource} in this {@link SoundMap} */
	public void update(){
		for(SoundSource s : this.values()) s.update();
	}
	
	/**
	 * Set the muted state of every {@link SoundSource} in this {@link SoundMap}
	 *
	 * @param muted true to mute the sound, false otherwise
	 */
	public void setMuted(boolean muted){
		for(SoundSource s : this.values()) s.setMuted(muted);
	}
	
	/**
	 * Set the paused state of every {@link SoundSource} in this {@link SoundMap}
	 *
	 * @param pause true to pause the sounds, false otherwise
	 */
	public void setPaused(boolean pause){
		if(pause) for(SoundSource s : this.values()) s.pause();
		else for(SoundSource s : this.values()) s.unpause();
	}
	
	/** @return See {@link #volume} */
	public double getVolume(){
		return this.volume;
	}
	
	/**
	 * Set the volume state of every {@link SoundSource} in this {@link SoundMap} This only sets the relative volume, i.e. the given volume and the internal volume of each
	 * sound are multiplied together
	 *
	 * @param volume The volume to set to
	 */
	public void setVolume(double volume){
		this.volume = Math.max(0, volume);
		for(SoundSource s : this.values()) s.setVolume(volume);
	}
	
	/** Like {@link #setVolume(double)} but add the given volume instead of setting it */
	public void addVolume(double volume){
		this.setVolume(this.getVolume() + volume);
	}
	
	/** @return An array containing every {@link SoundSource} in this {@link SoundMap} */
	public SoundSource[] toArray(){
		Collection<SoundSource> s = this.values();
		return s.toArray(new SoundSource[0]);
	}
	
	/**
	 * Remove any sounds from this {@link SoundMap} which are finished playing, i.e. their AL_SOURCE_STATE equals AL_STOPPED
	 *
	 * @return true if at least one sound was removed, false otherwise
	 */
	public boolean removeFinishedSounds(){
		boolean found = false;
		SoundSource[] play = this.toArray();
		for(int i = 0; i < play.length; i++){
			SoundSource s = this.get(play[i].getId());
			IntBuffer buf = BufferUtils.createIntBuffer(1);
			alGetSourcei(s.getId(), AL_SOURCE_STATE, buf);
			int state = buf.get(0);
			if(state == AL_STOPPED){
				this.remove(s.getId());
				found = true;
			}
		}
		return found;
	}
	
}
