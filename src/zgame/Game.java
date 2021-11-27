package zgame;

import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import zgame.graphics.Renderer;
import zgame.graphics.camera.GameCamera;
import zgame.input.keyboard.ZKeyInput;
import zgame.input.mouse.ZMouseInput;
import zgame.utils.ZConfig;

/**
 * The central class used to create a game. Create an extension of this class to begin making a game
 */
public abstract class Game{
	
	/*
	 * TODO fix Game and GameWindow restructure
	 * 
	 * TODO implement sound
	 */
	
	/** The {@link GameWindow} which is used by this {@link Game} as core interaction */
	private GameWindow window;
	
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
	
	/** The object tracking mouse input events */
	private ZMouseInput mouseInput;
	
	/** The object tracking keyboard input events */
	private ZKeyInput keyInput;
	
	/**
	 * Create a {@link Game} with no special parameters
	 * This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 */
	public Game(){
		this("Game");
	}
	
	/**
	 * Create a default {@link Game}. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
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
	 * @param winWidth See The current width of the window in pixels, this does not include decorators such as the minimize button
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
		this.window = new GameWindow(this, title, winWidth, winHeight, screenWidth, screenHeight, maxFps, useVsync, stretchToFill, printFps, tps, printTps);
		
		// Create input objects
		this.mouseInput = new ZMouseInput(this);
		this.keyInput = new ZKeyInput(this);
		
		// Init camera
		this.camera = new GameCamera();
		
		// setup callbacks
		this.initCallBacks();

		// Create the main loop
		this.renderLooper = new GameLooper(maxFps, this::loopFunction, this::shouldRender, this::keepRenderLoopFunction, !this.window.usesVsync(), "FPS", printFps);
		
		// Create the tick loop
		this.tickLooper = new GameLooper(tps, this::tickLoopFunction, this::shouldTick, this::keepTickLoopFunction, ZConfig.waitBetweenTicks(), "TPS", printTps);

		// Go to fullscreen if applicable
		this.window.setInFullScreenNow(enterFullScreen);
	}
	
	/**
	 * Begin running the main OpenGL loop. When the loop ends, the cleanup method is automatically run. Do not manually call {@link #end()}, terminate the main loop instead
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
	}
	
	/**
	 * Assign the current window all needed callbacks, i.e. input. 
	 * This is an expensive operation and should not be regularly called
	 * 
	 * @return true if the callbacks could be set, false if an error occured
	 */
	public boolean initCallBacks(){
		long w = this.getWindow().getCurrentWindowID();
		if(w == NULL){
			if(ZConfig.printErrors()) System.err.println("Error in Game.initCallBacks, cannnot init callbacks if the current window is NULL");
			return false;
		}
		glfwSetKeyCallback(w, this::keyPress);
		glfwSetCursorPosCallback(w, this::mouseMove);
		glfwSetMouseButtonCallback(w, this::mousePress);
		glfwSetScrollCallback(w, this::mouseWheelMove);
		glfwSetWindowSizeCallback(w, this.getWindow()::windowSizeCallback);
		return true;
	}
	
	/**
	 * The method directly used as a callback method a key press
	 * 
	 * @param window The id of the GLFW window used
	 * @param key The id of the key pressed
	 * @param scanCode The system specific scancode of the key
	 * @param action If the button was released, pressed, or held
	 * @param mods The modifiers held during the key press, i.e. shift, alt, ctrl
	 */
	private void keyPress(long window, int key, int scanCode, int action, int mods){
		this.keyPress(key, scanCode, action, mods);
		this.getKeyInput().keyPress(key, scanCode, action, mods);
	}
	
	/**
	 * Called when the window recieves a key press. Can overrite this method to perform actions directly when keys are pressed
	 * 
	 * @param key The id of the key pressed
	 * @param scanCode The system specific scancode of the key
	 * @param action If the button was released, pressed, or held
	 * @param mods The modifiers held during the key press, i.e. shift, alt, ctrl
	 */
	protected void keyPress(int key, int scanCode, int action, int mods){
	}
	
	/**
	 * The method directly used as a callback method for a mouse button press
	 * 
	 * @param window The id of the GLFW window where the button was pressed
	 * @param button The mouse button which was pressed
	 * @param action The action of the button, i.e. up or down
	 * @param mods The additional buttons pressed, i.e. shift, alt, ctrl
	 */
	private void mousePress(long window, int button, int action, int mods){
		this.mousePress(button, action, mods);
		this.getMouseInput().mousePress(button, action, mods);
	}
	
	/**
	 * Called when the window recieves a mouse button press. Can overrite this method to perform actions directly when mouse buttons are pressed
	 * 
	 * @param window The id of the GLFW window where the button was pressed
	 * @param button The mouse button which was pressed
	 * @param action The action of the button, i.e. up or down
	 * @param mods The additional buttons pressed, i.e. shift, alt, ctrl
	 */
	protected void mousePress(int button, int action, int mods){
	}
	
	/**
	 * The method directly used as a callback method for a mouse movement
	 * 
	 * @param window The id of the GLFW window where the button was pressed
	 * @param x The raw x pixel coordinate of the mouse on the GLFW window
	 * @param y The raw y pixel coordinate of the mouse on the GLFW window
	 */
	private void mouseMove(long window, double x, double y){
		this.mouseMove(x, y);
		this.getMouseInput().mouseMove(x, y);
	}
	
	/**
	 * Called when the window recieves mouse movement. Can overrite this method to perform actions directly when the mouse is moved
	 * 
	 * @param x The raw x pixel coordinate of the mouse on the GLFW window
	 * @param y The raw y pixel coordinate of the mouse on the GLFW window
	 */
	protected void mouseMove(double x, double y){
	}
	
	/**
	 * The method directly used as a callback method for mouse wheel scrolling
	 * 
	 * @param window The id of the GLFW window where the button was pressed
	 * @param x The amount the scroll wheel was moved on the x axis, unused
	 * @param y The amount the scroll wheel was moved on the y axis, i.e. number of scrolls, 1 for scroll up, -1 for scroll down
	 */
	private void mouseWheelMove(long window, double x, double y){
		this.mouseWheelMove(x, y);
		this.getMouseInput().mouseWheelMove(x, y);
	}
	
	/**
	 * Called when the window recieves a mouse wheel movement. Can overrite this method to perform actions directly when the mouse wheel is moved
	 * 
	 * @param x The amount the scroll wheel was moved on the x axis, unused
	 * @param y The amount the scroll wheel was moved on the y axis, i.e. number of scrolls, 1 for scroll up, -1 for scroll down
	 */
	protected void mouseWheelMove(double x, double y){
	}
	
	/**
	 * The function run by the rendering GameLooper as its main loop for OpenGL
	 */
	private void loopFunction(){
		// Update the window
		this.getWindow().update();

		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
		
		// Clear the main framebuffer
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Clear the internal renderer
		Renderer r = this.getWindow().getRenderer();
		r.clear();
		r.setCamera(this.getCamera());
		
		// Render objects on the renderer

		// Set up drawing to the buffer
		glLoadIdentity();
		glViewport(0, 0, this.getScreenWidth(), this.getScreenHeight());
		
		// Draw the background
		r.setCameraMode(false);
		this.renderBackground(r);
		
		// Draw the foreground, i.e. main objects
		glPushMatrix();
		r.setCameraMode(true);
		r.drawToRenderer();
		this.getCamera().transform(this.getWindow());
		render(r);
		glPopMatrix();
		
		// Draw the hud
		r.setCameraMode(false);
		this.renderHud(r);
		
		// Draw the renderer to the window
		r.drawToWindow(this.getWindow());
		glfwSwapBuffers(this.getWindow().getCurrentWindowID());
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
	 * Called once each time a frame is rendered to the screen. Use this method to define what is drawn each frame.
	 * Do not manually call this method.
	 * All objects drawn with this method will be affected by the game camera
	 * 
	 * @param r The Renderer to use for drawing
	 */
	protected void render(Renderer r){
	}
	
	/**
	 * Called once each time a frame is rendered to the screen, after the main render. Use this method to define what is drawn on top of the scree, i.e. a hud, menu, etc
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
		long w = this.getWindow().getCurrentWindowID();
		return w == NULL || !glfwWindowShouldClose(w);
	}
	
	/**
	 * The function run by the tick GameLooper as its main loop
	 */
	private void tickLoopFunction(){
		this.tick(this.tickLooper.getRateTime());
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

	/** @return See {@link #window} */
	public GameWindow getWindow(){
		return window;
	}

	/**
	 * @return The maximum number of frames to render per second. Use 0 for unlimited framerate. This value does nothing if {@link #useVsync} is true
	 */
	public int getMaxFps(){
		return this.renderLooper.getRate();
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
		return this.renderLooper.willPrintRate();
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
	
	/** @return See {@link #mouseInput} */
	public ZMouseInput getMouseInput(){
		return this.mouseInput;
	}
	
	/** @return The current x coordinate of the mouse in screen coordinates. Should use for things that do not move with the camera */
	public double mouseSX(){
		return this.getMouseInput().x();
	}
	
	/** @return The current y coordinate of the mouse in screen coordinates. Should use for things that do not move with the camera */
	public double mouseSY(){
		return this.getMouseInput().y();
	}
	
	/** @return See {@link #keyInput} */
	public ZKeyInput getKeyInput(){
		return this.keyInput;
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
	
	/** @return The current x coordinate of the mouse in game coordinates. Should use for things that move with the camera */
	public double mouseGX(){
		return this.getCamera().screenToGameX(this.mouseSX());
	}
	
	/** @return The current y coordinate of the mouse in game coordinates. Should use for things that move with the camera */
	public double mouseGY(){
		return this.getCamera().screenToGameY(this.mouseSY());
	}
}
