package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;

import zgame.core.graphics.buffer.GameBuffer;
import zgame.core.graphics.buffer.IndexBuffer;
import zgame.core.graphics.buffer.VertexArray;
import zgame.core.graphics.buffer.VertexBuffer;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.graphics.font.FontAsset;
import zgame.core.graphics.font.GameFont;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.graphics.image.GameImage;
import zgame.core.graphics.shader.ShaderProgram;
import zgame.core.utils.LimitedStack;
import zgame.core.utils.ZRect;
import zgame.core.window.GameWindow;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * A class that handles OpenGL operations related to drawing objects.
 * Create an instance of this class and call draw methods to draw to this Renderer,
 * then call drawToWindow to display the contents of this Renderer.
 * DO NOT directly call any OpenGL methods when using this class, otherwise unexpected results could happen.
 * Coordinate explanation:
 * OpenGL space: the coordinate system used by OpenGL, i.e. the upper left hand corner is (-1, 1) and the lower right hand corner is (1, -1)
 * Window coordinates: The pixels on the GLFW window itself
 * Screen coordinates: The in game coordinates, relative to what is displayed on the screen.
 * i.e. the upper left hand corner is always (0, 0),
 * and the lower right hand corner is always (Renderer.screen.width, Renderer.screen.height)
 * Game coordinates: The actual position of something in the game, regardless of where it would be rendered
 */
public class Renderer implements Destroyable{
	
	/** The color to use for rendering by default */
	public static final ZColor DEFAULT_COLOR = new ZColor(0);
	/** The default font to use for rendering. Null means rendering cannot happen unless a font is set */
	public static final GameFont DEFAULT_FONT = null;
	/** Default value for {@link #positioningEnabledStack} */
	public static final Boolean DEFAULT_POSITIONING_ENABLED = true;
	/** Default value for {@link #renderOnlyInsideStack} */
	public static final Boolean DEFAULT_RENDER_ONLY_INSIDE = true;
	/** Default value for {@link #limitedBoundsStack}. null means no limit */
	public static final ZRect DEFAULT_LIMITED_BOUNDS = null;
	
	/** The vertex buffer index for positional coordinates */
	public static final int VERTEX_POS_INDEX = 0;
	/** The vertex buffer index for texture coordinates */
	public static final int VERTEX_TEX_INDEX = 1;
	
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
	
	/** The list of all the stacks of this {@link Renderer} keeping track of the state of this {@link Renderer} */
	private ArrayList<LimitedStack<?>> stacks;
	
	/** The list of all the attribute related stacks of this {@link Renderer} keeping track of the state of this {@link Renderer} */
	private ArrayList<LimitedStack<?>> attributeStacks;
	
	/** The stack used to keep track of transformations. The last element is always the current model view matrix */
	private LimitedStack<Matrix4f> modelViewStack;
	/** The buffer used to track {@link #modelView} */
	private FloatBuffer modelViewBuff;
	
	/** The stack keeping track of the current color used by this {@link Renderer} */
	private LimitedStack<ZColor> colorStack;
	
	/** The stack keeping track of the current font of this {@link Renderer}. If the top of the stack is null, no text can be drawn. No font is set by default */
	private LimitedStack<GameFont> fontStack;
	
	/**
	 * The stack of buffers which this Renderer draws to, which later can be drawn to a window.
	 * All drawing operations happen to the top of the stack.
	 * Note that the stack will initially contain one buffer for drawing, based on the given size when initializing this {@link Renderer}
	 * Any buffers added to the stack must be externally managed, i.e., this class will not attempt to destroy them.
	 * If {@link #resize(int, int)} is called, it will destroy the initial buffer created by this object
	 */
	private LimitedStack<GameBuffer> bufferStack;
	
	/**
	 * The stack keeping track of the {@link GameCamera} which determines the relative location and scale of objects drawn in this renderer.
	 * If the top of the stack is null, no transformations will be applied
	 */
	private LimitedStack<GameCamera> cameraStack;
	
	/**
	 * A stack keeping track of the attribute of if positioning should be used.
	 * true if all render methods should automatically apply transformations to move from game coordinates to OpenGL coordinates, false otherwise.
	 * Essentially, if this is true, the render methods take game coordinates, if it is false, the render methods take OpenGL coordinates
	 */
	private LimitedStack<Boolean> positioningEnabledStack;
	
	/**
	 * A stack keeping track of the attribute of if things will only attempt to render if they are inside this {@link Renderer}'s bounds
	 * true if objects which would be rendered outside of the bounds of {@link #screen} should not be drawn, false otherwise.
	 * If this value is false, then all objects will be rendered, even if they should not be visible, which could cause performance issues
	 */
	private LimitedStack<Boolean> renderOnlyInsideStack;
	
	/** The stack keeping track of the current bounds which rendering is limited to, or null if no bounds is limited */
	private LimitedStack<ZRect> limitedBoundsStack;
	
	/**
	 * Create a new empty renderer
	 * 
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public Renderer(int width, int height){
		// Initialize stack list
		this.stacks = new ArrayList<LimitedStack<?>>();
		this.attributeStacks = new ArrayList<LimitedStack<?>>();
		
		// Buffer stack
		this.bufferStack = new LimitedStack<GameBuffer>(new GameBuffer(width, height, true), false);
		this.stacks.add(this.bufferStack);
		
		// Camera stack
		this.cameraStack = new LimitedStack<GameCamera>(null);
		this.stacks.add(this.cameraStack);
		
		// Model view initialization
		// The matrix is 4x4, so 16 floats
		this.modelViewBuff = BufferUtils.createFloatBuffer(16);
		// Model view stack
		this.modelViewStack = new LimitedStack<Matrix4f>(new Matrix4f());
		this.stacks.add(this.modelViewStack);
		
		// Font stack
		this.fontStack = new LimitedStack<GameFont>(DEFAULT_FONT);
		this.stacks.add(this.fontStack);
		this.attributeStacks.add(this.fontStack);
		
		// Color stack
		this.colorStack = new LimitedStack<ZColor>(DEFAULT_COLOR);
		this.stacks.add(this.colorStack);
		this.attributeStacks.add(this.colorStack);
		
		// Camera stack
		this.cameraStack = new LimitedStack<GameCamera>(null);
		this.stacks.add(cameraStack);
		
		// Positioning enabled stack
		this.positioningEnabledStack = new LimitedStack<Boolean>(DEFAULT_POSITIONING_ENABLED);
		this.stacks.add(this.positioningEnabledStack);
		this.attributeStacks.add(this.positioningEnabledStack);
		
		// Render only inside stack
		this.renderOnlyInsideStack = new LimitedStack<Boolean>(DEFAULT_RENDER_ONLY_INSIDE);
		this.stacks.add(this.renderOnlyInsideStack);
		this.attributeStacks.add(this.renderOnlyInsideStack);
		
		// rendering is unbounded by default
		this.limitedBoundsStack = new LimitedStack<ZRect>(DEFAULT_LIMITED_BOUNDS);
		this.stacks.add(this.limitedBoundsStack);
		this.updateLimitedBounds();
		
		// Text rendering buffers
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
		
		// Init the model view matrix
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
		this.rectVertArr.bind();
		// Generate a vertex buffer for drawing rectangles that fill the entire screen and can be scaled
		this.fillScreenPosBuff = new VertexBuffer(VERTEX_POS_INDEX, 2, GL_STATIC_DRAW, new float[]{
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
		this.imgVertArr.bind();
		// Generate a vertex buffer for texture coordinates for rendering images
		this.texCoordBuff = new VertexBuffer(VERTEX_TEX_INDEX, 2, GL_STATIC_DRAW, new float[]{
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
		this.textVertArr.bind();
		// Generate a vertex buffer for positional coordinates that regularly change
		this.posBuff = new VertexBuffer(VERTEX_POS_INDEX, 2, GL_DYNAMIC_DRAW, 4);
		this.posBuff.applyToVertexArray();
		// Generate a vertex buffer for texture coordinates that regularly change
		this.changeTexCoordBuff = new VertexBuffer(VERTEX_TEX_INDEX, 2, GL_DYNAMIC_DRAW, 4);
		this.changeTexCoordBuff.applyToVertexArray();
	}
	
	/** Free all resources used by the vertex arrays and vertex buffers */
	public void destroyVertexes(){
		this.fillScreenPosBuff.destroy();
		this.texCoordBuff.destroy();
		this.posBuff.destroy();
		this.changeTexCoordBuff.destroy();
		
		this.rectVertArr.destroy();
		this.imgVertArr.destroy();
		this.textVertArr.destroy();
	}
	
	/** Delete any resources used by this Renderer */
	@Override
	public void destroy(){
		this.getBuffer().destroy();
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		this.destroyVertexes();
	}
	
	/** Push the entire state of this renderer into its stacks */
	public void pushAll(){
		for(LimitedStack<?> s : this.stacks) s.push();
	}
	
	/** Pop the entire state of this renderer off its stacks */
	public void popAll(){
		GameBuffer oldBuffer = this.getBuffer();
		for(LimitedStack<?> s : this.stacks) s.pop();
		if(this.getBuffer() != oldBuffer) this.updateBuffer();
	}
	
	/**
	 * Push the values of the simple attributes of this renderer
	 * See {@link #colorStack}, {@link #fontStack}, {@link #positioningEnabledStack}, {@link #renderOnlyInsideStack}
	 */
	public void pushAttributes(){
		for(LimitedStack<?> s : this.attributeStacks) s.push();
	}
	
	/** Pop the values of the simple attributes of this renderer */
	public void popAttributes(){
		for(LimitedStack<?> s : this.attributeStacks) s.pop();
	}
	
	/**
	 * Modify the default size of this Renderer. This is a costly operation and should not regularly be run
	 * This will not modify the current top of the buffer stack, but the default buffer, unless the default buffer is at the top of the stack.
	 * This method will also destroy the buffer at the bottom of the stack
	 * 
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public void resize(int width, int height){
		this.bufferStack.getDefaultItem().regenerateBuffer(width, height);
	}
	
	/**
	 * Clear all rendered contents of this renderer. Calling this method will leave this Renderer's GameBuffer's Framebuffer as the bound framebuffer
	 */
	public void clear(){
		glBindFramebuffer(GL_FRAMEBUFFER, this.getBuffer().getFrameID());
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	/** @return The {@link Matrix4f} of the model view, i.e. the current transformation status of the renderer */
	public Matrix4f modelView(){
		return this.modelViewStack.peek();
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
		this.modelViewStack.replaceTop(matrix);
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
	
	/** @return The stack keeping track of the model view matrix */
	public LimitedStack<Matrix4f> getMatrixStack(){
		return this.modelViewStack;
	}
	
	/**
	 * Pop the current state of the transformation matrix, i.e. load the previous state of the transformations and discard the current state.
	 * This method does nothing if the stack is empty
	 * 
	 * @return true if the stack was popped, false if no element could be popped, i.e. the stack was empty
	 */
	public boolean popMatrix(){
		return this.modelViewStack.pop() != null;
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
	
	/** @return The top of {@link #positioningEnabledStack} */
	public boolean isPositioningEnabled(){
		return this.positioningEnabledStack.peek();
	}
	
	/** @param positioningEnabled Set the top of {@link #positioningEnabledStack} */
	public void setPositioningEnabled(boolean positioningEnabled){
		this.positioningEnabledStack.replaceTop(positioningEnabled);
	}
	
	/** @return See {@link #positioningEnabledStack} */
	public LimitedStack<Boolean> getPositioningEnabledStack(){
		return this.positioningEnabledStack;
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
		this.getBuffer().drawToBuffer();
		this.getBuffer().setViewport();
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
		glBindTexture(GL_TEXTURE_2D, this.getBuffer().getTextureID());
		
		// Draw the image
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
	}
	
	/** @return The top of {@link #limitedBounds} */
	public ZRect getLimitedBounds(){
		return this.limitedBoundsStack.peek();
	}
	
	/**
	 * Make this {@link Renderer} only draw things in the given bounds. Call {@link #unlimitBounds()} to turn this off.
	 * This is off by default
	 * All values are in game coordinates
	 * 
	 * @param x The upper left hand x coordinate of the bounds
	 * @param y The upper left hand y coordinate of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 */
	public void limitBounds(double x, double y, double w, double h){
		this.limitBounds(new ZRect(x, y, w, h));
	}
	
	/**
	 * Make this {@link Renderer} only draw things in the given bounds. Call {@link #unlimitBounds()} to turn this off.
	 * This is off by default
	 * Turns off the limit if bounds is null
	 * 
	 * @param bounds The bounds to limit to, in game coordinates
	 * @return true if the bounds were changed, false otherwise
	 */
	public boolean limitBounds(ZRect bounds){
		ZRect limited = this.getLimitedBounds();
		this.limitedBoundsStack.replaceTop(bounds);
		
		// If the new and old bounds are the same, don't change anything
		if(bounds == null && limited == null || bounds != null && bounds.equals(limited)) return false;
		this.updateLimitedBounds();
		
		return true;
	}
	
	/** Allow this {@link Renderer} to render anywhere on the screen, i.e. disable {@link #limitBounds(ZRect)}. */
	public void unlimitBounds(){
		this.limitBounds(null);
	}
	
	/** Update the current state of the limited bounds via calls to glScissor */
	private void updateLimitedBounds(){
		ZRect b = this.getLimitedBounds();
		if(b == null){
			glDisable(GL_SCISSOR_TEST);
			return;
		}
		double x = b.getX();
		double y = b.getY();
		double w = b.getWidth();
		double h = b.getHeight();
		y = y + h;
		GameCamera c = this.getCamera();
		if(c != null){
			x = c.gameToScreenX(x);
			y = c.gameToScreenY(y);
			w = c.sizeGameToScreenX(w);
			h = c.sizeGameToScreenY(h);
		}
		glEnable(GL_SCISSOR_TEST);
		glScissor((int)Math.round(x), (int)Math.round(this.getHeight() - y), (int)Math.round(w), (int)Math.round(h));
	}
	
	/** @return See {@link #limitedBoundsStack} */
	public LimitedStack<ZRect> getLimitedBoundsStack(){
		return this.limitedBoundsStack;
	}
	
	/**
	 * Call OpenGL operations that transform to draw to a location in game coordinates.
	 * This method assumes the coordinates to translate are centered in the given rectangular bounding box in game coordinates
	 * This method does not push or pop the matrix stack
	 * 
	 * @param r The bounds
	 */
	public void positionObject(ZRect r){
		this.positionObject(r.getX(), r.getY(), r.getWidth(), r.getHeight());
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
	public void positionObject(double x, double y, double w, double h){
		if(!this.isPositioningEnabled()) return;
		
		GameBuffer b = this.getBuffer();
		double rw = b.getInverseWidth();
		double rh = b.getInverseHeight();
		double hw = b.getInverseHalfWidth();
		double hh = b.getInverseHalfHeight();
		
		// OpenGL transformations happen in reverse order
		// Need to account for OpenGL, where the buffer is in the range [-1, 1] on both axis
		
		// Second, translate from the center to the upper left hand corner, -1 on the x axis, +1 on the y axis
		// That will translate the object so the object is centered on the upper left hand corner of the buffer
		// Then, translate by half of the percentage of the buffer that the object takes up.
		// This is not multiplied, because the OpenGL space is 2x2 because of the range [-1, 1], so half of 2 is 1, so multiply by nothing
		// That will make the upper left hand corner of the object align with the upper left hand corner of the buffer
		// Then, translate by the percentage of the buffer that the given position takes up.
		// This is multiplied by 2, for the same reason as before, but now it's the full amount, not half
		// That will put the object at the final location
		// The below line is just a mathematically simplified version of the commented out line
		// this.translate(-1 + w * rw + 2 * x * rw, 1 - h * rh - 2 * y * rh);
		this.translate(-1 + (x + w * .5) * hw, 1 - (y + h * .5) * hh);
		// First scale by the ratio of objectSize / bufferSize, i.e. the percentage of the buffer that object takes up
		// After this scaling, the object will be in the center of the buffer, and will be the correct size relative to the buffer
		this.scale(w * rw, h * rh);
	}
	
	/**
	 * Draw a rectangle, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * 
	 * @param x The bounds
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawRectangle(ZRect r){
		return this.drawRectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Draw a rectangle, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinate types depend on {@link #positioningEnabledStack}
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
	 * Draw a rectangular buffer at the specified location.
	 * If the given dimensions have a different aspect ratio that those of the given buffer, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * 
	 * @param r The bounds
	 * @param b The {@link GameBuffer} to draw
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawBuffer(ZRect r, GameBuffer b){
		return this.drawBuffer(r.getX(), r.getY(), r.getWidth(), r.getHeight(), b);
	}
	
	/**
	 * Draw a rectangular buffer at the specified location.
	 * If the given dimensions have a different aspect ratio that those of the given buffer, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * 
	 * @param x The x coordinate of the upper left hand corner of the buffer
	 * @param y The y coordinate of the upper left hand corner of the buffer
	 * @param w The width of the image
	 * @param h The height of the image
	 * @param b The {@link GameBuffer} to draw
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawBuffer(double x, double y, double w, double h, GameBuffer b){
		return this.drawImage(x, y, w, h, b.getTextureID());
	}
	
	/**
	 * Draw a rectangular image at the specified location.
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * 
	 * @param r The bounds
	 * @param img The {@link GameImage} to draw
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawImage(ZRect r, GameImage img){
		return this.drawImage(r, img.getId());
	}
	
	/**
	 * Draw a rectangular image at the specified location.
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * 
	 * @param x The x coordinate of the upper left hand corner of the image
	 * @param y The y coordinate of the upper left hand corner of the image
	 * @param w The width of the image
	 * @param h The height of the image
	 * @param img The {@link GameImage} to draw
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawImage(double x, double y, double w, double h, GameImage img){
		return this.drawImage(x, y, w, h, img.getId());
	}
	
	/**
	 * Draw a rectangular image at the specified location.
	 * Draw a rectangular image at the specified location on the given buffer
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * 
	 * @param x The x coordinate of the upper left hand corner of the image
	 * @param y The y coordinate of the upper left hand corner of the image
	 * @param w The width of the image
	 * @param h The height of the image
	 * @param img The OpenGL id of the image to draw
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawImage(ZRect r, int img){
		return this.drawImage(r.getX(), r.getY(), r.getWidth(), r.getHeight(), img);
	}
	
	/**
	 * Draw a rectangular image at the specified location.
	 * Draw a rectangular image at the specified location on the given buffer
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * 
	 * @param x The x coordinate of the upper left hand corner of the image
	 * @param y The y coordinate of the upper left hand corner of the image
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
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * It is unwise to call this method directly. Usually it's better to use a {@link TextBuffer} and draw to that, then draw the text buffer
	 * 
	 * @param x The x position of the text
	 * @param y The y position of the text
	 * @param text The text to draw
	 * @return true if the text was drawn, false otherwise
	 */
	public boolean drawText(double x, double y, String text){
		return drawText(x, y, text, this.getFont());
	}
	
	/**
	 * Draw the given text to the given position
	 * The text will be positioned such that it is written on a line, and the given position is the leftmost part of that line.
	 * i.e. the text starts at the given coordinates and is draw left to right
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * It is unwise to call this method directly. Usually it's better to use a {@link TextBuffer} and draw to that, then draw the text buffer
	 * 
	 * @param x The x position of the text
	 * @param y The y position of the text
	 * @param text The text to draw
	 * @param f The font to use for drawing
	 * @return true if the text was drawn, false otherwise
	 */
	public boolean drawText(double x, double y, String text, GameFont f){
		// Make sure a font exists, and that there is some text
		if(f == null || text == null || text.isEmpty()) return false;
		FontAsset fa = f.getAsset();
		
		// Bounds check for if the text should be drawn
		ZRect[] rects = f.stringBounds(x, y, text, 1, true);
		ZRect r = rects[text.length()];
		if(!this.shouldDraw(r.getX(), r.getY(), r.getWidth(), r.getHeight())) return false;
		
		// Mark the drawing bounds
		// Use the font shaders
		this.renderModeFont();
		// Use the font vertex array
		this.textVertArr.bind();
		// Update the current color for this draw operation
		this.updateColor();
		
		// Use the font's bitmap
		glBindTexture(GL_TEXTURE_2D, fa.getBitmapID());
		
		// Set up for text position and size
		this.xTextBuff.put(0, 0.0f);
		this.yTextBuff.put(0, 0.0f);
		
		// Find the size for positioning the object
		double posSize = f.fontScalar();
		
		// Position and scale the text
		this.pushMatrix();
		// Need to scale because text is upside down
		this.scale(1, -1);
		// Need to position with height - y to account for the negative scaling
		// Need to use posSize for the width and height to keep it scaled appropriately to OpenGL coordinates
		this.positionObject(x, this.getHeight() - y, posSize, posSize);
		
		// Draw every character of the text
		for(int i = 0; i < text.length(); i++){
			char c = text.charAt(i);
			
			// Find the vertices and texture coordinates of the character to draw
			// Must do this regardless to ensure the text moves over even if a character does not get drawn
			f.bounds(c, this.xTextBuff, this.yTextBuff, this.textQuad);
			
			// Only draw the character if it will be in the bounds of the buffer
			if(!this.shouldDraw(rects[i])) continue;
			
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
	 * Determine if the given bounds are contained within the bounds of the given buffer
	 * i.e. find out if something drawn within the given bounds would appear on the buffer
	 * This method accounts for the camera repositioning elements, i.e., if the camera will make something off the screen, this method accounts for that
	 * 
	 * @param x The upper left hand corner x coordinate of the object, in game coordinates
	 * @param y The upper left hand corner y coordinate of the object, in game coordinates
	 * @param w The width of the object, in game coordinates
	 * @param h The height of the object, in game coordinates
	 * @return true if the bounds should be drawn, false otherwise
	 */
	public boolean shouldDraw(double x, double y, double w, double h){
		return shouldDraw(new ZRect(x, y, w, h));
	}
	
	/**
	 * Determine if the given bounds are contained within the current state of this {@link Renderer}
	 * i.e. find out if something drawn within the given bounds would appear on the screen
	 * This method accounts for the camera repositioning elements, i.e., if the camera will make something off the screen, this method accounts for that
	 * 
	 * @param r The bounds
	 * @return true if the bounds should be drawn, false otherwise
	 */
	public boolean shouldDraw(ZRect drawBounds){
		if(!this.isRenderOnlyInside()) return true;
		ZRect renderBounds = this.getBounds();
		
		ZRect limited = this.getLimitedBounds();
		GameCamera c = this.getCamera();
		if(c != null){
			drawBounds = c.boundsGameToScreen(drawBounds);
			if(limited != null) limited = c.boundsGameToScreen(limited);
		}

		boolean yes = renderBounds.intersects(drawBounds);
		if(limited != null) yes &= limited.intersects(drawBounds);
		return yes;
	}
	
	/** Fill the screen with the current color, regardless of camera position */
	public void fill(){
		this.renderModeShapes();
		this.rectVertArr.bind();
		// Update the current color for this draw operation
		this.updateColor();
		
		this.pushMatrix();
		this.identityMatrix();
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		glBindVertexArray(0);
		this.popMatrix();
	}
	
	/** @return The top of {@link #colorStack} */
	public ZColor getColor(){
		return this.colorStack.peek();
	}
	
	/** @return See {@link #colorStack} */
	public LimitedStack<ZColor> getColorStack(){
		return this.colorStack;
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
		this.colorStack.replaceTop(color);
	}
	
	/** Update the uniform variable used to track the color, with the current value */
	public void updateColor(){
		float[] c = this.getColor().toFloat();
		int loc = glGetUniformLocation(this.loadedShader.getId(), "mainColor");
		if(loc != -1) glUniform4fv(loc, c);
	}
	
	/** @return The top of {@link #fontStack} */
	public GameFont getFont(){
		return this.fontStack.peek();
	}
	
	/** @param font Set the top of {@link #fontStack} */
	public void setFont(GameFont font){
		this.fontStack.replaceTop(font);
	}
	
	/** @return See {@link #fontStack} */
	public LimitedStack<GameFont> getFontStack(){
		return this.fontStack;
	}
	
	/** @return The size of {@link #font}. See {@link GameFont#getSize()} */
	public double getFontSize(){
		return this.getFont().getSize();
	}
	
	/** @param size Change the current size of the font. See {@link GameFont#getSize()} */
	public void setFontSize(double size){
		this.setFont(this.getFont().size(size));
	}
	
	/** @return The line spacing of {@link #font}. See {@link GameFont#getLineSpace()} */
	public double getFontLineSpace(){
		return this.getFont().getLineSpace();
	}
	
	/** @param lineSpace Change the current line space of the font. See {@link GameFont#getLineSpace()} */
	public void setFontLineSpace(double lineSpace){
		this.setFont(this.getFont().lineSpace(lineSpace));
	}
	
	/** @return The char spacing of {@link #font}. See {@link GameFont#getCharSpace()} */
	public double getFontCharSpace(){
		return this.getFont().getCharSpace();
	}
	
	/** @param charSpace Change the current line space of the font. See {@link GameFont#getCharSpace()} */
	public void setFontCharSpace(double charSpace){
		this.setFont(this.getFont().charSpace(charSpace));
	}
	
	/** @return The top of {@link #cameraStack} */
	public GameCamera getCamera(){
		return this.cameraStack.peek();
	}
	
	/** @param camera Set the top of {@link #cameraStack}. Can also use null to not use a camera for rendering */
	public void setCamera(GameCamera camera){
		this.cameraStack.replaceTop(camera);
	}
	
	/** @return The top of {@link #renderOnlyInsideStack} */
	public boolean isRenderOnlyInside(){
		return this.renderOnlyInsideStack.peek();
	}
	
	/** @param renderOnlyInside The top of {@link #renderOnlyInsideStack} */
	public void setRenderOnlyInside(boolean renderOnlyInside){
		this.renderOnlyInsideStack.replaceTop(renderOnlyInside);
	}
	
	/** @return See {@link #renderOnlyInsideStack} */
	public LimitedStack<Boolean> getRenderOnlyInsideStack(){
		return this.renderOnlyInsideStack;
	}
	
	/** @return The width, in pixels, of the underlying buffer of this Renderer */
	public int getWidth(){
		return this.getBuffer().getWidth();
	}
	
	/** @return The height, in pixels, of the underlying buffer of this Renderer */
	public int getHeight(){
		return this.getBuffer().getHeight();
	}
	
	/** @return A rectangle of the bounds of this {@link Renderer}, i.e. the position will be (0, 0), width will be {@link #getWidth()} and height will be {@link #getHeight()} */
	public ZRect getBounds(){
		return this.getBuffer().getBounds();
	}
	
	/** @return The ratio of the size of the internal buffer, i.e. the width divided by the height */
	public double getRatioWH(){
		return this.getBuffer().getRatioWH();
	}
	
	/** @return The ratio of the size of the internal buffer, i.e. the height divided by the width */
	public double getRatioHW(){
		return this.getBuffer().getRatioHW();
	}
	
	/** @return The OpenGL id used by this {@link Renderer}s {@link #buffer} */
	public int getBufferId(){
		return this.getBuffer().getTextureID();
	}
	
	/** @return See {@link #buffer} */
	public GameBuffer getBuffer(){
		return this.bufferStack.peek();
	}
	
	/**
	 * Set the buffer that this Renderer should draw to by pushing the given buffer onto {@link #bufferStack}
	 * 
	 * @param buffer See {@link #buffer}
	 * @return The buffer that was being used
	 */
	public GameBuffer pushBuffer(GameBuffer buffer){
		GameBuffer oldBuff = this.getBuffer();
		this.bufferStack.push(buffer);
		this.updateBuffer();
		return oldBuff;
	}
	
	/**
	 * Pop the top buffer off of {@link #bufferStack} and return it
	 * 
	 * @return The buffer, or null if no buffer could be popped
	 */
	public GameBuffer popBuffer(){
		GameBuffer b = this.bufferStack.pop();
		this.updateBuffer();
		return b;
	}
	
	/**
	 * Set the current buffer to draw to
	 * Must be very careful about using this method. Cannot set the buffer if there is only one buffer in the stack
	 * 
	 * @param buffer The new buffer
	 * @return The old buffer, or null if it could not be replaced
	 */
	public GameBuffer setBuffer(GameBuffer buffer){
		GameBuffer old = this.bufferStack.replaceTop(buffer);
		if(old != buffer) this.updateBuffer();
		return old;
	}
	
	/** Update the current state of OpenGL to use the buffer at the top of {@link #bufferStack} for rendering */
	private void updateBuffer(){
		GameBuffer b = this.getBuffer();
		b.drawToBuffer();
		b.setViewport();
	}
	
	/**
	 * Determine if the given bounds are in the bounds of this {@link Renderer}
	 * 
	 * @param bounds The bounds to check, in game coordinates
	 * @return true if they intersect, i.e. return true if any part of the given bounds is in this {@link Renderer}'s bounds, false otherwise
	 */
	public boolean gameBoundsInScreen(ZRect bounds){
		ZRect rBounds = this.getBounds();
		GameCamera c = this.getCamera();
		ZRect gBounds;
		if(c == null) gBounds = rBounds;
		else gBounds = c.boundsScreenToGame(rBounds.getX(), rBounds.getBounds().getY(), rBounds.getBounds().getWidth(), rBounds.getBounds().getHeight());
		return gBounds.intersects(bounds);
	}
	
	/**
	 * Convert an x coordinate value in window space, to a coordinate in screen space coordinates
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting coordinates
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenX(GameWindow window, double x){
		return windowToScreen(x, window.viewportX(), window.viewportWInverse(), this.getWidth());
	}
	
	/**
	 * Convert a y coordinate value in window space, to a coordinate in screen space coordinates
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting coordinates
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenY(GameWindow window, double y){
		return windowToScreen(y, window.viewportY(), window.viewportHInverse(), this.getHeight());
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
		return screenToWindow(x, window.viewportX(), window.viewportW(), this.getBuffer().getInverseWidth());
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in window space coordinates
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting coordinates
	 * @param y The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowY(GameWindow window, double y){
		return screenToWindow(y, window.viewportY(), window.viewportH(), this.getBuffer().getInverseHeight());
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
		return screenToGl(x, window.viewportX(), window.getWidth(), this.getBuffer().getInverseWidth(), window.getInverseWidth());
	};
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in OpenGL coordinates
	 * 
	 * @param y The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlY(GameWindow window, double y){
		return screenToGl(y, window.viewportY(), window.getHeight(), this.getBuffer().getInverseHeight(), window.getInverseHeight());
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
	 * @param window The window to use to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenX(GameWindow window, double x){
		return glToScreen(x, window.viewportX(), window.getInverseWidth(), this.getWidth(), window.getWidth());
	};
	
	/**
	 * Convert a y coordinate value in OpenGL space, to a coordinate in screen coordinates
	 * 
	 * @param y The value to convert
	 * @param window The window to use to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenY(GameWindow window, double y){
		return glToScreen(y, window.viewportY(), window.getInverseHeight(), this.getHeight(), window.getHeight());
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
		return sizeWindowToScreen(x, window.viewportWInverse(), this.getWidth());
	}
	
	/**
	 * Convert a size on the y axis in window space, to a size in screen space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenY(GameWindow window, double y){
		return sizeWindowToScreen(y, window.viewportHInverse(), this.getHeight());
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
		return sizeScreenToWindow(x, window.viewportW(), this.getBuffer().getInverseWidth());
	}
	
	/**
	 * Convert a size on the y axis in screen space, to a size in window space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowY(GameWindow window, double y){
		return sizeScreenToWindow(y, window.viewportH(), this.getBuffer().getInverseHeight());
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
		return sizeScreenToGl(x, window.getWidth(), this.getBuffer().getInverseWidth(), window.getInverseWidth());
	};
	
	/**
	 * Convert a size on the y axis in screen space, to a size in OpenGL space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlY(GameWindow window, double y){
		return sizeScreenToGl(y, window.getHeight(), this.getBuffer().getInverseHeight(), window.getInverseHeight());
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
		return sizeGlToScreen(x, window.getInverseWidth(), this.getWidth(), window.getWidth());
	};
	
	/**
	 * Convert a size on the y axis in OpenGL space, to a size in screen space
	 * 
	 * @param GameWindow the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeGlToScreenY(GameWindow window, double y){
		return sizeGlToScreen(y, window.getInverseHeight(), this.getHeight(), window.getHeight());
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
