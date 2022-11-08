package zusass.menu.comp;

import zgame.menu.MenuText;
import zusass.ZusassGame;

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
	 * @param game The game associated with this text
	 */
	public ZusassMenuText(double x, double y, double w, double h, String text, ZusassGame zgame){
		super(x, y, w, h, text, zgame);
		ZusassStyle.applyStyle(zgame, this);
	}
	
}
