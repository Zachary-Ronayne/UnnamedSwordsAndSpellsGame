package zgame.input.keyboard;

import zgame.input.ZButtonInput;
import zgame.window.GameWindow;

public abstract class ZKeyInput extends ZButtonInput<ZKeyEvent>{

	/** true if a shift key is down, false otherwise */
	private boolean shiftDown;
	/** true if a alt key is down, false otherwise */
	private boolean altDown;
	/** true if a ctrl alt is down, false otherwise */
	private boolean ctrlDown;

	/**
	 * Create a default {@link ZKeyInput} object
	 * 
	 * @param window The {@link GameWindow} which uses this input object
	 */
	public ZKeyInput(GameWindow window){
		super(window);
		this.shiftDown = false;
		this.altDown = false;
		this.ctrlDown = false;
	}

	@Override
	public void buttonAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.buttonAction(button, press, shift, alt, ctrl);
		this.shiftDown = shift;
		this.altDown = alt;
		this.ctrlDown = ctrl;
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
