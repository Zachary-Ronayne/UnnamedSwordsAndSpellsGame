package zgame.core.input.mouse;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.window.GameWindow;

public class GLFWMouseInput extends ZMouseInput{
	
	/**
	 * Initialize a mouse input to a default state
	 * 
	 * @param window The {@link GameWindow} which uses this input object
	 */
	public GLFWMouseInput(GameWindow window){
		super(window);
	}
	
	/** @return true if the left mouse button is down, false otherwise */
	@Override
	public boolean leftDown(){
		return this.buttonDown(GLFW_MOUSE_BUTTON_LEFT);
	}
	
	/** @return true if the right mouse button is down, false otherwise */
	@Override
	public boolean rightDown(){
		return this.buttonDown(GLFW_MOUSE_BUTTON_RIGHT);
	}
	
	/** @return true if the middle mouse button is down, false otherwise */
	@Override
	public boolean middleDown(){
		return this.buttonDown(GLFW_MOUSE_BUTTON_MIDDLE);
	}
}
