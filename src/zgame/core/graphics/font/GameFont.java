package zgame.core.graphics.font;

import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import static org.lwjgl.stb.STBTruetype.*;

import zgame.core.graphics.GameImage;
import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/** An object that represents a font to be used for rendering */
public class GameFont{
	
	// TODO add methods for finding the bounding box, like the total width, height, etc.
	
	/** The path to the file which this font was loaded from */
	private String path;
	
	/** The ID of the bitmap of the image holding the font */
	private int bitmapID;
	
	/** The data used by stb_truetype to represent a font */
	private STBTTBakedChar.Buffer charData;
	
	/** The width, in pixels, of the bitmap of this {@link GameFont} */
	private int width;
	/** The height, in pixels, of the bitmap of this {@link GameFont} */
	private int height;
	
	/** The first_char value used by stb_truetype for rendering fonts */
	private int firstChar;
	
	/** The resolution of the font, i.e. the number of pixels tall the font has to work with. Higher resolutions look better at larger font sizes, but take up more memory */
	private int resolution;
	
	/** The inverse of {@link #resolution} */
	private double resolutionInverse;
	
	/** The number of characters to load from the font */
	private int loadChars;
	
	/**
	 * Load a font from the given file path
	 * 
	 * @param path See {@link #path}
	 */
	public GameFont(String path){
		this(path, 32);
	}
	
	/**
	 * Load a font from the given file path
	 * 
	 * @param path See {@link #path}
	 * @param resolution See {@link #resolution}
	 */
	public GameFont(String path, int resolution){
		this(path, resolution, 128);
	}
	
	/**
	 * Load a font from the given file path
	 * 
	 * @param path See {@link #path}
	 * @param resolution See {@link #resolution}
	 * @param loadChars See {@link #loadChars}
	 */
	public GameFont(String path, int resolution, int loadChars){
		this(path, resolution, loadChars, 8);
	}
	
	/**
	 * Load a font from the given file path
	 * 
	 * @param path See {@link #path}
	 * @param resolution See {@link #resolution}
	 * @param loadChars See {@link #loadChars}
	 * @param sizeRatio This value is multiplied by the resolution to determine the width and height of the bitmap.
	 *        Increase this value if more characters from the bitmap need to be loaded
	 */
	public GameFont(String path, int resolution, int loadChars, int sizeRatio){
		this.path = path;
		this.firstChar = 32;
		this.resolution = resolution;
		this.resolutionInverse = 1.0 / this.resolution;
		this.width = sizeRatio * this.resolution;
		this.height = sizeRatio * this.resolution;
		this.loadChars = loadChars;
		init();
	}
	
	/** Initialize this {@link GameFont} based on the current path of the image */
	private void init(){
		// Load the font from the jar
		ByteBuffer data = ZAssetUtils.getJarBytes(this.getPath());
		
		// Check for errors
		int numFonts = stbtt_GetNumberOfFonts(data);
		boolean success = numFonts != -1;
		if(ZConfig.printSuccess() && success) ZStringUtils.print("Font '", path, "' loaded successfully. ", numFonts, " total fonts loaded.");
		else if(ZConfig.printErrors() && !success){
			ZStringUtils.print("Font '", path, "' failed to load via stb true type");
			return;
		}
		// Load the font
		ByteBuffer pixels = BufferUtils.createByteBuffer(this.width * this.height);
		this.charData = STBTTBakedChar.create(this.loadChars);
		int numChars = stbtt_BakeFontBitmap(data, this.resolution, pixels, this.width, this.height, firstChar, this.charData);
		if(ZConfig.printSuccess()){
			if(numChars > 0) ZStringUtils.print("    First unused row: ", numChars);
			else if(numChars < 0) ZStringUtils.print("    Characters which fit: ", -numChars);
			else ZStringUtils.print("    No Characters fit: ");
		}
		// Create a texture for the font bitmap
		this.bitmapID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.bitmapID);
		GameImage.setPixelSettings();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, this.width, this.height, 0, GL_ALPHA, GL_UNSIGNED_BYTE, pixels);
		
		// Free the data
		stbi_image_free(pixels);
		
		// Unbind the texture
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/** @return See {@link #path} */
	public String getPath(){
		return this.path;
	}
	
	/** @return See {@link #bitmapID} */
	public int getBitmapID(){
		return this.bitmapID;
	}
	
	/** @return See {@link #charData} */
	public STBTTBakedChar.Buffer getCharData(){
		return this.charData;
	}
	
	/** @return See {@link #width} */
	public int getWidth(){
		return this.width;
	}
	
	/** @return See {@link #height} */
	public int getHeight(){
		return this.height;
	}
	
	/** @return See {@link #firstChar} */
	public int getFirstChar(){
		return this.firstChar;
	}
	
	/** @return See {@link #resolution} */
	public int getResolution(){
		return this.resolution;
	}
	
	/** See {@link #resolutionInverse} */
	public double getResolutionInverse(){
		return this.resolutionInverse;
	}
	
	/** @return See {@link #loadChars} */
	public int getLoadChars(){
		return this.loadChars;
	}
	
	/**
	 * A convenience method which creates a {@link GameFont} with a file of the given name, assuming the file is located in {@link ZFilePaths#FONTS}
	 * 
	 * @param name The name of the file, including file extension
	 * @return The new font
	 */
	public static GameFont create(String name){
		return new GameFont(ZStringUtils.concat(ZFilePaths.FONTS, name));
	}
	
}
