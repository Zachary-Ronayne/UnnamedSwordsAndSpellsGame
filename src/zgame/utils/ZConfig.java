package zgame.utils;

/** Config values used by the game engine */
public class ZConfig{
	
	/** true if, when an error occurs in the engine, if it should print to the main System.out, false otherwise */
	private static final boolean PRINT_ERRORS = true;
	/** true if, when assets are loaded by the engine, if it should print to the main System.out, false otherwise */
	private static final boolean PRINT_SUCCESS = true;
	
	/** @return See {@link #PRINT_ERRORS} */
	public static boolean printErrors(){
		return PRINT_ERRORS;
	}
	
	/** @return See {@link #PRINT_SUCCESS} */
	public static boolean printSuccess(){
		return PRINT_SUCCESS;
	}
	
}
