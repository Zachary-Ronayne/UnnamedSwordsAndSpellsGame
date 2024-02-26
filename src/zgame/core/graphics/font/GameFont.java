package zgame.core.graphics.font;

import java.nio.FloatBuffer;

import org.lwjgl.stb.STBTTAlignedQuad;

import zgame.core.utils.ZRect2D;

/** An object which represents a font to be used for rendering, i.e. a {@link FontAsset} and information like font size */
public class GameFont{
	
	/** The font itself to use for rendering */
	private final FontAsset asset;
	
	/** The current size, in pixels, to render font. This size is effected by zooming with the camera */
	private final double size;
	/** The extra space added between lines of text on top of the font size, can be negative to reduce the space. This amount of space is based on the font size */
	private final double lineSpace;
	/** The extra space added between individual characters of text, can be negative to reduce the space. This amount of space is based on the font size */
	private final double charSpace;
	
	/**
	 * The product of the {@link #size} of this font with the resolution of its {@link #asset}
	 * This value is multiplied by things like the amount of space to add between characters and lines of text to account for the way the resolution effects rendering
	 */
	private final double resolutionRatio;
	/** The inverse of {@link #resolutionRatio} */
	private final double resolutionRatioInverse;
	
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
		this.resolutionRatio = this.getSize() * this.asset.getResolutionInverse();
		this.resolutionRatioInverse = 1.0 / this.getResolutionRatio();
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
			double lineY = (this.getSize() + this.getLineSpace()) * this.getResolutionRatioInverse();
			x.put(0, 0.0f);
			y.put(0, (float)(y.get(0) + lineY));
		}
		// Otherwise, get the bounds
		else a.findBakedQuad(c, x, y, quad);
		// Add the extra character space
		if(!newLine && this.getCharSpace() != 0) x.put(0, (float)(x.get(0) + this.getCharSpace() * this.getResolutionRatioInverse()));
		
		// Return if the character went to a new line
		return newLine;
	}
	
	/**
	 * Determine the width of a character, in pixels
	 *
	 * @param c The character to find the width of
	 * @return The width
	 */
	public double charWidth(char c){
		return this.getAsset().getCharWidth(this.getSize(), c);
	}
	
	/**
	 * Find the length of text in pixels
	 *
	 * @param text The text to check
	 * @return The length
	 */
	public double stringWidth(String text){
		return this.stringBounds(text).getWidth();
	}
	
	/**
	 * Find the bounds of a string drawn with this font, assuming the text is drawn at (0, 0)
	 *
	 * @param text The text to find the bounds of
	 * @return A rectangle with the bounds in screen coordinates
	 */
	public ZRect2D stringBounds(String text){
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
	public ZRect2D stringBounds(double x, double y, String text){
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
	public ZRect2D stringBounds(double x, double y, String text, boolean padding){
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
	public ZRect2D stringBounds(double x, double y, String text, double padding){
		if(text == null || text.isEmpty()) return new ZRect2D();
		return this.stringBounds(x, y, text, padding, false)[text.length()];
	}
	
	/**
	 * Find the maximum bounds of the individual characters of a string drawn with this font. This does not guarantee a pixel perfect bounding box
	 *
	 * @param text The text to find the bounds of
	 * @param x The x coordinate where the string is drawn, in screen coordinates
	 * @param y The y coordinate where the string is drawn, in screen coordinates
	 * @param padding true to add an amount of distance around each bounds, 0 for no padding
	 * @return An array of the bounds of each character, matching the index of text.
	 * 		The array also contains one extra element, indexed as the length of the string: the total bounds of the entire string,
	 * 		padded in the same way as individual characters
	 * 		An array with one empty rectangle is returned if the string is empty or not given
	 */
	public ZRect2D[] characterBounds(double x, double y, String text, double padding){
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
	 * 		The array also contains one extra element, indexed as the length of the string: the total bounds of the entire string,
	 * 		padded in the same way as individual characters
	 * 		An empty array is returned if the string is empty or not given
	 */
	public ZRect2D[] stringBounds(double x, double y, String text, double padding, boolean calcIndividuals){
		FontAsset a = this.getAsset();
		
		// If there is no string, then the array contains only one empty rectangle
		if(a == null || text == null || text.isEmpty()) return new ZRect2D[]{new ZRect2D()};
		
		// Set up buffers
		double pixelRatio = a.pixelRatio(this.getSize());
		double w = 0;
		double h = (a.getAscent() - a.getDescent()) * pixelRatio;
		y -= a.getAscent() * pixelRatio;
		
		// This part is really weird, but doing this aligns the text better. It's still not perfect, but it's better
		double add = this.getSize() * a.getResolutionInverse();
		x += add;
		y -= add;
		
		double baseX = x;
		double baseY = y;
		double maxWidth = w;
		double lineSpace = this.getLineSpace();
		double size = this.getMaxHeight();
		double charSpace = this.getCharSpace();
		double maxHeight = h;
		
		// Go through each letter and find the total width
		int i = 0;
		ZRect2D[] rects = new ZRect2D[text.length() + 1];
		do{
			char c = text.charAt(i);
			// If this character is a new line, move to the next line and don't find a new size
			if(c == '\n'){
				x = baseX;
				w = 0;
				rects[i] = new ZRect2D(x, y, 0, h);
				y += lineSpace + size;
				maxHeight += lineSpace + size;
				i++;
				continue;
			}
			double wbVal = this.charWidth(c);
			
			if(calcIndividuals) rects[i] = new ZRect2D(x + w, y, wbVal, h, padding);
			w += wbVal;
			maxWidth = Math.max(maxWidth, w);
			w += charSpace;
			
			i++;
		}while(i < text.length());
		rects[text.length()] = new ZRect2D(baseX, baseY, maxWidth, maxHeight, padding);
		return rects;
	}
	
	/** @return The value which must be used to scale the font returned by {@link #bounds(char, FloatBuffer, FloatBuffer, STBTTAlignedQuad)} into game coordinates */
	public double fontScalar(){
		// The double is because fonts are weird and render as half the size I intend them to. This double resolution inverse is very hacky
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
	 * @param asset See {@link #asset}
	 * @return A copy of this {@link GameFont}, but using the given font
	 */
	public GameFont asset(FontAsset asset){
		return new GameFont(asset, this.size, this.lineSpace, this.charSpace);
	}
	
	/**
	 * @param size See {@link #size}
	 * @return A copy of this {@link GameFont}, but using the given size
	 */
	public GameFont size(double size){
		return new GameFont(this.asset, size, this.lineSpace, this.charSpace);
	}
	
	/**
	 * @param lineSpace See {@link #lineSpace}
	 * @return A copy of this {@link GameFont}, but using the given line spacing
	 */
	public GameFont lineSpace(double lineSpace){
		return new GameFont(this.asset, this.size, lineSpace, this.charSpace);
	}
	
	/**
	 * @param charSpace See {@link #charSpace}
	 * @return A copy of this {@link GameFont}, but using the given character spacing
	 */
	public GameFont charSpace(double charSpace){
		return new GameFont(this.asset, this.size, this.lineSpace, charSpace);
	}
	
	/** @return See {@link #resolutionRatio} */
	public double getResolutionRatio(){
		return this.resolutionRatio;
	}
	
	/** @return See {@link #resolutionRatioInverse} */
	public double getResolutionRatioInverse(){
		return this.resolutionRatioInverse;
	}
	
	/** @return See {@link FontAsset#getMaxHeight(double)} */
	public double getMaxHeight(){
		return this.getAsset().getMaxHeight(this.getSize());
	}
	
}
