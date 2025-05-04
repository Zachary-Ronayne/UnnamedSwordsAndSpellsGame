package zgametest;

import zgame.core.Game;
import zgame.core.window.GameWindow;

import static org.lwjgl.glfw.GLFW.*;

/** A minimal implementation of {@link GameWindow} used for testing */
public class TestGame extends Game{
	public TestGame(){
		super();
	}
	
	@Override
	public void init(){
		super.start();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
	}
}