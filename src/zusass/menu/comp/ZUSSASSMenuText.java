package zusass.menu.comp;

import zgame.core.Game;
import zgame.menu.MenuText;
import zusass.ZUSASSData;

/** A {@link MenuText} used by the ZUSASS game */
public class ZUSSASSMenuText extends MenuText<ZUSASSData>{
	
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
	public ZUSSASSMenuText(double x, double y, double w, double h, String text, Game<ZUSASSData> game){
		super(x, y, w, h, text);
		ZUSASSStyle.applyStyle(game, this);
	}
	
}
