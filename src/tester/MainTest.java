package tester;

import zgame.GameWindow;
import zgame.graphics.GameImage;
import zgame.graphics.Renderer;
import zgame.utils.ZFilePaths;
import zgame.utils.ZStringUtils;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A simple main class used for testing the game code
 */
public class MainTest extends GameWindow{
	
	public static MainTest window;
	
	public static GameImage playerImage;
	public static double playerX = 200;
	public static double playerY = 500;
	public static double speed = 10;
	
	public MainTest(){
		super("test", 1280, 720, 1280, 720, 0, true, false, false);
	}
	
	public static void main(String[] args){
		window = new MainTest();
		playerImage = GameImage.create("player.png");
		window.start();
		playerImage.delete();
	}
	
	@Override
	public void keyPress(long id, int key, int scanCode, int action, int mods){
		if(action != 0){
			switch(key){
				case GLFW_KEY_LEFT:
					playerX -= speed;
					break;
				case GLFW_KEY_RIGHT:
					playerX += speed;
					break;
				case GLFW_KEY_UP:
					playerY -= speed;
					break;
				case GLFW_KEY_DOWN:
					playerY += speed;
					break;
			}
		}
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
		r.drawRectangle(100, 50, 400, 100);
		
		r.drawImage(playerX, playerY, 150, 150, playerImage);
		
		r.setColor(0, 0, 1, 0.5);
		r.drawRectangle(140, 70, 90, 400);
	}
	
}
