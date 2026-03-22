package zgame.core.sound;

import zgame.core.Game;
import zgame.core.utils.ZMath;
import zgame.things.type.Position3D;

/** A sound source which manages automatically updating all relevant fields for playing a sound */
public class ManagedSoundSource extends SoundSource{
	
	/** The name of the sound to play at this source, where the name corresponds to those used by {@link SoundManager} */
	private String soundName;
	
	/** The object tracking the position of this sound. Sounds will originate at the values of this position at the time they are produced */
	private Position3D position;
	
	/** The last time, in game time, that this source was played */
	private double lastPlayed;
	
	/** The offset of this sound on the x axis from {@link #position} */
	private double offsetX;
	/** The offset of this sound on the y axis from {@link #position} */
	private double offsetY;
	/** The offset of this sound on the z axis from {@link #position} */
	private double offsetZ;
	
	/** Minimum value the pitch will be set to when it plays */
	private double minPitch;
	/** Maximum value the pitch will be set to when it plays */
	private double maxPitch;
	
	/** Minimum value the volume will be set to when it plays */
	private double minVolume;
	/** Maximum value the volume will be set to when it plays */
	private double maxVolume;
	
	/**
	 * @param soundName See {@link #soundName}
	 * @param position See {@link #position}
	 */
	public ManagedSoundSource(String soundName, Position3D position){
		this(soundName, position, 1, 1, 1);
	}
	
	/**
	 * @param soundName See {@link #soundName}
	 * @param position See {@link #position}
	 * @param minPitch See {@link #minPitch}
	 * @param maxPitch See {@link #maxPitch}
	 * @param volume The value for both {@link #minVolume} and {@link #maxVolume}
	 */
	public ManagedSoundSource(String soundName, Position3D position, double minPitch, double maxPitch, double volume){
		this(soundName, position, minPitch, maxPitch, volume, volume);
	}
	
	/**
	 * @param soundName See {@link #soundName}
	 * @param position See {@link #position}
	 * @param minPitch See {@link #minPitch}
	 * @param maxPitch See {@link #maxPitch}
	 * @param minVolume See {@link #minVolume}
	 * @param maxVolume See {@link #maxVolume}
	 */
	public ManagedSoundSource(String soundName, Position3D position, double minPitch, double maxPitch, double minVolume, double maxVolume){
		super();
		this.position = position;
		this.soundName = soundName;
		this.setMinPitch(minPitch);
		this.setMaxPitch(maxPitch);
		this.setMinVolume(minVolume);
		this.setMaxVolume(maxVolume);
		this.setOffsetX(0);
		this.setOffsetY(0);
		this.setOffsetZ(0);
		
		this.updateLastPlayed();
	}
	
	public void playSound(){
		var name = this.getSoundName();
		// No name, cannot play
		if(name == null) return;
		
		// No position, can't play
		var pos = this.getPosition();
		if(pos == null) return;
		
		// Update general values
		this.updatePosition(pos.getX() + this.getOffsetX(), pos.getY() + this.getOffsetY(), pos.getZ() + this.getOffsetZ());
		this.updateDirection(0, 0, 0);
		this.updatePitch(ZMath.randomRange(this.getMinPitch(), this.getMaxPitch()));
		this.setVolume(ZMath.randomRange(this.getMinVolume(), this.getMaxVolume()));
		
		// Play the sound
		var game = Game.get();
		game.playEffect(this, name);
		this.updateLastPlayed();
	}
	
	/** @return See {@link #position} */
	public Position3D getPosition(){
		return this.position;
	}
	
	/** @param position See {@link #position} */
	public void setPosition(Position3D position){
		this.position = position;
	}
	
	/** @return See {@link #soundName} */
	public String getSoundName(){
		return this.soundName;
	}
	
	/** @param soundName See {@link #soundName} */
	public void setSoundName(String soundName){
		this.soundName = soundName;
	}
	
	/** @return See {@link #lastPlayed} */
	public double getLastPlayed(){
		return this.lastPlayed;
	}
	
	private void updateLastPlayed(){
		this.lastPlayed = Game.get().getTotalTickTime();
	}
	
	/** @return See {@link #offsetX} */
	public double getOffsetX(){
		return this.offsetX;
	}
	
	/** @param offsetX See {@link #offsetX} */
	public void setOffsetX(double offsetX){
		this.offsetX = offsetX;
	}
	
	/** @return See {@link #offsetY} */
	public double getOffsetY(){
		return this.offsetY;
	}
	
	/** @param offsetY See {@link #offsetY} */
	public void setOffsetY(double offsetY){
		this.offsetY = offsetY;
	}
	
	/** @return See {@link #offsetZ} */
	public double getOffsetZ(){
		return this.offsetZ;
	}
	
	/** @param offsetZ See {@link #offsetZ} */
	public void setOffsetZ(double offsetZ){
		this.offsetZ = offsetZ;
	}
	
	/**
	 * Set all offset position values
	 * @param x See {@link #offsetX}
	 * @param y See {@link #offsetY}
	 * @param z See {@link #offsetZ}
	 */
	public void setOffset(double x, double y, double z){
		this.setOffsetX(x);
		this.setOffsetY(y);
		this.setOffsetZ(z);
	}
	
	/** @return See {@link #minPitch} */
	public double getMinPitch(){
		return this.minPitch;
	}
	
	/** @param minPitch See {@link #minPitch} */
	public void setMinPitch(double minPitch){
		this.minPitch = minPitch;
	}
	
	/** @return See {@link #maxPitch} */
	public double getMaxPitch(){
		return this.maxPitch;
	}
	
	/** @param maxPitch See {@link #maxPitch} */
	public void setMaxPitch(double maxPitch){
		this.maxPitch = maxPitch;
	}
	
	/** @return See {@link #minVolume} */
	public double getMinVolume(){
		return this.minVolume;
	}
	
	/** @param minVolume See {@link #minVolume} */
	public void setMinVolume(double minVolume){
		this.minVolume = minVolume;
	}
	
	/** @return See {@link #maxVolume} */
	public double getMaxVolume(){
		return this.maxVolume;
	}
	
	/** @param maxVolume See {@link #maxVolume} */
	public void setMaxVolume(double maxVolume){
		this.maxVolume = maxVolume;
	}
	
	@Override
	public void setVolume(double volume){
		super.setVolume(volume);
		this.setMinVolume(volume);
		this.setMaxVolume(volume);
	}
}
