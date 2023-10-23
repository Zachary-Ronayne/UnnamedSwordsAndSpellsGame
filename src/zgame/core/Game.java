package zgame.core;

import static org.lwjgl.opengl.GL30.*;

import com.google.gson.JsonObject;

import zgame.core.file.Saveable;
import zgame.core.file.ZJsonFile;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.camera.CameraAxis;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.graphics.font.FontManager;
import zgame.core.graphics.font.GameFont;
import zgame.core.graphics.font.FontAsset;
import zgame.core.graphics.image.GameImage;
import zgame.core.graphics.image.ImageManager;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.sound.EffectsPlayer;
import zgame.core.sound.MusicPlayer;
import zgame.core.sound.SoundManager;
import zgame.core.sound.SoundSource;
import zgame.core.state.DefaultState;
import zgame.core.state.GameState;
import zgame.core.state.PlayState;
import zgame.core.utils.ZConfig;
import zgame.core.window.GlfwWindow;
import zgame.core.window.GameWindow;
import zgame.settings.SettingType;
import zgame.settings.Settings;
import zgame.stat.DefaultStatType;
import zgame.world.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * The central class used to create a game. Create an extension of this class to begin making a game
 */
public class Game implements Saveable, Destroyable{
	
	/**
	 * By default, the number of times a second the sound will be updated, i.e. updating streaming sounds, checking if sounds are still playing, checking which sounds need to
	 * play, etc. Generally shouldn't modify the value in a {@link Game}, but it can be modified through {@link Game#setSoundUpdates(int)} Setting the value too low can result
	 * in sounds getting stuck, particularly streaming sounds, i.e. music
	 */
	public static final int DEFAULT_SOUND_UPDATES = 100;
	
	/** The {@link GlfwWindow} used by this {@link Game} as the core interaction */
	private final GameWindow window;
	
	/** The {@link SoundManager} used by this {@link Game} to create sounds */
	private final SoundManager sounds;
	
	/** The {@link ImageManager} used by this {@link Game} to load images for ease of use in rendering */
	private final ImageManager images;
	
	/** The {@link FontManager} used by this {@link Game} to load fonts for rendering text */
	private final FontManager fonts;
	
	/** The looper to run the main OpenGL loop */
	private final GameLooper renderLooper;
	
	/** The Camera which determines the relative location and scale of objects drawn in the game */
	private final GameCamera camera;
	
	/** The {@link GameState} which this game is currently in */
	private GameState currentState;
	/** The {@link GameState} which this game will update to in the next tick, or null if the state will not update */
	private GameState nextCurrentState;
	/** The {@link PlayState} of this game, can be null if there is currently no play state */
	private PlayState playState;
	/** A {@link GameState} that needs to be destroyed on the next OpenGL loop, or null if one doesn't need to be destroyed */
	private GameState destroyState;
	
	/** The {@link GameLooper} which runs the regular time intervals */
	private final GameLooper tickLooper;
	/** The {@link Thread} which runs the game tick loop. This is a separate thread from the main thread, which the OpenGL loop will run on */
	private Thread tickThread;
	/** The {@link Runnable} used by {@link #tickThread} to run its thread */
	private TickLoopTask tickTask;
	/**
	 * The factor in which time passes during each game tick, i.e. this number is multiplied to dt each time the main loop calls {@link #tick(double)} Values higher than 1
	 * make the game faster, values less than 1 make the game slower, this value will not go below 0
	 */
	private double gameSpeed;
	/** The approximate total amount of time, in seconds, which the game has been ticked */
	private double totalTickTime;
	
	/** The {@link GameLooper} which regularly updates the sound */
	private final GameLooper soundLooper;
	/** The {@link Thread} which runs the game sound loop. This is a separate thread from the main thread, which the OpenGL loop will run on */
	private Thread soundThread;
	/** The {@link Runnable} used by {@link #soundThread} to run its thread */
	private SoundLoopTask soundTask;
	
	/** true if the game should only render frames when the game window has focus, false otherwise */
	private boolean focusedRender;
	/** true if the game should only update the state of the game when the game window has focus, false otherwise. If the game is not updating, this will also pause all sound */
	private boolean focusedUpdate;
	/** true if the game should only render frames when the game window is not minimized, false otherwise */
	private boolean minimizedRender;
	/** true if the game should only update the state of the game when the game is not minimized, false otherwise. If the game is not updating, this will also pause all sound */
	private boolean minimizedUpdate;
	/** Tracks if the sound effects were paused before pausing them due to the window losing focus or being minimized */
	private boolean effectsPaused;
	/** Tracks if the music was paused before pausing it due to the window losing focus or being minimized */
	private boolean musicPaused;
	/** Used to track if the state of the paused sounds have been updated since the window lost or gained focused, or was minimized or unminimized */
	private boolean updateSoundState;
	
	/** A list of things to do the next time this game has its open gl loop called. Once the loop happens, this list will be emptied */
	private final List<Runnable> nextLoopFuncs;
	
	/** The settings used by this game */
	private Settings settings;
	
	/** A simple helper class used by {@link #tickLooper} to run its loop on a separate thread */
	private class TickLoopTask implements Runnable{
		@Override
		public void run(){
			tickLooper.loop();
		}
	}
	
	/** A simple helper class used by {@link #soundLooper} to run its loop on a separate thread */
	private class SoundLoopTask implements Runnable{
		@Override
		public void run(){
			soundLooper.loop();
		}
	}
	
	/**
	 * Create a {@link Game} with no special parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 */
	public Game(){
		this("Game");
	}
	
	/**
	 * Create a {@link Game} with the given name. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 *
	 * @param title The title of the game to be displayed on the window
	 */
	public Game(String title){
		this(title, 1280, 720, 200, true, false, false, true);
	}
	
	/**
	 * Create a game with the given parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 *
	 * @param title The title of the game to be displayed on the window
	 * @param winWidth The current width of the window in pixels, this does not include decorators such as the minimize button
	 * @param winHeight The current height of the window in pixels, this does not include decorators such as the minimize button
	 * @param maxFps The maximum frames per second the game can draw, use 0 for unlimited FPS, does nothing if useVsync is true
	 * @param useVsync true to lock the framerate to the display refresh rate, false otherwise
	 * @param enterFullScreen True to immediately enter fullscreen
	 * @param stretchToFill true if, when drawing the final Renderer image to the screen, the image should stretch to fill up the entire screen, false to draw the image in
	 * 		the center of the screen leave black bars in areas that the image doesn't fill up
	 * @param printFps true to print the fps every second
	 */
	public Game(String title, int winWidth, int winHeight, int maxFps, boolean useVsync, boolean enterFullScreen, boolean stretchToFill, boolean printFps){
		this(title, winWidth, winHeight, winWidth, winHeight, maxFps, useVsync, enterFullScreen, stretchToFill, printFps, 60, true);
	}
	
	/**
	 * Create a {@link Game} with the given parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 *
	 * @param title The title of the game to be displayed on the window
	 * @param winWidth See The current width of the window in pixels, this does not include decorators such as the minimize button
	 * @param winHeight The current height of the window in pixels, this does not include decorators such as the minimize button
	 * @param screenWidth The width, in pixels, of the internal buffer to draw to
	 * @param screenHeight The height, in pixels, of the internal buffer to draw to
	 * @param maxFps The maximum frames per second the game can draw, use 0 for unlimited FPS, does nothing if useVsync is true
	 * @param useVsync true to lock the framerate to the display refresh rate, false otherwise
	 * @param enterFullScreen True to immediately enter fullscreen
	 * @param stretchToFill true if, when drawing the final Renderer image to the screen, the image should stretch to fill up the entire screen, false to draw the image in
	 * 		the center of the screen leave black bars in areas that the image doesn't fill up
	 * @param printFps true to, every second, print the number of frames rendered in that last second, false otherwise
	 * @param tps The number of ticks per second
	 * @param printTps true to, every second, print the number of ticks that occurred in that last second, false otherwise
	 */
	public Game(String title, int winWidth, int winHeight, int screenWidth, int screenHeight, int maxFps, boolean useVsync, boolean enterFullScreen, boolean stretchToFill, boolean printFps, int tps, boolean printTps){
		this.nextLoopFuncs = new ArrayList<>();
		
		// Init misc values
		this.gameSpeed = 1;
		this.totalTickTime = 0;
		
		this.focusedRender = false;
		this.focusedUpdate = false;
		this.minimizedRender = false;
		this.minimizedUpdate = false;
		this.effectsPaused = false;
		this.musicPaused = false;
		this.updateSoundState = false;
		
		this.currentState = new DefaultState();
		this.nextCurrentState = null;
		this.playState = null;
		this.destroyState = null;
		
		// Init stat enum
		DefaultStatType.init();
		
		// Init settings enum and this game's instance of settings
		SettingType.initCore();
		this.settings = new Settings(this);
		
		// Init sound
		this.sounds = new SoundManager();
		
		// Init window
		this.window = new GlfwWindow(title, winWidth, winHeight, screenWidth, screenHeight, maxFps, useVsync, stretchToFill, printFps, tps, printTps);
		
		// Init images
		this.images = new ImageManager();
		
		// Init fonts and set the default font
		this.fonts = new FontManager();
		this.fonts.add("zfont");
		this.getWindow().getRenderer().setFont(this.getFont("zfont"));
		
		// Init camera
		this.camera = new GameCamera();
		
		// Set up lambda calls for input
		this.window.setKeyActionMethod(this::keyAction);
		this.window.setMouseActionMethod(this::mouseAction);
		this.window.setMouseMoveMethod(this::mouseMove);
		this.window.setMouseWheelMoveMethod(this::mouseWheelMove);
		
		// Create the main loop
		this.renderLooper = new GameLooper(maxFps, this::loopFunction, this::shouldRender, this::keepRenderLoopFunction, this::renderLoopWaitFunction, "FPS", printFps);
		
		// Create the tick loop
		this.tickLooper = new GameLooper(tps, this::tickLoopFunction, this::shouldTick, this::keepTickLoopFunction, this::tickLoopWaitFunction, "TPS", printTps);
		
		// Create the sound loop
		this.soundLooper = new GameLooper(DEFAULT_SOUND_UPDATES, this::updateSounds, this::shouldUpdateSound, this::keepSoundLoopFunction, this::soundLoopWaitFunction,
				"Audio", false);
		
		// Go to fullscreen if applicable
		this.window.setInFullScreenNow(enterFullScreen);
	}
	
	/**
	 * Begin running the main OpenGL loop. When the loop ends, the cleanup method is automatically run. Do not manually call {@link #end()}, terminate the main loop instead.
	 * Calling this method will run the loop on the currently executing thread. This should only be the main Java thread, not an external thread. In parallel to the main
	 * thread, a second thread will run, which runs the game tick loop, and a third thread will run which updates the sounds
	 */
	public void start(){
		// Run the tick loop on its own thread first
		this.tickTask = new TickLoopTask();
		this.tickThread = new Thread(this.tickTask);
		this.tickThread.start();
		
		// Run the audio loop
		this.soundTask = new SoundLoopTask();
		this.soundThread = new Thread(this.soundTask);
		this.soundThread.start();
		
		// Run the render loop in the main thread
		this.renderLooper.loop();
		
		// End the program
		this.end();
	}
	
	/** Force the game to stop, but ensure the game closes without errors */
	public void stop(){
		try{
			this.soundLooper.end();
			while(this.soundLooper.isRunning()) Thread.sleep(1);
			this.tickLooper.end();
			while(this.tickLooper.isRunning()) Thread.sleep(1);
		}catch(InterruptedException e){
			ZConfig.error(e);
		}
		this.renderLooper.end();
	}
	
	/**
	 * End the program, freeing all resources
	 */
	private void end(){
		this.destroy();
	}
	
	@Override
	public void destroy(){
		// End the loopers
		this.renderLooper.end();
		this.tickLooper.end();
		
		// Free memory / destroy callbacks
		this.getWindow().destroy();
		
		// Free sounds
		this.sounds.destroy();
		
		// Free images
		this.images.destroy();
	}
	
	/**
	 * Run a function on the next OpenGL loop
	 * @param r The function to run
	 */
	public void onNextLoop(Runnable r){
		this.nextLoopFuncs.add(r);
	}
	
	/**
	 * Called when the window receives a key press. Can overwrite this method to perform actions directly when keys are pressed. Can also provide this {@link Game} with a
	 * {@link GameState} via {@link #setCurrentState(GameState)} to perform that state's actions.
	 *
	 * @param button The id of the key
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	protected void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.getCurrentState().keyAction(this, button, press, shift, alt, ctrl);
	}
	
	/**
	 * Called when the window receives a mouse button press. Can overwrite this method to perform actions directly when mouse buttons are pressed Can also provide this
	 * {@link Game} with a {@link GameState} via {@link #setCurrentState(GameState)} to perform that state's actions.
	 *
	 * @param button The ID of the mouse button
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	protected void mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.getCurrentState().mouseAction(this, button, press, shift, alt, ctrl);
	}
	
	/**
	 * Called when the window receives mouse movement. Can overwrite this method to perform actions directly when the mouse is moved Can also provide this {@link Game} with a
	 * {@link GameState} via {@link #setCurrentState(GameState)} to perform that state's actions.
	 *
	 * @param x The x coordinate in screen coordinates
	 * @param y The y coordinate in screen coordinates
	 */
	protected void mouseMove(double x, double y){
		this.getCurrentState().mouseMove(this, x, y);
	}
	
	/**
	 * Called when the window receives a mouse wheel movement. Can overwrite this method to perform actions directly when the mouse wheel is moved Can also provide this
	 * {@link Game} with a {@link GameState} via {@link #setCurrentState(GameState)} to perform that state's actions.
	 *
	 * @param amount The amount the scroll wheel was moved
	 */
	protected void mouseWheelMove(double amount){
		this.getCurrentState().mouseWheelMove(this, amount);
	}
	
	/**
	 * The function run by the rendering GameLooper as its main loop for OpenGL. This handles calling all the appropriate rendering methods and associated window methods for
	 * the main loop
	 */
	private void loopFunction(){
		try{
			// Update the state of the game
			this.updateCurrentState();
			
			// Run any functions that need to be run on the next OpenGL loop
			if(!this.nextLoopFuncs.isEmpty()){
				for(var f : this.nextLoopFuncs) f.run();
				this.nextLoopFuncs.clear();
			}
			
			// Update the window
			boolean focused = this.getWindow().isFocused();
			boolean minimized = this.getWindow().isMinimized();
			this.getWindow().checkEvents();
			
			// Only perform rendering operations if the window should be rendered, based on the state of the window's focus and minimize
			if(!(this.isFocusedRender() && !focused) && !(this.isMinimizedRender() && minimized)){
				// Clear the main framebuffer
				glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				glBindFramebuffer(GL_FRAMEBUFFER, 0);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				
				// Clear the internal renderer and set it up to use the renderer's frame buffer to draw to
				Renderer r = this.getWindow().getRenderer();
				r.clear();
				
				// Render objects using the renderer's frame buffer
				r.initToDraw();
				
				// Draw the background
				r.setCamera(null);
				r.identityMatrix();
				this.renderBackground(r);
				
				// Draw the foreground, i.e. main objects
				// Set the camera
				boolean useCam = this.getCurrentState().isUseCamera();
				if(useCam) r.setCamera(this.getCamera());
				else r.setCamera(null);
				// Move based on the camera, if applicable, and draw the objects
				r.identityMatrix();
				if(useCam) this.getCamera().transform(this.getWindow());
				this.render(r);
				
				// Draw the hud
				r.setCamera(null);
				r.identityMatrix();
				this.renderHud(r);
				
				// Draw the renderer's frame buffer to the window
				r.drawToWindow(this.getWindow());
			}
			// Update the window
			this.getWindow().swapBuffers();
			
			// Check if a state needs to be destroyed
			if(this.destroyState != null) this.destroyState.destroy();
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	/**
	 * Called once each time a frame is rendered to the screen, before the main render. Use this method to define what is drawn as a background, i.e. unaffected by the camera
	 * Do not manually call this method Can also provide this {@link Game} with a {@link GameState} via {@link #setCurrentState(GameState)} to perform that state's actions.
	 *
	 * @param r The Renderer to use for drawing
	 */
	protected void renderBackground(Renderer r){
		this.getCurrentState().renderBackground(this, r);
	}
	
	/**
	 * Called once each time a frame is rendered to the screen. Use this method to define what is drawn in the game each frame. Do not manually call this method. All objects
	 * drawn with this method will be affected by the game camera Can also provide this {@link Game} with a {@link GameState} via {@link #setCurrentState(GameState)} to
	 * perform that state's actions.
	 *
	 * @param r The Renderer to use for drawing
	 */
	protected void render(Renderer r){
		this.getCurrentState().render(this, r);
	}
	
	/**
	 * Called once each time a frame is rendered to the screen, after the main render. Use this method to define what is drawn on top of the screen, i.e. a hud, menu, etc. Do
	 * not manually call this method Can also provide this {@link Game} with a {@link GameState} via {@link #setCurrentState(GameState)} to perform that state's actions.
	 *
	 * @param r The Renderer to use for drawing
	 */
	protected void renderHud(Renderer r){
		this.getCurrentState().renderHud(this, r);
	}
	
	/**
	 * The function used to determine if each the main OpenGL loop should draw a frame
	 *
	 * @return true if the next frame should be drawn regardless, false otherwise
	 */
	protected boolean shouldRender(){
		return this.getWindow().usesVsync();
	}
	
	/**
	 * The function used to determine if the main OpenGL loop should end
	 *
	 * @return true if the loop should continue, false otherwise
	 */
	protected boolean keepRenderLoopFunction(){
		return this.getWindow().shouldClose();
	}
	
	/**
	 * The function used to determine if the main OpenGL loop should wait between rendering each frame
	 *
	 * @return true if the loop should wait, false otherwise
	 */
	protected boolean renderLoopWaitFunction(){
		return !this.getWindow().usesVsync();
	}
	
	/**
	 * The function run by the tick GameLooper as its main loop
	 */
	private void tickLoopFunction(){
		try{
			boolean focused = this.getWindow().isFocused();
			boolean minimized = this.getWindow().isMinimized();
			// If the game should pause when unfocused or minimized, then do nothing
			if(this.isFocusedUpdate() && !focused || this.isMinimizedUpdate() && minimized) return;
			this.tick(this.getTickLooper().getRateTime() * this.getGameSpeed());
			
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	/**
	 * Called each time a tick occurs. A tick is a game update, i.e. some amount of time passing
	 *
	 * @param dt The amount of time, in seconds, which passed in this tick
	 */
	protected void tick(double dt){
		this.totalTickTime += dt;
		this.getCurrentState().tick(this, dt);
	}
	
	/**
	 * The function used to determine if the tick loop should update each loop iteration regardless of time
	 *
	 * @return Usually false, unless this method is overwritten with different behavior
	 */
	protected boolean shouldTick(){
		return false;
	}
	
	/**
	 * The function used to determine if the tick loop should end
	 *
	 * @return Usually the same result as {@link #keepRenderLoopFunction()}, unless this method is overwritten with different behavior
	 */
	protected boolean keepTickLoopFunction(){
		return this.keepRenderLoopFunction();
	}
	
	/**
	 * The function used to determine if the main tick loop should wait between running each tick
	 *
	 * @return true if the loop should wait, false otherwise
	 */
	protected boolean tickLoopWaitFunction(){
		return ZConfig.waitBetweenTicks();
	}
	
	/** Update the sound state, what sounds are playing, if sounds should be muted, etc */
	private void updateSounds(){
		try{
			SoundManager sm = this.getSounds();
			EffectsPlayer ep = sm.getEffectsPlayer();
			MusicPlayer mp = sm.getMusicPlayer();
			sm.update();
			boolean focused = this.getWindow().isFocused();
			boolean minimized = this.getWindow().isMinimized();
			if(this.isFocusedUpdate() && !focused || this.isMinimizedUpdate() && minimized){
				// If the sound has not yet been paused since needing to pause, then save the pause state of the sound players, and then pause them both
				if(!this.updateSoundState){
					this.effectsPaused = ep.isPaused();
					this.musicPaused = ep.isPaused();
					ep.pause();
					mp.pause();
					this.updateSoundState = true;
				}
			}
			else{
				// If the sound has not been unpaused since no longer needing to be paused, set the pause state to what it was before the pause
				if(this.updateSoundState){
					ep.setPaused(this.effectsPaused);
					mp.setPaused(this.musicPaused);
					this.updateSoundState = false;
				}
			}
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	/**
	 * The function used to determine if the sound loop should update each loop iteration regardless of time
	 *
	 * @return Usually false, unless this method is overwritten with different behavior
	 */
	protected boolean shouldUpdateSound(){
		return false;
	}
	
	/**
	 * The function used to determine if the sound loop should end
	 *
	 * @return Usually the same result as {@link #keepRenderLoopFunction()}, unless this method is overwritten with different behavior
	 */
	protected boolean keepSoundLoopFunction(){
		return this.keepRenderLoopFunction();
	}
	
	/**
	 * The function used to determine if the sound loop should wait between running each tick
	 *
	 * @return Usually true, unless this method is overwritten with different behavior
	 */
	protected boolean soundLoopWaitFunction(){
		return true;
	}
	
	/** @return See {@link #focusedRender} */
	public boolean isFocusedRender(){
		return this.focusedRender;
	}
	
	/** @param focusedRender See {@link #focusedRender} */
	public void setFocusedRender(boolean focusedRender){
		this.focusedRender = focusedRender;
	}
	
	/** @return See {@link #focusedUpdate} */
	public boolean isFocusedUpdate(){
		return this.focusedUpdate;
	}
	
	/** @param focusedUpdate See {@link #focusedUpdate} */
	public void setFocusedUpdate(boolean focusedUpdate){
		this.focusedUpdate = focusedUpdate;
	}
	
	/** @return See {@link #minimizedRender} */
	public boolean isMinimizedRender(){
		return this.minimizedRender;
	}
	
	/** @param minimizedRender See {@link #minimizedRender} */
	public void setMinimizedRender(boolean minimizedRender){
		this.minimizedRender = minimizedRender;
	}
	
	/** @return See {@link #minimizedUpdate} */
	public boolean isMinimizedUpdate(){
		return this.minimizedUpdate;
	}
	
	/** @param minimizedUpdate See {@link #minimizedUpdate} */
	public void setMinimizedUpdate(boolean minimizedUpdate){
		this.minimizedUpdate = minimizedUpdate;
	}
	
	/** @return See {@link #window} */
	public GameWindow getWindow(){
		return window;
	}
	
	/**
	 * Load the necessary contents of this {@link Game} from a json file at the given path
	 *
	 * @param path The path to load from, including file extension
	 * @return true if the load was successful, false otherwise
	 */
	public boolean loadGame(String path){
		if(path == null) return false;
		ZJsonFile file = new ZJsonFile(path);
		JsonObject data = file.load();
		if(data == null) return false;
		try{
			return this.load(data);
		}catch(ClassCastException | IllegalStateException | NullPointerException e){
			ZConfig.error(e, "Failed to load a json object because it had invalid formatting. Object data:\n", data);
		}
		return false;
	}
	
	/**
	 * Save the necessary contents of this {@link Game} to a json file at the given path
	 *
	 * @param path The path to save to, including file extension
	 * @return true if the save was successful, false otherwise
	 */
	public boolean saveGame(String path){
		if(path == null) return false;
		try{
			ZJsonFile file = new ZJsonFile(path);
			JsonObject data = file.getData();
			this.save(data);
			file.setData(data);
			return file.save();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/** @return See {@link #sounds} */
	public SoundManager getSounds(){
		return sounds;
	}
	
	/**
	 * A convenience method, this method is equivalent to {@link SoundManager#playEffect(SoundSource, String)}
	 *
	 * @param source The source to play the effect on
	 * @param name The name of the sound to play
	 */
	public void playEffect(SoundSource source, String name){
		this.getSounds().playEffect(source, name);
	}
	
	/**
	 * A convenience method, this method is equivalent to{@link SoundManager#playMusic(String)}
	 *
	 * @param name The name of the music to play
	 */
	public void playMusic(String name){
		this.getSounds().playMusic(name);
	}
	
	/** @return See {@link #images} */
	public ImageManager getImages(){
		return this.images;
	}
	
	/** @return The image from {@link #images} with the given name */
	public GameImage getImage(String name){
		return this.getImages().get(name);
	}
	
	/** @return See {@link #fonts} */
	public FontManager getFonts(){
		return this.fonts;
	}
	
	/** @return See {@link FontManager#get(String)} */
	public FontAsset getFontAsset(String font){
		return this.getFonts().get(font);
	}
	
	/** @return A {@link GameFont} with the given font name */
	public GameFont getFont(String font){
		return new GameFont(this.getFonts().get(font));
	}
	
	/** @return The default font */
	public GameFont getDefaultFont(){
		return getFont("zfont");
	}
	
	/**
	 * @return The maximum number of frames to render per second. Use 0 for unlimited framerate. This value does nothing if vsync is turned on
	 */
	public int getMaxFps(){
		return this.getRenderLooper().getRate();
	}
	
	/** @param maxFps See {@link #getMaxFps()} */
	public void setMaxFps(int maxFps){
		this.renderLooper.setRate(maxFps);
	}
	
	/** @return The width, in pixels, of the internal buffer */
	public int getScreenWidth(){
		return this.getWindow().getScreenWidth();
	}
	
	/** @return The height, in pixels, of the internal buffer */
	public int getScreenHeight(){
		return this.getWindow().getScreenHeight();
	}
	
	/** @return The game x coordinate on the left side of what is displayed on the screen */
	public double getScreenLeft(){
		return this.getCamera().sizeScreenToGameX(-this.getCamera().getX().getPos());
	}
	
	/** @return The game x coordinate on the right side of what is displayed on the screen */
	public double getScreenRight(){
		return this.getScreenLeft() + this.getCamera().sizeScreenToGameX(this.getScreenWidth());
	}
	
	/** @return The game y coordinate of the top of what is displayed on the screen */
	public double getScreenTop(){
		return this.getCamera().sizeScreenToGameY(-this.getCamera().getY().getPos());
	}
	
	/** @return The game y coordinate at the bottom of what is displayed on the screen */
	public double getScreenBottom(){
		return this.getScreenTop() + this.getCamera().sizeScreenToGameY(this.getScreenHeight());
	}
	
	/** @return See {@link #renderLooper} */
	public GameLooper getRenderLooper(){
		return this.renderLooper;
	}
	
	/** @return See {@link #tickLooper} */
	public GameLooper getTickLooper(){
		return this.tickLooper;
	}
	
	/** @return true if the fps should be printed once each second, false otherwise */
	public boolean isPrintFps(){
		return this.getRenderLooper().willPrintRate();
	}
	
	/** @param print See {@link #isPrintFps()} */
	public void setPrintFps(boolean print){
		this.renderLooper.setPrintRate(print);
	}
	
	/** @return The number of times each second that this {@link Game} runs a game tick */
	public int getTps(){
		return this.tickLooper.getRate();
	}
	
	/** @param tps See {@link #getTps()} */
	public void setTps(int tps){
		tps = Math.max(1, tps);
		this.tickLooper.setRate(tps);
	}
	
	/** @return See {@link #gameSpeed} */
	public double getGameSpeed(){
		return this.gameSpeed;
	}
	
	/** @param gameSpeed See {@link #gameSpeed} */
	public void setGameSpeed(double gameSpeed){
		this.gameSpeed = Math.max(0, gameSpeed);
	}
	
	/** @return See {@link #totalTickTime} */
	public double getTotalTickTime(){
		return this.totalTickTime;
	}
	
	/** @return true if the tps should be printed once each second, false otherwise */
	public boolean isPrintTps(){
		return this.tickLooper.willPrintRate();
	}
	
	/** @param print See {@link #isPrintTps()} */
	public void setPrintTps(boolean print){
		this.tickLooper.setPrintRate(print);
	}
	
	/** @return The number of times each second that the sound will update */
	public int getSoundUpdates(){
		return this.soundLooper.getRate();
	}
	
	/** @param s See {@link #getSoundUpdates()} */
	public void setSoundUpdates(int s){
		this.soundLooper.setRate(s);
	}
	
	/** @return true if, once per second, the number of audio updates in that second should be printed */
	public boolean isPrintSoundUpdates(){
		return this.soundLooper.willPrintRate();
	}
	
	/** @param print true to, once per second, print the number of audio updates in that second, false otherwise */
	public void setPrintSoundUpdates(boolean print){
		this.soundLooper.setPrintRate(print);
	}
	
	/** @return Get the object tracking mouse input for this {@link Game} */
	public ZMouseInput getMouseInput(){
		return this.getWindow().getMouseInput();
	}
	
	/** @return Get the object tracking keyboard input for this {@link Game} */
	public ZKeyInput getKeyInput(){
		return this.getWindow().getKeyInput();
	}
	
	/** @return See {@link #camera} */
	public GameCamera getCamera(){
		return this.camera;
	}
	
	/**
	 * Center the camera to the given coordinates
	 *
	 * @param x The center of the camera x coordinate in game coordinates
	 * @param y The center of the camera y coordinate in game coordinates
	 */
	public void centerCamera(double x, double y){
		this.camera.setPos(this.getScreenWidth() * 0.5 - this.camera.sizeGameToScreenX(x), this.getScreenHeight() * 0.5 - this.camera.sizeGameToScreenY(y));
	}
	
	/** @return See {@link #currentState} */
	public GameState getCurrentState(){
		return this.currentState;
	}
	
	/**
	 * If the state should do nothing, use a {@link DefaultState} This method updates the state on the next OpenGL loop. The previously existing state will be destroyed and no
	 * longer usable This method should only ever set the state with a new instance of a state object
	 *
	 * @param newState See {@link #currentState}
	 */
	public void setCurrentState(GameState newState){
		this.nextCurrentState = newState;
	}
	
	/**
	 * This method instantly updates the state. Do not call this method outside of the main OpenGL loop
	 */
	private void updateCurrentState(){
		if(this.nextCurrentState == null) return;
		this.destroyState = this.currentState;
		this.currentState = this.nextCurrentState;
		this.playState = this.currentState.asPlay();
		this.currentState.onSet(this);
		this.nextCurrentState = null;
	}
	
	/** @return See {@link #playState} */
	public PlayState getPlayState(){
		return this.playState;
	}
	
	/** @return The {@link Room} that the current {@link #playState} is using, or null if there is no play state */
	public Room getCurrentRoom(){
		PlayState p = this.getPlayState();
		if(p == null) return null;
		return p.getCurrentRoom();
	}
	
	/**
	 * Zoom in the screen with {@link #camera} on just the x axis The zoom will reposition the camera so that the given coordinates are zoomed towards These coordinates are
	 * screen coordinates
	 *
	 * @param zoom The factor to zoom in by, which will be added to {@link CameraAxis#zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 * @param x The x coordinate to base the zoom
	 */
	public void zoomX(double zoom, double x){
		this.getCamera().getX().zoom(zoom, x, this.getScreenWidth());
	}
	
	/**
	 * Zoom in on just the y axis The zoom will reposition the camera so that the given coordinates are zoomed towards These coordinates are screen coordinates
	 *
	 * @param zoom The factor to zoom in by, which will be added to {@link CameraAxis#zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 * @param y The y coordinate to base the zoom
	 */
	public void zoomY(double zoom, double y){
		this.getCamera().getY().zoom(zoom, y, this.getScreenHeight());
	}
	
	/**
	 * Zoom in on both axes The zoom will reposition the camera so that the given coordinates are zoomed towards These coordinates are screen coordinates
	 *
	 * @param zoom The factor to zoom in by, which will be added to {@link CameraAxis#zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 * @param x The x coordinate to base the zoom
	 * @param y The y coordinate to base the zoom
	 */
	public void zoom(double zoom, double x, double y){
		this.zoomX(zoom, x);
		this.zoomY(zoom, y);
	}
	
	/** @return The current x coordinate of the mouse in screen coordinates. Should use for things that do not move with the camera */
	public double mouseSX(){
		return this.getMouseInput().x();
	}
	
	/** @return The current y coordinate of the mouse in screen coordinates. Should use for things that do not move with the camera */
	public double mouseSY(){
		return this.getMouseInput().y();
	}
	
	/** @return The current x coordinate of the mouse in game coordinates. Should use for things that move with the camera */
	public double mouseGX(){
		return this.getCamera().screenToGameX(this.mouseSX());
	}
	
	/** @return The current y coordinate of the mouse in game coordinates. Should use for things that move with the camera */
	public double mouseGY(){
		return this.getCamera().screenToGameY(this.mouseSY());
	}
	
	/** @return See {@link #settings} */
	public Settings getSettings(){
		return this.settings;
	}
	
	// TODO add convenience methods to directly get different types of settings, i.e. int, double, etc
}
