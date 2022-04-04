package zgame.core.graphics.image;

import zgame.core.asset.AssetManager;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZFilePaths;

/** A class used to keep track of and use images for a {@link Renderer} */
public class ImageManager extends AssetManager<GameImage>{
	
	/** Create a new empty {@link ImageManager} */
	public ImageManager(){
		super(ZFilePaths.IMAGES, "png");
	}
	
	@Override
	public GameImage create(String path){
		return GameImage.create(path);
	}
	
}
