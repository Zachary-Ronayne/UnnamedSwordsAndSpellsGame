package zgame.core.sound;

import org.lwjgl.PointerBuffer;
import static org.lwjgl.openal.AL11.*;

import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/**
 * A class that tracks a single created sound in OpenAL, which is buffered and tracked by a single id. This is designed for short sounds, like sound effects
 */
public class EffectSound extends Sound{
	
	/** The id OpenAL id tracking this sound */
	private int id;
	
	/**
	 * Create a new {@link EffectSound}. Call {@link #load()} to load in the data itself
	 * 
	 * @param path The path to load data from
	 */
	public EffectSound(String path){
		super(path);
		this.id = alGenBuffers();
	}
	
	@Override
	protected void bufferData(PointerBuffer p){
		// Set up the data with OpenAL
		alBufferData(this.getId(), this.getFormat(), p.getShortBuffer(this.getTotalSize()), this.getSampleRate());
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
	@Override
	public int[] getIds(){
		return new int[]{this.getId()};
	}
	
	/**
	 * Load a sound based on the given name. This method assumes the given name is only the file name with no extension,
	 * that the file is located in ZFilePaths.EFFECTS, and that it is of type .ogg
	 * 
	 * @param name The name of the file
	 * @return The loaded sound
	 */
	public static EffectSound loadSound(String name){
		EffectSound s = new EffectSound(ZStringUtils.concat(ZFilePaths.EFFECTS, name, ".ogg"));
		s.load();
		return s;
	}
	
}
