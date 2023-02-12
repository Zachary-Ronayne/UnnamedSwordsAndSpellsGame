package zgame.core.utils;

import zgame.core.window.GameWindow;

/** Config values used by the game engine */
public class ZConfig{
	
	/** true if, when an error occurs in the engine, if it should print to the main System.out, false otherwise */
	private static final boolean PRINT_ERRORS = false;
	/** true if, when assets are loaded by the engine, if it should print to the main System.out, false otherwise */
	private static final boolean PRINT_SUCCESS = false;
	/** true if debug statements should print to the main System.out, false otherwise */
	private static final boolean PRINT_DEBUG = false;
	
	/**
	 * true if, during the ticks of a {@link GameWindow}, the loop should wait between each loop iteration, false otherwise. Setting this to true should generally decrease CPU
	 * load, but in the case where each tick takes longer than expected, it may be preferable that this value is set to false
	 */
	private static final boolean WAIT_BETWEEN_TICKS = true;
	
	/**
	 * The number of samples to wait to pause a sound before actually pausing it, i.e. when a sound is paused, first the volume is set to zero, then this number of samples
	 * play, then the sound is put on pause. This is to prevent an unwanted tick sound each time the sound is stopped
	 */
	private static final int SOUND_PAUSE_DELAY = 5;
	
	/** @return See {@link #PRINT_ERRORS} */
	public static boolean printErrors(){
		return PRINT_ERRORS;
	}
	
	/**
	 * Print the given objects, only if printing success is enabled. See {@link #printSuccess()} and {@link ZStringUtils#print(Object...)}
	 * @param objs The objects to print
	 */
	public static void success(Object... objs){
		if(ZConfig.printSuccess()) ZStringUtils.print(objs);
	}
	/**
	 * Print the given objects, only if printing errors is enabled. See {@link #printErrors()} and {@link ZStringUtils#print(Object...)}
	 * @param objs The objects to print
	 */
	public static void error(Object... objs){
		if(ZConfig.printErrors()) ZStringUtils.print(objs);
	}
	/**
	 * Print the stacktrace of the given error if printing errors is enabled. See {@link #printErrors()}
	 * @param e The error to log
	 */
	public static void error(Exception e){
		if(ZConfig.printErrors()) e.printStackTrace();
	}
	/**
	 * Print the given objects, and then the stacktrace of the given error if printing errors is enabled. See {@link #printErrors()}
	 * @param e The error to log
	 * @param objs The objects to print
	 */
	public static void error(Exception e, Object... objs){
		error(objs);
		error(e);
	}
	/**
	 * Print the given objects, only if printing debug statements is enabled. See {@link #printDebug()} and {@link ZStringUtils#print(Object...)}
	 * @param objs The objects to print
	 */
	public static void debug(Object... objs){
		if(ZConfig.printDebug()) ZStringUtils.print(objs);
	}
	
	/** @return See {@link #PRINT_SUCCESS} */
	public static boolean printSuccess(){
		return PRINT_SUCCESS;
	}
	
	/** @return See {@link #PRINT_DEBUG} */
	public static boolean printDebug(){
		return PRINT_DEBUG;
	}
	
	/** @return See {@link #WAIT_BETWEEN_TICKS} */
	public static boolean waitBetweenTicks(){
		return WAIT_BETWEEN_TICKS;
	}
	
	/** @return See {@link #SOUND_PAUSE_DELAY} */
	public static int soundPauseDelay(){
		return SOUND_PAUSE_DELAY;
	}
	
}
