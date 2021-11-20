package zgame;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL30.*;

import zgame.graphics.DisplayList;
import zgame.graphics.Renderer;
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
	
	// TODO Add the tick loop in a similar way as the render loop

	// TODO implement sound
	
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
	/** The current ratio of {@link #width} divided by {@link #height} */
	private double ratio;
	
	/** The looper to run the main OpenGL loop */
	private GameLooper renderLooper;
	/** true to use vsync, i.e. lock the framerate to the refreshrate of the monitor, false otherwise */
	private boolean useVsync;
	
	/** The renderer used by this GameWindow to draw to the screen */
	private Renderer renderer;
	
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
		this(title, winWidth, winHeight, winWidth, winHeight, maxFps, useVsync, enterFullScreen, stretchToFill, printFps);
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
	public GameWindow(String title, int winWidth, int winHeight, int screenWidth, int screenHeight, int maxFps, boolean useVsync, boolean enterFullScreen, boolean stretchToFill, boolean printFps){
		this.windowTitle = title;
		this.width = 1;
		this.height = 1;
		this.setWidth(winWidth);
		this.setHeight(winHeight);
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
		
		// Set up display lists
		DisplayList.initLists();
		
		// Init renderer
		this.renderer = new Renderer(screenWidth, screenHeight);
		initTextureSettings();
		
		// Init fullscreen, this will also set up callbacks
		this.updateFullscreen = FullscreenState.NOTHING;
		this.setInFullScreenNow(enterFullScreen);
		
		// Start the main loop
		this.renderLooper = new GameLooper(maxFps, this::loopFunction, this::shouldRender, this::keepRunningFunction, !this.useVsync, "FPS", printFps);
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
		// glfwSetCursorPosCallback(w, Game::mouseMove);
		// glfwSetMouseButtonCallback(w, Game::mousePress);
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
	
	/** Initialize the settings for textures based on the needs of simple 2D pixel art textures with transparency */
	private void initTextureSettings(){
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	/**
	 * Begin running the main OpenGL loop. When the loop ends, the cleanup method is automatically run. Do not manually call {@link #end()}, terminate the main loop instead
	 * Calling this method will run the loop on the currently executing thread. This should only be the main Java thread, not an external thread
	 */
	public void start(){
		this.renderLooper.loop();
		this.end();
	}
	
	/**
	 * End the program, freeing all resources
	 */
	private void end(){
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
	 * The function run by the GameLooper as its main loop
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
		glLoadIdentity();
		glViewport(0, 0, this.getScreenWidth(), this.getScreenHeight());
		r.drawToRenderer();
		render(r);
		
		// Draw the renderer to the window
		r.drawToScreen(this, this.isStretchToFill());
		glfwSwapBuffers(this.getCurrentWindowID());
	}
	
	/**
	 * Called once each time a frame is rendered to the screen. Use this method to define what is drawn each frame.
	 * Do not manually call this method
	 * 
	 * @param r The Renderer to use for drawing
	 */
	protected abstract void render(Renderer r);
	
	/**
	 * The function used to determine if each the main OpenGL loop should draw a frame
	 * 
	 * @return true if the next frame should be drawn regardless, false otherwise
	 */
	private boolean shouldRender(){
		return this.usesVsync();
	}
	
	/**
	 * The function used to determine if the main OpenGL loop should end
	 * 
	 * @return true if the loop should continue, false otherwise
	 */
	private boolean keepRunningFunction(){
		long w = this.getCurrentWindowID();
		return w == NULL || !glfwWindowShouldClose(w);
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
	}
	
	/** @return See {@link #width} */
	public int getWidth(){
		return this.width;
	}

	/** Set the current width and update {@link #ratio} */
	private void setWidth(int width){
		this.width = width;
		this.updateRatio();
	}
	
	/** @return See {@link #height} */
	public int getHeight(){
		return this.height;
	}
	
	/** Set the current height and update {@link #ratio} */
	private void setHeight(int height){
		this.height = height;
		this.updateRatio();
	}
	
	/** @return The width, in pixels, of the internal buffer */
	public int getScreenWidth(){
		return this.renderer.getWidth();
	}
	
	/** @return The height, in pixels, of the internal buffer */
	public int getScreenHeight(){
		return this.renderer.getHeight();
	}

	/** Update the value of {@link #ratio} based on the current values of {@link #width} and {@link #height} */
	private void updateRatio(){
		this.ratio = (double)this.getWidth() / this.getHeight();
	}

	/** @return See {@link #ratio} */
	public double getRatio(){
		return this.ratio;
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
	
	/** @return true if the fps should be printed once each second, false otherwise */
	public boolean isPrintFps(){
		return this.renderLooper.willPrintRate();
	}
	
	/** @param print See {@link #isPrintFps()} */
	public void setPrintFps(boolean print){
		this.renderLooper.setPrintRate(print);
	}
	
}
