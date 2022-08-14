package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zgame.menu.MenuTextBox;
import zusass.ZUSASSData;
import zusass.menu.comp.ZUSASSStyle;

/** A {@link MenuTextBox} used by the ZUSSASS game */
public class ZUSASSTextBox extends MenuTextBox<ZUSASSData>{
	
	/**
	 * Create a new {@link ZUSASSTextBox} with the given values
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public ZUSASSTextBox(double x, double y, double w, double h, Game<ZUSASSData> game){
		super(x, y, w, h, game);
		ZUSASSStyle.applyStyle(game, this);
	}
	
}
