package zusass.utils;

import zgame.core.utils.ZStringUtils;

/** A class containing utilities for getting things like file locations of saves */
public final class ZUSASSConfig{
	
	/** The suffix of a file, marking it as a valid save file for the game */
	public static final String SAVE_FILE_SUFFIX = ".zusass.json";

	/** The location of all files associated with the ZUSASS game */
	private static final String DATA = "./ZUSASS_DATA/";
	/** The location of all saves associated with the ZUSASS game */
	private static final String SAVES = DATA +
		"saves/";
	
	/** @return The location where save files should be stored */
	public static String getSavesLocation(){
		return SAVES;
	}

	/**
	 * Convert a file name to a name including the appropriate suffix for a save file
	 * @param file The name to add the suffix to
	 * @return The file with the suffix
	 */
	public static String createSaveFileSuffix(String file){
		if(!validSaveFileName(file)) file = ZStringUtils.concat(file, SAVE_FILE_SUFFIX);
		return file;
	}

	/**
	 * Determine if the given file name is valid for a save file
	 * @param file The file to check
	 * @return true if it is a valid save file name, false otherwise
	 */
	public static boolean validSaveFileName(String file){
		return file.endsWith(SAVE_FILE_SUFFIX);
	}
	
	/** @return The most recently saved file path, or null if no files exist */
	public static String getMostRecentSave(){
		// TODO make this load the most recently saved file, not this hard coded one
		return createSaveFileSuffix(ZStringUtils.concat(getSavesLocation(), "zusassSave"));
	}
	
	/** Cannot instantiate {@link ZUSASSConfig} */
	public ZUSASSConfig(){
	}
}
