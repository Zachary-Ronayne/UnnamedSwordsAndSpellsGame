package zgame.core.window;

import org.lwjgl.glfw.*;

import zgame.core.Game;
import zgame.core.input.GLFWModUtils;
import zgame.core.input.keyboard.GLFWKeyInput;
import zgame.core.input.mouse.GLFWMouseInput;
import zgame.core.utils.ZConfig;

import org.lwjgl.glfw.GLFWNativeWin32;

import static org.lwjgl.system.windows.User32.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.system.MemoryUtil.*;

import java.awt.*;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

/** An implementation of {@link GameWindow} which uses GLFW methods */
public class GlfwWindow extends GameWindow{
	
	/** The number used by glfw to track the window */
	private long windowID;
	
	/** The object tracking mouse input events */
	private final GLFWMouseInput mouseInput;
	
	/** The object tracking keyboard input events */
	private final GLFWKeyInput keyInput;
	
	/** true if this window is currently being shown, false otherwise */
	private boolean showing;
	
	/** true if this window should be visible when it starts up, false otherwise */
	private boolean showOnInit;
	
	/** The last windowed x coordinate before entering fullscreen */
	private int lastWindowedX;
	/** The last windowed y coordinate before entering fullscreen */
	private int lastWindowedY;
	/** The last windowed width before entering fullscreen */
	private int lastWindowedWidth;
	/** The last windowed height before entering fullscreen */
	private int lastWindowedHeight;
	/** Some magic pointer to a windows reference */
	private long win32Id;
	/** The value of GWL_STYLE before any changes */
	private long originalStyle;
	/** The value of GWL_EXSTYLE before any changes */
	private long originalExStyle;
	
	// issue#66 make a formal full screen modes system, choosing between proper full screen, windowed full screen, accounting for other operating systems, maybe
	/** true if this window should use borderless fullscreen, false for normal fullscreen */
	private static final boolean BORDERLESS_FULLSCREEN = true;
	
	/**
	 * Create an empty {@link GlfwWindow}. This does not initialize anything for GLFW or OpenGL, call {@link #init()} for that
	 */
	public GlfwWindow(){
		super();
		this.showing = false;
		this.showOnInit = false;
		
		// Create input objects
		this.mouseInput = new GLFWMouseInput(this);
		this.keyInput = new GLFWKeyInput(this);
	}
	
	@Override
	public void init(){
		super.init();
		
		// Update screen width and height
		this.updateWindowSize();
		
		// Center the window and then show it
		this.center();
		if(this.isShowOnInit()) this.show();
	}
	
	@Override
	protected void createContext(){
		// For printing GLFW error messages to System.err
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Must call to init GLFW, if it returns false, then the program cannot run
		if(!glfwInit()) throw new IllegalStateException("GLFW failed to initialize");
		
		// Set up window behavior, hidden by default
		glfwDefaultWindowHints();
		// Set not visible
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		// Set resizable
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		// Create the window
		this.windowID = glfwCreateWindow(this.getWidth(), this.getHeight(), this.getWindowTitle(), NULL, Game.get().getWindow().getLongId());
		if(this.windowID == NULL) throw new RuntimeException("Failed to create the GLFW window");
		
		// Grab windows values from window creation
		if(BORDERLESS_FULLSCREEN){
			this.win32Id = GLFWNativeWin32.glfwGetWin32Window(this.windowID);
			this.originalStyle = GetWindowLongPtr(this.win32Id, GWL_STYLE);
			this.originalExStyle = GetWindowLongPtr(this.win32Id, GWL_EXSTYLE);
		}
		
		// Save the current position
		this.storeLastWindowBounds();
		
		// Don't show the window by default
		this.hide();
		
		// Set up window context
		obtainContext();
	}
	
	@Override
	public void obtainContext(){
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
		// Remove old ids
		var oldWindowId = this.getWindowID();
		this.windowID = NULL;
		
		// Free memory / destroy callbacks
		glfwFreeCallbacks(oldWindowId);
		glfwDestroyWindow(oldWindowId);
	}
	
	@Override
	public void onAllWindowsClosed(){
		// Terminate GLFW and free the error callback
		glfwTerminate();
		var func = glfwSetErrorCallback(null);
		if(func != null) func.free();
		
		
		// Give the system some time to stop anything else that might be running
		try{
			Thread.sleep(100);
		}catch(Exception e){
			ZConfig.error(e, "Error in waiting for window to close when ending glfw windows");
		}
	}
	
	@Override
	public boolean shouldClose(){
		long w = this.getCurrentWindowID();
		return w == NULL || !glfwWindowShouldClose(w);
	}
	
	@Override
	public long getLongId(){
		if(!this.isInitialized()) return NULL;
		return this.windowID;
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
			ZConfig.error("Error in GLFWWindow.initCallBacks, cannot init callbacks if the current window is NULL");
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
	
	@Override
	public void hide(){
		if(this.getWindowID() != NULL) glfwHideWindow(this.getWindowID());
		this.showing = false;
	}
	
	@Override
	public void show(){
		if(this.getWindowID() != NULL) glfwShowWindow(this.getWindowID());
		this.showing = true;
	}
	
	/** @return See {@link #showing} */
	public boolean isShowing(){
		return this.showing;
	}
	
	/** @return See {@link #showOnInit} */
	public boolean isShowOnInit(){
		return this.showOnInit;
	}
	
	/** @param showOnInit See {@link #showOnInit} */
	public void setShowOnInit(boolean showOnInit){
		this.showOnInit = showOnInit;
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
		try{
			this.keyAction(key, action != GLFW_RELEASE, GLFWModUtils.isShift(mods), GLFWModUtils.isAlt(mods), GLFWModUtils.isCtrl(mods));
		}catch(Exception e){
			ZConfig.exception(e);
		}
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
		try{
			this.mouseAction(button, action != GLFW_RELEASE, GLFWModUtils.isShift(mods), GLFWModUtils.isAlt(mods), GLFWModUtils.isCtrl(mods));
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	/**
	 * The method directly used as a callback for a GLFW mouse movement
	 *
	 * @param window The ID of the window from which the event occurred
	 * @param x The raw x pixel coordinate of the mouse on the GLFW window
	 * @param y The raw y pixel coordinate of the mouse on the GLFW window
	 */
	private void mouseMove(long window, double x, double y){
		try{
			this.mouseMove(x, y);
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	/**
	 * The method directly used as a callback for a GLFW mouse wheel movement
	 *
	 * @param x The amount of distance scrolled on the x axis, unused
	 * @param y The amount of distance scrolled on the y axis, used as the scroll amount
	 */
	private void mouseWheelMove(long window, double x, double y){
		try{
			this.mouseWheelMove(y);
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	/**
	 * The method directly used as a callback for a GLFW window size change
	 *
	 * @param window The id of the window which was changed
	 * @param w The new width
	 * @param h The new height
	 */
	private void windowSizeChanged(long window, int w, int h){
		try{
			// Throw away if the size is zero, glfw is weird and sends zero size when in fullscreen and clicking on a different monitor
			if(w == 0 || h == 0){
				return;
			}
			
			this.windowSizeChanged(w, h);
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	/**
	 * The method directly used as a callback for a GLFW window getting minimized, i.e. iconified
	 *
	 * @param window The id of the window which had its state changed
	 * @param min true if the window was minimized, false otherwise
	 */
	private void windowMinimize(long window, boolean min){
		try{
			this.windowMinimize(min);
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	/**
	 * The method directly used as a callback for a GLFW window losing or gaining focus, i.e. iconified
	 *
	 * @param window The id of the window which had its state changed
	 * @param focus true if the window gained focus, false otherwise
	 */
	private void windowFocus(long window, boolean focus){
		try{
			this.windowFocus(focus);
		}catch(Exception e){
			ZConfig.exception(e);
		}
	}
	
	@Override
	public void resize(int w, int h){
		super.resize(w, h);
		glfwSetWindowSize(this.getWindowID(), w, h);
	}
	
	@Override
	protected boolean enterFullScreen(){
		// Keep track of the last bounds the window was in
		this.storeLastWindowBounds();
		
		long monitor = this.center();
		
		if(monitor == NULL){
			ZConfig.error("Failed to find any monitors to create a fullscreen window");
			return false;
		}
		// Put the found monitor in full screen on that window
		var mode = glfwGetVideoMode(monitor);
		if(mode == null){
			ZConfig.error("Failed to get a video mode to create a fullscreen window");
			return false;
		}
		
		// Find where the monitor is
		int[] mx = new int[1];
		int[] my = new int[1];
		glfwGetMonitorPos(monitor, mx, my);
		
		if(BORDERLESS_FULLSCREEN){
			// Tell the window to take up the full screen
			glfwSetWindowAttrib(this.getWindowID(), GLFW_DECORATED, GLFW_FALSE);
			glfwSetWindowAttrib(this.getWindowID(), GLFW_AUTO_ICONIFY, GLFW_FALSE);
			
			// Tell windows to make the window take up full screen
			SetWindowPos(this.win32Id, HWND_TOP, mx[0], my[0], mode.width(), mode.height(), SWP_FRAMECHANGED);
			
			// Magic attributes to make borderless fullscreen work on windows, thank you https://github.com/Kira-NT for the cubes without borders mod showing an example of something like this
			long style = WS_VISIBLE | WS_CLIPCHILDREN | WS_CLIPSIBLINGS | WS_GROUP;
			long exStyle = WS_EX_APPWINDOW | WS_EX_ACCEPTFILES | WS_EX_COMPOSITED | WS_EX_LAYERED;
			SetWindowLongPtr(this.win32Id, GWL_STYLE, style);
			SetWindowLongPtr(this.win32Id, GWL_EXSTYLE, exStyle);
		}
		else{
			glfwSetWindowAttrib(this.getWindowID(), GLFW_DECORATED, GLFW_FALSE);
			
			glfwSetWindowPos(this.getWindowID(), mx[0], my[0]);
			glfwSetWindowSize(this.getWindowID(), mode.width(), mode.height());
		}
		
		return true;
	}
	
	@Override
	protected boolean exitFullScreen(){
		if(BORDERLESS_FULLSCREEN){
			// Tell windows to set the size back to what it was before going in full screen
			SetWindowPos(this.win32Id, HWND_TOP, this.lastWindowedX, this.lastWindowedY, this.lastWindowedWidth, this.lastWindowedHeight, SWP_FRAMECHANGED);
			
			// Tell windows to show the window and keep it at whatever it was originally
			long style = this.originalStyle | WS_VISIBLE;
			long exStyle = this.originalExStyle;
			SetWindowLongPtr(this.win32Id, GWL_STYLE, style);
			SetWindowLongPtr(this.win32Id, GWL_EXSTYLE, exStyle);
			
			// Tell the window to put the border, close button, etc
			glfwSetWindowAttrib(this.getWindowID(), GLFW_DECORATED, GLFW_TRUE);
		}
		else{
			glfwSetWindowAttrib(this.getWindowID(), GLFW_DECORATED, GLFW_TRUE);
		}
		
		this.restoreLastWindowBounds();
		
		return true;
	}
	
	/** Find the current position and size of this window and store the values in this class */
	private void storeLastWindowBounds(){
		var x = new int[1];
		var y = new int[1];
		var w = new int[1];
		var h = new int[1];
		
		glfwGetWindowPos(this.windowID, x, y);
		glfwGetWindowSize(this.windowID, w, h);
		
		this.lastWindowedX = x[0];
		this.lastWindowedY = y[0];
		this.lastWindowedWidth = w[0];
		this.lastWindowedHeight= h[0];
	}
	
	/** Set the current position and size of this window to values stored in this class */
	private void restoreLastWindowBounds(){
		glfwSetWindowSize(this.getWindowID(), this.lastWindowedWidth, this.lastWindowedHeight);
		glfwSetWindowPos(this.getWindowID(), this.lastWindowedX, this.lastWindowedY);
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
			ZConfig.error("Failed to center window, could not find window mode");
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
	
	/**
	 * Get the ID of the currently used window, i.e. either the windowed version or the full screen version
	 *
	 * @return The id
	 */
	public long getCurrentWindowID(){
		return this.getWindowID();
	}
	
	@Override
	public void setWindowTitle(String windowTitle){
		super.setWindowTitle(windowTitle);
		if(this.getWindowID() != NULL) glfwSetWindowTitle(this.getWindowID(), windowTitle);
	}
	
	/** @return See {@link #mouseInput} */
	public GLFWMouseInput getMouseInput(){
		return this.mouseInput;
	}
	
	/** @return See {@link #keyInput} */
	public GLFWKeyInput getKeyInput(){
		return this.keyInput;
	}
	
	@Override
	public void updateMouseNormally(boolean normal){
		if(normal) glfwSetInputMode(this.getCurrentWindowID(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		else glfwSetInputMode(this.getCurrentWindowID(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
}
