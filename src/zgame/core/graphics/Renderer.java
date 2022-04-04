package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;

import static org.lwjgl.stb.STBTruetype.*;

import zgame.core.graphics.buffer.IndexBuffer;
import zgame.core.graphics.buffer.VertexArray;
import zgame.core.graphics.buffer.VertexBuffer;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.graphics.font.GameFont;
import zgame.core.graphics.image.GameImage;
import zgame.core.graphics.shader.ShaderProgram;
import zgame.core.window.GameWindow;

import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;
import java.util.Stack;

/**
 * A class that handles OpenGL operations related to drawing objects.
 * Create an instance of this class and call draw methods to draw to this Renderer,
 * then call drawToWindow to display the contents of this Renderer.
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
	
	/** The stack used to keep track of transformations. The last element is always the current model view matrix */
	private Stack<Matrix4f> modelViewStack;
	/** The buffer used to track {@link #modelView} */
	private FloatBuffer modelViewBuff;
	
	/**
	 * true if objects which would be rendered outside of the bounds of {@link #screen} should not be drawn, false otherwise.
	 * If this value is false, then all objects will be rendered, even if they should not be visible, which could cause performance issues
	 */
	private boolean renderOnlyInside;
	
	/** The {@link VertexArray} for drawing plain rectangles */
	private VertexArray rectVertArr;
	/** A {@link VertexBuffer} which represents positional values that fill the entire OpenGL screen from (-1, -1) to (1, 1) */
	private VertexBuffer fillScreenPosBuff;
	/** The index buffer that tracks the indexes for drawing a rectangle */
	private IndexBuffer rectIndexBuff;
	
	/** A {@link VertexArray} for drawing text */
	private VertexArray textVertArr;
	/** A {@link VertexBuffer} which represents positional values for a texture whose positional values will regularly change */
	private VertexBuffer posBuff;
	/** A {@link VertexBuffer} which represents texture coordinates for a texture whose texture coordinates will regularly change */
	private VertexBuffer changeTexCoordBuff;
	
	/** The {@link VertexArray} for drawing images */
	private VertexArray imgVertArr;
	/** The {@link VertexBuffer} used to track the texture coordinates for drawing the entirety of a texture, i.e. from (0, 0) to (1, 1) */
	private VertexBuffer texCoordBuff;
	
	/** The current color used by this {@link Renderer} */
	private ZColor color;
	
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
		
		// Model view initialization
		// The matrix is 4x4, so 16 floats
		this.modelViewBuff = BufferUtils.createFloatBuffer(16);
		this.modelViewStack = new Stack<Matrix4f>();

		// Font values
		this.font = null;
		this.fontSize = 32;
		
		// Text buffers
		this.xTextBuff = BufferUtils.createFloatBuffer(1);
		this.yTextBuff = BufferUtils.createFloatBuffer(1);
		this.textQuad = STBTTAlignedQuad.create();
		
		// Load shaders
		this.shapeShader = new ShaderProgram("default");
		this.textureShader = new ShaderProgram("texture");
		this.fontShader = new ShaderProgram("font");
		this.framebufferShader = new ShaderProgram("framebuffer");
		this.renderModeImage();
		
		// Vertex arrays and vertex buffers
		this.initVertexes();

		// Set the default color
		this.setColor(new ZColor(0));
		
		// Init the model view matrix
		this.identityMatrix();
		this.updateMatrix();
	}
	
	/** Initialize all resources used by the vertex arrays and vertex buffers */
	public void initVertexes(){
		// Generate an index buffer for drawing rectangles
		this.rectIndexBuff = new IndexBuffer(new byte[]{
			/////////
			0, 1, 2,
			/////////
			0, 3, 2});

		// Generate a vertex array for drawing solid colored rectangles
		this.rectVertArr = new VertexArray();
		// Generate a vertex buffer for drawing rectangles that fill the entire screen and can be scaled
		this.fillScreenPosBuff = new VertexBuffer(0, 2, GL_STATIC_DRAW, new float[]{
			// Low Left Corner
			-1, -1,
			// Low Right Corner
			1, -1,
			// Up Right Corner
			1, 1,
			// Up Left Corner
			-1, 1});
		
		// Generate a vertex array for rendering images
		this.imgVertArr = new VertexArray();
		// Generate a vertex buffer for texture coordinates for rendering images
		this.texCoordBuff = new VertexBuffer(2, 2, GL_STATIC_DRAW, new float[]{
			// Low Left Corner
			0, 0,
			// Low Right Corner
			1, 0,
			// Up Right Corner
			1, 1,
			// Up Left Corner
			0, 1});
		// Add the positional data to the image rendering
		this.fillScreenPosBuff.bind();
		this.fillScreenPosBuff.applyToVertexArray();
		
		// Generate a vertex array for rendering text
		this.textVertArr = new VertexArray();
		// Generate a vertex buffer for positional coordinates that regularly change
		this.posBuff = new VertexBuffer(0, 2, GL_DYNAMIC_DRAW, 4);
		// Generate a vertex buffer for texture coordinates that regularly change
		this.changeTexCoordBuff = new VertexBuffer(2, 2, GL_DYNAMIC_DRAW, 4);
	}
	
	/** Free all resources used by the vertex arrays and vertex buffers */
	public void destroyVertexes(){
		this.fillScreenPosBuff.destroy();
		this.texCoordBuff.destroy();
		this.posBuff.destroy();
		this.changeTexCoordBuff.destroy();
		
		this.rectVertArr.delete();
		this.imgVertArr.delete();
		this.textVertArr.delete();
	}
	
	/** Delete any resources used by this Renderer */
	public void destroy(){
		this.screen.destroy();
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		this.destroyVertexes();
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
	
	/** @return The {@link Matrix4f} of the model view, i.e. the current transformation status of the renderer */
	public Matrix4f modelView(){
		return this.modelViewStack.lastElement();
	}
	
	/** Update the data of the model view matrix into OpenGL */
	public void updateMatrix(){
		this.modelView().get(this.modelViewBuff);
		int loc = glGetUniformLocation(this.loadedShader.getId(), "modelView");
		glUniformMatrix4fv(loc, false, this.modelViewBuff);
	}
	
	/**
	 * Set the transformation matrix used for rendering
	 * 
	 * @param matrix The matrix to use
	 */
	public void setMatrix(Matrix4f matrix){
		if(!modelViewStack.empty()) modelViewStack.pop();
		modelViewStack.push(matrix);
		this.updateMatrix();
	}
	
	/** Set the modelView matrix to the identity matrix */
	public void identityMatrix(){
		this.setMatrix(new Matrix4f());
	}
	
	/** Push the current state of the transformation matrix onto the matrix stack, i.e. save the current state of the transformations */
	public void pushMatrix(){
		this.modelViewStack.push(new Matrix4f(this.modelView()));
	}
	
	/**
	 * Pop the current state of the transformation matrix, i.e. load the previous state of the transformations and discard the current state.
	 * This method does nothing if the stack is empty
	 * 
	 * @return true if the stack was popped, false if no element could be popped, i.e. the stack was empty
	 */
	public boolean popMatrix(){
		if(this.modelViewStack.size() == 1) this.identityMatrix();
		this.modelViewStack.pop();
		return true;
	}
	
	/**
	 * Translate the transformation matrix by the given amount. The coordinates are based on OpenGL positions
	 * 
	 * @param x The amount on the x axis
	 * @param y The amount on the y axis
	 */
	public void translate(double x, double y){
		this.modelView().translate((float)x, (float)y, 0);
		this.updateMatrix();
	}
	
	/**
	 * Scale the transformation matrix by the given amount
	 * 
	 * @param x The amount on the x axis
	 * @param y The amount on the y axis
	 */
	public void scale(double x, double y){
		this.modelView().scale((float)x, (float)y, 1);
		this.updateMatrix();
	}
	
	/** Call this method before rendering normal shapes, i.e. solid rectangles */
	public void renderModeShapes(){
		this.setLoadedShader(this.shapeShader);
	}
	
	/** Call this method before rendering images, i.e. textures */
	public void renderModeImage(){
		this.setLoadedShader(this.textureShader);
	}
	
	/** Call this method before rendering font, i.e text */
	public void renderModeFont(){
		this.setLoadedShader(this.fontShader);
	}
	
	/** Call this method before rendering a frame buffer in place of a texture */
	public void renderModeBuffer(){
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
	 * Make all rendering operations draw to this Renderer and set up this {@link Renderer} to be ready for drawing
	 */
	public void initToDraw(){
		// Bind the screen as the frame buffer
		this.screen.drawToBuffer();
		// Load the identity matrix before setting a default shader
		this.identityMatrix();
		// Bind a default shader
		this.loadedShader = null;
		this.setLoadedShader(this.shapeShader);
	}
	
	/**
	 * Draw the contents of {@link #screen} to the given {@link GameWindow}.
	 * This method will leave this {@link Renderer} in the state for drawing buffers, i.e. {@link #renderModeBuffer()} is called.
	 * Additionally, this method will make all further drawing operations occur directly on the given {@link GameWindow}
	 * 
	 * @param window The window to draw to
	 */
	public void drawToWindow(GameWindow window){
		// Set the current shader for drawing a frame buffer
		this.renderModeBuffer();
		// Bind the vertex array for drawing an image that fills the entire OpenGL space
		this.imgVertArr.bind();
		
		// Position the image and the frame buffer to draw to the window
		glViewport(window.viewportX(), window.viewportY(), window.viewportW(), window.viewportH());
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		// Use the frame buffer texture
		glBindTexture(GL_TEXTURE_2D, this.screen.getTextureID());
		
		// Draw the image
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
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
		
		this.translate(-1 + (x + w * .5) * hw, 1 - (y + h * .5) * hh);
		this.scale(w * rw, h * rh);
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
		
		// Use the shape shader and the rectangle vertex array
		this.renderModeShapes();
		this.rectVertArr.bind();
		// Update the current color for this draw operation
		this.updateColor();
		
		this.pushMatrix();
		this.positionObject(x, y, w, h);
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		glBindVertexArray(0);
		this.popMatrix();
		
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
		this.imgVertArr.bind();
		glBindTexture(GL_TEXTURE_2D, img);
		
		this.pushMatrix();
		this.positionObject(x, y, w, h);
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		glBindVertexArray(0);
		
		this.popMatrix();
		
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
		
		// Use the font shaders
		this.renderModeFont();
		// Use the font vertex array
		this.textVertArr.bind();
		// Update the current color for this draw operation
		this.updateColor();
		
		// TODO allow for new line characters to give line breaks
		// TODO make line break sizes and character spacing parameters
		
		// Use the font's bitmap
		glBindTexture(GL_TEXTURE_2D, f.getBitmapID());
		
		// Set up for text position and size
		this.xTextBuff.put(0, 0.0f);
		this.yTextBuff.put(0, 0.0f);
		
		// TODO why is this like this?
		// Double the total font size because fonts are weird, this times 2 is hacky
		double posSize = this.getFontSize() * f.getResolutionInverse() * 2;
		
		// Position and scale the text
		this.pushMatrix();
		this.scale(1, -1);
		this.positionObject(x, -y + this.getHeight(), posSize, posSize);
		
		// Draw every character of the text
		for(int i = 0; i < text.length(); i++){
			char c = text.charAt(i);
			int charIndex = c - f.getFirstChar();
			// Ensure the character exists in the font, if it doesn't, render the zeroth character
			if(c < 0 || c >= f.getLoadChars()) charIndex = 0;
			
			// Find the vertices and texture coordinates of the character to draw
			this.textQuad = STBTTAlignedQuad.create();
			stbtt_GetBakedQuad(f.getCharData(), f.getWidth(), f.getHeight(), charIndex, this.xTextBuff, this.yTextBuff, this.textQuad, true);
			
			// Buffer the new data
			this.posBuff.updateData(new float[]{
				//////////////////////////////////////
				this.textQuad.x0(), this.textQuad.y0(),
				//////////////////////////////////////
				this.textQuad.x1(), this.textQuad.y0(),
				//////////////////////////////////////
				this.textQuad.x1(), this.textQuad.y1(),
				//////////////////////////////////////
				this.textQuad.x0(), this.textQuad.y1()});
			this.changeTexCoordBuff.updateData(new float[]{
				//////////////////////////////////////
				this.textQuad.s0(), this.textQuad.t0(),
				//////////////////////////////////////
				this.textQuad.s1(), this.textQuad.t0(),
				//////////////////////////////////////
				this.textQuad.s1(), this.textQuad.t1(),
				//////////////////////////////////////
				this.textQuad.s0(), this.textQuad.t1()});
			
			// Draw the square
			glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		}
		this.popMatrix();
		
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
		this.renderModeShapes();
		this.rectVertArr.bind();
		
		this.pushMatrix();
		this.identityMatrix();
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		glBindVertexArray(0);
		this.popMatrix();
	}
	
	/** @return See {@link #color} */
	public ZColor getColor(){
		return this.color;
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
	 * @param a The alpha amount (transparency), should be in the range [0-1]
	 */
	public void setColor(double r, double g, double b, double a){
		this.setColor(new ZColor(r, g, b, a));
	}
	
	/**
	 * Set the color currently used to draw basic shapes
	 * 
	 * @param color the new color
	 */
	public void setColor(ZColor color){
		this.color = color;
		this.updateColor();
	}
	
	/** Update the uniform variable used to track the color, with the current value */
	public void updateColor(){
		float[] c = this.getColor().toFloat();
		int loc = glGetUniformLocation(this.loadedShader.getId(), "mainColor");
		if(loc != -1) glUniform4fv(loc, c);
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
