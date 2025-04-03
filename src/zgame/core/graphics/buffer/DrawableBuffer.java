package zgame.core.graphics.buffer;

import java.util.function.BiConsumer;

import zgame.core.graphics.Renderer;
import zgame.core.utils.ZRect2D;

/** A {@link GameBuffer} that can easily be extended to draw on */
public class DrawableBuffer extends GameBuffer{
	
	/** true if this {@link DrawableBuffer} has had a value changed, and needs to be redrawn before the next time it's displayed */
	private boolean needRedraw;
	
	/**
	 * true if, before redrawing the contents of this buffer, if any bounds of the renderer are set through {@link Renderer#limitBounds(ZRect2D)},
	 * the bounds will be unlimited, false otherwise. true by default
	 */
	private boolean forceUnlimit;
	
	/**
	 * Create a {@link DrawableBuffer} of the given size.
	 *
	 * @param width See {@link #getWidth()}
	 * @param height See {@link #getHeight()}
	 */
	public DrawableBuffer(int width, int height){
		super(width, height, false);
		this.needRedraw = true;
		this.forceUnlimit = true;
	}
	
	/**
	 * Draw the contents of this buffer to the given renderer, redrawing it if needed
	 *
	 * @param x The x coordinate to draw the upper left hand corner of the buffer
	 * @param y The y coordinate to draw the upper left hand corner of the buffer
	 * @param r The renderer to draw this buffer to
	 */
	public void drawOnRenderer(double x, double y, Renderer r){
		if(this.needRedraw) this.redraw(r);
		// Make sure the color is reset to opaque
		r.pushColor();
		r.makeOpaque();
		
		// Draw the actual buffer
		super.drawOnRenderer(x, y, r);
		
		// Put the color back
		r.popColor();
	}
	
	/**
	 * Draw the contents of this buffer. The calls surrounding this method automatically keep track of all the parts of the renderer,
	 * so that the renderer remains in the same state as it began with when this method exits
	 *
	 * @param r The {@link Renderer} to use for drawing
	 */
	public void draw(Renderer r){
	}
	
	/** @return true if the buffer should not be redrawn, false otherwise */
	public boolean skipRedraw(){
		return false;
	}
	
	/**
	 * Redraw the current content of this buffer
	 *
	 * @param r The {@link Renderer} to use for drawing the buffer
	 */
	private void redraw(Renderer r){
		this.redraw(r, (rr, d) -> {
			var unlimited = this.isForceUnlimit();
			if(unlimited) rr.pushUnlimitedBounds();
			else rr.pushLimitedBounds(this.getBounds());
			
			// Clear the contents of the buffer and draw it
			this.clear();
			this.draw(rr);
			rr.popLimitedBounds();
		}, null);
	}
	
	/**
	 * Perform a redraw with an object
	 *
	 * @param <D> The type of the object to use with the redraw
	 * @param r The renderer to give to the draw function
	 * @param func The function to call to perform the actual drawing
	 * @param data The data to use for rendering
	 */
	protected final <D> void redraw(Renderer r, BiConsumer<Renderer, D> func, D data){
		if(!this.isBufferGenerated()) this.regenerateBuffer(this.getWidth(), this.getHeight());
		
		if(this.skipRedraw()) return;
		
		// Store the renderer's state
		r.pushAll();
		
		// Use no camera
		r.setCamera(null);
		
		// Use this object's buffer
		r.setBuffer(this);
		
		// Perform the actual drawing
		r.identityMatrix();
		func.accept(r, data);
		
		// Put the old state back
		r.popAll();
		
		// No longer need to redraw
		this.needRedraw = false;
	}
	
	/**
	 * Update the current state of {@link #needRedraw}. Keeps the value true if it's already true, or sets it to true if redraw is true
	 *
	 * @param redraw See {@link #needRedraw}
	 */
	public void updateRedraw(boolean redraw){
		this.needRedraw = this.needRedraw || redraw;
	}
	
	/** @return See {@link #needRedraw} */
	public boolean isNeedRedraw(){
		return this.needRedraw;
	}
	
	/** @return See {@link #forceUnlimit} */
	public boolean isForceUnlimit(){
		return this.forceUnlimit;
	}
	
	/** @param forceUnlimit See {@link #forceUnlimit} */
	public void setForceUnlimit(boolean forceUnlimit){
		if(this.forceUnlimit != forceUnlimit) this.updateRedraw(true);
		this.forceUnlimit = forceUnlimit;
	}
}
