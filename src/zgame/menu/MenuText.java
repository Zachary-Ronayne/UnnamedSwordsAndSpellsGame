package zgame.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.GameFont;

/** A {@link MenuThing} that holds text */
public class MenuText extends MenuThing{
	
	/** The text held by this {@link MenuText} */
	private String text;
	
	/** The {@link GameFont} to use when drawing {@link #text}. If this value is null, whatever font is in the {@link Renderer} will be used */
	private GameFont font;

	/** The {@link ZColor} to use to color {@link #text} */
	private ZColor fontColor;

	/** The size of {@link #text} when it is drawn on a button */
	private double fontSize;

	/**
	 * Create a blank {@link MenuText} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public MenuText(double x, double y, double w, double h){
		this(x, y, w, h, "");
	}
	
	/**
	 * Create a {@link MenuText} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 */
	public MenuText(double x, double y, double w, double h, String text){
		super(x, y, w, h);
		this.text = text;
		this.setFill(this.getFill().solid());

		this.font = null;
		this.fontColor = new ZColor(0);
		this.fontSize = 32;
	}
	
	/** @return See {@link #text} */
	public String getText(){
		return this.text;
	}
	
	/** @param text See {@link #text} */
	public void setText(String text){
		this.text = text;
	}

	/** @return See {@link #font} */
	public GameFont getFont(){
		return this.font;
	}
	
	/** @param font See {@link #font} */
	public void setFont(GameFont font){
		this.font = font;
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
		return this.fontSize;
	}
	
	/** @param fontSize See {@link #fontSize} */
	public void setFontSize(double fontSize){
		this.fontSize = fontSize;
	}

	@Override
	public void render(Game game, Renderer r){
		super.render(game, r);
		// TODO add text positioning to this
		if(this.getFont() != null) r.setFont(this.getFont());
		r.setColor(this.getFontColor());
		r.setFontSize(this.getFontSize());
		r.drawText(this.getX() + 5, this.getY() + this.getHeight() - 10, this.getText());
	}
	
}
