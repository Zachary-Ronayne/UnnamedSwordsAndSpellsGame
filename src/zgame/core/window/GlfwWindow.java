package zgame.core.window;

import org.lwjgl.glfw.*;

import zgame.core.Game;
import zgame.core.input.GLFWModUtils;
import zgame.core.input.keyboard.GLFWKeyInput;
import zgame.core.input.mouse.GLFWMouseInput;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;

/** An implementation of {@link GameWindow} which uses GLFW methods */
public class GlfwWindow extends GameWindow{
	
	/** The number used by glfw to track the main window */
	private long windowID;
	/** The number used by glfw to track the full screen window */
	private long fullScreenID;
	
	/** The object tracking mouse input events */
	private final GLFWMouseInput mouseInput;
	
	/** The object tracking keyboard input events */
	private final GLFWKeyInput keyInput;
	
	/**
	 * Create an empty {@link GlfwWindow}. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 */
	public GlfwWindow(){
		this("Game Window");
	}
	
	/**
	 * Create a default {@link GlfwWindow}. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 *
	 * @param title See {@link #getWindowTitle()}
	 */
	public GlfwWindow(String title){
		this(title, 1280, 720, 200, true, false, true);
	}
	
	/**
	 * Create a {@link GlfwWindow} with the given parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 *
	 * @param title See {@link #getWindowTitle()}
	 * @param winWidth See {@link #getWidth()}
	 * @param winHeight See {@link #getHeight()}
	 * @param maxFps See {@link Game#getMaxFps()}
	 * @param useVsync See {@link #usesVsync()}
	 * @param stretchToFill See {@link #isStretchToFill()}
	 * @param printFps See {@link Game#isPrintFps()}
	 */
	public GlfwWindow(String title, int winWidth, int winHeight, int maxFps, boolean useVsync, boolean stretchToFill, boolean printFps){
		this(title, winWidth, winHeight, winWidth, winHeight, maxFps, useVsync, stretchToFill, printFps, 60, true);
	}
	
	/**
	 * Create a {@link GlfwWindow} with the given parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 *
	 * @param title See {@link #getWindowTitle()}
	 * @param winWidth See {@link #getWidth()}
	 * @param winHeight See {@link #getHeight()}
	 * @param screenWidth The width, in pixels, of the internal buffer to draw to
	 * @param screenHeight The height, in pixels, of the internal buffer to draw to
	 * @param maxFps See {@link Game#getMaxFps()}
	 * @param useVsync See {@link #usesVsync()}
	 * @param stretchToFill See {@link #isStretchToFill()}
	 */
	public GlfwWindow(String title, int winWidth, int winHeight, int screenWidth, int screenHeight, int maxFps, boolean useVsync, boolean stretchToFill, boolean printFps, int tps, boolean printTps){
		super(title, winWidth, winHeight, screenWidth, screenHeight, maxFps, useVsync, stretchToFill, printFps, tps, printTps);
		
		// Set up window behavior
		glfwDefaultWindowHints();
		// Set not visible
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		// Set resizable
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		// Update screen width and height
		this.updateWindowSize();
		
		// Create input objects
		this.mouseInput = new GLFWMouseInput(this);
		this.keyInput = new GLFWKeyInput(this);
	}
	
	@Override
	protected void createContext(){
		// For printing GLFW error messages to System.err
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Must call to init GLFW, if it returns false, then the program cannot run
		if(!glfwInit()) throw new IllegalStateException("GLFW failed to initialize");
		
		// Create the window
		this.windowID = glfwCreateWindow(this.getWidth(), this.getHeight(), this.getWindowTitle(), NULL, NULL);
		if(this.windowID == NULL) throw new RuntimeException("Failed to create the GLFW window");
		
		// Set up window context
		glfwMakeContextCurrent(this.windowID);
	}
	
	@Override
	public void checkEvents(){
		super.checkEvents();
		
		// Poll for window events. The key callback above will only be invoked during this call.
		glfwPollEvents();
	}
	
	@Override
	public void swapBuffers(){
		glfwSwapBuffers(this.getCurrentWindowID());
	}
	
	/** End the program, freeing all resources */
	@Override
	public void destroy(){
		super.destroy();
		// Free memory / destroy callbacks
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
		var func = glfwSetErrorCallback(null);
		if(func != null) func.free();
	}
	
	@Override
	public boolean shouldClose(){
		long w = this.getCurrentWindowID();
		return w == NULL || !glfwWindowShouldClose(w);
	}
	
	/**
	 * Assign the current window all needed callbacks, i.e. input. This is an expensive operation and should not be regularly called
	 *
	 * @return true if the callbacks could be set, false if an error occurred
	 */
	@Override
	public boolean initCallBacks(){
		long w = this.getCurrentWindowID();
		if(w == NULL){
			if(ZConfig.printErrors()) System.err.println("Error in GLFWWindow.initCallBacks, cannot init callbacks if the current window is NULL");
			return false;
		}
		glfwSetKeyCallback(w, this::keyPress);
		glfwSetCursorPosCallback(w, this::mouseMove);
		glfwSetMouseButtonCallback(w, this::mousePress);
		glfwSetScrollCallback(w, this::mouseWheelMove);
		glfwSetWindowSizeCallback(w, this::windowSizeChanged);
		glfwSetWindowIconifyCallback(w, this::windowMinimize);
		glfwSetWindowFocusCallback(w, this::windowFocus);
		return true;
	}
	
	/**
	 * The method directly used as a callback for a GLFW keyboard press
	 *
	 * @param window The ID of the window from which the event occurred
	 * @param key The ID of the key pressed
	 * @param scanCode The ID of the system specific scancode
	 * @param action The action taken, i.e. released, pressed, held
	 * @param mods The value containing bits for modifiers, i.e. shift, alt, ctrl
	 */
	private void keyPress(long window, int key, int scanCode, int action, int mods){
		this.keyAction(key, action != GLFW_RELEASE, GLFWModUtils.isShift(mods), GLFWModUtils.isAlt(mods), GLFWModUtils.isCtrl(mods));
	}
	
	/**
	 * The method directly used as a callback for a GLFW mouse button press
	 *
	 * @param window The ID of the window from which the event occurred
	 * @param button The ID of the button pressed
	 * @param action The action taken, i.e. released, pressed, held
	 * @param mods The value containing bits for modifiers, i.e. shift, alt, ctrl
	 */
	private void mousePress(long window, int button, int action, int mods){
		this.mouseAction(button, action != GLFW_RELEASE, GLFWModUtils.isShift(mods), GLFWModUtils.isAlt(mods), GLFWModUtils.isCtrl(mods));
	}
	
	/**
	 * The method directly used as a callback for a GLFW mouse movement
	 *
	 * @param window The ID of the window from which the event occurred
	 * @param x The raw x pixel coordinate of the mouse on the GLFW window
	 * @param y The raw y pixel coordinate of the mouse on the GLFW window
	 */
	private void mouseMove(long window, double x, double y){
		this.mouseMove(x, y);
	}
	
	/**
	 * The method directly used as a callback for a GLFW mouse wheel movement
	 *
	 * @param x The amount of distance scrolled on the x axis, unused
	 * @param y The amount of distance scrolled on the y axis, used as the scroll amount
	 */
	private void mouseWheelMove(long window, double x, double y){
		this.mouseWheelMove(y);
	}
	
	/**
	 * The method directly used as a callback for a GLFW window size change
	 *
	 * @param window The id of the window which was changed
	 * @param w The new width
	 * @param h The new height
	 */
	private void windowSizeChanged(long window, int w, int h){
		this.windowSizeChanged(w, h);
	}
	
	/**
	 * The method directly used as a callback for a GLFW window getting minimized, i.e. iconified
	 *
	 * @param window The id of the window which had its state changed
	 * @param min true if the window was minimized, false otherwise
	 */
	private void windowMinimize(long window, boolean min){
		this.windowMinimize(min);
	}
	
	/**
	 * The method directly used as a callback for a GLFW window losing or gaining focus, i.e. iconified
	 *
	 * @param window The id of the window which had its state changed
	 * @param focus true if the window gained focus, false otherwise
	 */
	private void windowFocus(long window, boolean focus){
		this.windowFocus(focus);
	}
	
	@Override
	public void setSize(int w, int h){
		super.setSize(w, h);
		glfwSetWindowSize(this.getWindowID(), w, h);
	}
	
	@Override
	protected boolean enterFullScreen(){
		this.createFullScreenWindow();
		if(this.fullScreenID == NULL) return false;
		// Use the fullscreen window
		glfwMakeContextCurrent(this.fullScreenID);
		glfwShowWindow(this.fullScreenID);
		// Hide the old window
		glfwHideWindow(this.windowID);
		
		return true;
	}
	
	@Override
	protected boolean exitFullScreen(){
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
		
		return true;
	}
	
	/**
	 * Create a window to use for the fullscreen. In the case of multiple monitors, the monitor which will be used is the one with the upper left hand corner of the window in
	 * it The id is stored in {@link #fullScreenID}
	 */
	protected void createFullScreenWindow(){
		// Find which monitor the window is on and center it, additionally, save the old position before entering fullscreen
		long monitor = this.center();
		
		if(monitor == NULL){
			if(ZConfig.printErrors()) ZStringUtils.print("Failed to find any monitors to create a fullscreen window");
			return;
		}
		// Put the found monitor in full screen on that window
		GLFWVidMode mode = glfwGetVideoMode(monitor);
		if(mode == null){
			if(ZConfig.printErrors()) ZStringUtils.print("Failed to get a video mode to create a fullscreen window");
			return;
		}
		this.fullScreenID = glfwCreateWindow(mode.width(), mode.height(), ZStringUtils.concat(this.getWindowTitle(), " | Fullscreen"), monitor, this.getWindowID());
		if(this.fullScreenID == NULL){
			if(ZConfig.printErrors()) ZStringUtils.print("Failed to create a fullscreen window");
		}
	}
	
	@Override
	public void setWindowPosition(int x, int y){
		if(this.isInFullScreen()) return;
		glfwSetWindowPos(this.getWindowID(), x, y);
	}
	
	/**
	 * Update the {@link #width} and {@link #height} variables with the current size of the window Primarily used to update the values when entering fullscreen
	 */
	@Override
	public Dimension getWindowSize(){
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(this.getCurrentWindowID(), w, h);
		return new Dimension(w.get(0), h.get(0));
	}
	
	@Override
	public long center(){
		return this.center(this.getCurrentMonitor());
	}
	
	/**
	 * Center the window to the given monitor. Uses the primary monitor if the given monitor is NULL
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
		if(mode == null){
			if(ZConfig.printErrors()) ZStringUtils.print("Failed to center window, could not find window mode");
			return NULL;
		}
		int w = mode.width();
		int h = mode.height();
		glfwSetWindowPos(this.getWindowID(), mx.get(0) + (w - this.getWidth()) / 2, my.get(0) + (h - this.getHeight()) / 2);
		
		return monitor;
	}
	
	/** @return A {@link Point} containing the position of the window */
	public Point getWindowPos(){
		long winId = this.getWindowID();
		IntBuffer wx = BufferUtils.createIntBuffer(1);
		IntBuffer wy = BufferUtils.createIntBuffer(1);
		glfwGetWindowPos(winId, wx, wy);
		return new Point(wx.get(0), wy.get(0));
	}
	
	/**
	 * Find the monitor which contains the upper left hand corner of the window
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
			if(mode == null) continue;
			int w = mode.width();
			int h = mode.height();
			IntBuffer mx = BufferUtils.createIntBuffer(1);
			IntBuffer my = BufferUtils.createIntBuffer(1);
			glfwGetMonitorPos(id, mx, my);
			// If we find a monitor whose bounds contain the position of the monitor, return that id
			if(new Rectangle(mx.get(0), my.get(0), w, h).contains(wp.x, wp.y)) return id;
		}
		return glfwGetPrimaryMonitor();
	}
	
	@Override
	protected void setupVsync(boolean useVsync){
		if(useVsync) glfwSwapInterval(1);
		else glfwSwapInterval(0);
	}
	
	/** @return See {@link #windowID} */
	public long getWindowID(){
		return this.windowID;
	}
	
	/** @return See {@link #fullScreenID} */
	public long getFullScreenID(){
		return this.fullScreenID;
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
	
	/** @return See {@link #mouseInput} */
	public GLFWMouseInput getMouseInput(){
		return this.mouseInput;
	}
	
	/** @return See {@link #keyInput} */
	public GLFWKeyInput getKeyInput(){
		return this.keyInput;
	}
}
