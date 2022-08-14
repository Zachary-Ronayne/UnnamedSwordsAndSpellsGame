package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zusass.ZUSASSData;
import zusass.menu.comp.ZUSASSButton;

/** A class used to define similarities between buttons for the main menu */
public abstract class MainMenuButton extends ZUSASSButton{

	/**
	 * Create a main menu button with the appropriate parameters
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param text The text to display
	 * @param game The {@link Game} that uses this button
	 */
	public MainMenuButton(double x, double y, String text, Game<ZUSASSData> game){
		super(x, y, 500, 120, text, game);
		this.setFontSize(40);
	}
	
}
