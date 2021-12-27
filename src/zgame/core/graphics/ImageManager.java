package zgame.core.graphics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zgame.core.utils.AssetUtils;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/**
 * A class used to keep track of and use images for a {@link Renderer}
 */
public class ImageManager{
	
	/** A map containing all images handled by this {@link ImageManager} */
	private Map<String, GameImage> images;
	
	public ImageManager(){
		this.images = new HashMap<String, GameImage>();
	}
	
	/** Free any resources used by this {@link ImageManager} */
	public void end(){
		for(GameImage i : this.images.values()) i.delete();
	}
	
	/**
	 * Add the given image to the manager
	 * 
	 * @param img The image to add
	 * @param name The name of the image, also use this name to get the image
	 */
	public void addImage(GameImage img, String name){
		this.images.put(name, img);
	}
	
	/**
	 * Add the image of the given name to the manager. This method assumes the name corresponds to a .png image in ZFilePaths.IMAGES
	 * 
	 * @param name The name of the image, also use this name to get the image
	 */
	public void addImage(String name){
		this.addImage(GameImage.create(ZStringUtils.concat(name, ".png")), name);
	}
	
	/**
	 * @param name The name of the image to get
	 * @return The image, or null if the image does not exist
	 */
	public GameImage getImage(String name){
		return this.images.get(name);
	}

	/**
	 * Load all the images in {@link ZFilePaths#IMAGES}, where the name of the file without a file extension is how they will be referred to using {@link #getImage(String)}
	 */
	public void addAllImages(){
		List<String> names = AssetUtils.getFileNames(ZStringUtils.concat("/", ZFilePaths.IMAGES), false);
		for(String s : names) this.addImage(s);
	}
	
}
