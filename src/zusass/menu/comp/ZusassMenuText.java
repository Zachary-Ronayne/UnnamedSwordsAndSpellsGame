package zusass.menu.comp;

import zgame.core.graphics.ZColor;
import zgame.menu.MenuText;

/** A {@link MenuText} used by the Zusass game */
public class ZusassMenuText extends MenuText{
	
	/**
	 * Create a {@link MenuText} at the given position and size
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 */
	public ZusassMenuText(double x, double y, double w, double h, String text){
		this(x, y, w, h, text, false);
	}
	
	/**
	 * Create a {@link MenuText} at the given position and size
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 * @param textOnly true if only the text should display, false for a background and border
	 */
	public ZusassMenuText(double x, double y, double w, double h, String text, boolean textOnly){
		super(x, y, w, h, text);
		ZusassStyle.applyStyleText(this);
		if(textOnly){
			this.setBorder(new ZColor(0, 0, 0, 0));
			this.setFill(new ZColor(0, 0, 0, 0));
		}
	}
	
}
