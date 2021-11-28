package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;

/**
 * A class that manages an OpenGL Framebuffer for a Renderer to draw to
 */
public class GameBuffer{
	
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
	
	/**
	 * Create a GameBuffer of the given size
	 * 
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 */
	public GameBuffer(int width, int height){
		this.regenerateBuffer(width, height, true);
	}
	
	/**
	 * Recreate the OpenGL Framebuffer used by this {@link GameBuffer}. This is an expensive operation, should not be used frequently
	 * 
	 * @param width The new width of the buffer
	 * @param height The new height of the buffer
	 * @param printStatus true to print whether or not the buffer was created successful, false otherwise.
	 *        No success status will print if ZConfig.ZConfigprintSuccess() returns false,
	 *        and no failure status will print if ZConfig.printErrors() returns false
	 * @return true if the buffer was created, false otherwise
	 */
	public boolean regenerateBuffer(int width, int height, boolean printStatus){
		this.destory();

		this.width = 1;
		this.height = 1;
		this.setWidth(width);
		this.setHeight(height);
		
		// Create the texture
		this.textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.textureID);
		
		// Keep everything pixelated
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		
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
		if(success && ZConfig.printSuccess()) ZStringUtils.print("GameBuffer created successfully with id ", this.frameID);
		else if(!success && ZConfig.printErrors()) ZStringUtils.print("Failed to create GameBuffer with status ", status);
		
		// Bind the framebugger to the previous buffer
		glBindFramebuffer(GL_FRAMEBUFFER, oldBuffer);
		
		return success;
	}
	
	/**
	 * Recreate the OpenGL Framebuffer used by this {@link GameBuffer}. This is an expensive operation, should not be used frequently
	 * 
	 * @param width The new width of the buffer
	 * @param height The new height of the buffer
	 * @return true if the buffer was created, false otherwise
	 */
	public boolean regenerateBuffer(int width, int height){
		return this.regenerateBuffer(width, height, true);
	}
	
	/**
	 * After calling this method, all further OpenGL rendering operations will draw to this GameBuffer
	 */
	public void drawToBuffer(){
		glBindFramebuffer(GL_FRAMEBUFFER, this.getFrameID());
	}
	
	/** Erase all resources associated with this GameBuffer. After calling this method, this object should not be used */
	public void destory(){
		glDeleteTextures(this.getTextureID());
		glDeleteFramebuffers(this.getFrameID());
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
	
}
