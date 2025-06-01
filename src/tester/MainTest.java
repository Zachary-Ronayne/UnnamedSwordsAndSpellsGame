package tester;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.graphics.AlphaMode;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.TextOption;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.graphics.font.FontManager;
import zgame.core.graphics.font.GameFont;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.graphics.image.ImageManager;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.sound.EffectsPlayer;
import zgame.core.sound.MusicPlayer;
import zgame.core.sound.SoundManager;
import zgame.core.sound.SoundSource;
import zgame.core.state.GameState;
import zgame.core.state.MenuState;
import zgame.core.state.PlayState;
import zgame.core.utils.ZRect2D;
import zgame.core.utils.ZStringUtils;
import zgame.menu.*;
import zgame.menu.scroller.HorizontalScroller;
import zgame.menu.scroller.MenuScroller;
import zgame.menu.scroller.VerticalScroller;
import zgame.physics.material.MaterialConst;
import zgame.physics.material.Materials;
import zgame.things.entity.mobility.MobilityType;
import zgame.things.still.Door2D;
import zgame.things.still.tiles.BaseTiles2D;
import zgame.world.Room2D;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

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
 * m = toggle walking/running
 */
public class MainTest extends Game{
	
	public static Game testerGame;
	
	public static final boolean CIRCLE_PLAYER = false;
	
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
		super();
		var window = this.getWindow();
		window.setWindowTitle("test");
		this.setPrintFps(true);
		this.setPrintTps(true);
		this.setTps(100);
	}
	
	@Override
	public void init(){
		super.init();
		var window = this.getWindow();
		window.resize(1500, 720);
		window.resizeScreen(1000, 720);
		window.center();
		
		reset();
		
		// Add images
		ImageManager.instance().addAll();
		
		// TODO fix sounds not playing?
		// Add sounds
		var sm = testerGame.getSounds();
		sm.addAllSounds();
		
		// Set the sound scaling distance
		sm.setDistanceScalar(.04);
	}
	
	public static void main(String[] args){
		// Set up game
		testerGame = new MainTest();
//		testerGame.setCurrentState(new TesterGameState(testerGame));
//		testerGame.setCurrentState(new TesterMenuState(testerGame));
		testerGame.setCurrentState(new GameEngineState());
		testerGame.setInitSoundOnStart(true);
		
		// Start up the game
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
		winSource = sm.createSource(playerX, playerY, 0);
		loseSource = sm.createSource(0, 200, 0);
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.addProperty("playerX", playerX);
		obj.addProperty("playerY", playerY);
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException{
		var obj = e.getAsJsonObject();
		playerX = obj.get("playerX").getAsDouble();
		playerY = obj.get("playerY").getAsDouble();
		return true;
	}
	
	public static class GameEngineState extends PlayState{
		private final PlayerTester player;
		
		public GameEngineState(){
			super(new Room2D());
			var firstRoom = makeRoom();
			firstRoom.setTile(0, 4, BaseTiles2D.BOUNCY);
			firstRoom.setTile(1, 4, BaseTiles2D.BOUNCY);
			var secondRoom = makeRoom();
			for(int i = 0; i < 2; i++) secondRoom.setTile(i, 4, BaseTiles2D.HIGH_FRICTION);
			this.setCurrentRoom(firstRoom);
			
			if(CIRCLE_PLAYER) this.player = new PlayerTesterCircle(130, 430, 60);
			else this.player = new PlayerTesterRect(100, 400, 60, 100);
			
			this.player.setMass(100);
			this.player.setLockCamera(true);
			this.player.setCanWallJump(true);
			firstRoom.addThing(this.player);
			
			var d = new Door2D(700, 400);
			d.setLeadRoom(secondRoom, 50, 100);
			firstRoom.addThing(d);
			
			d = new Door2D(400, 500);
			d.setLeadRoom(firstRoom, 100, 400);
			secondRoom.addThing(d);
		}
		
		@Override
		public void onSet(){
			Game.get().getCamera().setPos(50, 100);
		}
		
		private Room2D makeRoom(){
			var r = new Room2D();
			r.makeWallsSolid();
			r.initTiles(13, 9, BaseTiles2D.BACK_DARK);
			for(int i = 0; i < r.getXTiles(); i++){
				for(int j = 0; j < r.getYTiles(); j++){
					boolean i0 = i % 2 == 0;
					boolean j0 = j % 2 == 0;
					if(i0 == j0) r.setTile(i, j, BaseTiles2D.BACK_LIGHT);
				}
			}
			for(int i = 0; i < 4; i++) r.setTile(4 + i, 6, BaseTiles2D.WALL_DARK);
			r.setTile(7, 5, BaseTiles2D.WALL_DARK);
			r.setTile(11, 3, BaseTiles2D.WALL_LIGHT);
			
			r.setFrontTile(7, 4, BaseTiles2D.WALL_CIRCLE);
			
			r.setFrontTile(11, 2, BaseTiles2D.WALL_BOTTOM_SLAB);
			
			return r;
		}
		
		@Override
		public void renderBackground(Renderer r){
			super.renderBackground(r);
			r.setColor(.1, .1, .1);
			r.fill();
		}
		
		@Override
		public void render(Renderer r){
			super.render(r);
			r.setColor(1, 1, 1);
			r.drawRectangle(990, 0, 20, 500);
		}
		
		@Override
		public void renderHud(Renderer r){
			super.renderHud(r);
			if(this.isPaused()){
				r.setColor(0.5, 0, 1);
				r.setFontSize(40);
				r.drawText(50, 100, "PAUSED");
			}
		}
		
		@Override
		public void playKeyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.playKeyAction(button, press, shift, alt, ctrl);
			if(press) return;
			
			var game = Game.get();
			if(shift && button == GLFW_KEY_SPACE) game.setCurrentState(new TesterGameState());
			
			var r = this.getCurrentRoom();
			if(button == GLFW_KEY_W) r.makeWallState(Room2D.WALL_CEILING, !r.isSolid(Room2D.WALL_CEILING));
			else if(button == GLFW_KEY_A) r.makeWallState(Room2D.WALL_LEFT, !r.isSolid(Room2D.WALL_LEFT));
			else if(button == GLFW_KEY_S) r.makeWallState(Room2D.WALL_FLOOR, !r.isSolid(Room2D.WALL_FLOOR));
			else if(button == GLFW_KEY_D) r.makeWallState(Room2D.WALL_RIGHT, !r.isSolid(Room2D.WALL_RIGHT));
			else if(button == GLFW_KEY_F) {
				var mobilityData = player.getMobilityData();
				var mobilityType = mobilityData.getType();
				if(mobilityType == MobilityType.WALKING) mobilityData.setType(shift ? MobilityType.FLYING_AXIS : MobilityType.FLYING);
				else mobilityData.setType(MobilityType.WALKING);
			}
			else if(button == GLFW_KEY_N) player.setNoClip(!player.isNoClip());
			else if(button == GLFW_KEY_MINUS) player.setJumpPower(player.getJumpPower() - 10);
			else if(button == GLFW_KEY_EQUAL) player.setJumpPower(player.getJumpPower() + 10);
			else if(shift && button == GLFW_KEY_L) player.setLockCamera(!player.isLockCamera());
			else if(button == GLFW_KEY_9) game.getCamera().zoom(-.5);
			else if(button == GLFW_KEY_0) game.getCamera().zoom(.5);
			else if(button == GLFW_KEY_J) game.getCamera().getX().shift(-50);
			else if(button == GLFW_KEY_L) game.getCamera().getX().shift(50);
			else if(button == GLFW_KEY_I) game.getCamera().getY().shift(-50);
			else if(button == GLFW_KEY_K) game.getCamera().getY().shift(50);
			else if(button == GLFW_KEY_F1) {
				if(shift){
					game.setPrintFps(true);
					game.setPrintTps(true);
					game.setPrintSoundUpdates(true);
				}
				else{
					game.setPrintFps(false);
					game.setPrintTps(false);
					game.setPrintSoundUpdates(false);
				}
			}
			else if(button == GLFW_KEY_M) player.toggleWalking();
			else if(button == GLFW_KEY_ESCAPE){
				this.setPaused(!this.isPaused());
			}
			
			boolean addFriction = button == GLFW_KEY_1;
			boolean subFriction = button == GLFW_KEY_2;
			if(addFriction) {
				this.getCurrentRoom().setWallMaterial(new MaterialConst(this.getCurrentRoom().getWallMaterial().getFriction() + (ctrl ? 0.01 : 0.05), 0));
			}
			else if(subFriction) {
				this.getCurrentRoom().setWallMaterial(new MaterialConst(this.getCurrentRoom().getWallMaterial().getFriction() - (ctrl ? 0.01 : 0.05), 0));
			}
			if(addFriction || subFriction) {
				if(shift) this.getCurrentRoom().setWallMaterial(Materials.BOUNDARY);
			}
		}
		
		@Override
		public boolean playMouseWheelMove(double amount){
			boolean input = super.playMouseWheelMove(amount);
			var game = Game.get();
			if(game.getKeyInput().shift()) {
				game.getCamera().zoom(amount);
				return true;
			}
			return input;
		}
		
		@Override
		public Room2D getCurrentRoom(){
			return (Room2D)super.getCurrentRoom();
		}
	}
	
	public static class TesterGameState extends GameState{
		
		private final TextBuffer textBuffer;
		
		private static final ZRect2D bufferBounds = new ZRect2D(0, 500, 500, 150);
		
		private static final String SAVES_PATH = "./saves";
		private static final String FILE_PATH = SAVES_PATH + "/testGame.json";
		
		public TesterGameState(){
			this.textBuffer = new TextBuffer((int)bufferBounds.width, (int)bufferBounds.height, FontManager.getDefaultFont());
			this.textBuffer.setText("Text from a buffer");
			this.textBuffer.setTextX(10);
			this.textBuffer.setTextY(75);
			this.textBuffer.setFont(this.textBuffer.getFont().size(40));
			this.textBuffer.regenerateBuffer();
		}
		
		@Override
		public void destroy(){
			super.destroy();
			this.textBuffer.destroy();
		}
		
		@Override
		public void keyAction(int key, boolean press, boolean shift, boolean alt, boolean ctrl){
			var game = Game.get();
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
					else game.setCurrentState(new TesterMenuState());
				}
				else if(key == GLFW_KEY_C && keys.ctrl()){
					makeSaveDir();
					game.saveGame(FILE_PATH);
				}
				else if(key == GLFW_KEY_V && keys.ctrl()){
					makeSaveDir();
					game.loadGame(FILE_PATH);
				}
			}
		}
		
		private void makeSaveDir(){
			File file = new File(SAVES_PATH);
			if(!file.exists() && !file.mkdir()) ZStringUtils.print("Failed to make saves dir");
		}
		
		@Override
		public boolean mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			boolean input = super.mouseAction(button, press, shift, alt, ctrl);
			if(shift && alt && ctrl && !press){
				changeR = Math.random();
				changeG = Math.random();
				changeB = Math.random();
				return true;
			}
			return input;
		}
		
		@Override
		public boolean mouseMove(double x, double y){
			boolean input = super.mouseMove(x, y);
			var game = Game.get();
			ZKeyInput keys = game.getKeyInput();
			ZMouseInput mouse = game.getMouseInput();
			if(keys.shift() && mouse.middleDown()){
				changeRect.x += x - mouse.lastX();
				changeRect.y += y - mouse.lastY();
				input = true;
			}
			return input;
		}
		
		@Override
		public boolean mouseWheelMove(double amount){
			boolean input = super.mouseWheelMove(amount);
			ZKeyInput keys = Game.get().getKeyInput();
			if(keys.shift()){
				double size = changeRect.width * Math.pow(1.1, amount);
				changeRect.width = (int)size;
				changeRect.height = (int)size;
				input = true;
			}
			return input;
		}
		
		@Override
		public void renderBackground(Renderer r){
			r.setColor(.1, .1, .1);
			r.fill();
		}
		
		@Override
		public void render(Renderer r){
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
			this.textBuffer.drawOnRenderer(bufferBounds.x, bufferBounds.y, r);
			
			r.makeOpaque();
			r.drawImage(playerX, playerY, 150, 150, ImageManager.image("player"));
			r.setAlpha(.5);
			r.drawImage(550, 100, 50, 50, ImageManager.image("goal"));
			
			r.setColor(0, 0, 1, 0.5);
			r.drawRectangle(140, 70, 90, 400);
			
			r.setColor(new ZColor(.9, .9, .9));
			r.drawRectangle(0, -100, 250, 100);
			r.setColor(new ZColor(0));
			r.setFont(FontManager.getDefaultFont());
			r.setFontSize(40);
			r.limitBounds(new ZRect2D(0, -100, 250, 100));
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
			
			ZRect2D[] bs = r.getFont().stringBounds(600, -400, s, 0, true);
			r.setColor(.25, .25, .25, .2);
			r.drawRectangle(new ZRect2D(bs[s.length()], 10));
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
			r.drawRectangle(new ZRect2D(bs[s.length()], 10));
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
			
			////////////////////////////////////////
			
			var options = new ArrayList<TextOption>();
			options.add(new TextOption("ABCDEFGHIJKLM", new ZColor(1, 0, 0), AlphaMode.NORMAL));
			options.add(new TextOption("NOPQRSTUVWXYZ\n", new ZColor(1, 1, 0), AlphaMode.NONE));
			options.add(new TextOption("abcdefghijklm", new ZColor(0, 1, 0), null));
			options.add(new TextOption("nopqrstuvwxyz\n", new ZColor(0, 1, 1), AlphaMode.NORMAL));
			options.add(new TextOption(" 0123456789.,", new ZColor(0, 0, 1), null));
			options.add(new TextOption("“”‘’\"'?!@_*#$\n", new ZColor(1, 0, 1), null));
			options.add(new TextOption("%&()+-/:;<=>", new ZColor(1, 1, 1), AlphaMode.NONE));
			options.add(new TextOption("[/]^`{|}~", new ZColor(0, 0, 0), null));
			
			r.pushAttributes();
			r.setFontSize(32);
			r.setFontLineSpace(40);
			r.setFontCharSpace(10);
			
			r.drawText(1250, -400, options);
		}
		
		@Override
		public void renderHud(Renderer r){
			var game = Game.get();
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
		public void tick(double dt){
			// Get values for updating things
			var game = Game.get();
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
					game.toggleFullscreen();
				}
			}
			else down[ONE] = true;
			if(keys.released(GLFW_KEY_2)){
				if(down[TWO]){
					down[TWO] = false;
					var window = game.getWindow();
					window.setUseVsync(!window.usesVsync());
				}
			}
			else down[TWO] = true;
			if(keys.released(GLFW_KEY_3)){
				if(down[THREE]){
					down[THREE] = false;
					var window = game.getWindow();
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
			game.getSounds().updateListenerPos(playerX, playerY, 0);
		}
	}
	
	public static class TesterMenuState extends MenuState{
		
		public TesterMenuState(){
			super(new TesterMenu());
			((TesterMenu)this.getMenu()).state = this;
		}
		
		@Override
		public void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.keyAction(button, press, shift, alt, ctrl);
			if(!press && alt && button == GLFW_KEY_SPACE) Game.get().setCurrentState(new TesterGameState());
		}
		
		@Override
		public void renderBackground(Renderer r){
			r.setColor(.1, .1, .1);
			r.fill();
			super.renderBackground(r);
			
			r.setFont(FontManager.getDefaultFont());
			r.setColor(0, 0, 1);
			r.setFontSize(30);
			r.setFontLineSpace(-4);
			r.setFontCharSpace(17);
			r.drawText(10, 90, "ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n 0123456789.,“”‘’\"'?!@_*#$\n%&()+-/:;<=>[/]^`{|}~");
		}
	}
	
	public static class TesterMenu extends Menu{
		public TesterMenuState state;
		
		public TesterMenu(){
			super(100, 250, 830, 380, true);
			
			this.setWidth(830);
			this.setHeight(380);
			this.regenerateBuffer();
			this.setFill(new ZColor(.1, .1, .2, 1));
			
			MenuScroller scrollX = new HorizontalScroller(0, 370, 800, 20, 200){
				@Override
				public void keyActionFocused(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
					super.keyActionFocused(button, press, shift, alt, ctrl);
					setScrollWheelEnabled(shift);
				}
			};
			scrollX.setScrollWheelEnabled(false);
			scrollX.setScrollWheelAsPercent(false);
			scrollX.setScrollWheelStrength(10);
			scrollX.setDefaultUseBuffer(false);
			scrollX.regenerateBuffer();
			scrollX.getButton().regenerateBuffer();
			scrollX.getButton().setHighlightColor(new ZColor(0, 0, 1, .5));
			scrollX.getButton().setFill(new ZColor(.5, .5, .5));
			MenuScroller scrollY = new VerticalScroller(820, 0, 20, 350, 200){
				@Override
				public void keyActionFocused(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
					super.keyActionFocused(button, press, shift, alt, ctrl);
					setScrollWheelEnabled(!shift);
				}
			};
			
			scrollY.setDefaultUseBuffer(true);
			scrollY.getButton().setHighlightColor(new ZColor(0, 0, 1, .5));
			scrollY.getButton().setFill(new ZColor(.5, .5, .5));
			
			MenuButton t;
			
			MenuHolder base = new MenuHolder();
			this.addThing(base);
			scrollX.setMovingThing(base);
			scrollY.setMovingThing(base);
			
			t = new MenuButton(10, 10, 300, 50){
				@Override
				public void click(){
					Game.get().setCurrentState(new TesterGameState());
				}
			};
			t.setFill(new ZColor(0, .2, .7));
			t.setFontColor(new ZColor(0));
			t.setText("Back");
			base.addThing(t);
			
			t = new MenuButton(50, 100, 200, 100){
				double pos = 0;
				
				@Override
				public void click(){
					Game.get().stop();
				}
				
				@Override
				public void keyActionFocused(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
					super.keyActionFocused(button, press, shift, alt, ctrl);
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
			t.setFontColor(new ZColor(0));
			t.setFont(new GameFont(FontManager.getDefaultFontAsset(), 50, 0, 0));
			base.addThing(t);
			
			t = new MenuButton(50, 220, 200, 50){
				@Override
				public void click(){
					createPopup();
				}
			};
			t.setText("popup");
			t.setTextY(45);
			t.setFont(new GameFont(FontManager.getDefaultFontAsset(), 25, 0, 0));
			t.setFontColor(new ZColor(0));
			base.addThing(t);
			
			base.getAllThings().addClass(MenuTextBox.class);
			makeTextBox(base,100, MenuTextBox.Mode.DEFAULT);
			makeTextBox(base,160, MenuTextBox.Mode.INT);
			makeTextBox(base,220, MenuTextBox.Mode.INT_POS);
			makeTextBox(base,280, MenuTextBox.Mode.FLOAT);
			makeTextBox(base,340, MenuTextBox.Mode.FLOAT_POS);
			
			this.addThing(scrollX);
			this.addThing(scrollY);
		}
		
		private void makeTextBox(MenuThing base, double y, MenuTextBox.Mode mode){
			var textBox = new MenuTextBox(300, y, 300, 50){
				@Override
				public void click(){
					if(mode == Mode.INT || mode == Mode.INT_POS) ZStringUtils.prints(getTextAsInt());
					else if(mode == Mode.FLOAT || mode == Mode.FLOAT_POS) ZStringUtils.prints(getTextAsDouble());
					else ZStringUtils.prints(getText());
				}
			};
			textBox.setHint("Type " + mode.name().toLowerCase());
			textBox.setFontSize(32);
			textBox.setMode(mode);
			base.addThing(textBox);
		}
		
		public void createPopup(){
			Menu menu = new Menu(){
				@Override
				public void render(Renderer r, ZRect2D bounds){
					super.render(r, bounds);
					r.setColor(.2, .2, .4, .3);
					r.fill();
				}
			};
			MenuButton b = new MenuButton(100, 100, 300, 100){
				@Override
				public void click(){
					state.removeTopMenu();
				}
			};
			b.setText("exit popup");
			b.setFontColor(new ZColor(0));
			b.setTextY(90);
			b.setFont(new GameFont(FontManager.getDefaultFontAsset(), 32, 0, 0));
			menu.addThing(b);
			this.state.popupMenu(menu);
		}
	}
}
