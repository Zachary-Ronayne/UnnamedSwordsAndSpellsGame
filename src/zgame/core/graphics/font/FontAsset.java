package zgame.core.graphics.font;

import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;

import static org.lwjgl.stb.STBTruetype.*;

import zgame.core.asset.Asset;
import zgame.core.graphics.image.GameImage;
import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/** An object that represents a font file to be used for rendering, but not things like font size, */
public class FontAsset extends Asset{
	
	/** The ID of the bitmap of the image holding the font */
	private int bitmapID;
	
	/** The buffer holding the raw data for the font */
	private ByteBuffer data;
	/** The data used by stb_truetype to represent a font */
	private STBTTBakedChar.Buffer charData;
	/** The data used by stv_truetype for tracking font metrics */
	private STBTTFontinfo info;
	
	/** The width, in pixels, of the bitmap of this {@link FontAsset} */
	private int width;
	/** The height, in pixels, of the bitmap of this {@link FontAsset} */
	private int height;
	
	/** The first_char value used by stb_truetype for rendering fonts */
	private int firstChar;
	
	/** The resolution of the font, i.e. the number of pixels tall the font has to work with. Higher resolutions look better at larger font sizes, but take up more memory */
	private int resolution;
	
	/** The inverse of {@link #resolution} */
	private double resolutionInverse;
	
	/** Two times the value of {@link #doubleResolutionInverse}. Used for hacky weird reasons */
	private double doubleResolutionInverse;
	
	/** The number of characters to load from the font */
	private int loadChars;
	
	/** The amount a character goes up from the line where it is drawn. This value must be scaled */
	private int ascent;
	/** The amount a character goes below the line where it is drawn, usually negative */
	private int descent;
	/** The space between one row's descent and the next row's ascent */
	private int lineGap;
	
	/**
	 * Load a font from the given file path
	 * 
	 * @param path See {@link #path}
	 */
	public FontAsset(String path){
		this(path, 32);
	}
	
	/**
	 * Load a font from the given file path
	 * 
	 * @param path See {@link #path}
	 * @param resolution See {@link #resolution}
	 */
	public FontAsset(String path, int resolution){
		this(path, resolution, 128);
	}
	
	/**
	 * Load a font from the given file path
	 * 
	 * @param path See {@link #path}
	 * @param resolution See {@link #resolution}
	 * @param loadChars See {@link #loadChars}
	 */
	public FontAsset(String path, int resolution, int loadChars){
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
	public FontAsset(String path, int resolution, int loadChars, int sizeRatio){
		super(path);
		this.firstChar = 32;
		this.resolution = resolution;
		this.resolutionInverse = 1.0 / this.resolution;
		this.doubleResolutionInverse = this.resolutionInverse * 2.0;
		this.width = sizeRatio * this.resolution;
		this.height = sizeRatio * this.resolution;
		this.loadChars = loadChars;
		init();
	}
	
	/** Initialize this {@link FontAsset} based on the current path of the font */
	private void init(){
		String path = this.getPath();
		
		// Load the font from the jar
		this.data = ZAssetUtils.getJarBytes(path);
		
		// Find the font info
		this.info = STBTTFontinfo.create();
		boolean infoSuccess = stbtt_InitFont(this.info, this.data);
		if(ZConfig.printErrors() && !infoSuccess) ZStringUtils.print("Font '", path, "' failed to load font info via stb true type");
		
		// Check for errors
		int numFonts = stbtt_GetNumberOfFonts(this.data);
		
		boolean success = numFonts != -1;
		if(ZConfig.printSuccess() && success) ZStringUtils.print("Font '", path, "' loaded successfully. ", numFonts, " total fonts loaded.");
		else if(ZConfig.printErrors() && !success){
			ZStringUtils.print("Font '", path, "' failed to load via stb true type");
			return;
		}
		
		// Load the font
		ByteBuffer pixels = BufferUtils.createByteBuffer(this.width * this.height);
		this.charData = STBTTBakedChar.create(this.loadChars);
		int numChars = stbtt_BakeFontBitmap(this.data, this.resolution, pixels, this.width, this.height, firstChar, this.charData);
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
		
		// Find metrics
		IntBuffer a = BufferUtils.createIntBuffer(1);
		IntBuffer d = BufferUtils.createIntBuffer(1);
		IntBuffer l = BufferUtils.createIntBuffer(1);
		stbtt_GetFontVMetrics(this.getInfo(), a, d, l);
		this.ascent = a.get(0);
		this.descent = d.get(0);
		this.lineGap = l.get(0);
	}
	
	@Override
	public void destroy(){
	}
	
	/**
	 * Determine a valid character index for this {@link FontAsset} based on the given character
	 * 
	 * @param c The character
	 * @return The index, or 0 if the index was invalid
	 */
	public int charIndex(char c){
		int charIndex = c - this.getFirstChar();
		if(c < 0 || c >= this.getLoadChars()) charIndex = 0;
		return charIndex;
	}
	
	/**
	 * Get a ratio which can be multiplied to determine the number of pixels a font should take up.
	 * Typically this needs to be multiplied by things like {@link #ascent}, {@link #descent}, and {@link #lineGap}
	 * 
	 * @param size The font size to use to determine the number of pixels
	 * @return The ratio
	 */
	// TODO make this use a map and store all of these values
	public double pixelRatio(double size){
		return stbtt_ScaleForPixelHeight(this.getInfo(), (float)size);
	}
	
	/** @return See {@link #bitmapID} */
	public int getBitmapID(){
		return this.bitmapID;
	}
	
	/** @return See {@link #charData} */
	public STBTTBakedChar.Buffer getCharData(){
		return this.charData;
	}
	
	/** @return See {@link #info} */
	public STBTTFontinfo getInfo(){
		return this.info;
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
	
	/** See {@link #getDoubleResolutionInverse} */
	public double getDoubleResolutionInverse(){
		return doubleResolutionInverse;
	}
	
	/** @return See {@link #loadChars} */
	public int getLoadChars(){
		return this.loadChars;
	}
	
	/** @return See {@link #ascent} */
	public int getAscent(){
		return this.ascent;
	}
	
	/** @return See {@link #descent} */
	public int getDescent(){
		return this.descent;
	}
	
	/** @return See {@link #lineGap} */
	public int getLineGap(){
		return this.lineGap;
	}
	
	/**
	 * A convenience method which creates a {@link FontAsset} with a file of the given name, assuming the file is located in {@link ZFilePaths#FONTS}
	 * 
	 * @param name The name of the file, including file extension
	 * @return The new font
	 */
	public static FontAsset create(String name){
		return new FontAsset(ZStringUtils.concat(ZFilePaths.FONTS, name));
	}
	
}
