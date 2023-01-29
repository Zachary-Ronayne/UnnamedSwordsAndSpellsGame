package zgame.core.graphics.buffer;

import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.image.GameImage;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZRect;
import zgame.core.utils.ZStringUtils;

/**
 * A class that manages an OpenGL Framebuffer for a Renderer to draw to
 */
public class GameBuffer implements Destroyable{
	
	/** The OpenGL texture ID used to track texture used by this GameBuffer's Framebuffer */
	private int textureID;
	
	/** The OpenGL Framebuffer ID used to track this GameBuffer's Framebuffer */
	private int frameID;
	
	/** The width, in pixels, of this GameBuffer */
	private int width;
	
	/** The height, in pixels, of this GameBuffer */
	private int height;
	
	/** Stores the inverse of {@link #width} */
	private double inverseWidth;
	/** Stores the inverse of {@link #height} */
	private double inverseHeight;
	/** Stores the inverse of half of {@link #width} */
	private double inverseHalfWidth;
	/** Stores the inverse of half of {@link #height} */
	private double inverseHalfHeight;
	/** Stores the ratio of {@link #width} divided by {@link #height} */
	private double ratioWH;
	/** Stores the ratio of {@link #height} divided by {@link #width} */
	private double ratioHW;
	
	/** true if the buffer has been generated, false otherwise */
	private boolean bufferGenerated;
	
	/**
	 * Create a GameBuffer of the given size
	 *
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 * @param generate true if the buffer should generate right away, false to not generate it
	 */
	public GameBuffer(int width, int height, boolean generate){
		this.bufferGenerated = false;
		if(generate) this.regenerateBuffer(width, height);
		else{
			this.width = width;
			this.height = height;
		}
	}
	
	/**
	 * Recreate the OpenGL Framebuffer used by this {@link GameBuffer}. This is an expensive operation, should not be used frequently
	 *
	 * @param width The new width of the buffer
	 * @param height The new height of the buffer
	 * @return true if the buffer was created, false otherwise
	 */
	public boolean regenerateBuffer(int width, int height){
		this.destroy();
		
		this.width = 1;
		this.height = 1;
		this.setWidth(width);
		this.setHeight(height);
		
		// Create the texture
		this.textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.textureID);
		
		// Keep everything pixelated
		GameImage.setPixelSettings();
		
		// Create the buffer
		ByteBuffer buff = BufferUtils.createByteBuffer(this.getWidth() * this.getHeight() * 4);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.getWidth(), this.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buff);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		// Make the frame buffer
		this.frameID = glGenFramebuffers();
		int oldBuffer = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
		glBindFramebuffer(GL_FRAMEBUFFER, this.frameID);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.textureID, 0);
		
		// Error check
		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		boolean success = status == GL_FRAMEBUFFER_COMPLETE;
		if(success && ZConfig.printDebug())
			ZStringUtils.print("GameBuffer created successfully with frame id: ", this.getFrameID(), ", and texture id: ", this.getTextureID());
		else if(!success && ZConfig.printErrors()) ZStringUtils.print("Failed to create GameBuffer with status ", status);
		
		// Bind the framebuffer to the previous buffer
		glBindFramebuffer(GL_FRAMEBUFFER, oldBuffer);
		
		this.bufferGenerated = success;
		return success;
	}
	
	/** Erase all resources associated with this GameBuffer. After calling this method, this object should not be used */
	@Override
	public void destroy(){
		if(!this.bufferGenerated) return;
		this.bufferGenerated = false;
		if(ZConfig.printDebug()) ZStringUtils.print("On game buffer: ", this, ", deleted frame buffer ID: ", this.getFrameID(), ", and texture ID: ", this.getTextureID());
		
		// Delete the buffer
		glDeleteFramebuffers(this.getFrameID());
		glDeleteTextures(this.getTextureID());
		
	}
	
	/**
	 * Draw the currently drawn content of this buffer to the given {@link Renderer}
	 * Coordinates are in game coordinates
	 *
	 * @param x The x coordinate to draw the upper left hand corner of the buffer
	 * @param y The y coordinate to draw the upper left hand corner of the buffer
	 * @param r The {@link Renderer} to use
	 */
	public void drawToRenderer(double x, double y, Renderer r){
		r.drawBuffer(x, y, this.getWidth(), this.getHeight(), this);
	}
	
	/** After calling this method, all further OpenGL rendering operations will draw to this GameBuffer */
	public void drawToBuffer(){
		glBindFramebuffer(GL_FRAMEBUFFER, this.getFrameID());
	}
	
	/** Set the viewport so that it matches the full size of this {@link GameBuffer} */
	public void setViewport(){
		glViewport(0, 0, this.getWidth(), this.getHeight());
	}
	
	/** Set the OpenGL clear color to fully transparent, then clear the currently bound buffer. Generally should call {@link #drawToBuffer()} before calling this method */
	public void clear(){
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	/** @return See {@link #textureID} */
	public int getTextureID(){
		return this.textureID;
	}
	
	/** @return See {@link #frameID} */
	public int getFrameID(){
		return this.frameID;
	}
	
	/** @return See {@link #width} */
	public int getWidth(){
		return this.width;
	}
	
	/** @return See {@link #inverseWidth} */
	public double getInverseWidth(){
		return this.inverseWidth;
	}
	
	/** @return See {@link #inverseHalfWidth} */
	public double getInverseHalfWidth(){
		return this.inverseHalfWidth;
	}
	
	/**
	 * Update the currently stored values for the buffer width, but do not update the buffer itself, should not be called without updating the buffer afterwards
	 *
	 * @param width {@link #width}
	 */
	private void setWidth(int width){
		this.width = width;
		this.inverseWidth = 1.0 / width;
		this.inverseHalfWidth = 1.0 / (width * 0.5);
		this.updateRatio();
	}
	
	/** @return See {@link #height} */
	public int getHeight(){
		return this.height;
	}
	
	/** @return See {@link #inverseHeight} */
	public double getInverseHeight(){
		return this.inverseHeight;
	}
	
	/** @return See {@link #inverseHalfHeight} */
	public double getInverseHalfHeight(){
		return this.inverseHalfHeight;
	}
	
	/**
	 * Update the currently stored values for the buffer height, but do not update the buffer itself, should not be called without updating the buffer afterwards
	 *
	 * @param height {@link #height}
	 */
	private void setHeight(int height){
		this.height = height;
		this.inverseHeight = 1.0 / height;
		this.inverseHalfHeight = 1.0 / (height * 0.5);
		this.updateRatio();
	}
	
	/** Updates the internal values of {@link #ratioWH} and {@link #ratioHW} */
	private void updateRatio(){
		this.ratioWH = (double)this.width / this.height;
		this.ratioHW = (double)this.height / this.width;
	}
	
	/** @return See {@link #ratioWH} */
	public double getRatioWH(){
		return this.ratioWH;
	}
	
	/** @return See {@link #ratioHW} */
	public double getRatioHW(){
		return this.ratioHW;
	}
	
	/** @return The bounds of this {@link GameBuffer}, i.e., a rectangle positioned at (0, 0) with the dimensions of this buffer */
	public ZRect getBounds(){
		return new ZRect(0, 0, this.getWidth(), this.getHeight());
	}
	
	/** @return See {@link #bufferGenerated} */
	public boolean isBufferGenerated(){
		return this.bufferGenerated;
	}
	
}
