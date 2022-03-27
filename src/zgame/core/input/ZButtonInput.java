package zgame.core.input;

import java.util.HashMap;
import java.util.Map;

import zgame.core.window.GameWindow;

/**
 * A class designed for input devices which use buttons that can either be pressed or not pressed
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
	 * The method called when a button has an action performed on it
	 * 
	 * @param button The mouse button which was pressed
	 * @param press true if the button was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public void buttonAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.buttonsDown.put(button, this.createEvent(button, shift, alt, ctrl, press));
	}
	
	/**
	 * Create a new event caused by this {@link ZButtonInput} object
	 * 
	 * @param button The id of the button pressed
	 * @param shift true if shift was down during this event, false otherwise
	 * @param alt true if alt was down during this event, false otherwise
	 * @param ctrl true if ctrl was down during this event, false otherwise
	 * @param press true if the button was down during this event, false otherwise
	 * @return The event
	 */
	public abstract B createEvent(int button, boolean shift, boolean alt, boolean ctrl, boolean press);
	
	/** @return See {@link #window} */
	public GameWindow getWindow(){
		return this.window;
	}
	
	/**
	 * Get a {@link ZButtonInputEvent} containing information about the desired button
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
	
}
