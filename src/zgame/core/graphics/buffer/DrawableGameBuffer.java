package zgame.core.graphics.buffer;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/**
 * An extension of {@link DrawableBuffer} that allows for rendering using a {@link Game} object
 */
public class DrawableGameBuffer extends DrawableBuffer{
	
	/**
	 * Create a new {@link DrawableGameBuffer} of the given width and height
	 * If given a value less than or equal to 0 for width or height, that value will be set to 1
	 *
	 * @param width The width of the buffer, in pixels
	 * @param height The height of the buffer, in pixels
	 */
	public DrawableGameBuffer(double width, double height){
		super(Math.max(1, (int)Math.round(width)), Math.max(1, (int)Math.round(height)));
	}
	
	/** {@link DrawableBuffer#draw(Renderer)} is not used by this class, use {@link #draw(Game, Renderer)} instead */
	@Override
	public final void draw(Renderer r){
	}
	
	/** {@link #drawOnRenderer(double, double, Renderer)} is not used by this class, use {@link #drawToRenderer(double, double, Renderer, Game)} instead */
	@Override
	public final void drawOnRenderer(double x, double y, Renderer r){
	}
	
	/**
	 * Draw the contents of this buffer to the given renderer, redrawing it if needed
	 *
	 * @param x The x coordinate to draw the upper left hand corner of the buffer
	 * @param y The y coordinate to draw the upper left hand corner of the buffer
	 * @param r The renderer to draw this buffer to
	 * @param game The game to use for drawing
	 */
	public void drawToRenderer(double x, double y, Renderer r, Game game){
		if(!this.skipRedraw()) this.redraw(r, (ren, g) -> draw(game, ren), game);
		super.drawOnRenderer(x, y, r);
	}
	
	/**
	 * Draw the buffer using the provided game. Override this method to draw custom values
	 *
	 * @param game The game to use for drawing
	 * @param r The renderer to draw to
	 */
	public void draw(Game game, Renderer r){
	}
	
}
