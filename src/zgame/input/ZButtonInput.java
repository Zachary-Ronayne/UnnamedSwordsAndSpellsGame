package zgame.input;

import java.util.HashMap;
import java.util.Map;

import zgame.GameWindow;
import zgame.input.mouse.ZMouseEvent;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A class designed for input devices using GLFW which use buttons that can either be pressed or not pressed
 * 
 * @param B The {@link ZButtonInputEvent} which will represent events performed by this input object
 */
public abstract class ZButtonInput<B extends ZButtonInputEvent>{
	
	/** The {@link GameWindow} using this {@link ZButtonInput} */
	private GameWindow window;

	/** The {@link Map} storing the state of every button and its associated actions */
	private Map<Integer, B> buttonsDown;
	
	/** Create a simple {@link ZButtonInput} and initialize every value */
	public ZButtonInput(GameWindow window){
		this.window = window;
		this.buttonsDown = new HashMap<Integer, B>();
	}

	/** Set all buttons to be not pressed */
	public void clear(){
		this.buttonsDown.clear();
	}

	/**
	 * The method called by GLFW for a button press callback
	 * 
	 * @param window The id of the GLFW window where the button was pressed
	 * @param button The mouse button which was pressed
	 * @param action The action of the button, i.e. up or down
	 * @param mods The additional buttons pressed, i.e. shift, alt, ctrl
	 */
	public void buttonPress(int button, int action, int mods){
		boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
		boolean alt = (mods & GLFW_MOD_ALT) != 0;
		boolean ctrl = (mods & GLFW_MOD_CONTROL) != 0;
		this.buttonsDown.put(button, this.createEvent(button, shift, alt, ctrl, action != GLFW_RELEASE));
	}

	/**
	 * Create a new event caused by this {@link ZButtonInput} object
	 * 
	 * @param button The id of the button pressted
	 * @param shift true if shift was down during this event, false otherwise
	 * @param alt true if alt was down during this event, false otherwise
	 * @param ctrl true if ctrl was down during this event, false otherwise
	 * @param press true if the button was down during this event, false otherwise
	 * @return
	 */
	public abstract B createEvent(int button, boolean shift, boolean alt, boolean ctrl, boolean press);
	
	/** @return See {@link #window} */
	public GameWindow getWindow(){
		return this.window;
	}
	
	/**
	 * Get a {@link ZMouseEvent} containing information about the desired button
	 * 
	 * @param button The ID of the button
	 * @return The event, or null if no such event exists
	 */
	public B buttonEvent(int button){
		return this.buttonsDown.get(button);
	}
	
	/**
	 * Determine if a particular button is pressed down
	 * 
	 * @param button The button to check, same conditions as {@link #buttonEvent(int)}
	 * @return true if the button is pressed, false otherwise. Will also return false if button represents an invalid button
	 */
	public boolean buttonDown(int button){
		B e = this.buttonEvent(button);
		if(e == null) return false;
		return e.isPress();
	}

	/**
	 * Determine if a particular button is not pressed down
	 * 
	 * @param button The button to check, same conditions as {@link #buttonEvent(int)}
	 * @return true if the button is not pressed, false otherwise. Will also return false if button represents an invalid button
	 */
	public boolean buttonUp(int button){
		return !this.buttonDown(button);
	}

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

}
