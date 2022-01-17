package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/** An object that manages a single OpenGL texture to be used as an image for rendering in the game */
public class GameImage{
	
	/** The OpenGL texture id associated with this {@link GameImage} */
	private int id;
	
	/** The path used to load this {@link GameImage} */
	private String path;
	
	/**
	 * Create a new GameImage and load it from the given path
	 * 
	 * @param path The path to load from
	 */
	public GameImage(String path){
		this.path = path;
		this.init();
	}
	
	/** Initialize this {@link GameImage} based on the current path of the image */
	private void init(){
		// Generate the id and use it
		this.id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		
		// Ensure the images load as intended
		stbi_set_flip_vertically_on_load(true);
		
		// Keep everything pixelated
		this.setPixelSettings();
		
		// Load the image from the jar
		ByteBuffer buff = ZAssetUtils.getJarBytes(this.getPath());
		
		// Load the image in with stbi
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer c = BufferUtils.createIntBuffer(1);
		ByteBuffer img = stbi_load_from_memory(buff, w, h, c, 0);
		boolean success = img != null;
		if(ZConfig.printSuccess() && success){
			ZStringUtils.print("Image '", path, "' loaded successfully");
			ZStringUtils.print("with width: ", w.get(0), ", height: ", h.get(0), ", channels: ", c.get(0));
		}
		else if(ZConfig.printErrors() && !success){
			ZStringUtils.print("Image '", path, "' failed to load via stbi");
			return;
		}
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, img);
		
		// Free the data
		stbi_image_free(img);
		
		// Unbind the texture
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/** Set the settings of the currently loaded texture as pixelated */
	private void setPixelSettings(){
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	}
	
	/** Erase any resources used by this GameImage */
	public void delete(){
		glDeleteTextures(this.getId());
	}
	
	/** Use this GameImage when drawing textures */
	public void use(){
		glBindTexture(GL_TEXTURE_2D, this.getId());
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
	/** @return See {@link #path} */
	public String getPath(){
		return this.path;
	}
	
	/**
	 * A convenience method which creates a GameImage with a file of the given name, assuming the file is located in ZFilePaths.IMAGES
	 * 
	 * @param name The name of the file, including file extension
	 * @return The new image
	 */
	public static GameImage create(String name){
		return new GameImage(ZStringUtils.concat(ZFilePaths.IMAGES, name));
	}
	
}
