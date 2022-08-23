package zgame.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.GameFont;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.utils.ZRect;

/**
 * A {@link MenuThing} that holds text
 * Note, for this class and anything that extends it, calls to updating the width and height will not update the text buffer's width and height beyond what was given to the
 * constructor. Must call {@link #getBuffer()} and call {@link TextBuffer#regenerateBuffer(int, int)} on it to resize where the text is drawn. It is not recommended to call this
 * method frequently, as it is a very expensive operation
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public class MenuText<D>extends MenuThing<D>{
	
	/** The {@link TextBuffer} which this {@link MenuText} will use to draw text */
	private TextBuffer buffer;
	
	/** The {@link ZColor} to use to color {@link #text} */
	private ZColor fontColor;
	
	/**
	 * Create a blank {@link MenuText} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public MenuText(double x, double y, double w, double h, Game<D> game){
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
	public MenuText(double x, double y, double w, double h, String text, Game<D> game){
		super(x, y, w, h);
		// Using zfont by default
		this.buffer = new TextBuffer(w, h, game.getFont("zfont"));
		this.buffer.setText(text);
		this.buffer.setTextX(10);
		this.buffer.setTextY(this.getHeight() * .9);
		
		this.setFill(this.getFill().solid());
		
		this.fontColor = new ZColor(0);
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.buffer.destroy();
	}
	
	/** @return See {@link #text} */
	public String getText(){
		return this.getBuffer().getText();
	}
	
	/** @param text See {@link #text} */
	public void setText(String text){
		this.getBuffer().setText(text);
	}
	
	/** @return See {@link #textX} */
	public double getTextX(){
		return this.getBuffer().getTextX();
	}
	
	/** @param textX See {@link #textX} */
	public void setTextX(double textX){
		this.getBuffer().setTextX(textX);
	}
	
	/** @return See {@link #textY} */
	public double getTextY(){
		return this.getBuffer().getTextY();
	}
	
	/** @param textY See {@link #textY} */
	public void setTextY(double textY){
		this.getBuffer().setTextY(textY);
	}
	
	/** @return See {@link #font} */
	public GameFont getFont(){
		return this.getBuffer().getFont();
	}
	
	/** @param font See {@link #font} */
	public void setFont(GameFont font){
		this.getBuffer().setFont(font);
	}
	
	/** @return See {@link #buffer} */
	public TextBuffer getBuffer(){
		return this.buffer;
	}
	
	/** @return See {@link #fontColor} */
	public ZColor getFontColor(){
		return this.fontColor;
	}
	
	/** @param fontColor See {@link #fontColor} */
	public void setFontColor(ZColor fontColor){
		this.fontColor = fontColor;
	}
	
	/** @return See {@link #fontSize} */
	public double getFontSize(){
		return this.getFont().getSize();
	}
	
	/** @param fontSize See {@link #fontSize} */
	public void setFontSize(double fontSize){
		this.setFont(this.getFont().size(fontSize));
	}
	
	/** Move the text of this {@link MenuText} so that it's in the center of it's bounds */
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
	public void render(Game<D> game, Renderer r){
		super.render(game, r);
		
		if(this.getFont() != null) r.setFont(this.getFont());
		r.setColor(this.getFontColor());
		r.setFontSize(this.getFontSize());
		
		this.drawText(r, this.getText());
	}
	
	/**
	 * Draw the given text on this menu at it's intended location
	 * 
	 * @param r The Renderer to use to draw the text
	 * @param text The text to draw
	 */
	public void drawText(Renderer r, String text){
		// TODO make this work properly with text boxes when there's a text hint. Store text separately
		this.buffer.setText(text);
		this.buffer.draw(this.getX(), this.getY(), r);
	}
	
}
