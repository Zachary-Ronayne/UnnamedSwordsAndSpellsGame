package tester;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.sound.EffectsPlayer;
import zgame.core.sound.MusicPlayer;
import zgame.core.sound.SoundManager;
import zgame.core.sound.SoundSource;
import zgame.core.state.GameState;
import zgame.core.state.MenuState;
import zgame.core.state.PlayState;
import zgame.core.window.GameWindow;
import zgame.menu.Menu;
import zgame.menu.MenuButton;
import zgame.things.Door;
import zgame.things.Room;
import zgame.things.entity.Player;
import zgame.things.tiles.BaseTiles;

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
 * 3 = toggle stretch to fill
 * 4 = toggle printing FPS/TPS
 * shift + 4: toggle printing the number of audio updates per second
 * 5 = toggle not rendering the game when the window doesn't have focus
 * shift + 5 = toggle not rendering the game when the window is minimized
 * alt + 5 = toggle not updating the game when the window doesn't have focus
 * shift + alt + 5 = toggle not updating the game when the window is minimized
 * shift + z = change green of flickering red square
 * shift + x = change blue of flickering red square
 * g = play win sound effect
 * h = play lose sound effect
 * m = play music
 * shift + m = play short music
 * n = scan for sound devices
 * p = toggle effects paused
 * o = toggle effects muted
 * shift + p = toggle music paused
 * shift + o = toggle music muted
 * l = toggle music looping
 * F1 = increase effects volume
 * F2 = decrease effects volume
 * shift + F1 = increase music volume
 * shift + F2 = decrease music volume
 * F3 = increase the win sound effect volume
 * F4 = decrease the win sound effect volume
 * shift + F3 = increase the lose sound effect volume
 * shift + F4 = decrease the lose sound effect volume
 * F5 = increase the good sound effect volume
 * F6 = decrease the good sound effect volume
 * shift + F5 = increase the bad sound effect volume
 * shift + F6 = decrease the bad sound effect volume
 * F11 = decrease game speed
 * F12 = increase game speed
 * shift + F11 = decrease TPS
 * shift + F12 = increase TPS
 * space = toggle between the demo state and menu state
 * shift + space = toggle between the game play state and the demo state
 * 
 * Indicators in the upper left hand corner for muted/paused: black = neither, red = muted, blue = paused, magenta = both muted and paused.
 * The size of the box represents the volume, a bigger box means higher volume
 * The left indicator is effects, the right indicator is music
 * The bigger the indicator, the higher the volume, if there is no indicator, the volume is set to zero
 * 
 * Indicator on the upper right hand corner for not updating or rendering the game:
 * black = always do it
 * red = don't do it when not in focus
 * blue = don't do it when minimized
 * magenta = don't do it when minimized or when not in focus
 * Left indicator is for rendering, right is for updating
 * 
 * In play state:
 * left arrow key = move left
 * right arrow key = move right
 * up arrow key = jump
 * w = toggle disable ceiling
 * a = toggle disable left wall
 * s = toggle disable floor
 * d = toggle disable right wall
 * plus = increase jump height
 * minus = decrease jump height
 * shift + scroll wheel = zoom in or out
 * L = toggle lock camera to player
 */
public class MainTest extends Game{
	
	public static Game testerGame;
	public static GameState testerState;
	public static PlayState engineState;
	public static MenuState menuState;
	public static GameWindow window;
	
	public static double camSpeed = 400;
	public static double camZoom = 2;
	public static boolean zoomOnlyX = false;
	public static boolean zoomOnlyY = false;
	
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
		testerGame = new MainTest();
		testerState = new TesterGameState();
		engineState = new GameEngineState();
		testerGame.setPlayState(engineState);
		menuState = new TesterMenuState();
		testerGame.enterPlayState();
		window = testerGame.getWindow();
		window.center();
		
		// Add images
		testerGame.getImages().addAll();
		
		// Add sounds
		SoundManager sm = testerGame.getSounds();
		sm.addAllSounds();
		
		// Set the sound scaling distance
		sm.setDistanceScalar(.04);
		
		// Start up the game
		reset();
		testerGame.start();
		
		// Close sound sources
		winSource.end();
		loseSource.end();
	}
	
	public static void reset(){
		playerX = 200;
		playerY = 500;
		changeRect = new Rectangle(600, 20, 200, 200);
		testerGame.getCamera().reset();
		
		if(winSource != null) winSource.end();
		if(loseSource != null) loseSource.end();
		SoundManager sm = testerGame.getSounds();
		winSource = sm.createSource(playerX, playerY);
		loseSource = sm.createSource(0, 200);
	}
	
	public static class GameEngineState extends PlayState{
		private Player player;
		
		public GameEngineState(){
			super(false);
			Room firstRoom = makeRoom();
			Room secondRoom = makeRoom();
			this.setCurrentRoom(firstRoom);
			
			this.player = new Player(100, 400, 60, 100);
			this.player.setMass(50);
			// this.player.setWalkAcceleration(100000);
			firstRoom.addThing(this.player);
			
			Door d = new Door(700, 400);
			d.setLeadRoom(secondRoom, 50, 100);
			firstRoom.addThing(d);
			
			d = new Door(400, 500);
			d.setLeadRoom(firstRoom, 100, 400);
			secondRoom.addThing(d);
		}
		
		@Override
		public void onSet(Game game){
			game.getCamera().setPos(50, 100);
		}
		
		private Room makeRoom(){
			Room r = new Room();
			r.makeWallsSolid();
			r.initTiles(13, 9, BaseTiles.BACK_DARK);
			for(int i = 0; i < r.getXTiles(); i++){
				for(int j = 0; j < r.getYTiles(); j++){
					boolean i0 = i % 2 == 0;
					boolean j0 = j % 2 == 0;
					if(i0 == j0) r.setTile(i, j, BaseTiles.BACK_LIGHT);
				}
			}
			for(int i = 0; i < 4; i++) r.setTile(4 + i, 6, BaseTiles.WALL_DARK);
			r.setTile(7, 5, BaseTiles.WALL_DARK);
			r.setTile(11, 3, BaseTiles.WALL_LIGHT);
			
			return r;
		}
		
		@Override
		public void renderBackground(Game game, Renderer r){
			super.renderBackground(game, r);
			r.setColor(.1, .1, .1);
			r.fill();
		}
		
		@Override
		public void render(Game game, Renderer r){
			super.render(game, r);
			r.setColor(1, 1, 1);
			r.drawRectangle(990, 0, 20, 500);
		}
		
		@Override
		public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.keyAction(game, button, press, shift, alt, ctrl);
			if(press) return;
			
			if(shift && button == GLFW_KEY_SPACE) game.setCurrentState(testerState);
			
			Room r = getCurrentRoom();
			if(button == GLFW_KEY_W) r.makeWallState(Room.WALL_CEILING, !r.isSolid(Room.WALL_CEILING));
			else if(button == GLFW_KEY_A) r.makeWallState(Room.WALL_LEFT, !r.isSolid(Room.WALL_LEFT));
			else if(button == GLFW_KEY_S) r.makeWallState(Room.WALL_FLOOR, !r.isSolid(Room.WALL_FLOOR));
			else if(button == GLFW_KEY_D) r.makeWallState(Room.WALL_RIGHT, !r.isSolid(Room.WALL_RIGHT));
			else if(button == GLFW_KEY_MINUS) player.setJumpPower(player.getJumpPower() - 10);
			else if(button == GLFW_KEY_EQUAL) player.setJumpPower(player.getJumpPower() + 10);
			else if(shift && button == GLFW_KEY_L) player.setLockCamera(!player.isLockCamera());
			else if(button == GLFW_KEY_9) game.getCamera().zoom(-.5);
			else if(button == GLFW_KEY_0) game.getCamera().zoom(.5);
			else if(button == GLFW_KEY_J) game.getCamera().getX().shift(-50);
			else if(button == GLFW_KEY_L) game.getCamera().getX().shift(50);
			else if(button == GLFW_KEY_I) game.getCamera().getY().shift(-50);
			else if(button == GLFW_KEY_K) game.getCamera().getY().shift(50);
		}
		
		@Override
		public void mouseWheelMove(Game game, double amount){
			super.mouseWheelMove(game, amount);
			if(game.getKeyInput().shift()) game.getCamera().zoom(amount);
		}
	}
	
	public static class TesterGameState extends GameState{
		@Override
		public void keyAction(Game game, int key, boolean press, boolean shift, boolean alt, boolean ctrl){
			ZKeyInput keys = game.getKeyInput();
			if(keys.shift()){
				if(key == GLFW_KEY_Z) green = (green + 0.05) % 1;
				if(key == GLFW_KEY_X) blue = (blue + 0.05) % 1;
			}
			if(!press){
				SoundManager s = game.getSounds();
				if(key == GLFW_KEY_G) game.playEffect(winSource, "win");
				else if(key == GLFW_KEY_H) game.playEffect(loseSource, "lose");
				else if(key == GLFW_KEY_M){
					if(shift) game.playMusic("song short");
					else game.playMusic("song");
				}
				else if(key == GLFW_KEY_N) s.scanDevices();
				else if(key == GLFW_KEY_P){
					if(shift) s.getMusicPlayer().togglePaused();
					else s.getEffectsPlayer().togglePaused();
				}
				else if(key == GLFW_KEY_O){
					if(shift) s.getMusicPlayer().toggleMuted();
					else s.getEffectsPlayer().toggleMuted();
				}
				else if(key == GLFW_KEY_L) s.getMusicPlayer().toggleLooping();
				
				else if(key == GLFW_KEY_5){
					if(!keys.shift() && !keys.alt()) game.setFocusedRender(!game.isFocusedRender());
					else if(keys.shift() && !keys.alt()) game.setMinimizedRender(!game.isMinimizedRender());
					else if(!keys.shift() && keys.alt()) game.setFocusedUpdate(!game.isFocusedUpdate());
					else game.setMinimizedUpdate(!game.isMinimizedUpdate());
				}
				
				else if(key == GLFW_KEY_F11){
					if(keys.shift()) game.setTps(game.getTps() - 5);
					else game.setGameSpeed(game.getGameSpeed() - 0.1);
				}
				else if(key == GLFW_KEY_F12){
					if(keys.shift()) game.setTps(game.getTps() + 5);
					else game.setGameSpeed(game.getGameSpeed() + 0.1);
				}
				else if(key == GLFW_KEY_SPACE){
					if(shift) game.setCurrentState(engineState);
					else game.setCurrentState(menuState);
				}
				
			}
		}
		
		@Override
		public void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			if(shift && alt && ctrl && !press){
				changeR = Math.random();
				changeG = Math.random();
				changeB = Math.random();
			}
		}
		
		@Override
		public void mouseMove(Game game, double x, double y){
			ZKeyInput keys = game.getKeyInput();
			ZMouseInput mouse = game.getMouseInput();
			if(keys.shift() && mouse.middleDown()){
				changeRect.x += x - mouse.lastX();
				changeRect.y += y - mouse.lastY();
			}
		}
		
		@Override
		public void mouseWheelMove(Game game, double amount){
			ZKeyInput keys = game.getKeyInput();
			if(keys.shift()){
				double size = changeRect.width * Math.pow(1.1, amount);
				changeRect.width = (int)size;
				changeRect.height = (int)size;
			}
		}
		
		@Override
		public void renderBackground(Game game, Renderer r){
			r.setColor(.1, .1, .1);
			r.fill();
		}
		
		@Override
		public void render(Game game, Renderer r){
			r.setColor(.2, .2, .2);
			r.drawRectangle(0, 0, 1000, 700);
			
			r.setColor(changeR, changeG, changeB);
			r.drawRectangle(changeRect.x, changeRect.y, changeRect.width, changeRect.height);
			
			r.setColor(red, green, blue);
			r.drawRectangle(100, 50, 400, 100);
			
			r.drawImage(550, 100, 50, 50, game.getImage("goal"));
			r.drawImage(playerX, playerY, 150, 150, game.getImage("player"));
			
			r.setColor(0, 0, 1, 0.5);
			r.drawRectangle(140, 70, 90, 400);
		}
		
		@Override
		public void renderHud(Game game, Renderer r){
			SoundManager sm = game.getSounds();
			EffectsPlayer e = sm.getEffectsPlayer();
			MusicPlayer m = sm.getMusicPlayer();
			
			double rr = e.isMuted() ? 1 : 0;
			double bb = e.isPaused() ? 1 : 0;
			r.setColor(rr, 0, bb);
			r.drawRectangle(5, 5, 30, 30 * e.getVolume());
			
			rr = m.isMuted() ? 1 : 0;
			bb = m.isPaused() ? 1 : 0;
			r.setColor(rr, 0, bb);
			r.drawRectangle(40, 5, 30, 30 * m.getVolume());
			
			rr = game.isFocusedRender() ? 1 : 0;
			bb = game.isMinimizedRender() ? 1 : 0;
			r.setColor(rr, 0, bb);
			r.drawRectangle(930, 5, 30, 30);
			
			rr = game.isFocusedUpdate() ? 1 : 0;
			bb = game.isMinimizedUpdate() ? 1 : 0;
			r.setColor(rr, 0, bb);
			r.drawRectangle(965, 5, 30, 30);
		}
		
		@Override
		public void tick(Game game, double dt){
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
					if(keys.shift()){
						game.setPrintSoundUpdates(!game.isPrintSoundUpdates());
					}
					else{
						game.setPrintFps(!game.isPrintFps());
						game.setPrintTps(!game.isPrintTps());
					}
				}
			}
			else down[FOUR] = true;
			if(keys.pressed(GLFW_KEY_R)) reset();
			
			// Adjust volume
			SoundManager sm = game.getSounds();
			EffectsPlayer ep = sm.getEffectsPlayer();
			MusicPlayer mp = sm.getMusicPlayer();
			if(keys.pressed(GLFW_KEY_F1)){
				if(keys.shift()) mp.addVolume(dt * 0.5);
				else ep.addVolume(dt * 0.5);
			}
			else if(keys.pressed(GLFW_KEY_F2)){
				if(keys.shift()) mp.addVolume(dt * -0.5);
				else ep.addVolume(dt * -0.5);
			}
			else if(keys.pressed(GLFW_KEY_F3)){
				if(keys.shift()) loseSource.addBaseVolume(dt * 0.5);
				else winSource.addBaseVolume(dt * 0.5);
			}
			else if(keys.pressed(GLFW_KEY_F4)){
				if(keys.shift()) loseSource.addBaseVolume(dt * -0.5);
				else winSource.addBaseVolume(dt * -0.5);
			}
			else if(keys.pressed(GLFW_KEY_F5)){
				if(keys.shift()) ep.addTypeVolume("bad", dt * 0.5);
				else ep.addTypeVolume("good", dt * 0.5);
			}
			else if(keys.pressed(GLFW_KEY_F6)){
				if(keys.shift()) ep.addTypeVolume("bad", dt * -0.5);
				else ep.addTypeVolume("good", dt * -0.5);
			}
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
			// Change the color of a rectangle based on which mouse buttons and modifier buttons are pressed
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
			game.getSounds().updateListenerPos(playerX, playerY);
		}
	}
	
	public static class TesterMenuState extends MenuState{
		public TesterMenuState(){
			super(new TesterMenu());
		}
		
		@Override
		public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.keyAction(game, button, press, shift, alt, ctrl);
			if(!press && button == GLFW_KEY_SPACE) game.setCurrentState(testerState);
		}
		
		@Override
		public void renderBackground(Game game, Renderer r){
			r.setColor(.1, .1, .1);
			r.fill();
			super.renderBackground(game, r);
		}
	}
	
	public static class TesterMenu extends Menu{
		public TesterMenu(){
			super(100, 400);
			this.setWidth(800);
			this.setHeight(300);
			this.setFill(new ZColor(.1, .1, .2, 1));
			
			MenuButton t = new MenuButton(10, 10, 300, 50){
				@Override
				public void click(Game game){
					game.setCurrentState(testerState);
				}
			};
			t.setFill(new ZColor(0, .2, .7));
			this.addThing(t);
			
			t = new MenuButton(50, 100, 200, 100){
				double pos = 0;
				
				@Override
				public void click(Game game){
					game.stop();
				}
				
				@Override
				public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
					if(button == GLFW_KEY_1 && !press){
						MenuButton b = new MenuButton(pos, this.getHeight(), 15, 10);
						b.setFill(new ZColor(0, 0, (pos / 100) % 1));
						this.addThing(b);
						pos += 20;
					}
				}
			};
			t.setFill(new ZColor(.5, 0, 0));
			t.setText("Exit");
			this.addThing(t);
		}
		
		@Override
		public void render(Game game, Renderer r){
			super.render(game, r);
			r.setFont(game.getFont("zfont"));
			r.setColor(0, 0, 1);
			r.setFontSize(30);
			r.setFontLineSpace(-4);
			r.setFontCharSpace(17);
			r.drawText(10, 90, "ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n 0123456789.,“”‘’\"'?!@_*#$\n%&()+-/:;<=>[/]^`{|}~");
		}
	}
	
}
