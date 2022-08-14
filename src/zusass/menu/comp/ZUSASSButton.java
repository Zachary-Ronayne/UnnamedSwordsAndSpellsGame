package zusass.menu.comp;

import zgame.core.Game;
import zgame.menu.MenuButton;
import zusass.ZUSASSData;

/** A {@link MenuButton} which is used by the ZUSASSGame */
public abstract class ZUSASSButton extends MenuButton<ZUSASSData>{
	
	/**
	 * Create a {@link ZUSASSButton} with the appropriate parameters
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 * @param game The {@link Game} that uses this button
	 */
	public ZUSASSButton(double x, double y, double w, double h, String text, Game<ZUSASSData> game){
		super(x, y, w, h, text);
		ZUSASSStyle.applyStyle(game, this);
	}
	
}