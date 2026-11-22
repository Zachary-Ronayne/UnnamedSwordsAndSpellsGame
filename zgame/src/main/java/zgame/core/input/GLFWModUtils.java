package zgame.core.input;

import static org.lwjgl.glfw.GLFW.*;

/** A class containing utility methods for determining which bits are set in the mods value of GLFW callback functions */
public final class GLFWModUtils{
	
	/**
	 * Determine if the given mods value from a GLFW function has the shift bit set
	 *
	 * @param mods The value to check
	 * @return true if the shift bit is set, false otherwise
	 */
	public static boolean isShift(int mods){
		return bitSet(mods, GLFW_MOD_SHIFT);
	}
	
	/**
	 * Determine if the given mods value from a GLFW function has the ctrl bit set
	 *
	 * @param mods The value to check
	 * @return true if the ctrl bit is set, false otherwise
	 */
	public static boolean isCtrl(int mods){
		return bitSet(mods, GLFW_MOD_CONTROL);
	}
	
	/**
	 * Determine if the given mods value from a GLFW function has the alt bit set
	 *
	 * @param mods The value to check
	 * @return true if the alt bit is set, false otherwise
	 */
	public static boolean isAlt(int mods){
		return bitSet(mods, GLFW_MOD_ALT);
	}
	
	/**
	 * Determine if the given mods value from a GLFW function has the given bit set
	 *
	 * @param mods The value to check
	 * @param bit The bit to check if it is set
	 * @return true if the shift bit is set, false otherwise
	 */
	public static boolean bitSet(int mods, int bit){
		return (mods & bit) != 0;
	}
	
	/** Cannot instantiate {@link GLFWModUtils} */
	private GLFWModUtils(){
	}
	
}
