package zgame.core.sound;

import static org.lwjgl.openal.AL11.*;

/**
 * A class tracking the central listener in OpenAL. This should primarily be used for the player, i.e. its position should be at the locaiton of who hears the sound. 
 * More than one instance of this class should not be used at a time, otherwise unexpected results could occur
 */
public class SoundListener extends SoundLocation{

	/**
	 * Create a new empty {@link SoundListener}
	 */
	public SoundListener(){
		super();
		alListener3f(AL_POSITION, 0, 0, 0);
		alListener3f(AL_VELOCITY, 0, 0, 0);
	}

	@Override
	public void updatePosition(double x, double y){
		alListener3f(AL_POSITION, (float)x, (float)y, 0);
	}
	
}
