package tester;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.graphics.font.GameFont;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.sound.EffectsPlayer;
import zgame.core.sound.MusicPlayer;
import zgame.core.sound.SoundManager;
import zgame.core.sound.SoundSource;
import zgame.core.state.GameState;
import zgame.core.state.MenuState;
import zgame.core.state.PlayState;
import zgame.core.utils.ZRect;
import zgame.core.utils.ZStringUtils;
import zgame.core.window.GameWindow;
import zgame.menu.Menu;
import zgame.menu.MenuButton;
import zgame.menu.MenuHolder;
import zgame.menu.MenuTextBox;
import zgame.menu.scroller.HorizontalScroller;
import zgame.menu.scroller.MenuScroller;
import zgame.menu.scroller.VerticalScroller;
import zgame.things.still.Door;
import zgame.things.still.tiles.BaseTiles;
import zgame.world.Room;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Rectangle;

import com.google.gson.JsonObject;

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
 * ctrl + c = save gave to file
 * ctrl + v = load game from file
 * <p>
 * Indicators in the upper left hand corner for muted/paused: black = neither, red = muted, blue = paused, magenta = both muted and paused.
 * The size of the box represents the volume, a bigger box means higher volume
 * The left indicator is effects, the right indicator is music
 * The bigger the indicator, the higher the volume, if there is no indicator, the volume is set to zero
 * <p>
 * Indicator on the upper right hand corner for not updating or rendering the game:
 * black = always do it
 * red = don't do it when not in focus
 * blue = don't do it when minimized
 * magenta = don't do it when minimized or when not in focus
 * Left indicator is for rendering, right is for updating
 * <p>
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
	public static GameWindow window;
	
	public static double camSpeed = 400;
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
	public static int ONE = 1;
	public static int TWO = 2;
	public static int THREE = 3;
	public static int FOUR = 4;
	
	public static SoundSource winSource;
	public static SoundSource loseSource;
	
	public MainTest(){
		super("test", 1500, 720, 1000, 720, 0, true, false, false, true, 100, true);
	}
	
	public static void main(String[] args){
		// Set up game
		testerGame = new MainTest();
//		testerGame.setCurrentState(new TesterGameState(testerGame));
		// testerGame.setCurrentState(new TesterMenuState(testerGame));
		 testerGame.setCurrentState(new GameEngineState());
		
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
		winSource.destroy();
		loseSource.destroy();
	}
	
	public static void reset(){
		playerX = 200;
		playerY = 500;
		changeRect = new Rectangle(600, 20, 200, 200);
		testerGame.getCamera().reset();
		
		if(winSource != null) winSource.destroy();
		if(loseSource != null) loseSource.destroy();
		SoundManager sm = testerGame.getSounds();
		winSource = sm.createSource(playerX, playerY);
		loseSource = sm.createSource(0, 200);
	}
	
	@Override
	public JsonObject save(JsonObject obj){
		obj.addProperty("playerX", playerX);
		obj.addProperty("playerY", playerY);
		return obj;
	}
	
	@Override
	public JsonObject load(JsonObject obj) throws ClassCastException, IllegalStateException{
		playerX = obj.get("playerX").getAsDouble();
		playerY = obj.get("playerY").getAsDouble();
		return obj;
	}
	
	public static class GameEngineState extends PlayState{
		private final PlayerTester player;
		
		public GameEngineState(){
			super(false);
			Room firstRoom = makeRoom();
			firstRoom.setTile(0, 4, BaseTiles.BOUNCY);
			firstRoom.setTile(1, 4, BaseTiles.BOUNCY);
			Room secondRoom = makeRoom();
			for(int i = 0; i < 2; i++) secondRoom.setTile(i, 4, BaseTiles.HIGH_FRICTION);
			this.setCurrentRoom(firstRoom);
			
			this.player = new PlayerTester(100, 400, 60, 100);
			this.player.setMass(100);
			this.player.setLockCamera(true);
			this.player.getWalk().setCanWallJump(true);
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
		public void playKeyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.playKeyAction(game, button, press, shift, alt, ctrl);
			if(press) return;
			
			if(shift && button == GLFW_KEY_SPACE) game.setCurrentState(new TesterGameState(game));
			
			var walk = player.getWalk();
			Room r = getCurrentRoom();
			if(button == GLFW_KEY_W) r.makeWallState(Room.WALL_CEILING, !r.isSolid(Room.WALL_CEILING));
			else if(button == GLFW_KEY_A) r.makeWallState(Room.WALL_LEFT, !r.isSolid(Room.WALL_LEFT));
			else if(button == GLFW_KEY_S) r.makeWallState(Room.WALL_FLOOR, !r.isSolid(Room.WALL_FLOOR));
			else if(button == GLFW_KEY_D) r.makeWallState(Room.WALL_RIGHT, !r.isSolid(Room.WALL_RIGHT));
			else if(button == GLFW_KEY_MINUS) walk.setJumpPower(walk.getJumpPower() - 10);
			else if(button == GLFW_KEY_EQUAL) walk.setJumpPower(walk.getJumpPower() + 10);
			else if(shift && button == GLFW_KEY_L) player.setLockCamera(!player.isLockCamera());
			else if(button == GLFW_KEY_9) game.getCamera().zoom(-.5);
			else if(button == GLFW_KEY_0) game.getCamera().zoom(.5);
			else if(button == GLFW_KEY_J) game.getCamera().getX().shift(-50);
			else if(button == GLFW_KEY_L) game.getCamera().getX().shift(50);
			else if(button == GLFW_KEY_I) game.getCamera().getY().shift(-50);
			else if(button == GLFW_KEY_K) game.getCamera().getY().shift(50);
		}
		
		@Override
		public void playMouseWheelMove(Game game, double amount){
			super.playMouseWheelMove(game, amount);
			if(game.getKeyInput().shift()) game.getCamera().zoom(amount);
		}
	}
	
	public static class TesterGameState extends GameState{
		
		private final TextBuffer textBuffer;
		
		private static final ZRect bufferBounds = new ZRect(0, 500, 500, 150);
		
		public TesterGameState(Game game){
			this.textBuffer = new TextBuffer((int)bufferBounds.width, (int)bufferBounds.height, game.getFont("zfont"));
			this.textBuffer.setText("Text from a buffer");
			this.textBuffer.setTextX(10);
			this.textBuffer.setTextY(75);
			this.textBuffer.setFont(this.textBuffer.getFont().size(40));
		}
		
		@Override
		public void destroy(){
			super.destroy();
			this.textBuffer.destroy();
		}
		
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
					if(shift) game.setCurrentState(new GameEngineState());
					else game.setCurrentState(new TesterMenuState(game));
				}
				else if(key == GLFW_KEY_C && keys.ctrl()) game.saveGame("./saves/testGame");
				else if(key == GLFW_KEY_V && keys.ctrl()) game.loadGame("./saves/testGame");
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
			
			// Drawing 1 pixel
			r.setColor(1, 0, 0);
			r.drawRectangle(0, 1000, 1, 1);
			
			r.setColor(changeR, changeG, changeB);
			r.drawRectangle(changeRect.x, changeRect.y, changeRect.width, changeRect.height);
			
			r.setColor(red, green, blue);
			r.drawRectangle(100, 50, 400, 100);
			
			r.setColor(1, 1, 1);
			r.drawRectangle(bufferBounds);
			r.setColor(1, 0, 0);
			this.textBuffer.drawToRenderer(bufferBounds.x, bufferBounds.y, r);
			
			r.makeOpaque();
			r.drawImage(playerX, playerY, 150, 150, game.getImage("player"));
			r.setAlpha(.5);
			r.drawImage(550, 100, 50, 50, game.getImage("goal"));
			
			r.setColor(0, 0, 1, 0.5);
			r.drawRectangle(140, 70, 90, 400);
			
			r.setColor(new ZColor(.9, .9, .9));
			r.drawRectangle(0, -100, 250, 100);
			r.setColor(new ZColor(0));
			r.setFont(game.getFont("zfont"));
			r.setFontSize(40);
			r.limitBounds(new ZRect(0, -100, 250, 100));
			r.drawText(0, -10, "a long string that should get cut off");
			r.unlimitBounds();
			
			String s = """
					   TL qgy Text on
					   multiple lines
					   and another
					                and spaces
					   				
					   and a nothing line
					   """;
			r.pushAttributes();
			r.setFontSize(32);
			r.setFontLineSpace(40);
			r.setFontCharSpace(10);
			
			r.setColor(new ZColor(1, 0, 1));
			r.drawText(600, -400, s);
			
			ZRect[] bs = r.getFont().stringBounds(600, -400, s, 0, true);
			r.setColor(.25, .25, .25, .2);
			r.drawRectangle(new ZRect(bs[s.length()], 5));
			r.setColor(.25, .25, .25, .4);
			r.drawRectangle(bs[s.length()]);
			r.setColor(.7, .7, .7, .1);
			for(int i = 0; i < s.length(); i++) r.drawRectangle(bs[i]);
			
			////////////////////////////////////////
			
			r.setColor(1, 0, 0);
			r.drawRectangle(580, -50, 2, 2);
			r.drawRectangle(600, -50, 1, 1);
			r.drawRectangle(620, -50, .5, .5);
			
			////////////////////////////////////////
			
			r.pushAttributes();
			r.setFontSize(200);
			s = "Ayg q p j";
			bs = r.getFont().stringBounds(1100, 0, s, 0, true);
			r.setColor(new ZColor(1, 0, 1));
			r.drawText(1100, 0, s);
			r.setColor(.25, .25, .25, .2);
			r.drawRectangle(new ZRect(bs[s.length()], 5));
			r.setColor(.25, .25, .25, .4);
			r.drawRectangle(bs[s.length()]);
			r.setColor(.7, .7, .7, .1);
			for(int i = 0; i < s.length(); i++) r.drawRectangle(bs[i]);
			r.popAttributes();
			
			////////////////////////////////////////
			
			r.setColor(0, 1, 0, .2);
			r.setFontCharSpace(0);
			r.drawText(-400, 400, "transparent text");
			
			r.popAttributes();
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
		
		public TesterMenuState(Game game){
			super(new TesterMenu(game));
			((TesterMenu)this.getMenu()).state = this;
		}
		
		@Override
		public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.keyAction(game, button, press, shift, alt, ctrl);
			if(!press && alt && button == GLFW_KEY_SPACE) game.setCurrentState(new TesterGameState(game));
		}
		
		@Override
		public void renderBackground(Game game, Renderer r){
			r.setColor(.1, .1, .1);
			r.fill();
			super.renderBackground(game, r);
			
			
			r.setFont(game.getFont("zfont"));
			r.setColor(0, 0, 1);
			r.setFontSize(30);
			r.setFontLineSpace(-4);
			r.setFontCharSpace(17);
			r.drawText(10, 90, "ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n 0123456789.,“”‘’\"'?!@_*#$\n%&()+-/:;<=>[/]^`{|}~");
		}
	}
	
	public static class TesterMenu extends Menu{
		public TesterMenuState state;
		
		public TesterMenu(Game game){
			super(100, 250, 830, 380, true);
			this.setWidth(830);
			this.setHeight(380);
			this.updateBuffer();
			this.setFill(new ZColor(.1, .1, .2, 1));
			
			MenuScroller scrollX = new HorizontalScroller(0, 370, 800, 20, 200, game){
				@Override
				public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
					super.keyAction(game, button, press, shift, alt, ctrl);
					setScrollWheelEnabled(shift);
				}
			};
			scrollX.setScrollWheelEnabled(false);
			scrollX.setScrollWheelAsPercent(false);
			scrollX.setScrollWheelStrength(10);
			scrollX.setDrawThingsToBuffer(false);
			scrollX.updateBuffer();
			scrollX.getButton().updateBuffer();
			this.addThing(scrollX);
			MenuScroller scrollY = new VerticalScroller(820, 0, 20, 350, 200, game){
				@Override
				public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
					super.keyAction(game, button, press, shift, alt, ctrl);
					setScrollWheelEnabled(!shift);
				}
			};
			this.addThing(scrollY);
			
			MenuButton t;
			
			MenuHolder base = new MenuHolder();
			this.addThing(base);
			scrollX.setMovingThing(base);
			scrollY.setMovingThing(base);
			
			t = new MenuButton(10, 10, 300, 50, game){
				@Override
				public void click(Game game){
					game.setCurrentState(new TesterGameState(game));
				}
			};
			t.setFill(new ZColor(0, .2, .7));
			t.setText("Back");
			t.updateBuffer();
			base.addThing(t);
			
			t = new MenuButton(50, 100, 200, 100, game){
				double pos = 0;
				
				@Override
				public void click(Game game){
					game.stop();
				}
				
				@Override
				public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
					if(button == GLFW_KEY_1 && !press){
						MenuButton b = new MenuButton(pos, this.getHeight(), 15, 10, game);
						b.setFill(new ZColor(0, 0, (pos / 100) % 1));
						this.addThing(b);
						pos += 20;
					}
				}
			};
			t.setFill(new ZColor(.5, 0, 0));
			t.setText("Exit");
			t.setFont(new GameFont(game.getFontAsset("zfont"), 50, 0, 0));
			base.addThing(t);
			
			t = new MenuButton(50, 220, 200, 50, game){
				@Override
				public void click(Game game){
					createPopup(game);
				}
			};
			t.setText("popup");
			t.setTextY(45);
			t.setFont(new GameFont(game.getFontAsset("zfont"), 25, 0, 0));
			base.addThing(t);
			
			MenuTextBox textBox = new MenuTextBox(300, 100, 300, 50, game){
				@Override
				public void click(Game game){
					ZStringUtils.prints(this.getText());
				}
			};
			textBox.setFont(new GameFont(game.getFontAsset("zfont"), 32, 0, 0));
			base.addThing(textBox);
		}
		
		public void createPopup(Game game){
			Menu menu = new Menu(){
				@Override
				public void render(Game game, Renderer r, ZRect bounds){
					super.render(game, r, bounds);
					r.setColor(.2, .2, .4, .3);
					r.fill();
				}
			};
			MenuButton b = new MenuButton(100, 100, 300, 100, game){
				@Override
				public void click(Game game){
					state.removeTopMenu();
				}
			};
			b.setText("exit popup");
			b.setTextY(90);
			b.setFont(new GameFont(game.getFontAsset("zfont"), 32, 0, 0));
			menu.addThing(b);
			this.state.popupMenu(menu);
		}
	}
}
