package zgame.core.sound;

import org.lwjgl.PointerBuffer;

import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

import static org.lwjgl.openal.AL11.*;

import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * A {@link Sound} which uses multiple buffers to play long music files.
 * Instances of this class cannot be used on multiple sources at the same time without interfering with one another.
 */
public class MusicSound extends Sound{
	
	/** The values used by OpenAL to track the buffers of this {@link MusicSound} */
	private final int[] ids;
	
	/** The number of buffers used by this music sound for splitting audio into multiple buffers */
	private final int numBuffers;
	
	/** The number of samples in each buffer */
	private final int bufferSize;
	
	/** The pointer pointing to the data for this {@link MusicSound} */
	private PointerBuffer pointer;
	/** The position in the pointer where the next selection of samples should be loaded from */
	private int pointerOffset;
	
	/**
	 * Create a {@link MusicSound} with generic buffer sizes
	 * 
	 * @param path The file path to the music
	 */
	public MusicSound(String path){
		this(path, 4, 8192);
	}
	
	/**
	 * Create a {@link MusicSound} with the given buffer sizes
	 * 
	 * @param path The file path to the music
	 * @param numBuffers See {@link #numBuffers}
	 * @param bufferSize See {@link #bufferSize}
	 */
	public MusicSound(String path, int numBuffers, int bufferSize){
		super(path);
		this.numBuffers = numBuffers;
		this.bufferSize = bufferSize;
		this.ids = new int[this.getNumBuffers()];
		this.reset();
		alGenBuffers(this.ids);
	}
	
	/** Reset the state of the pointer in this {@link MusicSound} to load data from the beginning of the sound */
	public void reset(){
		this.pointerOffset = 0;
	}
	
	@Override
	public void destroy(){
		this.pointer.free();
		super.destroy();
	}
	
	/**
	 * Buffer data from the currently stored buffer in {@link #pointer}
	 */
	protected void bufferData(){
		this.bufferData(null);
	}
	
	/**
	 * @param p The pointer to use for buffering the data, use null to use the pointer stored in this {@link MusicSound}
	 */
	@Override
	protected void bufferData(PointerBuffer p){
		if(p == null) p = this.pointer;
		if(this.pointer == null) this.pointer = p;
		int numBuffs = this.getNumBuffers();
		
		for(int i = 0; i < numBuffs; i++) this.bufferDataChunk(this.getIds()[i]);
	}
	
	/**
	 * Buffer the next chunk of data available in {@link #pointer}. Does nothing if there is no data left to buffer
	 * 
	 * @param id The buffer id to place the data
	 */
	protected void bufferDataChunk(int id){
		// Find the total number of samples in the buffer of this sound
		int totalSize = this.getTotalSize();
		
		// Get the pointer and the format, i.e. mono or stereo
		PointerBuffer p = this.pointer;
		int format = this.getFormat();
		
		// Get either the size of the buffer for the next buffer allocation, or the remaining size of the buffer
		int size = Math.min(this.getBufferSize(), totalSize - pointerOffset);
		// If there are no more samples, use zero samples
		if(size < 0) size = 0;
		
		// Buffer the chunk of data
		short[] data = new short[totalSize];
		ShortBuffer buff = p.getShortBuffer(0, totalSize);
		buff.get(data, 0, totalSize);
		alBufferData(id, format, Arrays.copyOfRange(data, pointerOffset, pointerOffset + size), this.getSampleRate());
		
		// Keep track of the change
		pointerOffset += size;
	}
	
	/** @return See {@link #numBuffers} */
	public int getNumBuffers(){
		return this.numBuffers;
	}
	
	/** @return See {@link #bufferSize} */
	public int getBufferSize(){
		return this.bufferSize;
	}
	
	/** @return See {@link #ids} */
	public int[] getIds(){
		return this.ids;
	}
	
	/**
	 * Load a sound based on the given name. This method assumes the given name is only the file name with no extension,
	 * that the file is located in {@link ZFilePaths#MUSIC}, and that it is of type .ogg
	 * 
	 * @param name The name of the file
	 * @return The loaded sound
	 */
	public static MusicSound loadMusic(String name){
		MusicSound s = new MusicSound(ZStringUtils.concat(ZFilePaths.MUSIC, name));
		s.load();
		return s;
	}
	
}
