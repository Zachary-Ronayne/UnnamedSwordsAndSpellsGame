package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import static org.lwjgl.stb.STBTruetype.*;

import zgame.core.graphics.camera.GameCamera;
import zgame.core.graphics.font.GameFont;
import zgame.core.window.GameWindow;

import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;

/**
 * A class that handles OpenGL operations related to drawing objects.
 * Create an instance of this class and call draw methods to draw to this Renderer,
 * then call drawToScreen to display the contents of this Renderer.
 * This class is dependent on {@link DisplayList}, be sure to initialize that class before using Renderer.
 * DO NOT directly call any OpenGL methods when using this class, otherwise unexpected results could happen.
 * Coordinate explanation:
 * OpenGL space: the coordinate system used by OpenGL, i.e. the upper left hand corner is (-1, 1) and the lower right hand corner is (1, -1)
 * Window coordinates: The pixels on the GLFW window itself
 * Screen coordinates: The in game coordinates, relative to what is displayed on the screen.
 * i.e. the upper left hand corner is always (0, 0),
 * and the lower right hand corner is always (Renderer.screen.width, Renderer.screen.height)
 * Game coordinates: The actual position of something in the game, regardless of where it would be rendered
 */
public class Renderer{
	
	/** The buffer for the x coordinate when rendering text */
	private FloatBuffer xTextBuff;
	/** The buffer for the y coordinate when rendering text */
	private FloatBuffer yTextBuff;
	/** The STBTTAlignedQuad buffer for the quad for rendering */
	private STBTTAlignedQuad textQuad;
	
	/** The shader used to draw basic shapes, i.e. solid colors */
	private ShaderProgram shapeShader;
	/** The shader used to draw textures, i.e. images */
	private ShaderProgram textureShader;
	/** The shader used to draw font, i.e. text */
	private ShaderProgram fontShader;
	/** The shader used to draw the frame buffer to the screen, as a texture */
	private ShaderProgram framebufferShader;
	/** The shader which is currently used */
	private ShaderProgram loadedShader;
	
	/** The buffer which this Renderer draws to, which later can be drawn to a window */
	private GameBuffer screen;
	
	/** The {@link GameCamera} which determines the relative location and scale of objects drawn in this renderer. If this is null, no transformations will be applied */
	private GameCamera camera;
	
	/**
	 * true if objects which would be rendered outside of the bounds of {@link #screen} should not be drawn, false otherwise.
	 * If this value is false, then all objects will be rendered, even if they should not be visible, which could cause performance issues
	 */
	private boolean renderOnlyInside;
	
	/** The current font of this {@link Renderer}. If this value is null, no text can be drawn */
	private GameFont font;
	
	/** The current size, in pixels, to render font. This size is effected by zooming with the camera */
	private double fontSize;
	
	/**
	 * Create a new empty renderer
	 * 
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public Renderer(int width, int height){
		// General initialization
		this.camera = null;
		this.setRenderOnlyInside(true);
		this.resize(width, height);
		
		// Font values
		this.font = null;
		this.fontSize = 32;
		
		// Buffers
		this.xTextBuff = BufferUtils.createFloatBuffer(1);
		this.yTextBuff = BufferUtils.createFloatBuffer(1);
		this.textQuad = STBTTAlignedQuad.create();
		
		// Load shaders
		this.shapeShader = new ShaderProgram("default");
		this.textureShader = new ShaderProgram("texture");
		this.fontShader = new ShaderProgram("font");
		this.framebufferShader = new ShaderProgram("framebuffer");
		this.renderModeImage();
	}
	
	/** Delete any resources used by this Renderer */
	public void destroy(){
		this.screen.destroy();
	}
	
	/**
	 * Modify the size of this Renderer. This is a costly operation and should not regularly be run
	 * 
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public void resize(int width, int height){
		if(this.screen != null) this.screen.destroy();
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
	
	/** Call this method before rendering font, i.e text */
	private void renderModeFont(){
		this.setLoadedShader(this.fontShader);
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
	 * Draw the contents of {@link #screen} to the given {@link GameWindow}.
	 * This method will leave this {@link Renderer} in the state for drawing buffers, i.e. {@link #renderModeBuffer()} is called.
	 * Additionally, this method will make all further drawing operations occur directly on the given {@link GameWindow}
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
	 * 
	 * @param x The x coordinate of the upper left hand corner
	 * @param y The y coordinate of the upper left hand corner
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
	 * @param x The x coordinate of the upper left hand corner of the rectangle
	 * @param y The y coordinate of the upper left hand corner of the rectangle
	 * @param w The width of the rectangle
	 * @param h The height of the rectangle
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawRectangle(double x, double y, double w, double h){
		if(!this.shouldDraw(x, y, w, h)) return false;
		
		this.renderModeShapes();
		
		glPushMatrix();
		this.positionObject(x, y, w, h);
		DisplayList.rect();
		glPopMatrix();
		
		return true;
	}
	
	/**
	 * Draw a rectangular image at the specified location. All values are in game coordinates.
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will stretch to fit the given dimensions
	 * 
	 * @param x The x coordinate of the upper right hand corner of the image
	 * @param y The y coordinate of the upper right hand corner of the image
	 * @param w The width of the image
	 * @param h The height of the image
	 * @param img The {@link GameImage} to draw
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawImage(double x, double y, double w, double h, GameImage img){
		return this.drawImage(x, y, w, h, img.getId());
	}
	
	/**
	 * Draw a rectangular image at the specified location. All values are in game coordinates.
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will stretch to fit the given dimensions
	 * 
	 * @param x The x coordinate of the upper right hand corner of the image
	 * @param y The y coordinate of the upper right hand corner of the image
	 * @param w The width of the image
	 * @param h The height of the image
	 * @param img The OpenGL id of the image to draw
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawImage(double x, double y, double w, double h, int img){
		if(!this.shouldDraw(x, y, w, h)) return false;
		
		this.renderModeImage();
		
		glPushMatrix();
		this.positionObject(x, y, w, h);
		glBindTexture(GL_TEXTURE_2D, img);
		DisplayList.texRect();
		glPopMatrix();
		
		return true;
	}
	
	/**
	 * Draw the given text to the given position
	 * The text will be positioned such that it is written on a line, and the given position is the leftmost part of that line.
	 * i.e. the text starts at the given coordinates and is draw left to right
	 * 
	 * @param x The x position of the text
	 * @param y The y position of the text
	 * @param text The text to draw
	 * @return true if the text was drawn, false otherwise
	 */
	public boolean drawText(double x, double y, String text){
		// TODO do a bounds check for drawing the text
		
		// Make sure a font exists, then use the font shader
		GameFont f = this.font;
		if(f == null) return false;
		this.renderModeFont();
		
		// TODO allow for new line characters to give line breaks
		// TODO make line break sizes and character spacing parameters
		
		// TODO make a new shader for text that sends the current color to the shader
		
		// Use the font's bitmap
		glBindTexture(GL_TEXTURE_2D, f.getBitmapID());
		
		// Set up for text position and size
		this.xTextBuff.put(0, 0.0f);
		this.yTextBuff.put(0, 0.0f);
		
		// Double the total font size because fonts are weird, this times 2 is hacky
		double posSize = this.getFontSize() * f.getResolutionInverse() * 2;
		
		// Position and scale the text
		glPushMatrix();
		glScaled(1, -1, 1);
		this.positionObject(x, -y + this.getHeight(), posSize, posSize);
		
		// Draw every character of the text
		glBegin(GL_QUADS);
		for(int i = 0; i < text.length(); i++){
			char c = text.charAt(i);
			int charIndex = c - f.getFirstChar();
			// Ensure the character exists in the font, if it doesn't, render the zeroth character
			if(c < 0 || c >= f.getLoadChars()) charIndex = 0;
			
			// Find the vertices and texture coordinates of the character to draw
			this.textQuad = STBTTAlignedQuad.create();
			stbtt_GetBakedQuad(f.getCharData(), f.getWidth(), f.getHeight(), charIndex, this.xTextBuff, this.yTextBuff, this.textQuad, true);
			glTexCoord2f(this.textQuad.s0(), this.textQuad.t0());
			glVertex2f(this.textQuad.x0(), this.textQuad.y0());
			glTexCoord2f(this.textQuad.s1(), this.textQuad.t0());
			glVertex2f(this.textQuad.x1(), this.textQuad.y0());
			glTexCoord2f(this.textQuad.s1(), this.textQuad.t1());
			glVertex2f(this.textQuad.x1(), this.textQuad.y1());
			glTexCoord2f(this.textQuad.s0(), this.textQuad.t1());
			glVertex2f(this.textQuad.x0(), this.textQuad.y1());
		}
		glEnd();
		glPopMatrix();
		return true;
	}
	
	/**
	 * Determine if the given bounds are contained within the current state of this {@link Renderer}
	 * i.e. find out if something drawn within the given bounds would appear on the screen
	 * 
	 * @param x The upper left hand corner x coordinate of the object, in game coordinates
	 * @param y The upper left hand corner y coordinate of the object, in game coordinates
	 * @param w The width of the object, in game coordinates
	 * @param h The height of the object, in game coordinates
	 * @return true if the bounds should be drawn, false otherwise
	 */
	public boolean shouldDraw(double x, double y, double w, double h){
		if(!this.isRenderOnlyInside()) return true;
		Rectangle2D.Double r = this.getBounds();
		if(this.camera == null) return r.intersects(x, y, w, h);
		else return r.intersects(this.camera.boundsGameToScreen(x, y, w, h));
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
	 * 
	 * @param c the new color
	 */
	public void setColor(ZColor c){
		this.setColor(c.red(), c.green(), c.blue(), c.alpha());
	}
	
	/**
	 * Set the color currently used to draw basic shapes
	 * 
	 * @param r The red amount, should be in the range [0-1]
	 * @param g The green amount, should be in the range [0-1]
	 * @param b The blue amount, should be in the range [0-1]
	 * @param a The alpha amount (transparency), should be in the range [0-1]
	 */
	public void setColor(double r, double g, double b, double a){
		glColor4d(r, g, b, a);
	}
	
	/** @return See {@link #font} */
	public GameFont getFont(){
		return this.font;
	}
	
	/** @param font See {@link #font} */
	public void setFont(GameFont font){
		this.font = font;
	}
	
	/** @return See {@link #fontSize} */
	public double getFontSize(){
		return this.fontSize;
	}
	
	/** @param font See {@link #fontSize} */
	public void setFontSize(double fontSize){
		this.fontSize = fontSize;
	}
	
	/** @param camera See {@link #camera}. Can also use null to not use a camera for rendering */
	public void setCamera(GameCamera camera){
		this.camera = camera;
	}
	
	/** @return See {@link #renderOnlyInside} */
	public boolean isRenderOnlyInside(){
		return this.renderOnlyInside;
	}
	
	/** @param renderOnlyInside See {@link #renderOnlyInside} */
	public void setRenderOnlyInside(boolean renderOnlyInside){
		this.renderOnlyInside = renderOnlyInside;
	}
	
	/** @return The width, in pixels, of the underlying buffer of this Renderer */
	public int getWidth(){
		return this.screen.getWidth();
	}
	
	/** @return The height, in pixels, of the underlying buffer of this Renderer */
	public int getHeight(){
		return this.screen.getHeight();
	}
	
	/** @return A rectangle of the bounds of this {@link Renderer}, i.e. the position will be (0, 0), width will be {@link #getWidth()} and height will be {@link #getHeight()} */
	public Rectangle2D.Double getBounds(){
		return new Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight());
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
	 * @return The converted size
	 */
	public double sizeWindowToScreenX(GameWindow window, double x){
		return sizeWindowToScreen(x, window.viewportWInverse(), this.screen.getWidth());
	}
	
	/**
	 * Convert a size on the y axis in window space, to a size in screen space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
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
	 * @return The converted size
	 */
	public double sizeScreenToWindowX(GameWindow window, double x){
		return sizeScreenToWindow(x, window.viewportW(), this.screen.getInverseWidth());
	}
	
	/**
	 * Convert a size on the y axis in screen space, to a size in window space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
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
	 * @return The converted size
	 */
	public double sizeScreenToGlX(GameWindow window, double x){
		return sizeScreenToGl(x, window.getWidth(), this.screen.getInverseWidth(), window.getInverseWidth());
	};
	
	/**
	 * Convert a size on the y axis in screen space, to a size in OpenGL space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
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
	 * @return The converted size
	 */
	public double sizeGlToScreenX(GameWindow window, double x){
		return sizeGlToScreen(x, window.getInverseWidth(), this.screen.getWidth(), window.getWidth());
	};
	
	/**
	 * Convert a size on the y axis in OpenGL space, to a size in screen space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
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
	 * @return The converted size
	 */
	public static double sizeGlToScreen(double p, double windowInverseSize, double screenSize, double windowSize){
		return sizeWindowToScreen(p * 0.5 * windowSize, windowInverseSize, screenSize);
	}
	
}
