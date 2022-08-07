package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zusass.ZUSASSData;
import zusass.menu.comp.ZUSASSButton;
import zusass.menu.savesmenu.SavesMenu;

/** A {@link ZUSASSButton} for navigating the {@link SavesMenu} */
public class SavesMenuButton extends ZUSASSButton{
	
	/**
	 * Create a main menu button with the appropriate parameters
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param text The text to display
	 * @param game The {@link Game} that uses this button
	 */
	public SavesMenuButton(double x, double y, String text, Game<ZUSASSData> game){
		super(x, y, 300, 60, text, game);
		this.setFontSize(40);
		this.setTextY(40);
		this.setFill(new ZColor(.6));
	}
	
}
