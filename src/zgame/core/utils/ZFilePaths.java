package zgame.core.utils;

/**
 * A class containing constants which specify the locations of files
 */
public final class ZFilePaths{
	
	// TODO potentially move the core game to a separate intellij project that Zusass depends on, to have fully separate jars
	
	/** The default place to load assets from */
	private static final String ASSETS_ROOT_DEFAULT = "assets";
	
	/** The path from an assets folder to shaders */
	private static final String SHADERS = "shaders/";
	
	/** The path from an assets folder to images */
	private static final String IMAGES = "images/";
	
	/** The path from an assets folder to the root sounds folder */
	private static final String SOUNDS = "sounds/";
	
	/** The path from the sounds folder to the effects folder */
	private static final String EFFECTS = "effects/";
	
	/** The path from the sounds folder to the music folder */
	private static final String MUSIC = "music/";
	
	/** The path from an assets folder to the fonts folder */
	private static final String FONTS = "fonts/";
	
	/** The root location of all assets to load from, can be modified before loading assets to a custom location */
	private static String assetsRoot = ASSETS_ROOT_DEFAULT;
	
	/** @return See {@link #assetsRoot} */
	public static String assets(){
		return assetsRoot;
	}
	
	/** @param assets See {@link #assetsRoot} */
	public static void setAssets(String assets){
		assetsRoot = assets;
	}
	
	/** The location of the assets core to the game engine */
	public static final String ASSETS_CORE = "assetsCore/";
	
	/** The location of the main folder containing shaders for the game engine */
	public static final String SHADERS_CORE = ASSETS_CORE + SHADERS;
	
	/** The location of the main folder containing fonts for the game engine */
	public static final String FONTS_CORE = ASSETS_CORE + FONTS;
	
	/**
	 * @param path A file path to move from {@link #assetsRoot}
	 * @return The new path
	 */
	public static String fromAssetFolder(String path){
		return assets() + "/" + path;
	}
	
	/** @return The location of the folder for custom shaders */
	public static String shaders(){
		return fromAssetFolder(SHADERS);
	}
	
	/** @return The location of the folder containing images */
	public static String images(){
		return fromAssetFolder(IMAGES);
	}
	
	/** @return The location of the root folder containing sounds */
	public static String sounds(){
		return fromAssetFolder(SOUNDS);
	}
	
	/** @return The location of the folder containing effects */
	public static String effects(){
		return sounds() + EFFECTS;
	}
	
	/** @return The location of the folder containing music */
	public static String music(){
		return sounds() + MUSIC;
	}
	
	/** @return The location of the folder containing fonts */
	public static String fonts(){
		return fromAssetFolder(FONTS);
	}
	
	/** Cannot instantiate ZFilePaths */
	private ZFilePaths(){
	}
}
