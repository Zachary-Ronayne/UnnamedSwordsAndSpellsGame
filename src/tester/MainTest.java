package tester;

import zgame.core.Game;
import zgame.core.graphics.GameImage;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.sound.EffectsPlayer;
import zgame.core.sound.MusicPlayer;
import zgame.core.sound.SoundManager;
import zgame.core.sound.SoundSource;
import zgame.core.window.GameWindow;

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
 * shift + alt + ctrl + middle click = randomize square color
 * shift + middle click + move mouse = move around square
 * shift + scroll wheel = grow/shrink square
 * 1 = toggle full screen
 * 2 = toggle vsync (i.e. either match monitor refresh rate, or unlimited FPS)
 * 3 = toggle strech to fill
 * 4 = toggle printing FPS/TPS
 * shift + z = change green of flickering red square
 * shift + x = change blue of flickering red square
 * g = play win sound effect
 * h = play lose sound effect
 * m = play music
 * n = scan for sound devices
 * p = toggle effects paused
 * o = toggle effects muted
 * shift + p = toggle music paused
 * shift + o = toggle music muted
 * Indicators in the upper left hand corner for muted/paused: black = neither, red = muted, blue = paused, magenta = both muted and paused
 * The left indicator is effects, the right indicator is music
 */
public class MainTest extends Game{
	
	public static Game game;
	public static GameWindow window;
	
	public static double camSpeed = 400;
	public static double camZoom = 2;
	public static boolean zoomOnlyX = false;
	public static boolean zoomOnlyY = false;
	
	public static GameImage playerImage;
	public static double playerX;
	public static double playerY;
	public static double speed = 200;
	
	public static double red = 0;
	public static double green = 0;
	public static double blue = 0;
	public static boolean redReverse = false;
	
	public static Rectangle changeRect;
	public static double changeR = 0;
	public static double changeG = 0;
	public static double changeB = 0;
	
	public static boolean[] down = new boolean[]{false, false, false, false, false};
	public static int R = 0;
	public static int ONE = 1;
	public static int TWO = 2;
	public static int THREE = 3;
	public static int FOUR = 4;

	public static SoundSource winSource;
	public static SoundSource loseSource;

	public MainTest(){
		super("test", 1500, 720, 1000, 700, 0, true, false, false, true, 100, true);
	}
	
	public static void main(String[] args){
		// Set up game
		game = new MainTest();
		window = game.getWindow();
		window.center();
		
		// Load files
		playerImage = GameImage.create("player.png");
		SoundManager sm = game.getSounds();
		sm.addEffect("win");
		sm.addEffect("lose");
		sm.addMusic("song");

		// Start up the game
		reset();
		game.start();
		
		// Close sound sources
		winSource.end();
		loseSource.end();

		// Delete external files
		playerImage.delete();
	}

	public static void reset(){
		playerX = 200;
		playerY = 500;
		changeRect = new Rectangle(600, 20, 200, 200);
		game.getCamera().reset();

		if(winSource != null) winSource.end();
		if(loseSource != null) loseSource.end();
		winSource = new SoundSource(playerX, playerY);
		// loseSource = new SoundSource(0, 200);
		loseSource = new SoundSource(playerX, playerY);
	}

	@Override
	protected void keyAction(int key, boolean press, boolean shift, boolean alt, boolean ctrl){
		ZKeyInput keys = game.getKeyInput();
		if(keys.shift()){
			if(key == GLFW_KEY_Z) green = (green + 0.05) % 1;
			if(key == GLFW_KEY_X) blue = (blue + 0.05) % 1;
		}

		if(!press){
			SoundManager s = game.getSounds();
			if(key == GLFW_KEY_G) s.playEffect(winSource, "win");
			else if(key == GLFW_KEY_H) s.playEffect(loseSource, "lose");
			else if(key == GLFW_KEY_M) s.playMusic("song");
			else if(key == GLFW_KEY_N) s.scanDevices();
			else if(key == GLFW_KEY_P){
				if(shift) s.getMusicPlayer().togglePaused();
				else s.getEffectsPlayer().togglePaused();
			}
			else if(key == GLFW_KEY_O){
				if(shift) s.getMusicPlayer().toggleMuted();
				else s.getEffectsPlayer().toggleMuted();
			}
		}
	}
	
	@Override
	protected void mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(shift && alt && ctrl && !press){
			changeR = Math.random();
			changeG = Math.random();
			changeB = Math.random();
		}
	}

	@Override
	protected void mouseMove(double x, double y){
		ZKeyInput keys = game.getKeyInput();
		ZMouseInput mouse = game.getMouseInput();
		if(keys.shift() && mouse.middleDown()){
			changeRect.x += x - mouse.lastX();
			changeRect.y += y - mouse.lastY();
		}
	}

	@Override
	protected void mouseWheelMove(double amount){
		ZKeyInput keys = game.getKeyInput();
		if(keys.shift()){
			double size = changeRect.width * Math.pow(1.1, amount);
			changeRect.width = (int)size;
			changeRect.height = (int)size;
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
		
		r.setColor(red, green, blue);
		r.drawRectangle(100, 50, 400, 100);
		
		r.drawImage(playerX, playerY, 150, 150, playerImage);
		
		r.setColor(0, 0, 1, 0.5);
		r.drawRectangle(140, 70, 90, 400);
	}
	
	@Override
	protected void renderHud(Renderer r){
		SoundManager sm = game.getSounds();
		EffectsPlayer e = sm.getEffectsPlayer();
		MusicPlayer m = sm.getMusicPlayer();

		double red = e.isMuted() ? 1 : 0;
		double blue = e.isPaused() ? 1 : 0;
		r.setColor(red, 0, blue);
		r.drawRectangle(5, 5, 30, 30);

		red = m.isMuted() ? 1 : 0;
		blue = m.isPaused() ? 1 : 0;
		r.setColor(red, 0, blue);
		r.drawRectangle(40, 5, 30, 30);
	}

	@Override
	protected void tick(double dt){
		// Get values for updating things
		ZMouseInput mouse = game.getMouseInput();
		ZKeyInput keys = game.getKeyInput();
		GameCamera cam = game.getCamera();
		double msx = game.mouseSX();
		double msy = game.mouseSY();
		double mgx = game.mouseGX();
		double mgy = game.mouseGY();
		
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
				game.setPrintFps(!game.isPrintFps());
				game.setPrintTps(!game.isPrintTps());
			}
		}
		else down[FOUR] = true;
		if(keys.pressed(GLFW_KEY_R)) reset();

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
			if(x) game.zoomX(scroll * dt, msx);
			if(y) game.zoomY(scroll * dt, msy);
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

		// Update sound positions
		game.getSounds().getListener().updatePosition(playerX, playerY);
	}
	
}
