package zgame.core.graphics.font;

import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;

import static org.lwjgl.stb.STBTruetype.*;

import zgame.core.asset.Asset;
import zgame.core.graphics.image.GameImage;
import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

/** An object that represents a font file to be used for rendering, but not things like font size */
public class FontAsset extends Asset{
	
	/** The number of int buffers available in {@link #intBuffers} */
	public static final int INT_BUFFERS = 2;
	/** The number of int buffers available in {@link #floatBuffers} */
	public static final int FLOAT_BUFFERS = 2;
	
	/** true if this asset has been initialized, false otherwise */
	private boolean initialized;
	
	/** The ID of the bitmap of the image holding the font */
	private int bitmapID;
	
	/** The data used by stb_truetype to represent a font */
	private STBTTBakedChar.Buffer charData;
	/** The data used by stv_truetype for tracking font metrics */
	private STBTTFontinfo info;
	
	/** The width, in pixels, of the bitmap of this {@link FontAsset} */
	private final int width;
	/** The height, in pixels, of the bitmap of this {@link FontAsset} */
	private final int height;
	
	/** The first_char value used by stb_truetype for rendering fonts */
	private final int firstChar;
	
	/** The resolution of the font, i.e. the number of pixels tall the font has to work with. Higher resolutions look better at larger font sizes, but take up more memory */
	private final int resolution;
	
	/** The inverse of {@link #resolution} */
	private final double resolutionInverse;
	
	/** Two times the value of {@link #resolutionInverse}. Used for hacky weird reasons */
	private final double doubleResolutionInverse;
	
	/** The number of characters to load from the font */
	private final int loadChars;
	
	/** The amount a character goes up from the line where it is drawn. This value must be scaled */
	private int ascent;
	/** The amount a character goes below the line where it is drawn, usually negative */
	private int descent;
	/** The space between one row's descent and the next row's ascent */
	private int lineGap;
	
	/**
	 * A map, mapped by font size, whose values are maps containing the width of every character used by this font, computed dynamically as the width of characters is
	 * requested
	 */
	private final Map<Double, Map<Character, Double>> widthMap;
	/** A map, mapped by font size, mapping the maximum height, in pixels, a character can take up, computed when a height is requested */
	private final Map<Double, Double> maxHeightMap;
	/** A map, mapped by font size, mapping the ratio returned by stbtt_ScaleForPixelHeight */
	private final Map<Double, Float> pixelRatioMap;
	
	/** Buffers used internally by stb true type methods */
	private final IntBuffer[] intBuffers;
	/** Buffers used internally by stb true type methods */
	private final FloatBuffer[] floatBuffers;
	/** A buffer used internally by stb true type methods */
	private STBTTAlignedQuad quadBuffer;
	
	/**
	 * Load a font from the given file path
	 *
	 * @param path See {@link #path}
	 */
	public FontAsset(String path){
		this(path, 64);
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
	 * 		Increase this value if more characters from the bitmap need to be loaded
	 */
	public FontAsset(String path, int resolution, int loadChars, int sizeRatio){
		super(path);
		this.initialized = false;
		
		this.firstChar = 32;
		this.resolution = resolution;
		this.resolutionInverse = 1.0 / this.resolution;
		this.doubleResolutionInverse = this.resolutionInverse * 2.0;
		this.width = sizeRatio * this.resolution;
		this.height = sizeRatio * this.resolution;
		this.loadChars = loadChars;
		
		this.widthMap = new HashMap<>();
		this.maxHeightMap = new HashMap<>();
		this.pixelRatioMap = new HashMap<>();
		
		this.intBuffers = new IntBuffer[INT_BUFFERS];
		this.floatBuffers = new FloatBuffer[FLOAT_BUFFERS];
		this.quadBuffer = null;
	}
	
	/** Initialize this {@link FontAsset} based on the current path of the font */
	public void init(){
		if(this.isInitialized()) return;
		
		String path = this.getPath();
		
		// Load the font from the jar
		var data = ZAssetUtils.getJarBytes(path);
		
		// Find the font info
		this.info = STBTTFontinfo.create();
		boolean infoSuccess = stbtt_InitFont(this.info, data);
		if(!infoSuccess) ZConfig.error("Font '", path, "' failed to load font info via stb true type");
		
		// Check for errors
		int numFonts = stbtt_GetNumberOfFonts(data);
		
		boolean success = numFonts != -1;
		if(success) ZConfig.success("Font '", path, "' loaded successfully. ", numFonts, " total fonts loaded.");
		else{
			ZConfig.error("Font '", path, "' failed to load via stb true type");
			return;
		}
		
		// Load the font
		ByteBuffer pixels = BufferUtils.createByteBuffer(this.width * this.height);
		this.charData = STBTTBakedChar.create(this.loadChars);
		int numChars = stbtt_BakeFontBitmap(data, this.resolution, pixels, this.width, this.height, firstChar, this.charData);
		if(numChars > 0) ZConfig.success("    First unused row: ", numChars);
		else if(numChars < 0) ZConfig.success("    Characters which fit: ", -numChars);
		else ZConfig.success("    No Characters fit: ");
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
		
		this.initialized = true;
	}
	
	/** @return See {@link #initialized} */
	public boolean isInitialized(){
		return this.initialized;
	}
	
	@Override
	public void destroy(){
		this.initialized = false;
	}
	
	/**
	 * Determine the maximum height of a character and store it in {@link #maxHeightMap}
	 *
	 * @param size The size of the font to compute the height for
	 */
	private synchronized void computeMaxHeight(double size){
		this.maxHeightMap.put(size, (this.getAscent() - this.getDescent()) * this.pixelRatio(size));
	}
	
	/**
	 * Determine the maximum size a font made with this asset can take up
	 *
	 * @param size The size of the font
	 * @return The maximum height
	 */
	public double getMaxHeight(double size){
		if(!this.maxHeightMap.containsKey(size)) this.computeMaxHeight(size);
		return this.maxHeightMap.get(size);
	}
	
	/**
	 * Compute the width of a character and store it in {@link #widthMap}
	 *
	 * @param c The character
	 */
	private synchronized void computeWidth(double size, char c){
		if(!this.widthMap.containsKey(size)) this.widthMap.put(size, new HashMap<>());
		Map<Character, Double> cMap = this.widthMap.get(size);
		
		FloatBuffer xb = this.getFloatBuffer(0);
		xb.put(0, 0f);
		stbtt_GetBakedQuad(this.getCharData(), this.getWidth(), this.getHeight(), this.charIndex(c), xb, this.getFloatBuffer(1), this.getQuadBuffer(), true);
		// Must account for the resolution
		cMap.put(c, (double)Math.abs(xb.get(0)) * size * this.getResolutionInverse());
	}
	
	/**
	 * Get the width a character using this asset takes up
	 *
	 * @param size The size of the font
	 * @param c The character
	 * @return The width
	 */
	public double getCharWidth(double size, char c){
		if(!this.widthMap.containsKey(size)) this.computeWidth(size, c);
		Map<Character, Double> cMap = this.widthMap.get(size);
		if(!cMap.containsKey(c)) this.computeWidth(size, c);
		return cMap.get(c);
	}
	
	/**
	 * Get an int buffer at the given index, creating a new one if necessary
	 *
	 * @param i The index of {@link #intBuffers} to get
	 * @return The buffer
	 */
	public IntBuffer getIntBuffer(int i){
		if(this.intBuffers[i] == null) this.intBuffers[i] = BufferUtils.createIntBuffer(1);
		return this.intBuffers[i];
	}
	
	/**
	 * Get a float buffer at the given index, creating a new one if necessary
	 *
	 * @param i The index of {@link #floatBuffers} to get
	 * @return The buffer
	 */
	public FloatBuffer getFloatBuffer(int i){
		if(this.floatBuffers[i] == null) this.floatBuffers[i] = BufferUtils.createFloatBuffer(1);
		return this.floatBuffers[i];
	}
	
	/** @return See {@link #quadBuffer}, initializes one if needed */
	public STBTTAlignedQuad getQuadBuffer(){
		if(this.quadBuffer == null) this.quadBuffer = STBTTAlignedQuad.create();
		return this.quadBuffer;
	}
	
	/**
	 * Get the bounds for where to draw a character using this {@link FontAsset}
	 *
	 * @param c The character to get the bounds for rendering
	 * @param x The float buffer for the x coordinate. This buffer will be updated to the position of the next character
	 * @param y The float buffer for the y coordinate. This buffer will be updated to the position of the next character
	 * @param quad The object to place the data for the position and texture coordinates
	 */
	public void findBakedQuad(char c, FloatBuffer x, FloatBuffer y, STBTTAlignedQuad quad){
		stbtt_GetBakedQuad(this.getCharData(), this.getWidth(), this.getHeight(), this.charIndex(c), x, y, quad, true);
	}
	
	/**
	 * Determine a valid character index for this {@link FontAsset} based on the given character
	 *
	 * @param c The character
	 * @return The index, or 0 if the index was invalid
	 */
	public int charIndex(char c){
		int charIndex = c - this.getFirstChar();
		if(c >= this.getLoadChars()) charIndex = 0;
		return charIndex;
	}
	
	/**
	 * Get a ratio which can be multiplied to determine the number of pixels a font should take up.
	 * Typically this needs to be multiplied by things like {@link #ascent}, {@link #descent}, and {@link #lineGap}
	 *
	 * @param size The font size to use to determine the number of pixels
	 * @return The ratio
	 */
	public double pixelRatio(double size){
		if(!this.pixelRatioMap.containsKey(size)) this.pixelRatioMap.put(size, stbtt_ScaleForPixelHeight(this.getInfo(), (float)size));
		return this.pixelRatioMap.get(size);
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
	
	/** See {@link #doubleResolutionInverse} */
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
		return new FontAsset(ZStringUtils.concat(ZFilePaths.fonts(), name));
	}
	
}
