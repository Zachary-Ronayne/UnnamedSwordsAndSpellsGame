package zgame;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL30.*;

import zgame.utils.ZConfig;
import zgame.utils.ZStringUtils;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * A class that handles one central window created by glfw.
 * This includes an option to move to full screen
 */
public class GameWindow{
	
	// TODO add error checking to everything using the config to print errors
	
	/** The title displayed on the window */
	private String windowTitle;
	
	/** The number used by glfw to track the main window */
	private long windowID;
	/** The number used by glfw to track the full screen window */
	private long fullScreenID;
	/** true if the Game is currently in full screen, false otherwise */
	private boolean inFullScreen;
	/** true if, on the next OpenGL loop, the screen should update if it is or is not in full screen, false otherwisde */
	private boolean updateFullscreen;
	
	/** The current width of the window in pixels, this does not include decorators such as the minimize button */
	private int width;
	/** The current height of the window in pixels, this does not include decorators such as the minimize button */
	private int height;
	
	/** The looper to run the main OpenGL loop */
	private GameLooper renderLooper;
	/** true to use vsync, i.e. lock the framerate to the refreshrate of the monitor, false otherwise */
	private boolean useVsync;
	
	/**
	 * Create a default GameWindow. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 * 
	 * @param title See {@link #windowTitle}
	 */
	public GameWindow(String title){
		this(title, 1280, 720, 200, true, false);
	}
	
	/**
	 * Create a GameWindow with the given parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 * 
	 * @param title See {@link #windowTitle}
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 * @param maxFps See {@link #getMaxFps()}
	 * @param useVsync See {@link #useVsync}
	 * @param enterFullScreen True to immediately enter fullscreen
	 */
	public GameWindow(String title, int width, int height, int maxFps, boolean useVsync, boolean enterFullScreen){
		this.windowTitle = title;
		this.width = width;
		this.height = height;
		this.useVsync = useVsync;
	
		// For printing error messages to System.err
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Must call to init GLFW, if it returns false, then the program cannot run
		if(!glfwInit()) throw new IllegalStateException("GLFW failed to initialize");
		
		// Set up window behavior
		glfwDefaultWindowHints();
		// Set not visible
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		// Set resizable
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		
		// Create the window
		this.windowID = glfwCreateWindow(this.width, this.height, this.windowTitle, NULL, NULL);
		if(this.windowID == NULL) throw new RuntimeException("Failed to create the GLFW window");
		
		// Update fullscreen
		this.setInFullScreen(false);
		this.setUseVsync(useVsync);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		// Init fullscreen, this will also set up callbacks
		this.updateFullscreen = false;
		this.setInFullScreen(enterFullScreen);
		
		// Start the main loop
		this.renderLooper = new GameLooper(maxFps, this::loopFunction, this::shouldRender, this::keepRunningFunction, !this.useVsync, "FPS", true);
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
		// glfwSetKeyCallback(w, App::keyPress);
		// glfwSetCursorPosCallback(w, Game::mouseMove);
		// glfwSetMouseButtonCallback(w, Game::mousePress);
		return true;
	}
	
	/** Initialize the settings for textures based on the needs of simple 2D pixel art textures */
	private void initTextureSettings(){
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	/** 
	 * Begin running the main OpenGL loop. When the loop ends, the cleanup method is automatically run. 
	 * Calling this method will run the loop on the currently executing thread. This should only be the main Java thread, not an external thread
	 */
	public void start(){
		this.renderLooper.loop();
		this.end();
	}
	
	public void end(){
		// Erase callbacks
		long w = this.getWindowID();
		glfwSetKeyCallback(w, null);
		glfwSetKeyCallback(w, null);
		glfwSetCursorPosCallback(w, null);
		glfwSetMouseButtonCallback(w, null);
		
		// Free memory
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
	 * The function run by the GameLooper
	 */
	private void loopFunction(){
		// Update fullscreen status
		if(updateFullscreen){
			updateFullscreen = false;
			this.toggleFullScreen();
		}
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
		
		// TODO Create the Renderer class and put this block in there
		// Clear the main framebuffer and the extra framebuffer
		// Framebuffer.drawToBuffer();
		// glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Framebuffer.drawToDefault();
		// glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Show the drawn screen
		glfwSwapBuffers(this.getCurrentWindowID());
	}
	
	// TODO add an abstract render function that gets called with a Renderer object, this is to allow for defining what gets drawn each frame
	
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
	
	/** @return See {@link #windowTitle} */
	public String getWindowTitle(){
		return this.windowTitle;
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
	
	/** @param windowTitle See {@link #windowTitle} */
	public void setWindowTitle(String windowTitle){
		this.windowTitle = windowTitle;
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
	
	/** @param inFullScreen See {@link #inFullScreen} */
	public void setInFullScreen(boolean inFullScreen){
		this.inFullScreen = inFullScreen;
		if(this.inFullScreen){
			this.createFullScreenWindow();
			
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
		}
		// Ensure the current window has the callbacks
		this.initCallBacks();
		
		// Update v-sync
		this.setUseVsync(this.usesVsync());
	}
	
	/**
	 * Create a window to use for the fullscreen. This window will appear on the primary monitor in the case of multiple monitors. 
	 * The id is stored in {@link #fullScreenID}
	 */
	private void createFullScreenWindow(){
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(monitor);
		this.fullScreenID = glfwCreateWindow(mode.width(), mode.height(), ZStringUtils.concat(this.getWindowTitle(), " | Fullscreen"), monitor, this.getWindowID());
		if(this.fullScreenID == NULL) throw new IllegalStateException("Failed to create a fullscreen window");
	}
	
	/**
	 * Toggle the current fullscreen state, i.e. if it is in fullscreen, exit fullscreen, otherwise, go to fullscreen
	 */
	public void toggleFullScreen(){
		this.setInFullScreen(!this.isInFullScreen());
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
		// Use the opposite of usesVsync for waiting between loops. If Vsync is enabled, then there is no need to wait between loop iterations
		GameLooper r = this.renderLooper;
		if(r != null) r.setWaitBetweenLoops(!this.usesVsync());
	}
	
}
