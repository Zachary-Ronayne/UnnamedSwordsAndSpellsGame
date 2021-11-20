package tester;

import zgame.GameWindow;
import zgame.graphics.GameImage;
import zgame.graphics.Renderer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A simple main class used for testing the game code
 */
public class MainTest extends GameWindow{
	
	public static MainTest window;
	
	public static GameImage playerImage;
	public static double playerX = 200;
	public static double playerY = 500;
	public static double speed = 200;
	public static double hMoveState = 0;
	public static double vMoveState = 0;
	
	public static double red = 0;
	public static boolean redReverse = false;
	
	public MainTest(){
		super("test", 1280, 720, 1200, 700, 0, true, false, false, true, 100, true);
	}
	
	public static void main(String[] args){
		window = new MainTest();
		playerImage = GameImage.create("player.png");
		window.center();
		window.start();
		
		playerImage.delete();
	}
	
	@Override
	public void keyPress(long id, int key, int scanCode, int action, int mods){
		double speedChange = (action != 0) ? 1 : 0;
		switch(key){
			case GLFW_KEY_LEFT:
				hMoveState = -speedChange;
				break;
			case GLFW_KEY_RIGHT:
				hMoveState = speedChange;
				break;
			case GLFW_KEY_UP:
				vMoveState = -speedChange;
				break;
			case GLFW_KEY_DOWN:
				vMoveState = speedChange;
				break;
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
			case GLFW_KEY_4:
				window.setPrintFps(!window.isPrintFps());
				break;
		}
	}
	
	@Override
	protected void render(Renderer r){
		r.setColor(.2, .2, .2);
		r.fill();
		
		r.setColor(red, 0, 0);
		r.drawRectangle(100, 50, 400, 100);
		
		r.drawImage(playerX, playerY, 150, 150, playerImage);
		
		r.setColor(0, 0, 1, 0.5);
		r.drawRectangle(140, 70, 90, 400);
	}
	
	@Override
	protected void tick(double dt){
		double redD = dt * 0.4;
		if(redReverse) red -= redD;
		else red += redD;
		if(red > 1){
			redReverse = true;
			red = 1;
		}
		else if(red < 0){
			redReverse = false;
			red = 0;
		}
		playerX += speed * hMoveState * dt;
		playerY += speed * vMoveState * dt;
	}
	
}
