package zgame.graphics;

import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import zgame.utils.ZConfig;
import zgame.utils.ZStringUtils;

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
	
	/**
	 * Create a GameBuffer of the given size
	 * 
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 */
	public GameBuffer(int width, int height){
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
		if(ZConfig.printErrors()){
			int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
			boolean success = status == GL_FRAMEBUFFER_COMPLETE;
			if(success && ZConfig.printSuccess()) ZStringUtils.print("GameBuffer created successfully with id ", this.frameID);
			else if(!success && ZConfig.printErrors()) ZStringUtils.print("Failed to create GameBuffer with status ", status);
		}
		
		// Bind the framebugger to the previous buffer
		glBindFramebuffer(GL_FRAMEBUFFER, oldBuffer);
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
	
	/** @param width {@link #width} */
	public void setWidth(int width){
		this.width = width;
	}
	
	/** @return See {@link #height} */
	public int getHeight(){
		return this.height;
	}
	
	/** @param height {@link #height} */
	public void setHeight(int height){
		this.height = height;
	}
	
}
