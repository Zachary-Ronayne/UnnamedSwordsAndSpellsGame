package zgame.core.sound;

import static org.lwjgl.openal.AL11.*;

/**
 * A {@link SoundPlayer} designed around playing music, i.e. long audio files
 */
public class MusicPlayer extends SoundPlayer{

	/** Create an empty {@link MusicPlayer} with no currently playing sounds */
	public MusicPlayer(){
		super();
	}
	
	@Override
	protected void runSound(SoundSource source, Sound sound){
		int sourceID = source.getId();
		alSourcei(sourceID, AL_BUFFER, sound.getId());
		alSourcePlay(sourceID);
	}
	
	@Override
	public void runUpdate(){
		this.removeFinishedSounds();
	}
	
}
