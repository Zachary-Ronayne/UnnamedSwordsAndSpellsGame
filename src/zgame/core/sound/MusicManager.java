package zgame.core.sound;

import zgame.core.asset.AssetManager;
import zgame.core.utils.ZFilePaths;

/** A class used by {@link SoundManager} to manage music */
public class MusicManager extends AssetManager<MusicSound>{
	
	/** Create a new empty {@link MusicManager} */
	public MusicManager(){
		super(ZFilePaths.MUSIC, "ogg");
	}

	@Override
	public MusicSound create(String path){
		return MusicSound.loadMusic(path);
	}
	
}
