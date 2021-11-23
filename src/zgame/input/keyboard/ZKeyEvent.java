package zgame.input.keyboard;

import zgame.GameWindow;
import zgame.input.ZButtonInputEvent;

/**
 * A class that holds the information associated when a keyboard presses or releases a key
 */
public class ZKeyEvent extends ZButtonInputEvent{

	/**
	 * Create a new {@link ZKeyEvent} with the given parameters
	 * 
	 * @param id The ID of the key, using GLFW constants
	 * @param window The GameWindow where the event happened
	 * @param shiftDown See {@link #isShiftDown()}
	 * @param altDown See {@link #isAltDown()}
	 * @param ctrlDown See {@link #isCtrlDown()}
	 * @param press true if the key was pressed down during this event, false otherwise
	 */
	public ZKeyEvent(int id, GameWindow window, boolean shiftDown, boolean altDown, boolean ctrlDown, boolean press){
		super(id, window, shiftDown, altDown, ctrlDown, press);
	}
	
}
