package zgame;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL30.*;

import zgame.graphics.DisplayList;
import zgame.graphics.Renderer;
import zgame.graphics.camera.GameCamera;
import zgame.input.mouse.ZMouseInput;
import zgame.utils.ZConfig;
import zgame.utils.ZStringUtils;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import java.awt.Rectangle;
import java.awt.Point;

/**
 * A class that handles one central window created by glfw.
 * This includes an option to move to full screen
 */
public abstract class GameWindow{
	
	// TODO implement keyboard input
	
	// TODO implement sound
	
	// TODO reorganize code relating to GameWindow, Renderer, and Camera to properly separate rendering from the window
	
	/** The title displayed on the window */
	private String windowTitle;
	
	/** The number used by glfw to track the main window */
	private long windowID;
	/** The number used by glfw to track the full screen window */
	private long fullScreenID;
	/** true if the Game is currently in full screen, false otherwise */
	private boolean inFullScreen;
	/** Determines if on the next OpenGL loop, the screen should update if it is or is not in full screen */
	private FullscreenState updateFullscreen;
	/** The position of the monitor before moving to full screen */
	private Point oldPosition;
	
	/** A simple enum for telling what should happen to the full screen status on the next OpenGL loop */
	private enum FullscreenState{
		/** Go to full screen */
		ENTER,
		/** Leave full screen */
		EXIT,
		/** Do not change the state of fullscreen */
		NOTHING;
		
		/** @return true if this state wants to make an update, false otherwise */
		public boolean shouldUpdate(){
			return this != NOTHING;
		}
		
		/** @return true if the state says to enter fullscreen, false otherwise */
		public boolean willEnter(){
			return this == ENTER;
		}
		
		/**
		 * Get the state corrsponding to a boolean value
		 * 
		 * @param enter tue to get the state for entering full screen, false to exit
		 * @return {@link #ENTER} or {@link #EXIT} depending on enter
		 */
		public static FullscreenState state(boolean enter){
			return enter ? ENTER : EXIT;
		}
	}
	
	/**
	 * true if, when drawing the final Renderer image to the screen, the image should stretch to fill up the entire screen,
	 * false to draw the image in the center of the screen leave black bars in areas that the image doesn't fill up
	 */
	private boolean stretchToFill;
	
	/** The current width of the window in pixels, this does not include decorators such as the minimize button */
	private int width;
	/** The current height of the window in pixels, this does not include decorators such as the minimize button */
	private int height;
	/** The inverse of {@link #width} */
	private double inverseWidth;
	/** The inverse of {@link #height} */
	private double inverseHeight;
	/** The current ratio of {@link #width} divided by {@link #height} */
	private double windowRatio;
	
	/** The x coordinate for the viewport of where the internal GameBuffer should be drawn with a renderer */
	private int viewportX;
	/** The y coordinate for the viewport of where the internal GameBuffer should be drawn with a renderer */
	private int viewportY;
	/** The width of the viewport for drawing the internal GameBuffer with a renderer */
	private int viewportW;
	/** The height of the viewport for drawing the internal GameBuffer with a renderer */
	private int viewportH;
	/** The inverse of {@link #viewportW} */
	private double viewportWInverse;
	/** The inverse of {@link #viewportH} */
	private double viewportHInverse;
	
	/** The looper to run the main OpenGL loop */
	private GameLooper renderLooper;
	/** true to use vsync, i.e. lock the framerate to the refreshrate of the monitor, false otherwise */
	private boolean useVsync;
	/** The renderer used by this GameWindow to draw to the screen */
	private Renderer renderer;
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
	
	/**
	 * Create an empty GameWindow. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 * 
	 * @param title See {@link #windowTitle}
	 */
	public GameWindow(){
		this("Game Window");
	}
	
	/**
	 * Create a default GameWindow. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 * 
	 * @param title See {@link #windowTitle}
	 */
	public GameWindow(String title){
		this(title, 1280, 720, 200, true, false, false, true);
	}
	
	/**
	 * Create a GameWindow with the given parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 * 
	 * @param title See {@link #windowTitle}
	 * @param winWidth See {@link #width}
	 * @param winHeight See {@link #height}
	 * @param maxFps See {@link #getMaxFps()}
	 * @param useVsync See {@link #useVsync}
	 * @param enterFullScreen True to immediately enter fullscreen
	 * @param stretchToFill See {@link #stretchToFill}
	 */
	public GameWindow(String title, int winWidth, int winHeight, int maxFps, boolean useVsync, boolean enterFullScreen, boolean stretchToFill, boolean printFps){
		this(title, winWidth, winHeight, winWidth, winHeight, maxFps, useVsync, enterFullScreen, stretchToFill, printFps, 60, true);
	}
	
	/**
	 * Create a GameWindow with the given parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 * 
	 * @param title See {@link #windowTitle}
	 * @param winWidth See {@link #width}
	 * @param winHeight See {@link #height}
	 * @param screenWidth The width, in pixels, of the internal buffer to draw to
	 * @param screenHeight The height, in pixels, of the internal buffer to draw to
	 * @param maxFps See {@link #getMaxFps()}
	 * @param useVsync See {@link #useVsync}
	 * @param enterFullScreen True to immediately enter fullscreen
	 * @param stretchToFill See {@link #stretchToFill}
	 */
	public GameWindow(String title, int winWidth, int winHeight, int screenWidth, int screenHeight, int maxFps, boolean useVsync, boolean enterFullScreen, boolean stretchToFill, boolean printFps, int tps, boolean printTps){
		// Init general values
		this.windowTitle = title;
		this.width = 1;
		this.height = 1;
		this.width = winWidth;
		this.height = winHeight;
		this.useVsync = useVsync;
		this.stretchToFill = stretchToFill;
		this.oldPosition = new Point(0, 0);
		
		// For printing error messages to System.err
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Must call to init GLFW, if it returns false, then the program cannot run
		if(!glfwInit()) throw new IllegalStateException("GLFW failed to initialize");
		
		// Set up window behavior
		glfwDefaultWindowHints();
		// Set not visible
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		// Set resizable
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		// Create the window
		this.windowID = glfwCreateWindow(this.getWidth(), this.getHeight(), this.windowTitle, NULL, NULL);
		if(this.windowID == NULL) throw new RuntimeException("Failed to create the GLFW window");
		
		// Create input objects
		this.mouseInput = new ZMouseInput(this);
		
		// Update fullscreen
		this.setInFullScreenNow(false);
		this.setUseVsync(useVsync);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		// Additional error messaging
		GLUtil.setupDebugMessageCallback(System.err);
		
		// Init renderer
		this.renderer = new Renderer(this, screenWidth, screenHeight);
		this.updateInternalValues();
		
		// Set up texture settings for drawing with an alpha channel
		initTextureSettings();
		
		// Set up display lists
		DisplayList.initLists();
		
		// Init fullscreen, this will also set up callbacks
		this.updateFullscreen = FullscreenState.NOTHING;
		this.setInFullScreenNow(enterFullScreen);
		
		// Init camera
		this.camera = new GameCamera();
		
		// Create the main loop
		this.renderLooper = new GameLooper(maxFps, this::loopFunction, this::shouldRender, this::keepRenderLoopFunction, !this.useVsync, "FPS", printFps);
		
		// Create the tick loop
		this.tickLooper = new GameLooper(tps, this::tickLoopFunction, this::shouldTick, this::keepTickLoopFunction, ZConfig.waitBetweenTicks(), "TPS", printTps);
	}
	
	/**
	 * Assign the current window all needed callbacks, i.e. input
	 * 
	 * @return true if the callbacks could be set, false if an error occured
	 */
	private boolean initCallBacks(){
		long w = this.getCurrentWindowID();
		if(w == NULL){
			if(ZConfig.printErrors()) System.err.println("Error in GameWindow.initCallBacks, cannnot init callbacks if the current window is NULL");
			return false;
		}
		glfwSetKeyCallback(w, this::keyPress);
		glfwSetCursorPosCallback(w, this.mouseInput::mouseMove);
		glfwSetMouseButtonCallback(w, this.mouseInput::mousePress);
		glfwSetScrollCallback(w, this.mouseInput::mouseWheelMove);
		glfwSetWindowSizeCallback(w, this::windowSizeCallback);
		return true;
	}
	
	/** Temporary keypress method for ease of testing */
	public void keyPress(long id, int key, int scanCode, int action, int mods){
	}
	
	/**
	 * Called when the window size is changed
	 * 
	 * @param window The window ID
	 * @param w The new width
	 * @param h The new height
	 */
	private void windowSizeCallback(long window, int w, int h){
		this.setWidth(w);
		this.setHeight(h);
	}
	
	/**
	 * Update the size of the window, directly changing the window. Does nothing if the {@link GameWindow} is in full screen, only works on a windowed version
	 * 
	 * @param w The new width, in pixels, not including any decorators such as the minimize button
	 * @param h The new height, in pixels, not including any decorators such as the minimize button
	 */
	public void setSize(int w, int h){
		if(this.isInFullScreen()) return;
		
		this.setWidth(w);
		this.setHeight(h);
		glfwSetWindowSize(this.getWindowID(), w, h);
	}
	
	/** Initialize the settings for textures based on the needs of simple 2D pixel art textures with transparency */
	private void initTextureSettings(){
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
		this.renderer.destory();
		long w = this.getWindowID();
		glfwFreeCallbacks(w);
		glfwDestroyWindow(w);
		long f = this.getFullScreenID();
		if(f != NULL){
			glfwFreeCallbacks(f);
			glfwDestroyWindow(f);
		}
		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	/**
	 * The function run by the rendering GameLooper as its main loop for OpenGL
	 */
	private void loopFunction(){
		// Update fullscreen status
		FullscreenState state = this.updateFullscreen;
		if(state.shouldUpdate()){
			this.setInFullScreenNow(state.willEnter());
			this.updateFullscreen = FullscreenState.NOTHING;
		}
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
		
		// Clear the main framebuffer
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Clear the internal renderer
		Renderer r = this.renderer;
		r.clear();
		
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
		this.getCamera().transform(this);
		render(r);
		glPopMatrix();
		
		// Draw the hud
		r.setCameraMode(false);
		this.renderHud(r);
		
		// Draw the renderer to the window
		r.drawToWindow(this);
		glfwSwapBuffers(this.getCurrentWindowID());
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
		return this.usesVsync();
	}
	
	/**
	 * The function used to determine if the main OpenGL loop should end
	 * 
	 * @return true if the loop should continue, false otherwise
	 */
	protected boolean keepRenderLoopFunction(){
		long w = this.getCurrentWindowID();
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
	
	/**
	 * Update the {@link #width} and {@link #height} variables with the current size of the window
	 * Primarily used to update the values when entering fullscreen
	 */
	private void updateScreenSize(){
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(this.getCurrentWindowID(), w, h);
		this.setWidth(w.get(0));
		this.setHeight(h.get(0));
	}
	
	/** @return See {@link #windowTitle} */
	public String getWindowTitle(){
		return this.windowTitle;
	}
	
	/** @param windowTitle See {@link #windowTitle} */
	public void setWindowTitle(String windowTitle){
		this.windowTitle = windowTitle;
	}
	
	/**
	 * Get the ID of the currently used window, i.e. either the windowed version or the full screen version
	 * 
	 * @return The id
	 */
	public long getCurrentWindowID(){
		if(this.isInFullScreen()) return this.getFullScreenID();
		else return this.getWindowID();
	}
	
	/** @return See {@link #windowID} */
	public long getWindowID(){
		return this.windowID;
	}
	
	/** @return See {@link #fullScreenID} */
	public long getFullScreenID(){
		return this.fullScreenID;
	}
	
	/** @return See {@link #inFullScreen} */
	public boolean isInFullScreen(){
		return this.inFullScreen;
	}
	
	/**
	 * This method instantly changes the fullscreen state, do not use when outside of the main OpenGL thread
	 * 
	 * @param inFullScreen See {@link #inFullScreen}
	 */
	private void setInFullScreenNow(boolean inFullScreen){
		this.inFullScreen = inFullScreen;
		if(this.inFullScreen){
			this.createFullScreenWindow();
			if(this.fullScreenID == NULL){
				this.inFullScreen = false;
				return;
			}
			// Use the fullscreen window
			glfwMakeContextCurrent(this.fullScreenID);
			glfwShowWindow(this.fullScreenID);
			// Hide the old window
			glfwHideWindow(this.windowID);
			// Ensure the fullscreen window has appropriate texture settings
			initTextureSettings();
		}
		else{
			long fullScreen = this.getFullScreenID();
			long window = this.getWindowID();
			// Get rid of the fullscreen window
			if(fullScreen != NULL){
				glfwDestroyWindow(fullScreen);
				this.fullScreenID = NULL;
			}
			// Use the windowed window
			glfwMakeContextCurrent(window);
			glfwShowWindow(window);
			
			// Put the window back where it was before going to full screen
			glfwSetWindowPos(window, this.oldPosition.x, this.oldPosition.y);
		}
		// Ensure the current window has the callbacks
		this.initCallBacks();
		
		// Update v-sync
		this.setUseVsync(this.usesVsync());
		
		// Update screen width and height
		this.updateScreenSize();
	}
	
	/**
	 * Create a window to use for the fullscreen. In the case of multiple monitors,
	 * the monitor which will be used is the one with the upper lefthand corner of the window in it
	 * The id is stored in {@link #fullScreenID}
	 */
	private void createFullScreenWindow(){
		// Find which monitor the window is on and center it, additionally, save the old position before entering fullscreen
		this.oldPosition = this.getWindowPos();
		long monitor = this.center();
		
		if(monitor == NULL){
			if(ZConfig.printErrors()) ZStringUtils.print("Failed to find any monitors to create a fullscreen window");
			return;
		}
		// Put the found monitor in full screen on that window
		GLFWVidMode mode = glfwGetVideoMode(monitor);
		this.fullScreenID = glfwCreateWindow(mode.width(), mode.height(), ZStringUtils.concat(this.getWindowTitle(), " | Fullscreen"), monitor, this.getWindowID());
		if(this.fullScreenID == NULL){
			if(ZConfig.printErrors()) ZStringUtils.print("Failed to create a fullscreen window");
			return;
		}
	}
	
	/**
	 * Center the window to the monitor which contains the upper left hand corner of the window. Does nothing if no monitor is found
	 * 
	 * @return The monitor id which the window was centered to
	 */
	public long center(){
		return this.center(this.getCurrentMonitor());
	}
	
	/**
	 * Center the window to the given monitor. Uses the primary monitor if the given monitor is null
	 * 
	 * @param monitor The monitor id to center to
	 * @return The monitor id which the window was centered to
	 */
	public long center(long monitor){
		if(monitor == NULL) monitor = glfwGetPrimaryMonitor();
		
		// Find the monitor position
		IntBuffer mx = BufferUtils.createIntBuffer(1);
		IntBuffer my = BufferUtils.createIntBuffer(1);
		glfwGetMonitorPos(monitor, mx, my);
		
		// Find the monitor width and center it
		GLFWVidMode mode = glfwGetVideoMode(monitor);
		int w = mode.width();
		int h = mode.height();
		glfwSetWindowPos(this.getWindowID(), mx.get(0) + (w - this.getWidth()) / 2, my.get(0) + (h - this.getHeight()) / 2);
		
		return monitor;
	}
	
	/** @return A Point containing the position of the window */
	public Point getWindowPos(){
		long winId = this.getWindowID();
		IntBuffer wx = BufferUtils.createIntBuffer(1);
		IntBuffer wy = BufferUtils.createIntBuffer(1);
		glfwGetWindowPos(winId, wx, wy);
		return new Point(wx.get(0), wy.get(0));
	}
	
	/**
	 * Find the monitor which contains the upperleft hand corner of the window
	 * 
	 * @return the id, or the primary monitor if no monitor is found
	 */
	public long getCurrentMonitor(){
		// First get the window position
		Point wp = this.getWindowPos();
		
		// Now check that window position against each monitor
		PointerBuffer buff = glfwGetMonitors();
		if(buff == null) return NULL;
		while(buff.hasRemaining()){
			long id = buff.get();
			GLFWVidMode mode = glfwGetVideoMode(id);
			int w = mode.width();
			int h = mode.height();
			IntBuffer mx = BufferUtils.createIntBuffer(1);
			IntBuffer my = BufferUtils.createIntBuffer(1);
			glfwGetMonitorPos(id, mx, my);
			// If we find a monitor whose bounds contain the position of the monitor, center it
			if(new Rectangle(mx.get(0), my.get(0), w, h).contains(wp.x, wp.y)) return id;
		}
		return glfwGetPrimaryMonitor();
	}
	
	/**
	 * Call to change the fullscreen state on the next OpenGL loop.
	 * If the window is already in the desired state, nothing happens
	 * 
	 * @param fullscreen true to enter fullscreen, false to exist.
	 */
	public void setFullscreen(boolean fullscreen){
		this.updateFullscreen = FullscreenState.state(fullscreen);
	}
	
	/**
	 * Call to change the fullscreen state on the next OpenGL loop to the opposite of its current state
	 */
	public void toggleFullscreen(){
		this.setFullscreen(!this.inFullScreen);
	}
	
	/** @return See {@link #stretchToFill} */
	public boolean isStretchToFill(){
		return this.stretchToFill;
	}
	
	/** @param stretchToFill See {@link #stretchToFill} */
	public void setStretchToFill(boolean stretchToFill){
		this.stretchToFill = stretchToFill;
		this.updateInternalValues();
	}
	
	/** @return See {@link #width} */
	public int getWidth(){
		return this.width;
	}
	
	/** @return See {@link #inverseWidth} */
	public double getInverseWidth(){
		return this.inverseWidth;
	}
	
	/** Set the current width and update {@link #windowRatio} */
	private void setWidth(int width){
		this.width = width;
		this.inverseWidth = 1.0 / this.width;
		this.updateInternalValues();
	}
	
	/** @return See {@link #height} */
	public int getHeight(){
		return this.height;
	}
	
	/** @return See {@link #inverseHeight} */
	public double getInverseHeight(){
		return this.inverseHeight;
	}
	
	/** Set the current height and update {@link #windowRatio} */
	private void setHeight(int height){
		this.height = height;
		this.inverseHeight = 1.0 / this.height;
		this.updateInternalValues();
	}
	
	/** @return The width, in pixels, of the internal buffer */
	public int getScreenWidth(){
		return this.renderer.getWidth();
	}
	
	/** @return The height, in pixels, of the internal buffer */
	public int getScreenHeight(){
		return this.renderer.getHeight();
	}
	
	/**
	 * Update the value of {@link #windowRatio} based on the current values of {@link #width} and {@link #height}
	 */
	private void updateRatios(){
		this.windowRatio = (double)this.getWidth() / this.getHeight();
	}
	
	/**
	 * Update the value of {@link #windowRatio} based on the current values of {@link #width} and {@link #height}
	 * Additionally, update the values of {@link #viewportX}, {@link #viewportY}, {@link #viewportW}, {@link #viewportH}
	 */
	private void updateInternalValues(){
		this.updateRatios();
		this.updateViewportValues();
	}
	
	/** @return See {@link #windowRatio} */
	public double getWindowRatio(){
		return this.windowRatio;
	}
	
	/** @return See {@link #viewportX} */
	public int viewportX(){
		return this.viewportX;
	}
	
	/** @return See {@link #viewportY} */
	public int viewportY(){
		return this.viewportY;
	}
	
	/** @return See {@link #viewportW} */
	public int viewportW(){
		return this.viewportW;
	}
	
	/** @return See {@link #viewportH} */
	public int viewportH(){
		return this.viewportH;
	}
	
	/** @return See {@link #viewportWInverse} */
	public double viewportWInverse(){
		return this.viewportWInverse;
	}
	
	/** @return See {@link #viewportHInverse} */
	public double viewportHInverse(){
		return this.viewportHInverse;
	}
	
	/**
	 * Update the stored state of the values to use for the viewport for drawing the contents of the screen via {@link #renderer}
	 * This method does nothing if {@link #renderer} is not yet initialized
	 */
	private void updateViewportValues(){
		// Cannot perform this action without renderer initialized
		if(this.renderer == null) return;
		
		// sw and sh for screen width and height
		int sw = this.getWidth();
		int sh = this.getHeight();
		
		if(this.isStretchToFill()){
			this.viewportX = 0;
			this.viewportY = 0;
			this.viewportW = sw;
			this.viewportH = sh;
		}
		else{
			// wRatio for the window aspect ratio and tRatio for this render's aspect ratio
			double wRatio = this.getWindowRatio();
			double tRatio = this.renderer.getRatioWH();
			int w;
			int h;
			if(tRatio < wRatio){
				h = sh;
				w = (int)Math.round(h * tRatio);
			}
			else{
				w = sw;
				h = (int)Math.round(w * this.renderer.getRatioHW());
			}
			this.viewportX = (sw - w) >> 1;
			this.viewportY = (sh - h) >> 1;
			this.viewportW = w;
			this.viewportH = h;
		}
		this.viewportWInverse = 1.0 / this.viewportW;
		this.viewportHInverse = 1.0 / this.viewportH;
	}
	
	/**
	 * Convert an x coordinate value in window space, to a coordinate in screen space coordinates
	 * 
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenX(double x){
		return this.renderer.windowToScreenX(this, x);
	}
	
	/**
	 * Convert a y coordinate value in window space, to a coordinate in screen space coordinates
	 * 
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenY(double y){
		return this.renderer.windowToScreenY(this, y);
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in window space coordinates
	 * 
	 * @param x The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowX(double x){
		return this.renderer.screenToWindowX(this, x);
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in window space coordinates
	 * 
	 * @param y The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowY(double y){
		return this.renderer.screenToWindowY(this, y);
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in OpenGL coordinates
	 * 
	 * @param x The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlX(double x){
		return this.renderer.screenToGlX(this, x);
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in OpenGL coordinates
	 * 
	 * @param y The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlY(double y){
		return this.renderer.screenToGlY(this, y);
	}
	
	/**
	 * Convert an x coordinate value in OpenGL space, to a coordinate in screen coordinates
	 * 
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenX(double x){
		return this.renderer.glToScreenX(this, x);
	}
	
	/**
	 * Convert a y coordinate value in OpenGL space, to a coordinate in screen coordinates
	 * 
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenY(double y){
		return this.renderer.glToScreenY(this, y);
	}
	
	/**
	 * Convert a size on the x axis in window space, to one in screen space
	 * 
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenX(double x){
		return this.renderer.sizeWindowToScreenX(this, x);
	}
	
	/**
	 * Convert a size on the y axis in window space, to one in screen space
	 * 
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenY(double y){
		return this.renderer.sizeWindowToScreenY(this, y);
	}
	
	/**
	 * Convert a size on the x axis in screen space, to one in window space
	 * 
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowX(double x){
		return this.renderer.sizeScreenToWindowX(this, x);
	}
	
	/**
	 * Convert a size on the y axis in screen space, to one in window space
	 * 
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowY(double y){
		return this.renderer.sizeScreenToWindowY(this, y);
	}
	
	/**
	 * Convert a size on the x axis in screen space, to one in OpenGL space
	 * 
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlX(double x){
		return this.renderer.sizeScreenToGlX(this, x);
	}
	
	/**
	 * Convert a size on the y axis in screen space, to one in OpenGL space
	 * 
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlY(double y){
		return this.renderer.sizeScreenToGlY(this, y);
	}
	
	/**
	 * Convert a size on the x axis in OpenGL space, to one in screen space
	 * 
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeGglToScreenX(double x){
		return this.renderer.sizeGlToScreenX(this, x);
	}
	
	/**
	 * Convert a size on the y axis in OpenGL space, to one in screen space
	 * 
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeGlToScreenY(double y){
		return this.renderer.sizeGlToScreenY(this, y);
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
	
	/** @return See {@link #useVsync} */
	public boolean usesVsync(){
		return this.useVsync;
	}
	
	/** @param useVsync See {@link #useVsync} */
	public void setUseVsync(boolean useVsync){
		this.useVsync = useVsync;
		if(this.usesVsync()) glfwSwapInterval(1);
		else glfwSwapInterval(0);
		// Use the opposite of usesVsync for waiting between loops. If Vsync is enabled, then there is no need to wait between loop iterations
		GameLooper r = this.renderLooper;
		if(r != null) r.setWaitBetweenLoops(!this.usesVsync());
	}
	
	/** @return See {@link #renderer} */
	public Renderer getRenderer(){
		return this.renderer;
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
