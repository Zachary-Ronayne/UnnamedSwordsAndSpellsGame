package zgame.graphics;

import zgame.GameWindow;

import static org.lwjgl.opengl.GL30.*;

/**
 * A class that handles OpenGL operations related to drawing objects.
 * Create an instance of this class and call draw methods to draw to this Renderer,
 * then call drawToScreen to display the contents of this Renderer.
 * This class is dependent on {@link zgame.graphics.DisplayList}, be sure to initialize that class before using Renderer.
 * DO NOT directly call any OpenGL methods when using this class, otherwise unexpected results could happen
 */
public class Renderer{
	
	// TODO implement camera
	
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
	/** The Camera which determines the relative location and scale of objects drawn with this Renderer */
	private Camera camera;
	
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
	 * @param stretchToFill true to stretch the image to the size of the window so that it fills the entire window,
	 *        false to center the image, and make it take up as much of the screen as possible while maintaining the original aspect ratio,
	 *        and leave black bars on the top and bottom or sides, depending on where excess space would be left
	 */
	public void drawToScreen(GameWindow window, boolean stretchToFill){
		// sw and sh for screen width and height
		int sw = window.getWidth();
		int sh = window.getHeight();
		
		this.renderModeBuffer();
		
		glPushMatrix();
		if(stretchToFill){
			glViewport(0, 0, sw, sh);
		}
		else{
			// wRatio for the window aspect ratio and tRatio for this render's aspect ratio
			double wRatio = window.getRatio();
			double tRatio = this.screen.getRatioWH();
			int w;
			int h;
			if(tRatio < wRatio){
				h = sh;
				w = (int)Math.round(h * tRatio);
			}
			else{
				w = sw;
				h = (int)Math.round(w * this.screen.getRatioHW());
			}
			glViewport((sw - w) >> 1, (sh - h) >> 1, w, h);
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, this.screen.getTextureID());
		DisplayList.texRect();
		glPopMatrix();
	}
	
	/**
	 * Call OpenGL operations that transforms to draw to a location in game coordinates.
	 * This method assumes the coordinates to translate are centered in the given rectangular bounding box in game coordinates
	 * This method does not push or pop the matrix stack
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
	
	/*
	 * TODO create a way of differentiating between operations
	 * like drawRectangle which should depend on camera position,
	 * and operations like fill which should not depend on the camera
	 */
	/** Fill the screen with the current color */
	public void fill(){
		this.renderModeShapes();
		DisplayList.rect();
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
	
}
