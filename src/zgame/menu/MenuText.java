package zgame.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.GameFont;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.utils.ZRect;

import java.util.Objects;

/**
 * A {@link MenuThing} that holds text Note, for this class and anything that extends it, calls to updating the width and height will not update the text buffer's width and
 * height beyond what was given to the constructor. Must call {@link #getTextBuffer()} and call {@link TextBuffer#regenerateBuffer(int, int)} on it to resize where the text is
 * drawn. It is not recommended to call this method frequently, as it is a very expensive operation
 */
public class MenuText extends MenuThing{
	
	/** The {@link TextBuffer} which this {@link MenuText} will use to draw text */
	private final TextBuffer textBuffer;
	
	/** The {@link ZColor} to use to color {@link #text} */
	private ZColor fontColor;
	
	/** The text to display for this menu */
	private String text;
	
	/** When rendering text, force the bounds to be limited to the intersection of bounds of this thing and the current limited bounds, false otherwise */
	private boolean limitIntersectionBounds;
	
	/**
	 * Create a blank {@link MenuText} at the given position and size
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public MenuText(double x, double y, double w, double h, Game game){
		this(x, y, w, h, "", game);
	}
	
	/**
	 * Create a {@link MenuText} at the given position and size
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 * @param game The game associated with this thing
	 */
	public MenuText(double x, double y, double w, double h, String text, Game game){
		super(x, y, w, h);
		this.text = text;
		// Using zfont by default
		this.textBuffer = new TextBuffer((int)Math.round(w), (int)Math.round(h), game.getFont("zfont"));
		this.setTextX(10);
		this.setTextY(this.getHeight() * .9);
		this.textBuffer.setForceUnlimit(false);

		this.setFill(this.getFill().solid());
		
		this.fontColor = new ZColor(0);
		
		this.setLimitIntersectionBounds(true);
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.textBuffer.destroy();
	}
	
	@Override
	public void regenerateBuffer(){
		super.regenerateBuffer();
		this.regenerateTextBuffer();
	}
	
	/** Regenerate {@link #textBuffer} to the size of this thing, or override for custom behavior */
	public void regenerateTextBuffer(){
		this.textBuffer.regenerateBuffer((int)Math.round(this.getWidth()), (int)Math.round(this.getHeight()));
	}
	
	/** @return true if this text box contains no text, false otherwise */
	public boolean isEmpty(){
		return this.text == null || this.text.isEmpty();
	}
	
	/** @return See {@link #text} */
	public String getText(){
		return this.text;
	}
	
	/** @return The value of {@link #text} assuming it's an integer. If it cannot be parsed as an integer, returns null */
	public Integer getTextAsInt(){
		try{
			return Integer.parseInt(this.getText());
		}catch(NumberFormatException e){
			return null;
		}
	}
	
	/** @return The value of {@link #text} assuming it's a double. If it cannot be parsed as a double, returns null */
	public Double getTextAsDouble(){
		try{
			return Double.parseDouble(this.getText());
		}catch(NumberFormatException e){
			return null;
		}
	}
	
	/** @param text See {@link #text} */
	public void setText(String text){
		if(Objects.equals(text, this.text)) return;
		this.text = text;
		this.getTextBuffer().updateRedraw(true);
	}
	
	/** @return See {@link TextBuffer#textX} */
	public double getTextX(){
		return this.textBuffer.getTextX();
	}
	
	/** @param textX See {@link TextBuffer#textX} */
	public void setTextX(double textX){
		this.textBuffer.setTextX(textX);
	}
	
	/** @return See {@link TextBuffer#textY} */
	public double getTextY(){
		return this.textBuffer.getTextY();
	}
	
	/** @param textY See {@link TextBuffer#textY} */
	public void setTextY(double textY){
		this.textBuffer.setTextY(textY);
	}
	
	/** @return See {@link TextBuffer#font} */
	public GameFont getFont(){
		return this.getTextBuffer().getFont();
	}
	
	/** @param font See {@link TextBuffer#font} */
	public void setFont(GameFont font){
		this.getTextBuffer().setFont(font);
	}
	
	/** @return See {@link #textBuffer} */
	public TextBuffer getTextBuffer(){
		return this.textBuffer;
	}
	
	/** @return See {@link #fontColor} */
	public ZColor getFontColor(){
		return this.fontColor;
	}
	
	/** @param fontColor See {@link #fontColor} */
	public void setFontColor(ZColor fontColor){
		this.fontColor = fontColor;
	}
	
	/** @return See {@link GameFont#size} */
	public double getFontSize(){
		return this.getFont().getSize();
	}
	
	/** @param fontSize See {@link GameFont#size} */
	public void setFontSize(double fontSize){
		this.setFont(this.getFont().size(fontSize));
	}
	
	/** @return See {@link #limitIntersectionBounds} */
	public boolean isLimitIntersectionBounds(){
		return this.limitIntersectionBounds;
	}
	
	/** @param limitIntersectionBounds See {@link #limitIntersectionBounds} */
	public void setLimitIntersectionBounds(boolean limitIntersectionBounds){
		this.limitIntersectionBounds = limitIntersectionBounds;
	}
	
	/** Move the text of this {@link MenuText} so that it's in the center of its bounds */
	public void centerText(){
		ZRect b = this.getTextBounds();
		this.centerTextHorizontal(b.width);
		this.centerTextVertical(b.height);
	}
	
	/** Move the text of this {@link MenuText} so that it's in the center of it's left and right bounds */
	public void centerTextHorizontal(){
		this.centerTextHorizontal(this.getTextBounds().width);
	}
	
	/**
	 * Move the text of this {@link MenuText} so that it's in the center of it's left and right bounds
	 *
	 * @param width The width of the text
	 */
	private void centerTextHorizontal(double width){
		this.setTextX((this.getWidth() - width) * 0.5);
	}
	
	/** Move the text of this {@link MenuText} so that it's in the center of it's top and bottom bounds */
	public void centerTextVertical(){
		this.centerTextVertical(this.getTextBounds().height);
	}
	
	/**
	 * Move the text of this {@link MenuText} so that it's in the center of it's top and bottom bounds
	 *
	 * @param height The height of the text
	 */
	private void centerTextVertical(double height){
		this.setTextY((this.getHeight() + height) * 0.5);
	}
	
	public ZRect getTextBounds(){
		return this.getFont().stringBounds(this.getText());
	}
	
	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		super.render(game, r, bounds);
		
		if(this.getFont() != null) r.setFont(this.getFont());
		r.setColor(this.getFontColor());
		r.setFontSize(this.getFontSize());
		
		if(this.getText() != null && !this.getText().isEmpty()) this.drawText(r, this.getText(), bounds);
	}
	
	/**
	 * Draw the given text on this menu at its intended location
	 *
	 * @param r The Renderer to use to draw the text
	 * @param text The text to draw
	 * @param bounds The bounds of this thing as it's being drawn
	 */
	public void drawText(Renderer r, String text, ZRect bounds){
		this.textBuffer.setText(text);
		
		var b = this.getTextLimitBounds();
		var limit = b != null;
		if(limit) {
			if(this.isLimitIntersectionBounds()) r.pushLimitedBoundsIntersection(b);
			else r.pushLimitedBounds(b);
		}
		this.textBuffer.drawToRenderer(bounds.getX(), bounds.getY(), r);
		if(limit) r.popLimitedBounds();
	}
	
	/** @return The bounds, in absolute coordinates, where text can be drawn. Text outside of this will be cut off */
	public ZRect getTextLimitBounds(){
		// Must find the bounds relative to the first parent which uses a buffer
		
		// issue#30 Which of these is correct? Or does it need to be something else?
//		return this.getBoundsToBuffer();
//		return this.getRelBounds();
		return this.getBounds();
	}
	
}
