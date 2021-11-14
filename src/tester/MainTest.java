package tester;

import zgame.GameWindow;
import zgame.graphics.Renderer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A simple main class used for testing the game code
 */
public class MainTest extends GameWindow{
	
	public static MainTest window;
	
	public MainTest(){
		super("test", 1280, 720, 1280, 720, 0, true, false, false);
	}
	
	public static void main(String[] args){
		window = new MainTest();
		window.start();
	}
	
	@Override
	public void keyPress(long id, int key, int scanCode, int action, int mods){
		if(action != 0) return;
		switch(key){
			case GLFW_KEY_1:
				window.toggleFullscreen();
				break;
			case GLFW_KEY_2:
				window.setUseVsync(!window.usesVsync());
				break;
			case GLFW_KEY_3:
				window.setStretchToFill(!window.isStretchToFill());
				break;
		}
	}
	
	@Override
	protected void render(Renderer r){
		r.setColor(.2, .2, .2);
		r.fill();
		
		r.setColor(1, 0, 0);
		r.drawRectangle(100, 50, 200, 50);
		
		r.setColor(0, 0, 1, 0.5);
		r.drawRectangle(140, 70, 45, 200);
	}
	
}
