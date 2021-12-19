package zgame.core.sound;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC11.*;

import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryUtil;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;

import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.IntBuffer;

/**
 * A class that represents a single device which can produce sound, i.e. a speaker, headphones, audio interface, but not a microphone
 */
public class SpeakerDevice{
	
	/** The id used by OpenAL to respresnt this {@link SpeakerDevice} */
	private long id;
	
	/** A vlue used by OpenAL to refer to the context of this {@link SpeakerDevice} */
	private long context;
	
	/** The human readble name associated with this {@link SpeakerDevice} */
	private String name;
	
	/** The {@link ALCapabilities} used by this device. Can be null if none have been set yet */
	private ALCapabilities alCapabilities;
	
	/** The {@link ALCCapabilities} used by this device. Can be null if none have been set yet */
	private ALCCapabilities alcCapabilities;
	
	/**
	 * Create a new SpeakerDevice based on the given name
	 * 
	 * @param name The name of the device, this should not be be a made up name, this is the actual name of the device on the machine
	 */
	public SpeakerDevice(String name){
		this.name = name;
		
		// Find the id
		this.id = alcOpenDevice(this.getName());
		
		// Error check
		if(this.id == NULL){
			if(ZConfig.printErrors()) ZStringUtils.prints("Failed to load audio device with name:", name);
			return;
		}
		
		// Print success
		if(ZConfig.printSuccess()) ZStringUtils.prints("Successfully loaded audio device with name:", name);
	}

	/** Use this device for audio */
	public void use(){
		this.alcCapabilities = ALC.createCapabilities(this.getId());
		this.context = alcCreateContext(this.getId(), (IntBuffer)null);
		boolean result = alcMakeContextCurrent(this.getContext());
		this.alCapabilities = AL.createCapabilities(this.getAlcCapabilities());

		if(result && ZConfig.printSuccess()) ZStringUtils.print("Successfully made context current with device name '", this.getName(), "' using context: ", this.getContext());
		else if(!result && ZConfig.printErrors()) ZStringUtils.print("Failed to make context current with device name '", this.getName(), "' using context: ", this.getContext());
	}
	
	/** Free any resources used by this SpeakerDevice */
	public void end(){
		alcMakeContextCurrent(MemoryUtil.NULL);
		if(this.getContext() != NULL) alcDestroyContext(this.getContext());
		alcCloseDevice(this.getId());
	}
	
	/** @return See {@link #id} */
	public long getId(){
		return this.id;
	}
	
	/** @return See {@link #context} */
	public long getContext(){
		return this.context;
	}
	
	/** @return See {@link #name} */
	public String getName(){
		return this.name;
	}
	
	/** @return See {@link #alCapabilities} */
	public ALCapabilities getAlCapabilities(){
		return this.alCapabilities;
	}
	
	/** @return See {@link #alcCapabilities} */
	public ALCCapabilities getAlcCapabilities(){
		return this.alcCapabilities;
	}
	
}
