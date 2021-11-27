package zgame.input.keyboard;

import zgame.Game;
import zgame.input.ZButtonInput;

public class ZKeyInput extends ZButtonInput<ZKeyEvent>{

	/** true if a shift key is down, false otherwise */
	private boolean shiftDown;
	/** true if a alt key is down, false otherwise */
	private boolean altDown;
	/** true if a ctrl alt is down, false otherwise */
	private boolean ctrlDown;

	/**
	 * Create a default {@link ZKeyInput} object
	 * 
	 * @param game The game which uses this input object
	 */
	public ZKeyInput(Game game){
		super(game);
		this.shiftDown = false;
		this.altDown = false;
		this.ctrlDown = false;
	}
	
	/**
	 * The method called when a key is pressed
	 * 
	 * @param id The id of the GLFW window used
	 * @param key The id of the key pressed
	 * @param scanCode The system specific scancode of the key
	 * @param action If the button was released, pressed, or held
	 * @param mods The modifiers held during the key press, i.e. shift, alt, ctrl
	 */
	public void keyPress(int key, int scanCode, int action, int mods){
		this.buttonPress(key, action, mods);
		this.shiftDown = ZButtonInput.isShift(mods);
		this.altDown = ZButtonInput.isAlt(mods);
		this.ctrlDown = ZButtonInput.isCtrl(mods);
	}

	/**
	 * Equivalent to calling {@link #buttonDown(int)}, renammed for clarity. 
	 * Checks if a key is pressed
	 * 
	 * @param key The key to check
	 * @return true if the key was pressed, false otherwise
	 */
	public boolean pressed(int key){
		return this.buttonDown(key);
	}

	/**
	 * Equivalent to calling {@link #buttonUp(int)}, renammed for clarity.
	 * Checks if a key is released
	 * 
	 * @param key The key to check
	 * @return true if the key was released, false otherwise
	 */
	public boolean released(int key){
		return this.buttonUp(key);
	}
	
	/** @return See {@link #shiftDown} */
	public boolean shift(){
		return this.shiftDown;
	}

	/** @return See {@link #altDown} */
	public boolean alt(){
		return this.altDown;
	}

	/** @return See {@link #ctrlDown} */
	public boolean ctrl(){
		return this.ctrlDown;
	}

	@Override
	public ZKeyEvent createEvent(int button, boolean shift, boolean alt, boolean ctrl, boolean press){
		return new ZKeyEvent(button, shift, alt, ctrl, press);
	}
	
}
