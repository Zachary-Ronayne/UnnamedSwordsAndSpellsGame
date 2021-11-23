package zgame.graphics;

import static org.lwjgl.opengl.GL30.*;

import zgame.GameWindow;

/**
 * A class that handles OpenGL operations related to drawing objects.
 * Create an instance of this class and call draw methods to draw to this Renderer,
 * then call drawToScreen to display the contents of this Renderer.
 * This class is dependent on {@link zgame.graphics.DisplayList}, be sure to initialize that class before using Renderer.
 * DO NOT directly call any OpenGL methods when using this class, otherwise unexpected results could happen.
 * Coordinate explanation:
 * Window coordinates: The pixels on the GLFW window itself
 * Screen coordinates: The in game coordinates, relative to what is displayed on the screen.
 * i.e. the upper left hand corner is always (0, 0),
 * and the lower right hand corner is always (Renderer.screen.width, Renderer.screen.height)
 * Game coordinates: The actual position of something in the game, regardless of where it would be rendered
 */
public class Renderer{
	
	/** The shader used to draw basic shapes, i.e. solid colors */
	private ShaderProgram shapeShader;
	/** The shader used to draw textures, i.e. images */
	private ShaderProgram textureShader;
	/** The shader used to draw the frame buffer to the screen, as a texture */
	private ShaderProgram framebufferShader;
	/** The shader which is currently used */
	private ShaderProgram loadedShader;
	
	/** The buffer which this Renderer draws to, which later can be drawn to a window */
	private GameBuffer screen;
	
	// TODO implement with camera
	/** true if objects which would be rendered outside of the bounds of {@link #screen} should not be drawn, false otherwise */
	private boolean renderOnlyInside;
	
	/**
	 * Create a new empty renderer
	 * 
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public Renderer(int width, int height){
		this.setRenderOnlyInside(true);
		this.resize(width, height);
		
		// Load shaders
		this.shapeShader = new ShaderProgram("default");
		this.textureShader = new ShaderProgram("texture");
		this.framebufferShader = new ShaderProgram("framebuffer");
		this.renderModeImage();
	}
	
	/** Delete any resources used by this Renderer */
	public void destory(){
		this.screen.destory();
	}
	
	/**
	 * Modify the size of this Renderer. This is a costly operation and should not regularly be run
	 * 
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public void resize(int width, int height){
		if(this.screen != null) this.screen.destory();
		this.screen = new GameBuffer(width, height);
	}
	
	/**
	 * Clear all rendered contents of this renderer. Calling this method will leave this Renderer's GameBuffer's Framebuffer as the bound framebuffer
	 */
	public void clear(){
		GameBuffer s = this.screen;
		glBindFramebuffer(GL_FRAMEBUFFER, s.getFrameID());
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	/** Call this method before rendering normal shapes, i.e. solid rectangles */
	private void renderModeShapes(){
		this.setLoadedShader(this.shapeShader);
	}
	
	/** Call this method before rendering images, i.e. textures */
	private void renderModeImage(){
		this.setLoadedShader(this.textureShader);
	}
	
	/** Call this method before rendering a frame buffer in place of a texture */
	private void renderModeBuffer(){
		this.setLoadedShader(this.framebufferShader);
	}
	
	/**
	 * Set the currently used shader
	 * 
	 * @param shader The shader to use.
	 */
	private void setLoadedShader(ShaderProgram shader){
		if(this.loadedShader != shader){
			shader.use();
			this.loadedShader = shader;
		}
	}
	
	/**
	 * Make all rendering operations draw to this Renderer
	 */
	public void drawToRenderer(){
		this.screen.drawToBuffer();
	}
	
	/**
	 * Draw the contents of {@link #screen} to the given GameWindow.
	 * This method will leave the Renderer in the state for drawing buffers, i.e. {@link #renderModeBuffer()} is called.
	 * Additionally, this method will make all further drawing operations occur directly on the given GameWindow
	 * 
	 * @param window The window to draw to
	 */
	public void drawToWindow(GameWindow window){
		this.renderModeBuffer();
		glPushMatrix();
		glViewport(window.viewportX(), window.viewportY(), window.viewportW(), window.viewportH());
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, this.screen.getTextureID());
		DisplayList.texRect();
		glPopMatrix();
	}
	
	/**
	 * Call OpenGL operations that transform to draw to a location in game coordinates.
	 * This method assumes the coordinates to translate are centered in the given rectangular bounding box in game coordinates
	 * This method does not push or pop the matrix stack
	 * Coordinates are in camera coordinates
	 * 
	 * @param x The x coordinate of the upper lefthand corner
	 * @param y The y coordinate of the upper lefthand corner
	 * @param w The width
	 * @param h The height
	 */
	private void positionObject(double x, double y, double w, double h){
		GameBuffer b = this.screen;
		
		double rw = b.getInverseWidth();
		double rh = b.getInverseHeight();
		double hw = b.getInverseHalfWidth();
		double hh = b.getInverseHalfHeight();
		
		glTranslated(-1 + (x + w * .5) * hw, 1 - (y + h * .5) * hh, 0);
		glScaled(w * rw, h * rh, 1);
	}
	
	/**
	 * Draw a rectangle, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinates are in camera coordinates
	 * 
	 * @param x The x coordinate of the upper right hand corner of the rectangle
	 * @param y The y coordinate of the upper right hand corner of the rectangle
	 * @param w The width of the rectangle
	 * @param h The height of the rectangle
	 */
	public void drawRectangle(double x, double y, double w, double h){
		this.renderModeShapes();
		
		glPushMatrix();
		this.positionObject(x, y, w, h);
		DisplayList.rect();
		glPopMatrix();
	}
	
	/**
	 * Draw a rectangular image at the specified location. All values are in game coordinates.
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will strech to fit the given dimensions
	 * 
	 * @param x The x coordinate of the upper right hand corner of the image
	 * @param y The y coordinate of the upper right hand corner of the image
	 * @param w The width of the image
	 * @param h The height of the image
	 */
	public void drawImage(double x, double y, double w, double h, GameImage img){
		this.renderModeImage();
		
		glPushMatrix();
		this.positionObject(x, y, w, h);
		img.use();
		DisplayList.texRect();
		glPopMatrix();
	}
	
	/** Fill the screen with the current color, regardless of camera position */
	public void fill(){
		glPushMatrix();
		glLoadIdentity();
		this.renderModeShapes();
		DisplayList.rect();
		glPopMatrix();
	}
	
	/**
	 * Set the color, fully opaque, currently used to draw basic shapes
	 * 
	 * @param r The red amount, should be in the range [0-1]
	 * @param g The green amount, should be in the range [0-1]
	 * @param b The blue amount, should be in the range [0-1]
	 */
	public void setColor(double r, double g, double b){
		this.setColor(r, g, b, 1);
	}
	
	/**
	 * Set the color currently used to draw basic shapes
	 * s
	 * 
	 * @param r The red amount, should be in the range [0-1]
	 * @param g The green amount, should be in the range [0-1]
	 * @param b The blue amount, should be in the range [0-1]
	 * @param a The alpha amount, should be in the range [0-1]
	 */
	public void setColor(double r, double g, double b, double a){
		glColor4d(r, g, b, a);
	}
	
	/** @return See {@link #renderOnlyInside} */
	public boolean isRenderOnlyInside(){
		return renderOnlyInside;
	}
	
	/** @param renderOnlyInside See {@link #renderOnlyInside} */
	public void setRenderOnlyInside(boolean renderOnlyInside){
		this.renderOnlyInside = renderOnlyInside;
	}
	
	/** @return The width, in pixels, of the underlyng buffer of this Renderer */
	public int getWidth(){
		return this.screen.getWidth();
	}
	
	/** @return The height, in pixels, of the underlyng buffer of this Renderer */
	public int getHeight(){
		return this.screen.getHeight();
	}
	
	/** @return The ratio of the size of the internal buffer, i.e. the width divided by the height */
	public double getRatioWH(){
		return this.screen.getRatioWH();
	}
	
	/** @return The ratio of the size of the internal buffer, i.e. the height divided by the width */
	public double getRatioHW(){
		return this.screen.getRatioHW();
	}
	
	/**
	 * Convert an x coordinate value in window space, to a coordinate in screen space coordinates
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting coordinates
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenX(GameWindow window, double x){
		return windowToScreen(x, window.viewportX(), window.viewportWInverse(), this.screen.getWidth());
	}
	
	/**
	 * Convert a y coordinate value in window space, to a coordinate in screen space coordinates
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting coordinates
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenY(GameWindow window, double y){
		return windowToScreen(y, window.viewportY(), window.viewportHInverse(), this.screen.getHeight());
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
	 * @param GameWindow the {@link GameWindow} to use for reference for converting coordinates
	 * @param x The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowX(GameWindow window, double x){
		return screenToWindow(x, window.viewportX(), window.viewportW(), this.screen.getInverseWidth());
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in window space coordinates
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting coordinates
	 * @param y The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowY(GameWindow window, double y){
		return screenToWindow(y, window.viewportY(), window.viewportH(), this.screen.getInverseHeight());
	}
	
	/**
	 * Convert a coordinate value in screen space, to a coordinate in window space coordinates
	 * 
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowSize The size of the window
	 * @param screenSize The size of the screen to convert from
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
	public double screenToGlX(GameWindow window, double x){
		return screenToGl(x, window.viewportX(), window.getWidth(), this.screen.getInverseWidth(), window.getInverseWidth());
	};
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in OpenGL coordinates
	 * 
	 * @param y The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlY(GameWindow window, double y){
		return screenToGl(y, window.viewportY(), window.getHeight(), this.screen.getInverseHeight(), window.getInverseHeight());
	};
	
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
	public double glToScreenX(GameWindow window, double x){
		return glToScreen(x, window.viewportX(), window.getInverseWidth(), this.screen.getWidth(), window.getWidth());
	};
	
	/**
	 * Convert a y coordinate value in OpenGL space, to a coordinate in screen coordinates
	 * 
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenY(GameWindow window, double y){
		return glToScreen(y, window.viewportY(), window.getInverseHeight(), this.screen.getHeight(), window.getHeight());
	};
	
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
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param x The value to convert
	 * @return The conveted size
	 */
	public double sizeWindowToScreenX(GameWindow window, double x){
		return sizeWindowToScreen(x, window.viewportWInverse(), this.screen.getWidth());
	}
	
	/**
	 * Convert a size on the y axis in window space, to a size in screen space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The conveted size
	 */
	public double sizeWindowToScreenY(GameWindow window, double y){
		return sizeWindowToScreen(y, window.viewportHInverse(), this.screen.getHeight());
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
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param x The value to convert
	 * @return The conveted size
	 */
	public double sizeScreenToWindowX(GameWindow window, double x){
		return sizeScreenToWindow(x, window.viewportW(), this.screen.getInverseWidth());
	}
	
	/**
	 * Convert a size on the y axis in screen space, to a size in window space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The conveted size
	 */
	public double sizeScreenToWindowY(GameWindow window, double y){
		return sizeScreenToWindow(y, window.viewportH(), this.screen.getInverseHeight());
	}
	
	/**
	 * Convert a size in screen space, to a size in window space
	 * 
	 * @param p The value to convert
	 * @param windowSize The size of the window
	 * @param screenSize The size of the screen to convert from
	 * @return The converted size
	 */
	public static double sizeScreenToWindow(double p, double windowSize, double screenInverseSize){
		return p * screenInverseSize * windowSize;
	}
	
	/**
	 * Convert a size on the x axis in screen space, to a size in OpenGL space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param x The value to convert
	 * @return The conveted size
	 */
	public double sizeScreenToGlX(GameWindow window, double x){
		return sizeScreenToGl(x, window.getWidth(), this.screen.getInverseWidth(), window.getInverseWidth());
	};
	
	/**
	 * Convert a size on the y axis in screen space, to a size in OpenGL space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The conveted size
	 */
	public double sizeScreenToGlY(GameWindow window, double y){
		return sizeScreenToGl(y, window.getHeight(), this.screen.getInverseHeight(), window.getInverseHeight());
	};
	
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
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param x The value to convert
	 * @return The conveted size
	 */
	public double sizeGlToScreenX(GameWindow window, double x){
		return sizeGlToScreen(x, window.getInverseWidth(), this.screen.getWidth(), window.getWidth());
	};
	
	/**
	 * Convert a size on the y axis in OpenGL space, to a size in screen space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The conveted size
	 */
	public double sizeGlToScreenY(GameWindow window, double y){
		return sizeGlToScreen(y, window.getInverseHeight(), this.screen.getHeight(), window.getHeight());
	};
	
	/**
	 * Convert a size in OpenGL space, to a size in screen space
	 * 
	 * @param p The value to convert
	 * @param windowInverseSize The inverse of the size of the window
	 * @param screenSize The size of the screen to convert to
	 * @param windowSize The size of the window to convert to
	 * @return The conveted size
	 */
	public static double sizeGlToScreen(double p, double windowInverseSize, double screenSize, double windowSize){
		return sizeWindowToScreen(p * 0.5 * windowSize, windowInverseSize, screenSize);
	}
	
}
