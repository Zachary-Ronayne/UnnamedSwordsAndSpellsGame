package zgame.core.graphics.texture;

import zgame.core.asset.AssetManager;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;

/** An {@link zgame.core.asset.AssetManager} which handles loading mapped textures */
public class TextureMappingManager extends AssetManager<TextureMapping>{
	
	/** The singleton instance which manages mapped texture loading */
	private static TextureMappingManager instance;
	
	/**
	 * Create a new empty {@link TextureMappingManager}
	 */
	public TextureMappingManager(){
		super(ZFilePaths.textureMappings(), "json");
	}
	
	/**
	 * See {@link #get(String)}
	 * @param name The name of the image to get
	 * @return The mapped texture, or null if none was loaded with the given name
	 */
	public static TextureMapping textureMapping(String name){
		return instance().get(name);
	}
	
	/**
	 * Get the mapped texture with the given name as a {@link TextureMappingRect3DMapping}, this method assumes the mapped texture will be of the appropriate type
	 *
	 * @param name The name of the asset to get
	 * @return The mapped texture
	 */
	public static TextureMappingRect3DMapping rect3D(String name){
		return (TextureMappingRect3DMapping)textureMapping(name);
	}
	
	@Override
	public TextureMapping create(String path){
		return TextureMapping.create(path);
	}
	
	/** @return The singleton instance for managing mapped textures */
	public static TextureMappingManager instance(){
		if(instance == null){
			ZConfig.error("Failed to get TextureMappingManager instance, call TextureMappingManager.init() or Game.initAssetManagers() before using images");
		}
		
		return instance;
	}
	
	/** Initialize the singleton for {@link TextureMappingManager}, must be called before any mapped textures are used */
	public static void init(){
		if(instance != null) return;
		instance = new TextureMappingManager();
	}
}
