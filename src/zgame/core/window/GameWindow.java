package zgame.core.window;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION;
import static org.lwjgl.opengl.GL43.glDebugMessageControl;

import zgame.core.Game;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.buffer.GameBuffer;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.utils.OnOffState;

import java.awt.Point;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import zgame.core.utils.ZRect2D;
import zgame.settings.BooleanTypeSetting;

import java.awt.Dimension;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * A class that handles one central window. This includes an option to move to full screen
 */
public abstract class GameWindow implements Destroyable{
	
	/** The title displayed on the window */
	private String windowTitle;
	
	/** true if this window has been initialized, false otherwise, i.e. it has not been initialized or is destroyed */
	private boolean initialized;
	
	/** true if the Game is currently in full screen, false otherwise */
	private boolean inFullScreen;
	/** Determines if on the next OpenGL loop, the screen should update if it is or is not in full screen */
	private OnOffState updateFullscreen;
	/** The position of the window before moving to full screen */
	private Point oldPosition;
	
	/** true if this window is currently minimized, false otherwise */
	private boolean minimized;
	/** true if this window is currently in focus, false otherwise */
	private boolean focused;
	
	/** true to use vsync, i.e. lock the framerate to the refresh rate of the monitor, false otherwise */
	private boolean useVsync;
	/** Determines if on the next OpenGL loop, vsync should update */
	private OnOffState updateVsync;
	
	/** A lambda function which is called each time a key is pressed or released, can be null to do nothing */
	private ButtonAction keyActionMethod;
	/** A lambda function which is called each time a mouse button is pressed or released, can be null to do nothing */
	private ButtonAction mouseActionMethod;
	/** A lambda function which is called each time a mouse is moved, can be null to do nothing */
	private MouseMove mouseMoveMethod;
	/** A lambda function which is called each time a mouse wheel is moved, can be null to do nothing */
	private MouseWheelMove mouseWheelMoveMethod;
	
	/**
	 * true if, when drawing the final {@link Renderer} image to the screen, the image should stretch to fill up the entire screen, false to draw the image in the center of
	 * the screen leave black bars in areas that the image doesn't fill up
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
	
	/** true if the mouse moves normally, false otherwise */
	private boolean mouseNormally;
	
	/** true if the internal buffer should be resized any time the window changes size, to match the window, false otherwise */
	private boolean resizeScreenOnResizeWindow;
	
	/** true if the window should only render when the window has focus, false otherwise */
	private boolean focusedRender;
	/** true if the game should only render frames when the game window is not minimized, false otherwise */
	private boolean minimizedRender;
	
	/** A list of actions to run when this window changes size */
	private final ArrayList<SizeChange> sizeChangeListeners;
	
	/** Functions to call when this window enters full screen */
	private final ArrayList<EnterFullScreen> enterFullScreenListeners;
	
	/** The function used to draw this window, or null if no rendering is defined */
	private RenderFunc renderFunc;
	
	/** The main buffer rendered to for this window */
	private final GameBuffer windowBuffer;
	
	/** The renderer this window uses to draw graphics */
	private final Renderer renderer;
	
	/** An interface for a lambda method which is called each time a key or mouse button is pressed or released */
	public interface ButtonAction{
		/**
		 * Called when a key is pressed or released
		 *
		 * @param key The id of the key
		 * @param press true if the key was pressed, false for released
		 * @param shift true if shift is pressed, false otherwise
		 * @param alt true if alt is pressed, false otherwise
		 * @param ctrl true if ctrl is pressed, false otherwise
		 */
		void act(int key, boolean press, boolean shift, boolean alt, boolean ctrl);
	}
	
	/** An interface for a lambda method which is called each time a mouse is moved */
	public interface MouseMove{
		/**
		 * Called when a mouse button is pressed or released
		 *
		 * @param x The x coordinate in screen coordinates
		 * @param y The y coordinate in screen coordinates
		 */
		void act(double x, double y);
	}
	
	/** An interface for a lambda method which is called each time a mouse wheel is moved */
	public interface MouseWheelMove{
		/**
		 * Called when a mouse button is pressed or released
		 *
		 * @param amount The amount the scroll wheel was moved
		 */
		void act(double amount);
	}
	
	/** An interface for a lambda called when the window size changes */
	public interface SizeChange{
		/**
		 * Called when the size of this window changes
		 *
		 * @param width The new width of the window
		 * @param height The new height of the window
		 */
		void changed(int width, int height);
	}
	
	/** An interface for a lambda called when the window enters full screen */
	public interface EnterFullScreen{
		/**
		 * Called when a window goes into full screen
		 *
		 * @param window The window that went into full screen
		 */
		void entered(GameWindow window);
	}
	
	/** An interface for passing a function to use to draw the contents of the window */
	public interface RenderFunc{
		/**
		 * Called when a window needs to be redrawn
		 *
		 * @param r The renderer to draw with
		 */
		void render(Renderer r);
	}
	
	/**
	 * Create a new default {@link GameWindow}.
	 * This does no initialization for OpenGL or window managerment, call {@link #init()} to do that
	 */
	public GameWindow(){
		// Init general values
		this.windowTitle = "Game";
		this.initialized = false;
		this.width = 1280;
		this.height = 720;
		this.focused = true;
		this.minimized = false;
		this.useVsync = true;
		this.stretchToFill = false;
		this.oldPosition = new Point(0, 0);
		this.keyActionMethod = null;
		this.mouseActionMethod = null;
		this.mouseMoveMethod = null;
		this.mouseWheelMoveMethod = null;
		this.resizeScreenOnResizeWindow = false;
		this.focusedRender = false;
		this.minimizedRender = false;
		this.sizeChangeListeners = new ArrayList<>();
		this.enterFullScreenListeners = new ArrayList<>();
		
		// Set up full screen
		this.updateFullscreen = OnOffState.NOTHING;
		
		// Set up vsync
		this.updateVsync = OnOffState.NOTHING;
		
		// Init mouse movement, use normal movement by default
		this.mouseNormally = true;
		
		// Set up the internal buffer
		this.windowBuffer = new GameBuffer(this.getWidth(), this.getHeight());
		
		// Set up the blank renderer
		this.renderer = new Renderer();
	}
	
	/**
	 * Call to allow this window to be used, calling all initialization values for OpenGL.
	 * Override this and call super to implement custom start behavior for a window implementation
	 */
	public void init(){
		// Ensure window context is set up
		this.createContext();
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		// Turn off debug notifications
		glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer)null, false);
		
		// Additional error messaging
		GLUtil.setupDebugMessageCallback(System.err);
		
		// Set up full screen
		this.updateFullscreen = OnOffState.NOTHING;
		
		// Init internal buffer
		this.getWindowBuffer().regenerateBuffer();
		this.updateInternalValues();
		
		// Set up vsync
		this.updateVsync = OnOffState.NOTHING;
		this.setUseVsyncNow(this.usesVsync());
		
		// setup callbacks
		this.initCallBacks();
		
		// Set up renderer
		this.renderer.init();
		
		// Set up texture settings for drawing with an alpha channel
		initTextureSettings();
		
		// Init mouse movement, use normal movement by default
		this.mouseNormally = true;
		this.updateMouseNormally(this.isMouseNormally());
		
		this.initialized = true;
	}
	
	/** @return See {@link #renderer}. Should be used sparingly, generally should be passed in to appropriate methods when needed */
	public Renderer getRenderer(){
		return this.renderer;
	}
	
	/** Called during object initialization. Must establish window context with OpenGL before further initialization can occur */
	protected abstract void createContext();
	
	/** Call the appropriate methods to  */
	public abstract void obtainContext();
	
	/**
	 * Call this method once at the beginning of each OpenGL loop to check for events, i.e. keyboard input, mouse input, window size changed, etc. This method will also update
	 * the fullscreen and vsync status
	 */
	public void checkEvents(){
		// Update fullscreen status
		if(this.updateFullscreen.shouldUpdate()){
			this.setInFullScreenNow(this.updateFullscreen.willEnter());
			this.updateFullscreen = OnOffState.NOTHING;
		}
		// Update vsync status
		if(this.updateVsync.shouldUpdate()){
			this.setUseVsyncNow(this.updateVsync.willEnter());
			this.updateVsync = OnOffState.NOTHING;
		}
	}
	
	/** Call this method once at the end of each OpenGL loop to swap the buffers, i.e. to put the final image on the screen */
	public abstract void swapBuffers();
	
	/** End the program, freeing all resources. Do not call directly outside of the main loop */
	@Override
	public void destroy(){
		this.getWindowBuffer().destroy();
		this.getRenderer().destroy();
		this.initialized = false;
	}
	
	/** Perform any necessary logic for when all windows have been closed */
	public abstract void onAllWindowsClosed();
	
	/** @return true if the current window is no longer used and should close */
	public abstract boolean shouldClose();
	
	/** @return An id representing this window as a long */
	public abstract long getLongId();
	
	/**
	 * Assign the current window all needed callbacks, i.e. input, window size changing, etc. This will usually be an expensive operation and should not be regularly called
	 *
	 * @return true if the callbacks were set, false if an error occurred
	 */
	public abstract boolean initCallBacks();
	
	/** @return See {@link #initialized} */
	public boolean isInitialized(){
		return this.initialized;
	}
	
	/**
	 * Draw the given contents of this window's internal buffer using the given window and game, along with checking any window events which need to happen
	 */
	public void loopFunction(){
		// Update the window
		boolean focused = this.isFocused();
		boolean minimized = this.isMinimized();
		this.checkEvents();
		
		// Only perform rendering operations if the window should be rendered, based on the state of the window's focus and minimize
		if(!(this.isFocusedRender() && !focused) && !(this.isMinimizedRender() && minimized)){
			var r = this.getRenderer();
			r.pushBuffer(this.getWindowBuffer());

			// Clear the main framebuffer
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			// Clear the internal renderer and set it up to use the renderer's frame buffer to draw to
			r.clear();

			// Render objects using the renderer's frame buffer
			r.initToDraw();

			var fun = this.getRenderFunc();
			if(fun != null) fun.render(r);

			// Draw the renderer's frame buffer to the window
			r.drawToWindow(this);

			// Update the window
			this.swapBuffers();

			r.popBuffer();
		}
	}
	
	/**
	 * Call this method when a key is acted on, i.e. pressed or released
	 *
	 * @param key The id of the key
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public void keyAction(int key, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.keyActionMethod != null) this.keyActionMethod.act(key, press, shift, alt, ctrl);
		this.getKeyInput().buttonAction(key, press, shift, alt, ctrl);
	}
	
	/**
	 * Call this method when a mouse button is acted on, i.e. pressed or released
	 *
	 * @param button The ID of the mouse button
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	protected void mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.mouseActionMethod != null) this.mouseActionMethod.act(button, press, shift, alt, ctrl);
		this.getMouseInput().buttonAction(button, press, shift, alt, ctrl);
	}
	
	/**
	 * Call this method when the mouse is moved
	 *
	 * @param x The raw x pixel coordinate of the mouse on the window
	 * @param y The raw y pixel coordinate of the mouse on the window
	 */
	protected void mouseMove(double x, double y){
		if(this.mouseMoveMethod != null) this.mouseMoveMethod.act(this.windowToScreenX(x), this.windowToScreenY(y));
		this.getMouseInput().mouseMove(x, y);
	}
	
	/**
	 * Call this method when the mouse wheel is moved
	 *
	 * @param amount The amount the scroll wheel was moved
	 */
	protected void mouseWheelMove(double amount){
		if(this.mouseWheelMoveMethod != null) this.mouseWheelMoveMethod.act(amount);
		this.getMouseInput().mouseWheelMove(amount);
	}
	
	/**
	 * Call this method when the window size is changed
	 *
	 * @param w The new width
	 * @param h The new height
	 */
	protected void windowSizeChanged(int w, int h){
		this.setWidth(w);
		this.setHeight(h);
		if(this.isResizeScreenOnResizeWindow()) this.resizeScreen(w, h);
		
		this.updateWindowSize();
	}
	
	/**
	 * Call this method when the window is minimized or unminimized
	 *
	 * @param min true if the window was minimized, false otherwise
	 */
	protected void windowMinimize(boolean min){
		this.minimized = min;
	}
	
	/**
	 * Call this method when the window gains or loses focus
	 *
	 * @param focus true if the window gained focus, false otherwise
	 */
	protected void windowFocus(boolean focus){
		this.focused = focus;
	}
	
	/**
	 * Set the desired size of {@link #windowBuffer} and the window's size without calling any OpenGL operations.
	 * The buffer will need to be regenerated and the window's actual size updated after a call to this
	 *
	 * @param w The new width
	 * @param h The new height
	 */
	public void setSize(int w, int h){
		this.windowBuffer.setSize(w, h);
		this.setWidth(w);
		this.setHeight(h);
	}
	
	/**
	 * Update the size of the window, directly changing the window. Does nothing if the {@link GameWindow} is in full screen, only works on a windowed version. This method
	 * should be overwritten and called as super to directly update the size, it should not be called outside the main OpenGL loop or initialization
	 *
	 * @param w The new width, in pixels, not including any decorators such as the minimize button
	 * @param h The new height, in pixels, not including any decorators such as the minimize button
	 */
	public void resize(int w, int h){
		if(this.isInFullScreen()) return;
		if(this.isResizeScreenOnResizeWindow()) this.resizeScreen(w, h);
		
		this.setWidth(w);
		this.setHeight(h);
	}
	
	/**
	 * Set the size of this window and the internal buffer used to render to the screen and perform needed updates to recalculate any necessary values.
	 * This is an expensive operation and should only be used during initialization or with things like changing a setting
	 *
	 * @param w The new width
	 * @param h The new height
	 */
	public void resizeUniform(int w, int h){
		this.resize(w, h);
		this.resizeScreen(w, h);
		this.updateWindowSize();
	}
	
	/**
	 * Initialize the settings for textures based on the needs of simple 2D pixel art textures with transparency Can overwrite this method to use different settings
	 */
	public void initTextureSettings(){
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	/** @return See {@link #windowTitle} */
	public String getWindowTitle(){
		return this.windowTitle;
	}
	
	/** @param windowTitle See {@link #windowTitle} */
	public void setWindowTitle(String windowTitle){
		this.windowTitle = windowTitle;
	}
	
	/** @return See {@link #inFullScreen} */
	public boolean isInFullScreen(){
		return this.inFullScreen;
	}
	
	/**
	 * This method instantly changes the fullscreen state, do not use when outside of the main OpenGL thread or initialization
	 *
	 * @param inFullScreen See {@link #inFullScreen}
	 */
	public void setInFullScreenNow(boolean inFullScreen){
		this.inFullScreen = inFullScreen;
		if(this.inFullScreen){
			// Store the old position of the window
			this.oldPosition = this.getWindowPos();
			this.enterFullScreen();
			
			for(var listener : this.enterFullScreenListeners) listener.entered(this);
		}
		else{
			this.exitFullScreen();
			
			// Put the window back where it was before going to full screen
			this.setWindowPosition(this.oldPosition.x, this.oldPosition.y);
		}
		// Reset the renderer vertex objects
		this.getRenderer().reloadVertexes();
		
		// Ensure the current window has the callbacks
		this.initCallBacks();
		
		// Update v-sync to account for full screen
		this.updateVsync();
		
		// Update screen width and height
		this.updateWindowSize();
		if(this.isResizeScreenOnResizeWindow()) this.resizeScreen(this.getWidth(), this.getHeight());
		else this.resizeScreen(this.getScreenWidth(), this.getScreenHeight());
		
		// Ensure the window has appropriate texture settings
		this.initTextureSettings();
		
		// Make sure no buttons are pressed
		this.getMouseInput().clear();
		this.getKeyInput().clear();
	}
	
	/**
	 * Called when the window needs to enter fullscreen
	 *
	 * @return true if entering was successful, false otherwise
	 */
	protected abstract boolean enterFullScreen();
	
	/**
	 * Called when the window needs to exit fullscreen
	 *
	 * @return true if exiting was successful, false otherwise
	 */
	protected abstract boolean exitFullScreen();
	
	/**
	 * Modify the size of {@link #windowBuffer}, this is a costly operation and should not regularly be run
	 *
	 * @param width The width, in pixels, of the size of the internal buffer
	 * @param height The height, in pixels, of the size of the internal buffer
	 */
	public void resizeScreen(int width, int height){
		this.getWindowBuffer().regenerateBuffer(width, height);
		this.updateWindowSize();
	}
	
	/** Ensure the current stored width and height of the window match the current window size */
	public void updateWindowSize(){
		Dimension s = this.getWindowSize();
		this.setWidth(s.width);
		this.setHeight(s.height);
		
		for(var listener : this.sizeChangeListeners) listener.changed(s.width, s.height);
	}
	
	/**
	 * Depending on implementation, this method may do nothing if the window is currently in full screen
	 *
	 * @param x the x coordinate position
	 * @param y the y coordinate position
	 */
	public abstract void setWindowPosition(int x, int y);
	
	/** @return A Point containing the position of the window */
	public abstract Point getWindowPos();
	
	/**
	 * @return A {@link Dimension} containing the width and height of the content of the window in pixels, this should not include decorators such as a menu bar, minimize
	 * 		button, etc
	 */
	public abstract Dimension getWindowSize();
	
	/** @return See {@link #minimized} */
	public boolean isMinimized(){
		return this.minimized;
	}
	
	/** @return See {@link #focused} */
	public boolean isFocused(){
		return this.focused;
	}
	
	/**
	 * Center the window to the monitor which contains the upper left hand corner of the window. Does nothing if no monitor is found
	 *
	 * @return The monitor id which the window was centered to
	 */
	public abstract long center();
	
	/**
	 * Call to change the fullscreen state on the next OpenGL loop. If the window is already in the desired state, nothing happens
	 * When using this window with a {@link Game}, the setting {@link BooleanTypeSetting#FULLSCREEN} should be set instead of calling this method
	 *
	 * @param fullscreen true to enter fullscreen, false to exist.
	 */
	public void setFullscreen(boolean fullscreen){
		this.updateFullscreen = OnOffState.state(fullscreen);
	}
	
	/** @return See {@link #useVsync} */
	public boolean usesVsync(){
		return this.useVsync;
	}
	
	/**
	 * Set whether or not to use vsync on the next OpenGL loop
	 *
	 * @param useVsync See {@link #useVsync}
	 */
	public void setUseVsync(boolean useVsync){
		this.updateVsync = OnOffState.state(useVsync);
	}
	
	/**
	 * This method instantly changes the vsync state, do not call this outside of initialization or the OpenGL loop.
	 *
	 * @param useVsync See {@link #useVsync}
	 */
	private void setUseVsyncNow(boolean useVsync){
		this.useVsync = useVsync;
		this.setupVsync(useVsync);
	}
	
	/**
	 * Perform any needed operations to enable or disable vsync
	 *
	 * @param useVsync true to turn vsync on, false to turn it off
	 */
	protected abstract void setupVsync(boolean useVsync);
	
	/**
	 * Update the state of using vsync on this GameWindow, i.e. call the appropriate methods to turn vsync on or off depending on the value of {@link #useVsync}
	 */
	private void updateVsync(){
		this.setUseVsyncNow(this.usesVsync());
	}
	
	/** @return See {@link #windowBuffer} */
	public GameBuffer getWindowBuffer(){
		return this.windowBuffer;
	}
	
	/** @param keyActionMethod See {@link #keyActionMethod} */
	public void setKeyActionMethod(ButtonAction keyActionMethod){
		this.keyActionMethod = keyActionMethod;
	}
	
	/** @param mouseActionMethod See {@link #mouseActionMethod} */
	public void setMouseActionMethod(ButtonAction mouseActionMethod){
		this.mouseActionMethod = mouseActionMethod;
	}
	
	/** @param mouseMoveMethod See {@link #mouseMoveMethod} */
	public void setMouseMoveMethod(MouseMove mouseMoveMethod){
		this.mouseMoveMethod = mouseMoveMethod;
	}
	
	/** @param mouseWheelMoveMethod See {@link #mouseWheelMoveMethod} */
	public void setMouseWheelMoveMethod(MouseWheelMove mouseWheelMoveMethod){
		this.mouseWheelMoveMethod = mouseWheelMoveMethod;
	}
	
	/** @return The {@link ZMouseInput} object which controls mouse input for the window */
	public abstract ZMouseInput getMouseInput();
	
	/** @return See {@link #mouseNormally} */
	public boolean isMouseNormally(){
		return this.mouseNormally;
	}
	
	/** @param normal See {@link #mouseNormally} */
	public final void setMouseNormally(boolean normal){
		if(this.mouseNormally == normal) return;
		this.mouseNormally = normal;
		updateMouseNormally(this.mouseNormally);
	}
	
	/**
	 * Set the mouse to act normally or to be invisible and stuck to the center of the window
	 *
	 * @param normal true for normal, false otherwise
	 */
	public abstract void updateMouseNormally(boolean normal);
	
	/**
	 * Update the mouse to act normally or to be invisible and stuck to the center of the window depending on the current value of {@link #isMouseNormally()}
	 */
	public void updateMouseNormally(){
		this.updateMouseNormally(this.isMouseNormally());
	}
	
	/** @return The {@link ZKeyInput} object which controls keyboard input for the window */
	public abstract ZKeyInput getKeyInput();
	
	/** @return The width, in pixels, of {@link #windowBuffer} */
	public int getScreenWidth(){
		return this.getWindowBuffer().getWidth();
	}
	
	/** @return The height, in pixels, of {@link #windowBuffer} */
	public int getScreenHeight(){
		return this.getWindowBuffer().getHeight();
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
	
	/**
	 * Update the value of {@link #windowRatio} based on the current values of {@link #width} and {@link #height}
	 */
	private void updateRatios(){
		this.windowRatio = (double)this.getWidth() / this.getHeight();
	}
	
	/**
	 * Update the value of {@link #windowRatio} based on the current values of {@link #width} and {@link #height} Additionally, update the values of {@link #viewportX},
	 * {@link #viewportY}, {@link #viewportW}, {@link #viewportH}
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
	 * Update the stored state of the values to use for the viewport for drawing the contents of the screen
	 */
	public void updateViewportValues(){
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
			var buffer = this.getWindowBuffer();
			// wRatio for the window aspect ratio and tRatio for the buffer's aspect ratio
			double wRatio = this.getWindowRatio();
			double tRatio = buffer.getRatioWH();
			int w;
			int h;
			if(tRatio < wRatio){
				h = sh;
				w = (int)Math.round(h * tRatio);
			}
			else{
				w = sw;
				h = (int)Math.round(w * buffer.getRatioHW());
			}
			this.viewportX = (sw - w) >> 1;
			this.viewportY = (sh - h) >> 1;
			this.viewportW = w;
			this.viewportH = h;
		}
		this.viewportWInverse = 1.0 / this.viewportW;
		this.viewportHInverse = 1.0 / this.viewportH;
	}
	
	/** @return See {@link #resizeScreenOnResizeWindow} */
	public boolean isResizeScreenOnResizeWindow(){
		return this.resizeScreenOnResizeWindow;
	}
	
	/** @param resizeScreenOnResizeWindow See {@link #resizeScreenOnResizeWindow} */
	public void setResizeScreenOnResizeWindow(boolean resizeScreenOnResizeWindow){
		this.resizeScreenOnResizeWindow = resizeScreenOnResizeWindow;
	}
	
	/** @return See {@link #focusedRender} */
	public boolean isFocusedRender(){
		return this.focusedRender;
	}
	
	/** @param focusedRender See {@link #focusedRender} */
	public void setFocusedRender(boolean focusedRender){
		this.focusedRender = focusedRender;
	}
	
	/** @return See {@link #minimizedRender} */
	public boolean isMinimizedRender(){
		return this.minimizedRender;
	}
	
	/** @param minimizedRender See {@link #minimizedRender} */
	public void setMinimizedRender(boolean minimizedRender){
		this.minimizedRender = minimizedRender;
	}
	
	
	/** @return See {@link #renderFunc} */
	public RenderFunc getRenderFunc(){
		return this.renderFunc;
	}
	
	/** @param renderFunc See {@link #renderFunc} */
	public void setRenderFunc(RenderFunc renderFunc){
		this.renderFunc = renderFunc;
	}
	
	/**
	 * Add the given action to perform on a window changing
	 *
	 * @param listener The action to perform on the window size changing
	 */
	public void addSizeChangeListener(SizeChange listener){
		this.sizeChangeListeners.add(listener);
	}
	
	/**
	 * Remove the given action from this window
	 *
	 * @param listener The action to no longer perform on the window size changing
	 */
	public void removeSizeChangeListener(SizeChange listener){
		this.sizeChangeListeners.remove(listener);
	}
	
	/**
	 * Add the given action to perform on a window changing
	 *
	 * @param listener The action to perform on the window entering full screen
	 */
	public void addEnterFullScreenListener(EnterFullScreen listener){
		this.enterFullScreenListeners.add(listener);
	}
	
	/**
	 * Remove the given action from this window
	 *
	 * @param listener The action to no longer perform on the window entering full screen
	 */
	public void removeEnterFullScreenListener(EnterFullScreen listener){
		this.enterFullScreenListeners.remove(listener);
	}
	
	/**
	 * Determine if the given bounds are in the bounds of {@link #windowBuffer}
	 *
	 * @param bounds The bounds to check, in game coordinates
	 * @return true if they intersect, i.e. return true if any part of the given bounds is in {@link #windowBuffer} bounds, false otherwise
	 */
	public boolean gameBoundsInScreen(ZRect2D bounds){
		ZRect2D rBounds = this.getWindowBuffer().getBounds();
		GameCamera c = Game.get().getCamera();
		ZRect2D gBounds;
		if(c == null) gBounds = rBounds;
		else gBounds = c.boundsScreenToGame(rBounds.getX(), rBounds.getBounds().getY(), rBounds.getBounds().getWidth(), rBounds.getBounds().getHeight());
		return gBounds.intersects(bounds);
	}
	
	/**
	 * Convert an x coordinate value in window space, to a coordinate in screen space coordinates
	 *
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenX(double x){
		return windowToScreen(x, this.viewportX(), this.viewportWInverse(), this.getWindowBuffer().getWidth());
	}
	
	/**
	 * Convert a y coordinate value in window space, to a coordinate in screen space coordinates
	 *
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenY(double y){
		return windowToScreen(y, this.viewportY(), this.viewportHInverse(), this.getWindowBuffer().getHeight());
	}
	
	/**
	 * Convert a coordinate value in window space, to a coordinate in screen space coordinates
	 *
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowInverseSize The inverse of the size of the window
	 * @param screenSize The size of the screen to convert to
	 * @return The value in screen coordinates
	 */
	public static double windowToScreen(double p, double viewportPos, double windowInverseSize, double screenSize){
		return (p - viewportPos) * windowInverseSize * screenSize;
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in window space coordinates
	 *
	 * @param x The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowX(double x){
		return screenToWindow(x, this.viewportX(), this.viewportW(), this.getWindowBuffer().getInverseWidth());
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in window space coordinates
	 *
	 * @param y The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowY(double y){
		return screenToWindow(y, this.viewportY(), this.viewportH(), this.getWindowBuffer().getInverseHeight());
	}
	
	/**
	 * Convert a coordinate value in screen space, to a coordinate in window space coordinates
	 *
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowSize The size of the window
	 * @param screenInverseSize The inverse of the size of the screen
	 * @return The value in window coordinates
	 */
	public static double screenToWindow(double p, double viewportPos, double windowSize, double screenInverseSize){
		return p * screenInverseSize * windowSize + viewportPos;
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in OpenGL coordinates
	 *
	 * @param x The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlX(double x){
		return screenToGl(x, this.viewportX(), this.getWidth(), this.getWindowBuffer().getInverseWidth(), this.getInverseWidth());
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in OpenGL coordinates
	 *
	 * @param y The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlY(double y){
		return screenToGl(y, this.viewportY(), this.getHeight(), this.getWindowBuffer().getInverseHeight(), this.getInverseHeight());
	}
	
	/**
	 * Convert a coordinate value in screen space, to a coordinate in OpenGL space coordinates
	 *
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowSize The size of the window
	 * @param screenInverseSize The inverse of the size of the screen to convert from
	 * @param windowInverseSize The inverse of the size of the window to convert from
	 * @return The value in OpenGL coordinates
	 */
	public static double screenToGl(double p, double viewportPos, double windowSize, double screenInverseSize, double windowInverseSize){
		return screenToWindow(p, viewportPos, windowSize, screenInverseSize) * windowInverseSize * 2 - 1;
	}
	
	/**
	 * Convert an x coordinate value in OpenGL space, to a coordinate in screen coordinates
	 *
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenX(double x){
		return glToScreen(x, this.viewportX(), this.getInverseWidth(), this.getWindowBuffer().getWidth(), this.getWidth());
	}
	
	/**
	 * Convert a y coordinate value in OpenGL space, to a coordinate in screen coordinates
	 *
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenY(double y){
		return glToScreen(y, this.viewportY(), this.getInverseHeight(), this.getWindowBuffer().getHeight(), this.getHeight());
	}
	
	/**
	 * Convert a coordinate value in OpenGL space, to a coordinate in screen space coordinates
	 *
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowInverseSize The inverse of the size of the window
	 * @param screenSize The size of the screen to convert to
	 * @param windowSize The size of the window to convert to
	 * @return The value in OpenGL coordinates
	 */
	public static double glToScreen(double p, double viewportPos, double windowInverseSize, double screenSize, double windowSize){
		return windowToScreen(((p + 1) * 0.5) * windowSize, viewportPos, windowInverseSize, screenSize);
	}
	
	/**
	 * Convert a size on the x axis in window space, to a size in screen space
	 *
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenX(double x){
		return sizeWindowToScreen(x, this.viewportWInverse(), this.getWindowBuffer().getWidth());
	}
	
	/**
	 * Convert a size on the y axis in window space, to a size in screen space
	 *
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenY(double y){
		return sizeWindowToScreen(y, this.viewportHInverse(), this.getWindowBuffer().getHeight());
	}
	
	/**
	 * Convert a size in window space, to a size in screen space
	 *
	 * @param p The value to convert
	 * @param windowInverseSize The size of the window
	 * @param screenSize The size of the screen to convert to
	 * @return The converted size
	 */
	public static double sizeWindowToScreen(double p, double windowInverseSize, double screenSize){
		return p * windowInverseSize * screenSize;
	}
	
	/**
	 * Convert a size on the x axis in screen space, to a size in window space
	 *
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowX(double x){
		return sizeScreenToWindow(x, this.viewportW(), this.getWindowBuffer().getInverseWidth());
	}
	
	/**
	 * Convert a size on the y axis in screen space, to a size in window space
	 *
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowY(double y){
		return sizeScreenToWindow(y, this.viewportH(), this.getWindowBuffer().getInverseHeight());
	}
	
	/**
	 * Convert a size in screen space, to a size in window space
	 *
	 * @param p The value to convert
	 * @param windowSize The size of the window
	 * @param screenInverseSize The inverse of the size of the screen to convert from
	 * @return The converted size
	 */
	public static double sizeScreenToWindow(double p, double windowSize, double screenInverseSize){
		return p * screenInverseSize * windowSize;
	}
	
	/**
	 * Convert a size on the x axis in screen space, to a size in OpenGL space
	 *
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlX(double x){
		return sizeScreenToGl(x, this.getWidth(), this.getWindowBuffer().getInverseWidth(), this.getInverseWidth());
	}
	
	/**
	 * Convert a size on the y axis in screen space, to a size in OpenGL space
	 *
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlY(double y){
		return sizeScreenToGl(y, this.getHeight(), this.getWindowBuffer().getInverseHeight(), this.getInverseHeight());
	}
	
	/**
	 * Convert a size in screen space, to a size in OpenGL space
	 *
	 * @param p The value to convert
	 * @param windowSize The size of the window
	 * @param screenInverseSize The inverse of the size of the screen to convert from
	 * @param windowInverseSize The inverse of the size of the window to convert from
	 * @return The converted size
	 */
	public static double sizeScreenToGl(double p, double windowSize, double screenInverseSize, double windowInverseSize){
		return sizeScreenToWindow(p, windowSize, screenInverseSize) * windowInverseSize * 2;
	}
	
	/**
	 * Convert a size on the x axis in OpenGL space, to a size in screen space
	 *
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeGlToScreenX(double x){
		return sizeGlToScreen(x, this.getInverseWidth(), this.getWindowBuffer().getWidth(), this.getWidth());
	}
	
	/**
	 * Convert a size on the y axis in OpenGL space, to a size in screen space
	 *
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeGlToScreenY(double y){
		return sizeGlToScreen(y, this.getInverseHeight(), this.getWindowBuffer().getHeight(), this.getHeight());
	}
	
	/**
	 * Convert a size in OpenGL space, to a size in screen space
	 *
	 * @param p The value to convert
	 * @param windowInverseSize The inverse of the size of the window
	 * @param screenSize The size of the screen to convert to
	 * @param windowSize The size of the window to convert to
	 * @return The converted size
	 */
	public static double sizeGlToScreen(double p, double windowInverseSize, double screenSize, double windowSize){
		return sizeWindowToScreen(p * 0.5 * windowSize, windowInverseSize, screenSize);
	}
	
}
