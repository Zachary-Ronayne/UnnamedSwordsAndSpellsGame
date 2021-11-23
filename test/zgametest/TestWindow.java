package zgametest;

import zgame.GameWindow;
import zgame.graphics.Renderer;

import static org.lwjgl.glfw.GLFW.*;

/** A minimal implementation of {@link GameWindow} used for testing */
public class TestWindow extends GameWindow{
	public TestWindow(){
		super("", 400, 150, 200, 100, 10, true, false, false, false, 10, false);
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
	}
	
	@Override
	protected void render(Renderer r){
	}
	
	@Override
	protected void tick(double dt){
	}
}