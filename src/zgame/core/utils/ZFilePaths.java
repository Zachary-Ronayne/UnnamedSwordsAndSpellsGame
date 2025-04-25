package zgame.core.utils;

/**
 * A class containing constants which specify the locations of files
 */
public final class ZFilePaths{
	
	// TODO make the root assets folder configurable
	
	// TODO allow the required assets, like the shaders and the default font, to always be pulled from the default location
	
	/** The location of the main assets folder */
	public static final String ASSETS = "assets/";
	
	/** The location of the main folder containing shaders */
	public static final String SHADERS = ASSETS + "shaders/";
	
	/** The location of the main folder containing images */
	public static final String IMAGES = ASSETS + "images/";
	
	/** The location of the main folder containing sound */
	public static final String SOUND = ASSETS + "sounds/";
	
	/** The location of the main folder containing music */
	public static final String MUSIC = SOUND + "music/";
	
	/** The location of the main folder containing sound effects */
	public static final String EFFECTS = SOUND + "effects/";
	
	/** The location of the main folder containing fonts */
	public static final String FONTS = ASSETS + "fonts/";
	
	/** Cannot instantiate ZFilePaths */
	private ZFilePaths(){
	}
}
