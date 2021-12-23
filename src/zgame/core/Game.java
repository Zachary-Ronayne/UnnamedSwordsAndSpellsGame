package zgame.core;

import static org.lwjgl.opengl.GL30.*;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.sound.SoundManager;
import zgame.core.utils.ZConfig;
import zgame.core.window.GLFWWindow;
import zgame.core.window.GameWindow;

/**
 * The central class used to create a game. Create an extension of this class to begin making a game
 */
public class Game{
	
	/*
	 * 
	 * TODO fix issue of sound playing a click noise when it comes to its natural end
	 * 
	 * TODO use alSourcei(source, AL_SOURCE_RELATIVE, AL_TRUE); to make music global?
	 * 
	 * TODO fix sound issue where pressing a key to play a sound too early will cause a crash
	 * 
	 * TODO find out how to do sound scaling, like the position of the listener vs source
	 * 
	 * TODO buffer sound for music
	 * 
	 * TODO add adjustable sound volume, i.e. SoundPlayer and SoundManager should have volume scalars, which trickle down to each individual sound
	 * 
	 * TODO refactor some things, change the names of the method calls in GameWindow, like instead of beginning and end, call it swap buffers or whatever
	 * 
	 * TODO create an image manager, something similar to the sound manager that you can give images, and it will handle creation and freeing resources
	 * 
	 * TODO add option to turn off rendering/sounds/ticking when the window is minimized or not in focus or not visible
	 * 
	 * TODO add game speed option, i.e. change the amount of time that passes in each main call to tick via a multiplier, also change the speed of sound playback, also add a pause function
	 * 
	 * TODO add catagories of sound, i.e. you could have voices, background noise, footsteps, and have all of them volume controlled differently
	 * 
	 * TODO go through code and remedy any inconsistancies
	 * 
	 * TODO update code formatting, lines too long so add wrapping, allow for space between block statements and comments, update comments to always link to classes and methods
	 * 
	 */
	
	/** The {@link GLFWWindow} used by this {@link Game} as the core interaction */
	private GameWindow window;
	
	/** The {@link SoundManager} used by this {@link Game} to create sounds */
	private SoundManager sounds;
	
	/** The looper to run the main OpenGL loop */
	private GameLooper renderLooper;
	
	/** The Camera which determines the relative location and scale of objects drawn in the game */
	private GameCamera camera;
	
	/** The {@link GameLooper} which runs the regular time intervals */
	private GameLooper tickLooper;
	/** The {@link Thread} which runs the game tick loop. This is a separate thread from the main thread, which the OpenGL loop will run on */
	private Thread tickThread;
	/** The {@link Runnable} used by {@link #tickThread} to run its thread */
	private TickLoopTask tickTask;
	
	/** A simple helper class used by {@link #tickLooper} to run its loop on a separate thread */
	private class TickLoopTask implements Runnable{
		@Override
		public void run(){
			tickLooper.loop();
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
	 * @param stretchToFill true if, when drawing the final Renderer image to the screen, the image should stretch to fill up the entire screen,
	 *        false to draw the image in the center of the screen leave black bars in areas that the image doesn't fill up
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
	 * @param stretchToFill true if, when drawing the final Renderer image to the screen, the image should stretch to fill up the entire screen,
	 *        false to draw the image in the center of the screen leave black bars in areas that the image doesn't fill up
	 */
	public Game(String title, int winWidth, int winHeight, int screenWidth, int screenHeight, int maxFps, boolean useVsync, boolean enterFullScreen, boolean stretchToFill, boolean printFps, int tps, boolean printTps){
		// Init sound
		this.sounds = new SoundManager();
		
		// Init window
		this.window = new GLFWWindow(title, winWidth, winHeight, screenWidth, screenHeight, maxFps, useVsync, stretchToFill, printFps, tps, printTps);
		
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
		
		// Go to fullscreen if applicable
		this.window.setInFullScreenNow(enterFullScreen);
	}
	
	/**
	 * Begin running the main OpenGL loop. When the loop ends, the cleanup method is automatically run. Do not manually call {@link #end()}, terminate the main loop instead.
	 * Calling this method will run the loop on the currently executing thread. This should only be the main Java thread, not an external thread.
	 * In parallel to the main thread, a second thread will run, which runs the game tick loop
	 */
	public void start(){
		// Run the tick loop on its own thread first
		this.tickTask = new TickLoopTask();
		this.tickThread = new Thread(this.tickTask);
		this.tickThread.start();
		
		// Run the render loop in the main thread
		this.renderLooper.loop();
		
		// End the program
		this.end();
	}
	
	/**
	 * End the program, freeing all resources
	 */
	private void end(){
		// End the loopers
		this.renderLooper.end();
		this.tickLooper.end();
		
		// Free memory / destory callbacks
		this.getWindow().end();
		
		// Free sounds
		this.sounds.end();
	}
	
	/**
	 * Called when the window recieves a key press. Can overrite this method to perform actions directly when keys are pressed
	 * 
	 * @param key The id of the key
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	protected void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	/**
	 * Called when the window recieves a mouse button press. Can overrite this method to perform actions directly when mouse buttons are pressed
	 * 
	 * @param button The ID of the mouse button
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	protected void mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	/**
	 * Called when the window recieves mouse movement. Can overrite this method to perform actions directly when the mouse is moved
	 * 
	 * @param x The x coordinate in screen coordinates
	 * @param y The y coordinate in screen coordinates
	 */
	protected void mouseMove(double x, double y){
	}
	
	/**
	 * Called when the window recieves a mouse wheel movement. Can overrite this method to perform actions directly when the mouse wheel is moved
	 * 
	 * @param amount The amount the scroll wheel was moved
	 */
	protected void mouseWheelMove(double amount){
	}
	
	/**
	 * The function run by the rendering GameLooper as its main loop for OpenGL.
	 * This handles calling all the appropriate rendering methods and associated window methods for the main loop
	 */
	private void loopFunction(){
		// Update sounds
		this.getSounds().update();
		
		// Update the window
		this.getWindow().loopBegin();
		
		// Clear the main framebuffer
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Clear the internal renderer
		Renderer r = this.getWindow().getRenderer();
		r.clear();
		
		// Render objects on the renderer
		
		// Set up drawing to the buffer
		glLoadIdentity();
		glViewport(0, 0, this.getScreenWidth(), this.getScreenHeight());
		
		// Draw the background
		r.setCamera(null);
		this.renderBackground(r);
		
		// Draw the foreground, i.e. main objects
		glPushMatrix();
		r.setCamera(this.getCamera());
		r.drawToRenderer();
		this.getCamera().transform(this.getWindow());
		render(r);
		glPopMatrix();
		
		// Draw the hud
		r.setCamera(null);
		this.renderHud(r);
		
		// Draw the renderer to the window
		r.drawToWindow(this.getWindow());
		
		// Update the window
		this.getWindow().loopEnd();
	}
	
	/**
	 * Called once each time a frame is rendered to the screen, before the main render. Use this method to define what is drawn as a background, i.e. unaffected by the camera
	 * Do not manually call this method
	 * 
	 * @param r The Renderer to use for drawing
	 */
	protected void renderBackground(Renderer r){
	}
	
	/**
	 * Called once each time a frame is rendered to the screen. Use this method to define what is drawn in the game each frame.
	 * Do not manually call this method.
	 * All objects drawn with this method will be affected by the game camera
	 * 
	 * @param r The Renderer to use for drawing
	 */
	protected void render(Renderer r){
	}
	
	/**
	 * Called once each time a frame is rendered to the screen, after the main render. Use this method to define what is drawn on top of the screen, i.e. a hud, menu, etc
	 * Do not manually call this method
	 * 
	 * @param r The Renderer to use for drawing
	 */
	protected void renderHud(Renderer r){
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
		this.tick(this.getTickLooper().getRateTime());
	}
	
	/**
	 * Called each time a tick occurs. A tick is a game update, i.e. some amount of time passing
	 * 
	 * @param dt The amount of time, in seconds, which passed in this tick
	 */
	protected void tick(double dt){
	}
	
	/**
	 * The function used to determine if each the tick loop should update each time regardless of time
	 * 
	 * @return Usually false, unless this method is overritten with different behavior
	 */
	protected boolean shouldTick(){
		return false;
	}
	
	/**
	 * The function used to determine if the tick loop should end
	 * 
	 * @return Usually the same result as {@link #keepRenderLoopFunction()}, unless this method is overritten with different behavior
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
	
	/** @return See {@link #window} */
	public GameWindow getWindow(){
		return window;
	}
	
	/** @return See {@link #sounds} */
	public SoundManager getSounds(){
		return sounds;
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
	
	/** @return The number of times each second that this GameWindow runs a game tick */
	public int getTps(){
		return this.tickLooper.getRate();
	}
	
	/** @param See {@link #getTps()} */
	public void setTps(int tps){
		this.tickLooper.setRate(tps);
	}
	
	/** @return true if the tps should be printed once each second, false otherwise */
	public boolean isPrintTps(){
		return this.tickLooper.willPrintRate();
	}
	
	/** @param print See {@link #isPrintTps()} */
	public void setPrintTps(boolean print){
		this.tickLooper.setPrintRate(print);
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
	 * Zoom in the screen with {@link #camera} on just the x axis
	 * The zoom will reposition the camera so that the given coordinates are zoomed towards
	 * These cooridnates are screen coordinates
	 * 
	 * @param zoom The factor to zoom in by, which will be added to {@link #zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 * @param x The x coordinate to base the zoom
	 */
	public void zoomX(double zoom, double x){
		this.getCamera().getX().zoom(zoom, x, this.getScreenWidth());
	}
	
	/**
	 * Zoom in on just the y axis
	 * The zoom will reposition the camera so that the given coordinates are zoomed towards
	 * These cooridnates are screen coordinates
	 * 
	 * @param zoom The factor to zoom in by, which will be added to {@link #zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 * @param y The y coordinate to base the zoom
	 */
	public void zoomY(double zoom, double y){
		this.getCamera().getY().zoom(zoom, y, this.getScreenHeight());
	}
	
	/**
	 * Zoom in on both axes
	 * The zoom will reposition the camera so that the given coordinates are zoomed towards
	 * These cooridnates are screen coordinates
	 * 
	 * @param zoom The factor to zoom in by, which will be added to {@link #zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
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
}
