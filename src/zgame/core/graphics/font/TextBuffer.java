package zgame.core.graphics.font;

import zgame.core.graphics.GameBuffer;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.camera.GameCamera;

/** An object that holds a {@link Renderer} to keep track of a {@link GameBuffer} for easily rendering text without having to redraw it repeatedly */
public class TextBuffer extends GameBuffer{
	
	/** The text to be drawn to this buffer */
	private String text;
	
	/** The font to use to draw {@link #text} */
	private GameFont font;
	
	/** The x coordinate, in screen coordinates, in the buffer to draw the text */
	private double textX;
	
	/** The y coordinate, in screen coordinates, in the buffer to draw the text */
	private double textY;
	
	/** true if this TextBuffer has had a value changed, and needs to be redrawn before the next time it's drawn */
	private boolean needRedraw;
	
	/**
	 * Create a {@link TextBuffer} of the given size.
	 * Will likely want to call {@link #setPosition(double, double)}. The default position is 0 for x, and half the height for y, which may cause some text to get clipped
	 * off
	 * 
	 * @param width See {@link #getWidth()}
	 * @param height See {@link #getHeight()}
	 * @param font See {@link #font}
	 */
	public TextBuffer(int width, int height, GameFont font){
		super(width, height, false);
		this.font = font;
		this.textX = 0;
		this.textY = this.getHeight() * 0.5;
		this.text = "";
		this.needRedraw = true;
	}
	
	/**
	 * @param x The x coordinate to draw the upper left hand corner of the buffer
	 * @param y The y coordinate to draw the upper left hand corner of the buffer
	 * @param r The renderer to draw this buffer to
	 */
	public void draw(double x, double y, Renderer r){
		if(this.needRedraw) this.redraw(r);
		// Only need to draw the text if there is any text
		if(!this.getText().isEmpty()) r.drawBuffer(x, y, this.getWidth(), this.getHeight(), this);
	}
	
	// TODO abstract out all this redraw stuff into its own object that extends GameBuffer
	/**
	 * Redraw the current text to this buffer
	 * 
	 * @param r The {@link Renderer} to use for drawing the text
	 */
	private void redraw(Renderer r){
		
		// No need to draw anything if the text is empty
		if(this.getText().isEmpty()){
			this.needRedraw = false;
			return;
		}
		if(!this.isBufferGenerated()) this.regenerateBuffer(this.getWidth(), this.getHeight());
		
		// Save the renderer's camera and set it to not use it for this operation
		GameCamera oldCam = r.getCamera();
		r.setCamera(null);
		
		// Save the current buffer and use this object's buffer
		GameBuffer oldBuffer = r.setBuffer(this);
		
		// Clear this buffer
		this.clear();
		
		// Use the font of this buffer
		r.setFont(this.getFont());
		
		// Draw the text
		r.pushMatrix();
		r.identityMatrix();
		r.drawText(this.getTextX(), this.getTextY(), this.getText(), this.getFont());
		r.popMatrix();
		
		// Put the old buffer and camera back
		r.setBuffer(oldBuffer);
		r.setCamera(oldCam);
		
		// No longer need to redraw
		this.needRedraw = false;
	}
	
	/**
	 * Update the current state of {@link #needRedraw}. Keeps the value true if it's already true, or sets it to true if redraw is true
	 * 
	 * @param redraw See {@link #needRedraw}
	 */
	private void updateRedraw(boolean redraw){
		this.needRedraw = this.needRedraw || redraw;
	}
	
	/** @return See {@link #text} */
	public String getText(){
		return this.text;
	}
	
	/**
	 * Update the value of {@link #text}, but do not redraw anything
	 * 
	 * @param text See {@link #text}
	 */
	public void setText(String text){
		if(text == null) text = "";
		this.updateRedraw(!this.getText().equals(text));
		this.text = text;
	}
	
	/** @return See {@link #font} */
	public GameFont getFont(){
		return this.font;
	}
	
	/**
	 * Update the value of {@link #font}, but do not redraw anything
	 * 
	 * @param font See {@link #font}
	 */
	public void setFont(GameFont font){
		this.updateRedraw(font != this.font);
		this.font = font;
	}
	
	/**
	 * Update the values of {@link #textX} and {@link #textY}, but do not redraw anything
	 * 
	 * @param textX See {@link #textX}
	 * @param textY See {@link #textY}
	 */
	public void setTextPosition(double x, double y){
		this.updateRedraw(x != this.textX || y != this.textY);
		this.textX = x;
		this.textY = y;
	}
	
	/** @return See {@link #textX} */
	public double getTextX(){
		return this.textX;
	}
	
	/**
	 * Update the value of {@link #textX}, but do not redraw anything
	 * 
	 * @param textX See {@link #textX}
	 */
	public void setTextX(double textX){
		this.updateRedraw(textX != this.textX);
		this.textX = textX;
	}
	
	/** @return See {@link #textY} */
	public double getTextY(){
		return this.textY;
	}
	
	/**
	 * Update the value of {@link #textY}, but do not redraw anything
	 * 
	 * @param textY See {@link #textY}
	 */
	public void setTextY(double textY){
		this.updateRedraw(textY != this.textY);
		this.textY = textY;
	}
	
}
