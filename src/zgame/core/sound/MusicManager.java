package zgame.core.sound;

import zgame.core.Game;
import zgame.core.asset.AssetManager;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;

/** A class used by {@link SoundManager} to manage music */
public class MusicManager extends AssetManager<MusicSound>{
	
	/** The singleton instance which manages music loading */
	private static MusicManager instance;
	
	/** Create a new empty {@link MusicManager} */
	private MusicManager(){
		super(ZFilePaths.MUSIC, "ogg");
	}
	
	@Override
	public MusicSound create(String path){
		return MusicSound.loadMusic(path);
	}
	
	
	/** @return The singleton instance for music management */
	public static MusicManager instance(){
		if(instance == null){
			ZConfig.error("Failed to get MusicManager instance, call MusicManager.init() or Game.initAssetManagers() before using music");
		}
		
		return instance;
	}
	
	/** Initialize the singleton for {@link MusicManager}, must be called before any music is used */
	public static void init(){
		if(instance != null) return;
		instance = new MusicManager();
	}
	
	/** Destroy all resources used by the manager, generally should only be called by {@link Game} on shut down */
	public static void destroyMusic(){
		if(instance != null) instance.destroy();
		instance = null;
	}
}
