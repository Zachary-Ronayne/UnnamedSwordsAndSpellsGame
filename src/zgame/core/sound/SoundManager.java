package zgame.core.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.ALC11.*;

import org.lwjgl.openal.ALUtil;

import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/**
 * A class that handles multiple {@link SoundPlayer} objects, music and effects
 */
public class SoundManager{
	
	/** The {@link EffectsPlayer} tracking the sound effects played by this {@link SoundManager} */
	private EffectsPlayer effectsPlayer;
	
	/** The {@link MusicPlayer} tracking the music played by this {@link SoundManager} */
	private MusicPlayer musicPlayer;
	
	/** The single {@link SoundSource} which plays music. Will automatically always play relative to the OpenAL listener */
	private SoundSource musicSource;
	
	/** The single {@link SoundListener} which determines where sound is located */
	private SoundListener listener;
	
	/** The {@link SpeakerDevice} which is currently being used to play sounds */
	private SpeakerDevice currentDevice;
	
	/** The {@link SpeakerDevice} which is used as the current machine's default speaker device */
	private SpeakerDevice defaultDevice;
	
	/** The amount the sound positions of sounds are scaled. i.e, multiply this value to every coordinate used by this {@link SoundManager} */
	private double distanceScalar;
	
	/**
	 * A list of every {@link SpeakerDevice} available on the current machine since the last scan.
	 * There are no guarantees about the order of this list, i.e. the default device could be anywhere in the list
	 */
	private List<SpeakerDevice> devices;
	
	/** A map of every {@link EffectSound} currently available through this {@link SoundManager}. The key is a string representing the name of the sound */
	private Map<String, EffectSound> effects;
	
	/** A map of every piece of music currently available through this {@link SoundManager}. The key is a string representing the name of the sound */
	private Map<String, MusicSound> music;
	
	/** Initialize the {@link SoundManager} to its default state */
	public SoundManager(){
		this(1);
	}
	
	/**
	 * Initialize the {@link SoundManager} to its default state
	 * 
	 * @param distanceScalar See {@link #distanceScalar}
	 */
	public SoundManager(double distanceScalar){
		this.distanceScalar = distanceScalar;
		this.effects = new HashMap<String, EffectSound>();
		this.music = new HashMap<String, MusicSound>();
		
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
		
		// Scan for all devices, load any which are not already in the list, and remove and close any devices which no longer can be found
		// This is to avoid reloading devices which have already been loaded
		
		// First get the newly loaded names and the names before this scan
		List<String> names = ALUtil.getStringList(0, ALC_ALL_DEVICES_SPECIFIER);
		List<String> oldNames = new ArrayList<String>();
		for(SpeakerDevice d : this.devices) oldNames.add(d.getName());
		// Next remove any devices with names which are not in the new list of names
		for(int i = 0; i < this.devices.size(); i++){
			SpeakerDevice d = this.devices.get(i);
			if(!names.contains(d.getName())){
				d.end();
				this.devices.remove(i);
				i--;
			}
		}
		// Next add all new devices
		for(int i = 0; i < names.size(); i++) if(!oldNames.contains(names.get(i))) this.devices.add(new SpeakerDevice(names.get(i)));
		
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
		this.closeDevices();
		if(this.effects != null) for(Map.Entry<String, EffectSound> s : this.effects.entrySet()) s.getValue().end();
		if(this.music != null) for(Map.Entry<String, MusicSound> s : this.music.entrySet()) s.getValue().end();
		if(this.musicSource != null) this.musicSource.end();
	}
	
	/** Free all resources used by audio devices sed by this {@link SoundManager} */
	private void closeDevices(){
		for(SpeakerDevice s : this.devices) s.end();
	}
	
	/** Update the state of the effects and music player */
	public void update(){
		this.getEffectsPlayer().updateState();
		this.getMusicPlayer().updateState();
	}
	
	/**
	 * Add a sound effect used by this {@link SoundManager}. Sounds added here will automatically be deleted when {@link #end()} is called
	 * 
	 * @param effect The sound to add
	 * @param name The name of the sound, use this value when playing sounds
	 */
	public void addEffect(EffectSound effect, String name){
		this.effects.put(name, effect);
	}
	
	/**
	 * Add a sound effect used by this {@link SoundManager}. Sounds added here will automatically be deleted when {@link #end()} is called
	 * 
	 * @param name The name of the sound, which must exist as a .ogg file in {@link ZFilePaths#EFFECTS} use this value when playing sounds
	 */
	public void addEffect(String name){
		EffectSound e = EffectSound.loadSound(name);
		this.addEffect(e, name);
	}
	
	/**
	 * Remove a sound effect used by this {@link SoundManager}
	 * 
	 * @param name The name of the sound to use. After calling this method, the sound with the given name will not be able to play
	 */
	public void removeEffect(String name){
		this.effects.remove(name);
	}
	
	/**
	 * Add a music sound used by this {@link SoundManager}. Sounds added here will automatically be deleted when {@link #end()} is called
	 * 
	 * @param music The sound to add
	 * @param name The name of the sound, use this value when playing sounds
	 */
	public void addMusic(MusicSound music, String name){
		this.music.put(name, music);
	}
	
	/**
	 * Add a music sound used by this SoundManager Sounds added here will automatically be deleted when {@link #end()} is called
	 * 
	 * @param name The name of the sound, which must exist as a .ogg file in {@link ZFilePaths#MUSIC}, use this value when playing sounds
	 */
	public void addMusic(String name){
		this.addMusic(MusicSound.loadMusic(name), name);
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
	 * Load all the sounds as effects in {@link ZFilePaths#EFFECTS}, where the name of the file without a file extension is how they will be referred to using
	 * {@link #playEffect(SoundSource, String)}
	 * The files should be stored such that {@link ZFilePaths#EFFECT} contains folders, where each folder is named as the type of sound contained by that folder.
	 * Then, each of those folders will contain the sound files which will be of the type of the folder they are in
	 */
	public void addAllEffects(){
		// First find all the folders
		List<String> folders = ZAssetUtils.getAllFolders(ZFilePaths.EFFECTS);
		
		// Now for each folder, add every effect in those folders, using the folder as the type
		for(String f : folders){
			// Get every file in the folder
			List<String> names = ZAssetUtils.getAllFiles(ZStringUtils.concat(ZFilePaths.EFFECTS, f), false);
			
			// Add each file
			for(String n : names) this.addEffect(EffectSound.loadSound(ZStringUtils.concat(f, "/", n), f), n);
		}
	}
	
	/**
	 * Load all the sounds as music in {@link ZFilePaths#MUSIC}, where the name of the file without a file extension is how they will be referred to using
	 * {@link #playMusic(String)}
	 */
	public void addAllMusic(){
		List<String> names = ZAssetUtils.getNames(ZFilePaths.MUSIC, false);
		for(String s : names) this.addMusic(s);
	}
	
	/**
	 * Load all the sounds as effects in {@link ZFilePaths#EFFECTS},
	 * and all the sounds as music in {@link ZFilePaths#MUSIC},
	 * where the name of the file without a file extension is how they will be referred to using {@link #playEffect(SoundSource, String)} and {@link #playMusic(String)}
	 */
	public void addAllSounds(){
		this.addAllEffects();
		this.addAllMusic();
	}
	
	/**
	 * Play the given sound at the given source
	 * 
	 * @param source The source to play the sound
	 * @param name The name of the sound, i.e. the name used when calling {@link #addEffect(EffectSound, String)}
	 */
	public void playEffect(SoundSource source, String name){
		this.getEffectsPlayer().playSound(source, this.effects.get(name));
	}
	
	/**
	 * Play the given music sound
	 * 
	 * @param name The name of the sound, i.e. the name used when calling {@link #addMusic(EffectSound, String)}
	 */
	public void playMusic(String name){
		this.getMusicPlayer().playSound(this.getMusicSource(), this.music.get(name));
	}
	
	/**
	 * Update the position of the listener of this SoundManager
	 * 
	 * @param x The new x coordinate in game coordinates
	 * @param y The new y coordinate in game coordinates
	 */
	public void updateListenerPos(double x, double y){
		this.updateSoundPos(getListener(), x, y);
	}
	
	/**
	 * Update the position of the given source based on the scaling of this sound manager
	 * 
	 * @param s The {@link SoundSource} to update
	 * @param x The new x coordinate in game coordinates
	 * @param y The new y coordinate in game coordinates
	 */
	public void updateSourcePos(SoundSource s, double x, double y){
		this.updateSoundPos(s, x, y);
	}
	
	/**
	 * Update the position of the given {@link SoundLocation} based on the scaling of this sound manager
	 * 
	 * @param s The {@link SoundLocation} to update
	 * @param x The new x coordinate in game coordinates
	 * @param y The new y coordinate in game coordinates
	 */
	private void updateSoundPos(SoundLocation s, double x, double y){
		s.updatePosition(x * this.getDistanceScalar(), y * this.getDistanceScalar());
	}
	
	/**
	 * Create a {@link SoundSource} at the given coordinates which will be scaled by the scalar of this {@link SoundManager}
	 * 
	 * @param x The new x coordinate in game coordinates
	 * @param y The new y coordinate in game coordinates
	 * @return The source
	 */
	public SoundSource createSource(double x, double y){
		SoundSource s = new SoundSource(x, y);
		this.updateSourcePos(s, x, y);
		return s;
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
	
	/** @return See {@link #distanceScalar} */
	public double getDistanceScalar(){
		return this.distanceScalar;
	}
	
	/** @param distanceScalar See {@link #distanceScalar} */
	public void setDistanceScalar(double distanceScalar){
		this.distanceScalar = distanceScalar;
	}
	
}
