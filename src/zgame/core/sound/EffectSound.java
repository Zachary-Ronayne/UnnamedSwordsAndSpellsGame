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
	
	/** The type of sound that this effect is, i.e., is this dialog, footsteps, background noises, etc. Can be null to use no specified type */
	private String type;
	
	/**
	 * Create a new {@link EffectSound}. Call {@link #load()} to load in the data itself
	 * 
	 * @param path The path to load data from
	 */
	public EffectSound(String path){
		this(path, null);
	}
	
	/**
	 * Create a new {@link EffectSound}. Call {@link #load()} to load in the data itself
	 * 
	 * @param path The path to load data from
	 * @param type See {@link #type}
	 */
	public EffectSound(String path, String type){
		super(path);
		this.id = alGenBuffers();
		this.type = type;
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
	
	/** @return See {@link #type} */
	public String getType(){
		return this.type;
	}
	
	/**
	 * Load a sound based on the given name. This method assumes the given name is only the file name with no extension,
	 * that the file is located in {@link ZFilePaths#EFFECTS}, and that it is of type .ogg
	 * The sound will have null for its sound type
	 * 
	 * @param name The name of the file
	 * @return The loaded sound
	 */
	public static EffectSound loadSound(String name){
		return loadSound(name, null);
	}
	
	/**
	 * Load a sound based on the given name. This method assumes the given name is only the file name with no extension,
	 * that the file is located in {@link ZFilePaths#EFFECTS}, and that it is of type .ogg
	 * 
	 * @param name The name of the file. This should also include any sub folders needed to get to the sound from {@link ZFilePaths#EFFECTS}
	 * @param type The type of the sound
	 * @return The loaded sound
	 */
	public static EffectSound loadSound(String name, String type){
		EffectSound s = new EffectSound(ZStringUtils.concat(ZFilePaths.EFFECTS, name, ".ogg"), type);
		s.load();
		return s;
	}
	
}
