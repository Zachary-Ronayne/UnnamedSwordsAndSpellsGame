package zgame.core.graphics.font;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.buffer.DrawableBuffer;
import zgame.core.graphics.buffer.GameBuffer;

/** An object that holds a {@link Renderer} to keep track of a {@link GameBuffer} for easily rendering text without having to redraw it repeatedly */
public class TextBuffer extends DrawableBuffer{
	
	/** The text to be drawn to this buffer */
	private String text;
	
	/** The font to use to draw {@link #text} */
	private GameFont font;
	
	/** The x coordinate, in screen coordinates, in the buffer to draw the text */
	private double textX;
	
	/** The y coordinate, in screen coordinates, in the buffer to draw the text */
	private double textY;
	
	/**
	 * Create a {@link TextBuffer} of the given size.
	 * Will likely want to call {@link #setPosition(double, double)}. The default position is 0 for x, and half the height for y, which may cause some text to get clipped
	 * off
	 * 
	 * @param width See {@link #getWidth()}
	 * @param height See {@link #getHeight()}
	 */
	public TextBuffer(int width, int height){
		this(width, height, null);
	}

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
		super(width, height);
		this.font = font;
		this.textX = 0;
		this.textY = this.getHeight() * 0.5;
		this.text = "";
	}
	
	@Override
	public void drawToRenderer(double x, double y, Renderer r){
		// Only need to draw the text if there is any text
		if(this.skipRedraw()) return;
		super.drawToRenderer(x, y, r);
	}
	
	@Override
	public void draw(Renderer r){
		GameFont f = this.getFont();
		if(f == null) return;
		r.setFont(f);
		r.drawText(this.getTextX(), this.getTextY(), this.getText(), this.getFont());
	}
	
	@Override
	public boolean skipRedraw(){
		// No need to draw anything if the text is empty
		return this.getText().isEmpty();
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
