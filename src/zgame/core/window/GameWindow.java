package zgame.core.window;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION;
import static org.lwjgl.opengl.GL43.glDebugMessageControl;

import zgame.core.Game;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.utils.OnOffState;

import java.awt.Point;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import java.awt.Dimension;
import java.nio.IntBuffer;

/**
 * A class that handles one central window. This includes an option to move to full screen
 */
public abstract class GameWindow implements Destroyable{
	
	/** The game associated with this window, or null if no association exists */
	private Game game;
	
	/** The title displayed on the window */
	private String windowTitle;
	
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
	
	/** The renderer used by this {@link GameWindow} to draw to a buffer which can later be drawn to the window */
	private final Renderer renderer;
	
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
	
	/**
	 * Create a GameWindow with the given parameters. This also handles all of the setup for LWJGL, including OpenGL and OpenAL
	 *
	 * @param title See {@link #windowTitle}
	 * @param winWidth See {@link #width}
	 * @param winHeight See {@link #height}
	 * @param screenWidth The width, in pixels, of the internal buffer to draw to
	 * @param screenHeight The height, in pixels, of the internal buffer to draw to
	 * @param maxFps See {@link Game#getMaxFps()}
	 * @param useVsync See {@link #useVsync}
	 * @param stretchToFill See {@link #stretchToFill}
	 */
	public GameWindow(String title, int winWidth, int winHeight, int screenWidth, int screenHeight, int maxFps, boolean useVsync, boolean stretchToFill, boolean printFps, int tps, boolean printTps){
		// Init general values
		this.windowTitle = title;
		this.width = winWidth;
		this.height = winHeight;
		this.focused = true;
		this.minimized = false;
		this.useVsync = useVsync;
		this.stretchToFill = stretchToFill;
		this.oldPosition = new Point(0, 0);
		this.keyActionMethod = null;
		this.mouseActionMethod = null;
		this.mouseMoveMethod = null;
		this.mouseWheelMoveMethod = null;
		this.resizeScreenOnResizeWindow = false;
		
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
		
		// Init renderer
		this.renderer = new Renderer(screenWidth, screenHeight);
		this.updateInternalValues();
		
		// Set up vsync
		this.updateVsync = OnOffState.NOTHING;
		this.setUseVsyncNow(useVsync);
		
		// setup callbacks
		this.initCallBacks();
		
		// Set up texture settings for drawing with an alpha channel
		initTextureSettings();
		
		// Init mouse movement, use normal movement by default
		this.mouseNormally = true;
		this.updateMouseNormally(true);
	}
	
	/** Called during object initialization. Must establish window context with OpenGL before further initialization can occur */
	protected abstract void createContext();
	
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
		this.getRenderer().destroy();
	}
	
	/** @return true if the current window is no longer used and should close */
	public abstract boolean shouldClose();
	
	/**
	 * Assign the current window all needed callbacks, i.e. input, window size changing, etc. This will usually be an expensive operation and should not be regularly called
	 *
	 * @return true if the callbacks were set, false if an error occurred
	 */
	public abstract boolean initCallBacks();
	
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
	 * Update the size of the window, directly changing the window. Does nothing if the {@link GameWindow} is in full screen, only works on a windowed version. This method
	 * should be overwritten and called as super to directly update the size, it should not be called outside the main OpenGL loop or initialization
	 *
	 * @param w The new width, in pixels, not including any decorators such as the minimize button
	 * @param h The new height, in pixels, not including any decorators such as the minimize button
	 */
	public void setSize(int w, int h){
		if(this.isInFullScreen()) return;
		if(this.isResizeScreenOnResizeWindow()) this.resizeScreen(w, h);
		
		this.setWidth(w);
		this.setHeight(h);
	}
	
	/**
	 * Set the size of this window and the internal buffer used to render to the screen and perform needed updates to recalculate any necessary values.
	 * This is an expensive operation and should only be used during initialization or with things like changing a setting
	 * @param w The new width
	 * @param h The new height
	 */
	public void setSizeUniform(int w, int h){
		this.setSize(w, h);
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
			
			// Apply any needed states from the game's type
			if(this.game != null) game.getRenderStyle().setupCore(this.game, this.getRenderer());
		}
		else{
			this.exitFullScreen();
			
			// Put the window back where it was before going to full screen
			this.setWindowPosition(this.oldPosition.x, this.oldPosition.y);
		}
		// Reset the renderer vertex objects
		var r = this.getRenderer();
		r.destroyVertexes();
		r.initVertexes();
		
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
	 * Modify the size of this the screen of {@link #renderer}. This is a costly operation and should not regularly be run
	 *
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public void resizeScreen(int width, int height){
		this.renderer.resize(width, height);
		this.updateWindowSize();
	}
	
	/** Ensure the current stored width and height of the window match the current window size */
	public void updateWindowSize(){
		Dimension s = this.getWindowSize();
		this.setWidth(s.width);
		this.setHeight(s.height);
		
		var game = this.getGame();
		if(game != null) game.onWindowSizeChange(s.width, s.height);
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
	
	/** @param game See {@link #game} */
	public void setGame(Game game){
		this.game = game;
	}
	
	/** @return See {@link #game} */
	public Game getGame(){
		return this.game;
	}
	
	/**
	 * Call to change the fullscreen state on the next OpenGL loop. If the window is already in the desired state, nothing happens
	 * When using this window with a {@link Game}, the setting {@link zgame.settings.BooleanTypeSetting#FULLSCREEN} should be set instead of calling this method
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
	
	/** @return See {@link #renderer} */
	public Renderer getRenderer(){
		return this.renderer;
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
	
	/** @return The width, in pixels, of the internal buffer */
	public int getScreenWidth(){
		return this.getRenderer().getBaseWidth();
	}
	
	/** @return The height, in pixels, of the internal buffer */
	public int getScreenHeight(){
		return this.getRenderer().getBaseHeight();
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
		var game = this.getGame();
		if(game != null) game.onWindowSizeChange(this.getWidth(), this.getHeight());
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
	 * Update the stored state of the values to use for the viewport for drawing the contents of the screen via {@link #renderer} This method does nothing the given renderer
	 * is not yet initialized
	 */
	public void updateViewportValues(){
		// Cannot perform this action without renderer initialized
		Renderer r = getRenderer();
		if(r == null) return;
		
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
			// wRatio for the window aspect ratio and tRatio for the render's aspect ratio
			double wRatio = this.getWindowRatio();
			double tRatio = r.getRatioWH();
			int w;
			int h;
			if(tRatio < wRatio){
				h = sh;
				w = (int)Math.round(h * tRatio);
			}
			else{
				w = sw;
				h = (int)Math.round(w * r.getRatioHW());
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
	
	/**
	 * Convert an x coordinate value in window space, to a coordinate in screen space coordinates
	 *
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenX(double x){
		return this.getRenderer().windowToScreenX(this, x);
	}
	
	/**
	 * Convert a y coordinate value in window space, to a coordinate in screen space coordinates
	 *
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenY(double y){
		return this.getRenderer().windowToScreenY(this, y);
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in window space coordinates
	 *
	 * @param x The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowX(double x){
		return this.getRenderer().screenToWindowX(this, x);
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in window space coordinates
	 *
	 * @param y The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowY(double y){
		return this.getRenderer().screenToWindowY(this, y);
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in OpenGL coordinates
	 *
	 * @param x The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlX(double x){
		return this.getRenderer().screenToGlX(this, x);
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in OpenGL coordinates
	 *
	 * @param y The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlY(double y){
		return this.getRenderer().screenToGlY(this, y);
	}
	
	/**
	 * Convert an x coordinate value in OpenGL space, to a coordinate in screen coordinates
	 *
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenX(double x){
		return this.getRenderer().glToScreenX(this, x);
	}
	
	/**
	 * Convert a y coordinate value in OpenGL space, to a coordinate in screen coordinates
	 *
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenY(double y){
		return this.getRenderer().glToScreenY(this, y);
	}
	
	/**
	 * Convert a size on the x axis in window space, to one in screen space
	 *
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenX(double x){
		return this.getRenderer().sizeWindowToScreenX(this, x);
	}
	
	/**
	 * Convert a size on the y axis in window space, to one in screen space
	 *
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenY(double y){
		return this.getRenderer().sizeWindowToScreenY(this, y);
	}
	
	/**
	 * Convert a size on the x axis in screen space, to one in window space
	 *
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowX(double x){
		return this.getRenderer().sizeScreenToWindowX(this, x);
	}
	
	/**
	 * Convert a size on the y axis in screen space, to one in window space
	 *
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowY(double y){
		return this.getRenderer().sizeScreenToWindowY(this, y);
	}
	
	/**
	 * Convert a size on the x axis in screen space, to one in OpenGL space
	 *
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlX(double x){
		return this.getRenderer().sizeScreenToGlX(this, x);
	}
	
	/**
	 * Convert a size on the y axis in screen space, to one in OpenGL space
	 *
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlY(double y){
		return this.getRenderer().sizeScreenToGlY(this, y);
	}
	
	/**
	 * Convert a size on the x axis in OpenGL space, to one in screen space
	 *
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeGlToScreenX(double x){
		return this.getRenderer().sizeGlToScreenX(this, x);
	}
	
	/**
	 * Convert a size on the y axis in OpenGL space, to one in screen space
	 *
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeGlToScreenY(double y){
		return this.getRenderer().sizeGlToScreenY(this, y);
	}
	
}
