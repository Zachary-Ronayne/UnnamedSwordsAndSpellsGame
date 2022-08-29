package zgame.core.graphics.font;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;

import zgame.core.utils.ZRect;

import static org.lwjgl.stb.STBTruetype.*;

/** An object which represents a font to be used for rendering, i.e. a {@link FontAsset} and information like font size */
public class GameFont{
	
	/** The font itself to use for rendering */
	private FontAsset asset;
	
	/** The current size, in pixels, to render font. This size is effected by zooming with the camera */
	private double size;
	/** The extra space added between lines of text on top of the font size, can be negative to reduce the space. This amount of space is based on the font size */
	private double lineSpace;
	/** The extra space added between individual characters of text, can be negative to reduce the space. This amount of space is based on the font size */
	private double charSpace;

	/** A buffer used internally for finding the size of the font, this one is for the width */
	private IntBuffer widthBuffer;
	/** A buffer used internally for finding the size of the font, this one is for the bearing */
	private IntBuffer bearingBuffer;
	
	/**
	 * Create a new font object with default values. After the font is created, the values of this object cannot be modified
	 * 
	 * @param asset See {@link #asset}
	 */
	public GameFont(FontAsset asset){
		this(asset, 32, 0, 0);
	}
	
	/**
	 * Create a new font object. After the font is created, the values of this object cannot be modified
	 * 
	 * @param asset See {@link #asset}
	 * @param size See {@link #size}
	 * @param lineSpace See {@link #lineSpace}
	 * @param charSpace See {@link #charSpace}
	 */
	public GameFont(FontAsset asset, double size, double lineSpace, double charSpace){
		this.asset = asset;
		this.size = size;
		this.lineSpace = lineSpace;
		this.charSpace = charSpace;

		this.widthBuffer = null;
		this.bearingBuffer = null;
	}
	
	/**
	 * Get the bounds for where to draw a character using this {@link GameFont}
	 * 
	 * @param c The character to get the bounds for rendering
	 * @param x The float buffer for the x coordinate. This buffer will be updated to the position of the next character
	 * @param y The float buffer for the y coordinate. This buffer will be updated to the position of the next character
	 * @param quad The object to place the data for the position and texture coordinates
	 * @return true if finding this bounding box moved the text to the next line, false otherwise
	 */
	public boolean bounds(char c, FloatBuffer x, FloatBuffer y, STBTTAlignedQuad quad){
		FontAsset a = this.getAsset();
		
		// If the character is a new line, then advance the text to a new line
		boolean newLine = c == '\n';
		if(newLine){
			double lineY = this.getSize() + this.getLineSpace();
			x.put(0, 0.0f);
			y.put(0, (float)(y.get(0) + lineY));
			newLine = true;
		}
		if(!newLine && this.getCharSpace() != 0) x.put(0, (float)(x.get(0) + this.getCharSpace()));
		
		// Get the bounds
		stbtt_GetBakedQuad(a.getCharData(), a.getWidth(), a.getHeight(), a.charIndex(c), x, y, quad, true);
		
		// Return if the character went to a new line
		return newLine;
	}
	
	/**
	 * Find the length of text in pixels
	 * 
	 * @param text The text to check
	 * @return The length
	 */
	public double stringWidth(String text){
		return this.stringBounds(text).width;
	}
	
	/**
	 * Find the bounds of a string drawn with this font, assuming the text is drawn at (0, 0)
	 * 
	 * @param text The text to find the bounds of
	 * @return A rectangle with the bounds in screen coordinates
	 */
	public ZRect stringBounds(String text){
		return this.stringBounds(0, 0, text);
	}
	
	/**
	 * Find the maximum bounds of a string drawn with this font. This does not guarantee a pixel perfect bounding box,
	 * i.e. the edges of the bounds may contain pixels which are not a part of the bounds
	 * 
	 * @param text The text to find the bounds of
	 * @param x The x coordinate where the string is drawn, in screen coordinates
	 * @param y The y coordinate where the string is drawn, in screen coordinates
	 * @return A rectangle with the bounds in screen coordinates
	 */
	public ZRect stringBounds(double x, double y, String text){
		return this.stringBounds(x, y, text, true);
	}
	
	/**
	 * Find the maximum bounds of a string drawn with this font. This does not guarantee a pixel perfect bounding box,
	 * i.e. the edges of the bounds may contain pixels which are not a part of the bounds. This is done by adding a small amount of padding to the final bounds.
	 * If this padding is undesired, pass false for the padding variable
	 * 
	 * @param text The text to find the bounds of
	 * @param x The x coordinate where the string is drawn, in screen coordinates
	 * @param y The y coordinate where the string is drawn, in screen coordinates
	 * @param padding true to add a pixel of padding around the bounds, false to not add it
	 * @return A rectangle with the bounds in screen coordinates
	 */
	public ZRect stringBounds(double x, double y, String text, boolean padding){
		return this.stringBounds(x, y, text, padding ? 1 : 0);
	}
	
	/**
	 * Find the maximum bounds of a string drawn with this font. This does not guarantee a pixel perfect bounding box
	 * 
	 * @param text The text to find the bounds of
	 * @param x The x coordinate where the string is drawn, in screen coordinates
	 * @param y The y coordinate where the string is drawn, in screen coordinates
	 * @param padding true to add an amount of distance around the bounds, 0 for no padding
	 * @return A rectangle with the bounds in screen coordinates
	 */
	public ZRect stringBounds(double x, double y, String text, double padding){
		if(text == null || text.isEmpty()) return new ZRect();
		return this.stringBounds(x, y, text, padding, false)[text.length()];
	}
	
	/**
	 * Find the maximum bounds of the individual characters of a string drawn with this font. This does not guarantee a pixel perfect bounding box
	 * 
	 * @param text The text to find the bounds of
	 * @param x The x coordinate where the string is drawn, in screen coordinates
	 * @param y The y coordinate where the string is drawn, in screen coordinates
	 * @param padding true to add an amount of distance around each bounds, 0 for no padding
	 * @param calcIndividuals If true, the full array will be populated, if false, only the last element, containing the full string bounds will be populated
	 * @return An array of the bounds of each character, matching the index of text.
	 *         The array also contains one extra element, indexed as the the length of the string: the total bounds of the entire string,
	 *         padded in the same way as individual characters
	 *         An empty array is returned if the string is empty or not given
	 */
	public ZRect[] characterBounds(double x, double y, String text, double padding){
		return this.stringBounds(x, y, text, padding, true);
	}
	
	/**
	 * Find the maximum bounds of the individual characters of a string drawn with this font. This does not guarantee a pixel perfect bounding box
	 * 
	 * @param text The text to find the bounds of
	 * @param x The x coordinate where the string is drawn, in screen coordinates
	 * @param y The y coordinate where the string is drawn, in screen coordinates
	 * @param padding true to add an amount of distance around each bounds, 0 for no padding
	 * @param calcIndividuals If true, the full array will be populated, if false, only the last element, containing the full string bounds will be populated
	 * @return An array of the bounds of each character, matching the index of text.
	 *         The array also contains one extra element, indexed as the the length of the string: the total bounds of the entire string,
	 *         padded in the same way as individual characters
	 *         An empty array is returned if the string is empty or not given
	 */
	public ZRect[] stringBounds(double x, double y, String text, double padding, boolean calcIndividuals){
		FontAsset a = this.getAsset();
		double baseX = x;

		// If there is no string, then the array is empty
		if(a == null || text == null || text.isEmpty()) return new ZRect[0];
		
		// Set up buffers
		if(this.widthBuffer == null) this.widthBuffer = BufferUtils.createIntBuffer(1);
		if(this.bearingBuffer == null) this.bearingBuffer = BufferUtils.createIntBuffer(1);
		double pixelRatio = a.pixelRatio(this.getSize());
		double w = 0;
		double h = (a.getAscent() - a.getDescent()) * pixelRatio;
		y -= a.getAscent() * pixelRatio;
		
		// Go through each letter and find the total width
		int i = 0;
		ZRect[] rects = new ZRect[text.length() + 1];
		do{
			char c = text.charAt(i);
			
			if(c == '\n'){
				rects[i] = new ZRect();
				y += this.getSize() + this.getLineSpace();
				x = baseX;
				w = 0;
				i++;
				continue;
			}

			stbtt_GetCodepointHMetrics(a.getInfo(), c, this.widthBuffer, this.bearingBuffer);
			double wbVal = this.widthBuffer.get(0) * pixelRatio;
			if(calcIndividuals) rects[i] = new ZRect(x + w, y, wbVal, h, padding);
			w += wbVal;
			i++;
		}while(i < text.length());

		rects[text.length()] = new ZRect(x, y, w, h, padding);
		return rects;
	}
	
	/** @return The value which must be used to scale the font returned by {@link #bounds(char, FloatBuffer, FloatBuffer, STBTTAlignedQuad)} into game coordinates */
	public double fontScalar(){
		// The double is because fonts are weird and render as half the size I intend them to. This 2 is very hacky
		return this.getSize() * this.getAsset().getDoubleResolutionInverse();
	}
	
	/** @return See {@link #asset} */
	public FontAsset getAsset(){
		return this.asset;
	}
	
	/** @return See {@link #size} */
	public double getSize(){
		return this.size;
	}
	
	/** @return See {@link #lineSpace} */
	public double getLineSpace(){
		return this.lineSpace;
	}
	
	/** @return See {@link #charSpace} */
	public double getCharSpace(){
		return this.charSpace;
	}
	
	/**
	 * @return A copy of this {@link GameFont}, but using the given font
	 * @param asset See {@link #asset}
	 */
	public GameFont asset(FontAsset asset){
		return new GameFont(asset, this.size, this.lineSpace, this.charSpace);
	}
	
	/**
	 * @return A copy of this {@link GameFont}, but using the given size
	 * @param size See {@link #size}
	 */
	public GameFont size(double size){
		return new GameFont(this.asset, size, this.lineSpace, this.charSpace);
	}
	
	/**
	 * @return A copy of this {@link GameFont}, but using the given line spacing
	 * @param lineSpace See {@link #lineSpace}
	 */
	public GameFont lineSpace(double lineSpace){
		return new GameFont(this.asset, this.size, lineSpace, this.charSpace);
	}
	
	/**
	 * @return A copy of this {@link GameFont}, but using the given character spacing
	 * @param charSpace See {@link #charSpace}
	 */
	public GameFont charSpace(double charSpace){
		return new GameFont(this.asset, this.size, this.lineSpace, charSpace);
	}
	
}
