package zgametest;

import zgame.Game;
import zgame.window.GameWindow;

import static org.lwjgl.glfw.GLFW.*;

/** A minimal implementation of {@link GameWindow} used for testing */
public class TestGame extends Game{
	public TestGame(){
		super("", 400, 150, 200, 100, 10, true, false, false, false, 10, false);
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
	}
}