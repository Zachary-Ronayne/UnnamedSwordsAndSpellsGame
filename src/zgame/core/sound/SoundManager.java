package zgame.core.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.ALC11.*;

import org.lwjgl.openal.ALUtil;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;

/**
 * A class that handles multiple SoundPlayers; music and effects
 */
public class SoundManager{
	
	/** The {@link EffectsPlayer} tracking the sound effects played by this {@link SoundManager} */
	private EffectsPlayer effectsPlayer;
	
	/** The {@link MusicPlayer} tracking the music played by this {@link SoundManager} */
	private MusicPlayer musicPlayer;
	
	/** The single {@link SoundSource} which plays music. Generally should position this sound to be in the same location as the player of the game */
	private SoundSource musicSource;
	
	/** The single {@link SoundListener} which determines where sound is located */
	private SoundListener listener;
	
	/** The {@link SpeakerDevice} which is currently being used to play sounds */
	private SpeakerDevice currentDevice;
	
	/** The {@link SpeakerDevice} which is used as the current machine's default speaker device */
	private SpeakerDevice defaultDevice;
	
	/**
	 * A list of every {@link SpeakerDevice} available on the current machine since the last scan.
	 * There are no guarentees about the order of this list, i.e. the default device could be anywhere in the list
	 */
	private List<SpeakerDevice> devices;
	
	/** A map of every sound effect currently available through this {@link SoundManager}. The key is a string representing the name of the sound */
	private Map<String, Sound> effects;
	
	/** A map of every piece of music currently available through this {@link SoundManager}. The key is a string representing the name of the sound */
	private Map<String, Sound> music;
	
	public SoundManager(){
		this.effects = new HashMap<String, Sound>();
		this.music = new HashMap<String, Sound>();
		
		this.devices = new ArrayList<SpeakerDevice>();
		this.scanDevices();
		
		this.effectsPlayer = new EffectsPlayer();
		this.musicPlayer = new MusicPlayer();
		this.musicSource = new SoundSource();
		this.listener = new SoundListener();
	}
	
	/**
	 * Scan all available devices for playing back sound, and update the internal values, i.e. {@link #currentDevice}, {@link #defaultDevice}, and {@link #devices}
	 * If {@link #currentDevice} is no longer available, it is set to the default device.
	 * This method closes any previously loaded devices, and loads all newly loaded devices
	 */
	public void scanDevices(){
		// Reset the players for effects and music
		if(this.effectsPlayer != null) this.effectsPlayer.reset();
		if(this.musicPlayer != null) this.musicPlayer.reset();

		// Record the name of the old current device
		String oldName = (this.currentDevice == null) ? "" : this.currentDevice.getName();
		
		// Close resources opened by the manager
		this.end();
		
		// Scan for all devices and load them in
		this.devices.clear();
		List<String> names = ALUtil.getStringList(0, ALC_ALL_DEVICES_SPECIFIER);
		for(int i = 0; i < names.size(); i++) devices.add(new SpeakerDevice(names.get(i)));
		
		// Find default device name
		String defaultName = alcGetString(0, ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
		// Find the default device in the list of devices, and set the default device
		this.defaultDevice = null;
		for(SpeakerDevice s : this.devices){
			if(s.getName().equals(defaultName)){
				this.defaultDevice = s;
				break;
			}
		}
		// If the default device was not found, manually find it and add it to the list
		if(this.defaultDevice == null){
			this.defaultDevice = new SpeakerDevice(defaultName);
			this.devices.add(this.defaultDevice);
		}
		// Set current device, keeping one with the same name if it's available
		this.currentDevice = null;
		for(SpeakerDevice s : this.devices){
			if(s.getName().equals(oldName)){
				this.currentDevice = s;
				break;
			}
		}
		// If the old current device was not found, use the default device as the current device
		if(this.currentDevice == null) this.currentDevice = this.defaultDevice;
		
		// Use the current device
		this.currentDevice.use();
		if(ZConfig.printSuccess()) ZStringUtils.prints("Using sound device:", this.currentDevice.getName());
	}
	
	/** Clear any resources used by this {@link SoundManager} */
	public void end(){
		for(SpeakerDevice s : this.devices) s.end();
		if(this.effects != null) for(Map.Entry<String, Sound> s : this.effects.entrySet()) s.getValue().end();
		if(this.music != null) for(Map.Entry<String, Sound> s : this.music.entrySet()) s.getValue().end();
		if(this.musicSource != null) this.musicSource.end();
	}
	
	/** Update the state of the effects and music player */
	public void update(){
		this.getEffectsPlayer().updateState();
		this.getMusicPlayer().updateState();
	}
	
	/**
	 * Add a sound effect used by this SoundManager. Sounds added here will automatically be deleted when {@link #end()} is called
	 * 
	 * @param effect The sound to add
	 * @param name The name of the sound, use this value when playing sounds
	 */
	public void addEffect(Sound effect, String name){
		this.effects.put(name, effect);
	}
	
	/**
	 * Add a sound effect used by this SoundManager. Sounds added here will automatically be deleted when {@link #end()} is called
	 * 
	 * @param name The name of the sound, which must exist as a .ogg file in ZFilePaths.EFFECTS use this value when playing sounds
	 */
	public void addEffect(String name){
		this.addEffect(Sound.loadEffect(name), name);
	}
	
	/**
	 * Remove a sound effect used by this SoundManager
	 * 
	 * @param name The name of the sound to use. After calling this method, the sound with the given name will not be able to play
	 */
	public void removeEffect(String name){
		this.effects.remove(name);
	}
	
	/**
	 * Add a music sound used by this SoundManager Sounds added here will automatically be deleted when {@link #end()} is called
	 * 
	 * @param music The sound to add
	 * @param name The name of the sound, use this value when playing sounds
	 */
	public void addMusic(Sound music, String name){
		this.music.put(name, music);
	}
	
	/**
	 * Add a music sound used by this SoundManager Sounds added here will automatically be deleted when {@link #end()} is called
	 * 
	 * @param name The name of the sound, which must exist as a .ogg file in ZFilePaths.MUSIC, use this value when playing sounds
	 */
	public void addMusic(String name){
		this.addMusic(Sound.loadMusic(name), name);
	}
	
	/**
	 * Remove a music sound used by this SoundManager
	 * 
	 * @param name The name of the sound to use. After calling this method, the sound with the given name will not be able to play
	 */
	public void removeMusic(String name){
		this.music.remove(name);
	}
	
	/**
	 * Play the given sound at the given source
	 * 
	 * @param source The source to play the sound
	 * @param name The name of the sound, i.e. the name used when calling {@link #addEffect(Sound, String)}
	 */
	public void playEffect(SoundSource source, String name){
		this.getEffectsPlayer().playSound(source, this.effects.get(name));
	}
	
	/**
	 * Play the given music sound. Once the music sound ends, no further music will play
	 * 
	 * @param name The name of the sound, i.e. the name used when calling {@link #addMusic(Sound, String)}
	 */
	public void playMusic(String name){
		this.getMusicPlayer().playSound(this.getMusicSource(), this.music.get(name));
	}
	
	/** @return See {@link #musicSource} */
	public SoundSource getMusicSource(){
		return this.musicSource;
	}
	
	/** @return See {@link #listener} */
	public SoundListener getListener(){
		return this.listener;
	}
	
	/** @return See {@link #effectsPlayer} */
	public EffectsPlayer getEffectsPlayer(){
		return effectsPlayer;
	}
	
	/** @return See {@link #musicPlayer} */
	public MusicPlayer getMusicPlayer(){
		return musicPlayer;
	}
	
	/** @return See {@link #currentDevice} */
	public SpeakerDevice getCurrentDevice(){
		return currentDevice;
	}
	
	/** @return See {@link #defaultDevice} */
	public SpeakerDevice getDefaultDevice(){
		return defaultDevice;
	}
	
	/** @return A copy of {@link #devices}. The returned list is distinct from the internal list of devices */
	public SpeakerDevice[] getDevices(){
		SpeakerDevice[] arr = new SpeakerDevice[this.devices.size()];
		for(int i = 0; i < arr.length; i++){ arr[i] = this.devices.get(i); }
		return arr;
	}
	
}
