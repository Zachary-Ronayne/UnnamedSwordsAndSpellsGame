package zusass.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;

/** A class containing utilities for getting things like file locations of saves */
public final class ZusassConfig{
	
	/** The suffix of a file, marking it as a valid save file for the game */
	public static final String SAVE_FILE_SUFFIX = ".zusass.json";
	
	/** The location of all files associated with the Zusass game */
	private static final String DATA = "./ZUSASS_DATA/";
	/** The location of all saves associated with the Zusass game */
	private static final String SAVES = DATA + "saves/";
	
	/** The file location of the global settings for all games */
	private static final String GLOBAL_SETTINGS_PATH = DATA + "globalSettings";
	
	/** @return The location where save files should be stored */
	public static String getSavesLocation(){
		return SAVES;
	}
	
	/** @return See {@link #GLOBAL_SETTINGS_PATH} */
	public static String getGlobalSettingsPath(){
		return GLOBAL_SETTINGS_PATH;
	}
	
	/**
	 * Convert a file name to a name including the appropriate suffix for a save file
	 *
	 * @param file The name to add the suffix to
	 * @return The file with the suffix
	 */
	public static String createSaveFileSuffix(String file){
		if(!validSaveFileName(file)) file = ZStringUtils.concat(file, SAVE_FILE_SUFFIX);
		return file;
	}
	
	/**
	 * Make a path to a save file in the saves location
	 *
	 * @param name The name of the save file, no path or extension
	 * @return The path based on the name
	 */
	public static String createSaveFilePath(String name){
		return ZStringUtils.concat(getSavesLocation(), createSaveFileSuffix(name));
	}
	
	/**
	 * Determine if the given file name is valid for a save file
	 *
	 * @param file The file to check
	 * @return true if it is a valid save file name, false otherwise
	 */
	public static boolean validSaveFileName(String file){
		return file.endsWith(SAVE_FILE_SUFFIX);
	}
	
	/** @return The most recently saved valid file, or null if no valid files exist */
	public static String getMostRecentSave(){
		List<File> files = getAllFiles();
		if(files == null || files.isEmpty()) return null;
		String path = files.get(0).getPath();
		if(!validSaveFileName(path)) return null;
		return path;
	}
	
	/**
	 * Get all files in the saves location of the Zusass game
	 *
	 * @return The list of files, or null if an error was encountered
	 */
	public static List<File> getAllFiles(){
		String path = ZusassConfig.getSavesLocation();
		List<File> files = new ArrayList<>();
		try{
			// Find all files
			File file = new File(path);
			if(!file.isDirectory()) return null;
			File[] loadedFiles = file.listFiles();
			if(loadedFiles == null) return null;
			
			// Sort files by last modified
			files.addAll(Arrays.asList(loadedFiles));
			files.sort((a, b) -> Double.compare(b.lastModified(), a.lastModified()));
			
		}catch(NullPointerException | SecurityException e){
			ZConfig.error(e, "Failed to find file location for saves at:", path);
			return null;
		}
		return files;
	}
	
	/** Cannot instantiate {@link ZusassConfig} */
	public ZusassConfig(){
	}
}
