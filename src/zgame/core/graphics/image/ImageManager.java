package zgame.core.graphics.image;

import zgame.core.Game;
import zgame.core.asset.AssetManager;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;

/** A class used to keep track of and use images for a {@link Renderer} */
public final class ImageManager extends AssetManager<GameImage>{
	
	/** The singleton instance which manages image loading */
	private static ImageManager instance;
	
	/** Create a new empty {@link ImageManager} */
	private ImageManager(){
		super(ZFilePaths.IMAGES, "png");
	}
	
	@Override
	public GameImage create(String path){
		return GameImage.create(path);
	}
	
	/**
	 * See {@link #get(String)}
	 * @param name The name of the image to get
	 * @return The image, or null if none was loaded with the given name
	 */
	public static GameImage image(String name){
		return instance().get(name);
	}
	
	/** @return The singleton instance for image management */
	public static ImageManager instance(){
		if(instance == null){
			ZConfig.error("Failed to get ImageManager instance, call ImageManager.init() or Game.initAssetManagers() before using images");
		}
		
		return instance;
	}
	
	/** Initialize the singleton for {@link ImageManager}, must be called before any images are used */
	public static void init(){
		if(instance != null) return;
		instance = new ImageManager();
	}
	
	/** Destroy all resources used by the manager, generally should only be called by {@link Game} on shut down */
	public static void destroyImages(){
		if(instance != null) instance.destroy();
		instance = null;
	}
	
}
