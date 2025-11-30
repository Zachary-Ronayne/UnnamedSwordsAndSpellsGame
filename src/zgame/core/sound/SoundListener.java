package zgame.core.sound;

import static org.lwjgl.openal.AL11.*;

/**
 * A class tracking the central listener in OpenAL. This should primarily be used for the player, i.e. its position should be at the location of who hears the sound. More than
 * one instance of this class should not be used at a time, otherwise unexpected results could occur
 */
public class SoundListener extends SoundLocation{
	
	/**
	 * Create a new empty {@link SoundListener}
	 */
	public SoundListener(){
		super();
		this.updatePosition(0, 0, 0);
		this.updateOrientation(0, 0, 0, 0, 0, 0);
		alListener3f(AL_VELOCITY, 0, 0, 0);
	}
	
	@Override
	public void updatePosition(double x, double y, double z){
		alListener3f(AL_POSITION, (float)x, (float)y, (float)z);
	}
	
	@Override
	public void updateDirection(double x, double y, double z){
		// Listeners don't use a direction
	}
	
	@Override
	public void updateOrientation(double yx, double yy, double yz, double px, double py, double pz){
		alListenerfv(AL_ORIENTATION, new float[] {(float)yz, (float)yy, (float)yz, (float)px, (float)py, (float)pz});
	}
}
