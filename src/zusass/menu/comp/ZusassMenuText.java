package zusass.menu.comp;

import zgame.core.Game;
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
	 * @param game The game associated with this text
	 */
	public ZusassMenuText(double x, double y, double w, double h, String text, Game game){
		super(x, y, w, h, text, game);
		ZusassStyle.applyStyle(game, this);
	}
	
}
