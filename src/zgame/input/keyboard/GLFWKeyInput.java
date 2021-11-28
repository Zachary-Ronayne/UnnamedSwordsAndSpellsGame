package zgame.input.keyboard;

import zgame.window.GameWindow;

/**
 * A KeyInput class which can be used with GLFW events
 */
public class GLFWKeyInput extends ZKeyInput{

	/**
	 * Initialize a key input to a default state
	 * 
	 * @param window The {@link GameWindow} which uses this input object
	 */
	public GLFWKeyInput(GameWindow window){
		super(window);
	}
	
}
