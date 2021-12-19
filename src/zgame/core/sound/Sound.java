package zgame.core.sound;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.stb.STBVorbis.*;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/**
 * A class that tracks a single created sound in OpenAL
 */
public class Sound{
	
	/** The id OpenAL id tracking this sound */
	private int id;
	/** The file path to the location of where this sound was loaded */
	private String path;
	/** true if this {@link Sound} is in mono, i.e. one channel, or stereo, i.e. two channels */
	private boolean mono;
	/** The sample rate of this audio file, i.e. number of samples per second */
	private int sampleRate;
	
	/**
	 * Create a new sound, loading the data from the given path
	 * 
	 * @param path
	 */
	public Sound(String path){
		this.path = path;
		this.init();
	}
	
	/** Set up the state of this sound based on the current value of {@link #path} */
	private void init(){
		this.id = alGenBuffers();
		this.load();
	}
	
	/** Load this sound from memory and associate that data with the id of this {@link Sound} */
	private void load(){
		// Load the bytes of the sound from the jar
		ByteBuffer buff = null;
		InputStream stream = null;
		try{
			stream = getClass().getClassLoader().getResourceAsStream(this.getPath());
			byte[] bytes = stream.readAllBytes();
			buff = BufferUtils.createByteBuffer(bytes.length);
			buff.put(bytes);
			buff.flip();
			stream.close();
		}catch(IOException e){
			if(ZConfig.printErrors()) ZStringUtils.print("Sound '", this.getPath(), "' failed to load from the jar");
			return;
		}
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
			ZStringUtils.print("Sound '", this.getPath(), "' loaded successfully in ", (this.isMono() ? "mono" : "stereo"), ", with sample rate: ", this.getSampleRate(), ", ", samplesLoaded, " samples loaded, and id: ", this.getId());
		}
		else if(ZConfig.printErrors() && !success){
			ZStringUtils.print("Sound '", this.getPath(), "' failed to load via stb");
			return;
		}
		
		// Set up the data with OpenAL
		int format = (channelCount == 2) ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
		alBufferData(this.getId(), format, pointer.getShortBuffer(samplesLoaded * channelCount), this.getSampleRate());
		pointer.free();
	}
	
	/** Free any resources used by this {@link Sound} After calling this method, this sound cannot be played */
	public void end(){
		alDeleteBuffers(this.getId());
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
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
	
	/** @return See {@link #sampleRate} */
	public int getSampleRate(){
		return this.sampleRate;
	}

	/**
	 * Load a sound based on the given name. This method assumes the given name is only the file name with no extension, 
	 * that the file is located in ZFilePaths.EFFECTS, and that it is of type .ogg
	 * 
	 * @param name The name of the file
	 * @return The loaded sound
	 */
	public static Sound loadEffect(String name){
		return new Sound(ZStringUtils.concat(ZFilePaths.EFFECTS, name, ".ogg"));
	}
	
	/**
	 * Load a sound based on the given name. This method assumes the given name is only the file name with no extension,
	 * that the file is located in ZFilePaths.MUSIC, and that it is of type .ogg
	 * 
	 * @param name The name of the file
	 * @return The loaded sound
	 */
	public static Sound loadMusic(String name){
		return new Sound(ZStringUtils.concat(ZFilePaths.MUSIC, name, ".ogg"));
	}

}
