package zgame.core.utils;

/**
 * A class containing constants which specify the locations of files
 */
public final class ZFilePaths{
	
	/** The locaiton of the main assets folder */
	public static final String ASSETS = "assets/";
	
	/** The location of the main folder containing shaders */
	public static final String SHADERS = ASSETS +
		"shaders/";
	
	/** The location of the main folder containing images */
	public static final String IMAGES = ASSETS +
		"images/";
	
	/** Cannot instantiate ZFilePaths */
	private ZFilePaths(){
	}
}
