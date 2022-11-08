package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zgame.menu.MenuTextBox;
import zusass.menu.comp.ZusassStyle;

/** A {@link MenuTextBox} used by the Zusass game */
public class ZusassTextBox extends MenuTextBox{
	
	/**
	 * Create a new {@link ZusassTextBox} with the given values
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public ZusassTextBox(double x, double y, double w, double h, Game game){
		super(x, y, w, h, game);
		ZusassStyle.applyStyle(game, this);
	}
	
}
