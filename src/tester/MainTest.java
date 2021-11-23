package tester;

import zgame.GameWindow;
import zgame.graphics.GameImage;
import zgame.graphics.Renderer;
import zgame.graphics.camera.GameCamera;
import zgame.input.keyboard.ZKeyInput;
import zgame.input.mouse.ZMouseInput;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Rectangle;

/**
 * A simple main class used for testing the game code
 * WASD = move camera
 * hold minus = zoom only on the x axis
 * hold plus = zoom only on the y axis
 * mouse wheel = zoom based on mouse position
 * right click and hold = pan camera
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
	public static boolean zoomOnlyX = false;
	public static boolean zoomOnlyY = false;
	
	public static GameImage playerImage;
	public static double playerX = 200;
	public static double playerY = 500;
	public static double speed = 200;
	
	public static double red = 0;
	public static boolean redReverse = false;
	
	public static Rectangle changeRect = new Rectangle(600, 20, 200, 200);
	public static double changeR = 0;
	public static double changeG = 0;
	public static double changeB = 0;
	
	public static boolean[] down = new boolean[]{false, false, false, false, false};
	public static int R = 0;
	public static int ONE = 1;
	public static int TWO = 2;
	public static int THREE = 3;
	public static int FOUR = 4;
	
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
		// Get values for updating things
		ZMouseInput mouse = window.getMouseInput();
		ZKeyInput keys = window.getKeyInput();
		GameCamera cam = window.getCamera();
		double msx = window.mouseSX();
		double msy = window.mouseSY();
		double mgx = window.mouseGX();
		double mgy = window.mouseGY();
		
		// Camera actions via keys
		zoomOnlyX = keys.pressed(GLFW_KEY_MINUS);
		zoomOnlyY = keys.pressed(GLFW_KEY_EQUAL);
		double shiftXState = 0;
		double shiftYState = 0;
		if(keys.pressed(GLFW_KEY_A)) shiftXState = -1;
		if(keys.pressed(GLFW_KEY_D)) shiftXState = 1;
		if(keys.pressed(GLFW_KEY_W)) shiftYState = -1;
		if(keys.pressed(GLFW_KEY_S)) shiftYState = 1;
		cam.shift(camSpeed * shiftXState * dt, camSpeed * shiftYState * dt);
		
		// Misc actions via keys
		if(keys.released(GLFW_KEY_1)){
			if(down[ONE]){
				down[ONE] = false;
				window.toggleFullscreen();
			}
		}
		else down[ONE] = true;
		if(keys.released(GLFW_KEY_2)){
			if(down[TWO]){
				down[TWO] = false;
				window.setUseVsync(!window.usesVsync());
			}
		}
		else down[TWO] = true;
		if(keys.released(GLFW_KEY_3)){
			if(down[THREE]){
				down[THREE] = false;
				window.setStretchToFill(!window.isStretchToFill());
			}
		}
		else down[THREE] = true;
		if(keys.released(GLFW_KEY_4)){
			if(down[FOUR]){
				down[FOUR] = false;
				window.setPrintFps(!window.isPrintFps());
				window.setPrintTps(!window.isPrintTps());
			}
		}
		else down[FOUR] = true;
		if(keys.pressed(GLFW_KEY_R)) window.getCamera().reset();

		// Move the player with keys
		double hMoveState = 0;
		double vMoveState = 0;
		if(keys.pressed(GLFW_KEY_LEFT)) hMoveState = -1;
		if(keys.pressed(GLFW_KEY_RIGHT)) hMoveState = 1;
		if(keys.pressed(GLFW_KEY_UP)) vMoveState = -1;
		if(keys.pressed(GLFW_KEY_DOWN)) vMoveState = 1;
		playerX += speed * hMoveState * dt;
		playerY += speed * vMoveState * dt;
		
		// Camera movement via mouse
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
		// Change the color of a rectangle based on which moust buttons and modifier buttons are pressed
		if(changeRect.contains(mgx, mgy)){
			if(mouse.leftDown() && keys.shift()) changeR = (changeR + dt * 0.5) % 1;
			if(mouse.middleDown() && keys.alt()) changeG = (changeG + dt * 0.5) % 1;
			if(mouse.rightDown() && keys.ctrl()) changeB = (changeB + dt * 0.5) % 1;
		}
		// Change the color of a rectangle over time
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
	}
	
}
