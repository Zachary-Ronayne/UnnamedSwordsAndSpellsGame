package zgame.core.graphics.font;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.TextOption;
import zgame.core.graphics.buffer.DrawableBuffer;
import zgame.core.graphics.buffer.GameBuffer;
import zgame.core.utils.ZArrayUtils;

import java.util.ArrayList;
import java.util.Objects;

/** An object that holds a {@link Renderer} to keep track of a {@link GameBuffer} for easily rendering text without having to redraw it repeatedly */
public class TextBuffer extends DrawableBuffer{
	
	/** The way to draw the text to display for this buffer */
	private ArrayList<TextOption> options;
	
	/** The font to use to draw the text of {@link #options} */
	private GameFont font;
	
	/** The x coordinate, in screen coordinates, in the buffer to draw the text */
	private double textX;
	
	/** The y coordinate, in screen coordinates, in the buffer to draw the text */
	private double textY;
	
	/**
	 * Create a {@link TextBuffer} of the given size.
	 * Will likely want to call {@link #setTextPosition(double, double)}. The default position is 0 for x, and half the height for y, which may cause some text to get clipped
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
	 * Will likely want to call {@link #setTextPosition(double, double)}. The default position is 0 for x, and half the height for y, which may cause some text to get clipped
	 * off
	 *
	 * @param width See {@link #getWidth()}
	 * @param height See {@link #getHeight()}
	 * @param font See {@link #font}
	 */
	public TextBuffer(int width, int height, GameFont font){
		this(width, height, new ArrayList<>(), font);
	}
	
	/**
	 * Create a {@link TextBuffer} of the given size.
	 * Will likely want to call {@link #setTextPosition(double, double)}. The default position is 0 for x, and half the height for y, which may cause some text to get clipped
	 * off
	 *
	 * @param width See {@link #getWidth()}
	 * @param height See {@link #getHeight()}
	 * @param options See {@link #options}
	 * @param font See {@link #font}
	 */
	public TextBuffer(int width, int height, ArrayList<TextOption> options, GameFont font){
		super(width, height);
		this.font = font;
		this.textX = 0;
		this.textY = this.getHeight() * 0.5;
		this.options = options;
	}
	
	@Override
	public void drawOnRenderer(double x, double y, Renderer r){
		// Only need to draw the text if there is any text
		if(this.skipRedraw()) return;
		super.drawOnRenderer(x, y, r);
	}
	
	@Override
	public void draw(Renderer r){
		var f = this.getFont();
		if(f == null) return;
		r.setFont(f);
		r.drawText(this.getTextX(), this.getTextY(), f, this.options);
	}
	
	@Override
	public boolean skipRedraw(){
		// No need to draw anything if the text is empty
		return this.getText().isEmpty();
	}
	
	/** @return The raw string value displayed by this thing, ignoring all other values in {@link TextBuffer#options} */
	public String getText(){
		if(this.options == null) return "";
		var sb = new StringBuilder();
		for(var op : this.options) sb.append(op.getText());
		return sb.toString();
	}
	
	/** @param text Set the full text used by this buffer, this will override anything in {@link #options} */
	public void setText(String text){
		if(this.options.size() == 1){
			var t = this.getText();
			if(Objects.equals(text, t)) return;
		}
		var op = this.options.isEmpty() ? new TextOption(text) : this.options.get(0);
		
		this.options = ZArrayUtils.singleList(new TextOption(text, op.getColor(), op.getAlpha()));
		this.updateRedraw(true);
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
	 * @param x See {@link #textX}
	 * @param y See {@link #textY}
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
	
	/** Reposition the text so that it is at the center of the buffer on the x axis */
	public void centerTextX(){
		this.setTextX(this.getWidth() * 0.5 - this.getFont().stringWidth(this.getText()) * 0.5);
	}
	
	/** Reposition the text so that it is at the center of the buffer on the y axis */
	public void centerTextY(){
		this.setTextY(this.getHeight() * 0.5 + this.getFont().getMaxHeight() * 0.5);
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
	
	/** @return See {@link #options} */
	public ArrayList<TextOption> getOptions(){
		return this.options;
	}
	
	/** @param options See {@link #options} */
	public void setOptions(ArrayList<TextOption> options){
		if(options.size() > 1){
			this.options = options;
			this.updateRedraw(true);
			return;
		}
		
		var oldText = this.getText();
		this.options = options;
		var newText = this.getText();
		this.updateRedraw(!Objects.equals(oldText, newText));
	}
	
	@Override
	public boolean regenerateBuffer(int width, int height){
		var t = this.getText();
		if(this.getWidth() == width && this.getHeight() == height && (t == null || t.isBlank())) return false;
		return super.regenerateBuffer(width, height);
	}
}
