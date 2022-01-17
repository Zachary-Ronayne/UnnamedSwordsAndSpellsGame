package zgame.core.sound;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.stb.STBVorbis.*;

import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;

/** A class that represents much of the data needed to keep track of OpenAL sounds, but does not handle tracking an ID */
public abstract class Sound{
	
	/** The file path to the location of where this sound was loaded */
	private String path;
	/** true if this {@link EffectSound} is in mono, i.e. one channel, or stereo, i.e. two channels */
	private boolean mono;
	/** The sample rate of this audio file, i.e. number of samples per second */
	private int sampleRate;
	/** The number of samples in this sound */
	private int samples;
	
	/**
	 * Create a new sound. Call {@link #load()} to load in the data itself
	 * 
	 * @param path The path to load data from
	 */
	public Sound(String path){
		this.path = path;
	}
	
	/**
	 * Run methods to buffer the data
	 * 
	 * @param p The data to buffer
	 */
	protected abstract void bufferData(PointerBuffer p);
	
	/**
	 * Load this sound from memory and associate that data with the id of this {@link EffectSound}
	 * 
	 * @return The pointer is if is still open, or null if it is not open, also returns null if any load errors occured
	 */
	public PointerBuffer load(){
		return this.load(true);
	}
	
	/**
	 * Load this sound from memory and associate that data with the id of this {@link EffectSound}
	 * 
	 * @param freePointer true to free the pointer used to load the data, false to keep it open
	 * @return The pointer is if is still open, or null if it is not open, also returns null if any load errors occured
	 */
	public PointerBuffer load(boolean freePointer){
		// Load the bytes of the sound from the jar
		ByteBuffer buff = ZAssetUtils.getJarBytes(this.getPath());

		// Load in the sound in with stb
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		IntBuffer sampleRate = BufferUtils.createIntBuffer(1);
		PointerBuffer pointer = BufferUtils.createPointerBuffer(1);
		// Returns the number of samples loaded
		int samplesLoaded = stb_vorbis_decode_memory(buff, channels, sampleRate, pointer);
		
		// Determine metadata
		int channelCount = channels.get(0);
		this.mono = channelCount != 2;
		this.sampleRate = sampleRate.get(0);
		
		// Determine success
		boolean success = samplesLoaded != -1;
		if(ZConfig.printSuccess() && success){
			ZStringUtils.print("Sound '", this.getPath(), "' loaded successfully in ", (this.isMono() ? "mono" : "stereo"), ", with sample rate: ", this.getSampleRate(), ", ", samplesLoaded, " samples loaded, and ids: ", this.getIdString());
		}
		else if(ZConfig.printErrors() && !success){
			ZStringUtils.print("Sound '", this.getPath(), "' failed to load via stb");
			return null;
		}
		this.samples = samplesLoaded;
		this.bufferData(pointer);
		if(freePointer){
			pointer.free();
			return null;
		}
		return pointer;
	}
	
	/** Free any resources used by this {@link EffectSound} After calling this method, this sound cannot be played */
	public void end(){
		for(int i : this.getIds()) alDeleteBuffers(i);
	}
	
	/** @return The ids used by this {@link Sound} for buffering */
	public abstract int[] getIds();
	
	/** @return See {@link #path} */
	public String getPath(){
		return this.path;
	}
	
	/** @return See {@link #mono} */
	public boolean isMono(){
		return this.mono;
	}
	
	/** @return 1 if this sound is mono, 2 otherwise, i.e. 2 if it is stereo */
	public int getChannels(){
		return this.isMono() ? 1 : 2;
	}
	
	/** @return The OpenAL format of this {@link Sound} */
	public int getFormat(){
		return this.isMono() ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
	}
	
	/** @return See {@link #sampleRate} */
	public int getSampleRate(){
		return this.sampleRate;
	}
	
	/** @return See {@link #samples} */
	public int getSamples(){
		return this.samples;
	}
	
	/** @return The total size of this {@link Sound}, i.e. the total number of samples across all channels */
	public int getTotalSize(){
		return this.getSamples() * this.getChannels();
	}
	
	/** @return A user readble string containing each id used as a buffer for this Abstract sound */
	public String getIdString(){
		int[] ids = this.getIds();
		Integer[] arr = new Integer[ids.length];
		for(int i = 0; i < ids.length; i++) arr[i] = ids[i];
		return ZStringUtils.arrStr(arr);
	}
}
