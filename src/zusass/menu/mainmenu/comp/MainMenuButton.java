package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** A class used to define similarities between buttons for the main menu */
public abstract class MainMenuButton extends ZusassButton{

	/**
	 * Create a main menu button with the appropriate parameters
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param text The text to display
	 * @param zgame The {@link Game} that uses this button
	 */
	public MainMenuButton(double x, double y, String text, ZusassGame zgame){
		super(x, y, 500, 120, text, zgame);
		this.setFontSize(40);
	}
	
}
