package zgame.core.sound;

import java.util.List;

import zgame.core.asset.AssetManager;
import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/** A class used by {@link SoundManager} to manage sound effects */
public class EffectsManager extends AssetManager<EffectSound>{
	
	/** Create a new empty {@link EffectsManager} */
	public EffectsManager(){
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
	
}
