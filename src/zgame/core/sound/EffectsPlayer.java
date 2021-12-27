package zgame.core.sound;

import static org.lwjgl.openal.AL11.*;

/**
 * A {@link SoundPlayer} designed around playing short sound effects
 */
public class EffectsPlayer extends SoundPlayer<EffectSound>{

	/** Create an empty {@link EffectsPlayer} with no currently playing sounds */
	public EffectsPlayer(){
		super();
	}

	@Override
	protected void runSound(SoundSource source, EffectSound sound){
		int sourceID = source.getId();
		alSourcei(sourceID, AL_BUFFER, sound.getId());
		alSourcePlay(sourceID);
	}

	@Override
	public void runUpdate(){
		this.removeFinishedSounds();
	}
	
}
