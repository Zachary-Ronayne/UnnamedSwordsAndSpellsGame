package zgame.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** A {@link MenuThing} that holds text */
public class MenuText extends MenuThing{

	/** The text held by this {@link MenuText} */
	private String text;

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
	}
	
	/** @return See {@link #text} */
	public String getText(){
		return this.text;
	}

	/** @param text See {@link #text} */
	public void setText(String text){
		this.text = text;
	}

	/**
	 * Draw a rectangle at the size and position of this {@link MenuText}
	 */
	@Override
	public void renderO(Game game, Renderer r){
		r.drawRectangle(getX(), getY(), getWidth(), getHeight());
	}
	
}
