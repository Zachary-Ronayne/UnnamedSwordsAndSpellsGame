package zgame.core.sound;

import zgame.physics.ForwardVector;
import zgame.things.type.Position3D;

import static org.lwjgl.openal.AL11.*;

/**
 * A class tracking the central listener in OpenAL. This should primarily be used for the player, i.e. its position should be at the location of who hears the sound. More than
 * one instance of this class should not be used at a time, otherwise unexpected results could occur
 */
public class SoundListener extends SoundLocation implements Position3D{
	
	/** The current x position of the listener */
	private double x;
	/** The current y position of the listener */
	private double y;
	/** The current z position of the listener */
	private double z;
	
	/**
	 * Create a new empty {@link SoundListener}
	 */
	public SoundListener(){
		super();
		this.updatePosition(0, 0, 0);
		this.updateOrientation(new ForwardVector());
		alListener3f(AL_VELOCITY, 0, 0, 0);
	}
	
	@Override
	public void updatePosition(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		this.updatePosition();
	}
	
	/** Call the appropriate OpenAL methods for updating the position of the listener based on its currently stored position values */
	public void updatePosition(){
		var sm = SoundManager.get();
		double scalar = sm.getDistanceScalar();
		alListener3f(AL_POSITION, (float)(this.getX() * scalar), (float)(this.getY() * scalar), (float)(this.getZ() * scalar));
	}
	
	@Override
	public void updateDirection(double x, double y, double z){
		// Listeners don't use a direction
	}
	
	@Override
	public void updateOrientation(ForwardVector v){
		alListenerfv(AL_ORIENTATION, new float[] {
				(float)v.getYawX(), (float)v.getYawY(), (float)v.getYawZ(),
				(float)v.getPitchX(), (float)v.getPitchY(), (float)v.getPitchZ()
		});
	}
	
	/** @return See {@link #x} */
	@Override
	public double getX(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	@Override
	public double getY(){
		return this.y;
	}
	
	/** @return See {@link #z} */
	@Override
	public double getZ(){
		return this.z;
	}
}
