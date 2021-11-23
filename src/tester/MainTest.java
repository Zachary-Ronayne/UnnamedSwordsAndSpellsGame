package tester;

import zgame.GameWindow;
import zgame.graphics.GameImage;
import zgame.graphics.Renderer;
import zgame.graphics.camera.GameCamera;
import zgame.input.mouse.ZMouseInput;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Rectangle;

/**
 * A simple main class used for testing the game code
 * WASD = move camera
 * hold minus = zoom only on the x axis
 * hold plus = zoom only on the y axis
 * mouse wheel = zoom based on mouse position
 * r = reset camera
 * arrow keys = move player
 * Click with mouse buttons while hovering the square and holding shift/alt/ctrl to change the square color
 * 1 = toggle full screen
 * 2 = toggle vsync (i.e. either match monitor refresh rate, or unlimited FPS)
 * 3 = toggle strech to fill
 * 4 = toggle printing FPS/TPS
 */
public class MainTest extends GameWindow{
	
	public static MainTest window;
	
	public static double camSpeed = 400;
	public static double camZoom = 2;
	public static double shiftXState = 0;
	public static double shiftYState = 0;
	public static boolean zoomOnlyX = false;
	public static boolean zoomOnlyY = false;
	
	public static GameImage playerImage;
	public static double playerX = 200;
	public static double playerY = 500;
	public static double speed = 200;
	public static double hMoveState = 0;
	public static double vMoveState = 0;
	
	public static double red = 0;
	public static boolean redReverse = false;
	
	public static Rectangle changeRect = new Rectangle(600, 20, 200, 200);
	public static double changeR = 0;
	public static double changeG = 0;
	public static double changeB = 0;
	
	public MainTest(){
		super("test", 1500, 720, 1000, 700, 0, true, false, false, true, 100, true);
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
			case GLFW_KEY_MINUS:
				zoomOnlyX = action != 0;
				break;
			case GLFW_KEY_EQUAL:
				zoomOnlyY = action != 0;
				break;
			case GLFW_KEY_A:
				shiftXState = -speedChange;
				break;
			case GLFW_KEY_D:
				shiftXState = speedChange;
				break;
			case GLFW_KEY_W:
				shiftYState = -speedChange;
				break;
			case GLFW_KEY_S:
				shiftYState = speedChange;
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
				window.setPrintTps(!window.isPrintTps());
				break;
			case GLFW_KEY_R:
				window.getCamera().reset();
				break;
		}
	}
	
	@Override
	protected void renderBackground(Renderer r){
		r.setColor(.1, .1, .1);
		r.fill();
	}
	
	@Override
	protected void render(Renderer r){
		r.setColor(.2, .2, .2);
		r.drawRectangle(0, 0, 1000, 700);
		
		r.setColor(changeR, changeG, changeB);
		r.drawRectangle(changeRect.x, changeRect.y, changeRect.width, changeRect.height);
		
		r.setColor(red, 0, 0);
		r.drawRectangle(100, 50, 400, 100);
		
		r.drawImage(playerX, playerY, 150, 150, playerImage);
		
		r.setColor(0, 0, 1, 0.5);
		r.drawRectangle(140, 70, 90, 400);
	}
	
	@Override
	protected void renderHud(Renderer r){
		r.setColor(0, 1, 0, .5);
		r.drawRectangle(5, 5, 30, 30);
	}
	
	@Override
	protected void tick(double dt){
		ZMouseInput mouse = window.getMouseInput();
		GameCamera cam = window.getCamera();
		double msx = window.mouseSX();
		double msy = window.mouseSY();
		double mgx = window.mouseGX();
		double mgy = window.mouseGY();
		
		cam.shift(camSpeed * shiftXState * dt, camSpeed * shiftYState * dt);
		
		if(mouse.rightDown()){
			if(!cam.isAnchored()) cam.setAnchor(msx, msy);
			else cam.pan(msx, msy);
		}
		else cam.releaseAnchor();
		
		double scroll = mouse.useScrollAmount() * 10;
		if(scroll != 0){
			boolean both = !zoomOnlyX && !zoomOnlyY;
			boolean x = zoomOnlyX || both;
			boolean y = zoomOnlyY || both;
			if(x) window.zoomX(scroll * dt, msx);
			if(y) window.zoomY(scroll * dt, msy);
		}

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
		
		if(changeRect.contains(mgx, mgy)){
			if(mouse.leftDown() && mouse.buttonEvent(GLFW_MOUSE_BUTTON_LEFT).isShiftDown()) changeR = (changeR + dt * 0.5) % 1;
			if(mouse.middleDown() && mouse.buttonEvent(GLFW_MOUSE_BUTTON_MIDDLE).isAltDown()) changeG = (changeG + dt * 0.5) % 1;
			if(mouse.rightDown() && mouse.buttonEvent(GLFW_MOUSE_BUTTON_RIGHT).isCtrlDown()) changeB = (changeB + dt * 0.5) % 1;
		}
	}
	
}
