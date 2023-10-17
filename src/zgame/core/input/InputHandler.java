package zgame.core.input;

import zgame.core.Game;

/** A utility to track if a button has been pressed in the last tick */
public class InputHandler{
	
	/** The index representing the key that is pressed */
	private int inputKey;
	/** true if the button represented by {@link #inputKey} is currently pressed down, and releasing it will cause {@link #tick(Game)} to return true */
	private boolean pressed;
	/** The type of device to get input from */
	private InputType type;
	
	/**
	 * Create a new input handler
	 *
	 * @param type See {@link #type}
	 * @param inputKey See {@link #inputKey}
	 */
	public InputHandler(InputType type, int inputKey){
		this.type = type;
		this.inputKey = inputKey;
	}
	
	/**
	 * Update the state of this handler based on the input devices of the given game
	 * @param game The game
	 * @return true if this tick represents an input press, and the control should perform its action, false otherwise
	 */
	public boolean tick(Game game){
		boolean nowPressed;
		switch(this.type){
			case KEYBOARD -> nowPressed = game.getKeyInput().pressed(this.inputKey);
			case MOUSE_BUTTONS -> nowPressed = game.getMouseInput().buttonDown(this.inputKey);
			default -> {return false;}
		}
		
		if(this.pressed && !nowPressed){
			this.pressed = false;
			return true;
		}
		if(nowPressed) this.pressed = true;
		return false;
	}
	
	/** @return See {@link #type} */
	public InputType getType(){
		return this.type;
	}
	
	/** @param type See {@link #type} */
	public void setType(InputType type){
		this.type = type;
	}
	
	/** @return See {@link #inputKey} */
	public int getInputKey(){
		return this.inputKey;
	}
	
	/** @param inputKey See {@link #inputKey} */
	public void setInputKey(int inputKey){
		this.inputKey = inputKey;
	}
	
	/** @return See {@link #pressed} */
	public boolean isPressed(){
		return this.pressed;
	}
}
