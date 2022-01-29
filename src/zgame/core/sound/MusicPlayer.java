package zgame.core.sound;

import static org.lwjgl.openal.AL11.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * A {@link SoundPlayer} designed around playing music, i.e. long audio files
 */
public class MusicPlayer extends SoundPlayer<MusicSound>{
	
	/** The {@link SoundSource} and {@link MusicSound} currently being played by this {@link MusicPlayer}, can be null if no sound is playing */
	private SoundPair<MusicSound> currentSong;
	
	/** true to play the currently playing music again after it ends, false otherwise */
	private boolean loop;
	
	/** Create an empty {@link MusicPlayer} with no currently playing sounds */
	public MusicPlayer(){
		this(false);
	}
	
	/**
	 * Create an empty {@link MusicPlayer} with no currently playing sounds
	 * 
	 * @param loop See {@link #loop}
	 */
	public MusicPlayer(boolean loop){
		super();
		this.loop = loop;
		this.currentSong = null;
	}
	
	@Override
	protected void runSound(SoundSource source, MusicSound sound){
		this.currentSong = new SoundPair<MusicSound>(source, sound);
		int sourceID = source.getId();
		
		// Set the source to play relative to the listener
		alSourcei(sourceID, AL_SOURCE_RELATIVE, AL_TRUE);
		
		// Remove all buffers and bring the sound to the beginning
		alSourcei(sourceID, AL_BUFFER, 0);
		alSourceRewind(sourceID);
		sound.reset();
		
		// Queue all buffers
		sound.bufferData();
		
		// Play the actual sound
		alSourceQueueBuffers(sourceID, sound.getIds());
		alSourcePlay(sourceID);
	}
	
	@Override
	public void runUpdate(){
		// Update the buffers
		if(this.currentSong != null){
			int sourceID = this.currentSong.getSource().getId();
			int processed = alGetSourcei(sourceID, AL_BUFFERS_PROCESSED);
			int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
			
			// Unqueue all finished buffers and load the next set of data
			while(processed > 0){
				IntBuffer idBuff = BufferUtils.createIntBuffer(1);
				alSourceUnqueueBuffers(sourceID, idBuff);
				processed--;
				int id = idBuff.get(0);
				this.currentSong.getSound().bufferDataChunk(id);
				alSourceQueueBuffers(sourceID, id);
			}
			// Loop or stop the music if the sound has ended
			if(this.isLoop()){
				if(state == AL_STOPPED){
					MusicSound sound = this.currentSong.getSound();
					this.removeFinishedSounds();
					this.playSound(this.currentSong.getSource(), sound);
				}
			}
			else{
				if(this.removeFinishedSounds()) this.currentSong = null;
			}
		}
	}
	
	/** @return See {@link #loop} */
	public boolean isLoop(){
		return this.loop;
	}
	
	/** @param loop See {@link #loop} */
	public void setLoop(boolean loop){
		this.loop = loop;
	}
	
	/** If the music is currently looping, stop it looping, otherwise start looping. See {@link #loop} */
	public void toggleLooping(){
		this.setLoop(!this.isLoop());
	}
	
}
