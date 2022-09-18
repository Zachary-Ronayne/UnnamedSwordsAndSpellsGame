package zgame.core.graphics.buffer;

import zgame.core.graphics.Renderer;

/** A {@link GameBuffer} that can easily be extended to draw on */
public class DrawableBuffer extends GameBuffer{
	
	/** true if this {@link DrawableBuffer} has had a value changed, and needs to be redrawn before the next time it's displayed */
	private boolean needRedraw;
	
	/**
	 * Create a {@link DrawableBuffer} of the given size.
	 * 
	 * @param width See {@link #getWidth()}
	 * @param height See {@link #getHeight()}
	 */
	public DrawableBuffer(int width, int height){
		super(width, height, false);
		this.needRedraw = true;
	}
	
	/**
	 * Draw the contents of this buffer to the given renderer, redrawing it if needed
	 * 
	 * @param x The x coordinate to draw the upper left hand corner of the buffer
	 * @param y The y coordinate to draw the upper left hand corner of the buffer
	 * @param r The renderer to draw this buffer to
	 */
	public void drawToRenderer(double x, double y, Renderer r){
		if(this.needRedraw) this.redraw(r);
		super.drawToRenderer(x, y, r);
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
	 * Redraw the current text to this buffer
	 * 
	 * @param r The {@link Renderer} to use for drawing the text
	 */
	private void redraw(Renderer r){
		if(!this.isBufferGenerated()) this.regenerateBuffer(this.getWidth(), this.getHeight());
		
		if(skipRedraw()) return;
		
		// Store the renderer's state
		r.pushAll();
		
		// Use no camera
		r.setCamera(null);
		
		// Use this object's buffer
		r.setBuffer(this);
		
		// Clear this buffer
		this.clear();
		
		// Perform the actual drawing
		r.identityMatrix();
		this.draw(r);
		
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
	
}
