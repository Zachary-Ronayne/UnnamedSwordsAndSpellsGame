package zgame.core.sound;

import java.util.List;

import zgame.core.Game;
import zgame.core.asset.AssetManager;
import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/** A class used by {@link SoundManager} to manage sound effects */
public class EffectsManager extends AssetManager<EffectSound>{
	
	/** The singleton instance which manages sound effect loading */
	private static EffectsManager instance;
	
	/** Create a new empty {@link EffectsManager} */
	private EffectsManager(){
		super(ZFilePaths.EFFECTS, "ogg");
	}
	
	@Override
	public EffectSound create(String path){
		return EffectSound.loadSound(path);
	}
	
	@Override
	public void addAll(){
		// First find all the folders
		List<String> folders = ZAssetUtils.getAllFolders(ZFilePaths.EFFECTS);
		
		// Now for each folder, add every effect in those folders, using the folder as the type
		for(String f : folders){
			// Get every file in the folder
			List<String> names = ZAssetUtils.getAllFiles(ZStringUtils.concat(ZFilePaths.EFFECTS, f), false);
			
			// Add each file
			for(String n : names) this.add(EffectSound.loadSound(ZStringUtils.concat(f, "/", n), f), n);
		}
	}
	
	/** @return The singleton instance for sound effect management */
	public static EffectsManager instance(){
		if(instance == null){
			ZConfig.error("Failed to get EffectsManager instance, call EffectsManager.init() or Game.initAssetManagers() before using sound effects");
		}
		
		return instance;
	}
	
	/** Initialize the singleton for {@link EffectsManager}, must be called before any sound effects are used */
	public static void init(){
		if(instance != null) return;
		instance = new EffectsManager();
	}
	
	/** Destroy all resources used by the manager, generally should only be called by {@link Game} on shut down */
	public static void destroyEffects(){
		if(instance != null) instance.destroy();
		instance = null;
	}
	
}
