package zgame.core.utils;

/** Config values used by the game engine */
public class ZConfig{
	
	/** true if, when an error occurs in the engine, if it should print to the main System.out, false otherwise */
	private static final boolean PRINT_ERRORS = true;
	/** true if, when assets are loaded by the engine, if it should print to the main System.out, false otherwise */
	private static final boolean PRINT_SUCCESS = true;
	
	/**
	 * true if, during the ticks of a {@link zgame.window.GameWindow}, the loop should wait between each loop iteration, false otherwise. 
	 * This should generally decrease CPU load, but in the case where each tick takes longer than expected, it may be preferable that this value is set to false
	 */
	private static final boolean WAIT_BETWEEN_TICKS = true;
	
	/**
	 * The number of samples to wait to pause a sound before actually pausing it,
	 * i.e. when a sound is paused, first the volume is set to zero, then this number of samples play, then the sound is put on pause.
	 * This is to prevent an unwanted tick sound each time the sound is stopped
	 */
	private static final int SOUND_PAUSE_DELAY = 5;
	
	/** @return See {@link #PRINT_ERRORS} */
	public static boolean printErrors(){
		return PRINT_ERRORS;
	}
	
	/** @return See {@link #PRINT_SUCCESS} */
	public static boolean printSuccess(){
		return PRINT_SUCCESS;
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
