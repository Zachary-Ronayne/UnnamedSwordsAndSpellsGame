package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zusass.ZUSASSData;
import zusass.menu.comp.ZUSASSButton;
import zusass.menu.savesmenu.SavesMenu;

/** A {@link ZUSASSButton} for navigating the {@link SavesMenu} */
public class SavesMenuButton extends ZUSASSButton{

	// The {@link SavesMenu} associated with this button
	private SavesMenu menu;
	
	/**
	 * Create a main menu button with the appropriate parameters
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param text The text to display
	 * @param game The {@link Game} that uses this button
	 */
	public SavesMenuButton(double x, double y, String text, SavesMenu menu, Game<ZUSASSData> game){
		super(x, y, 150, 40, text, game);
		this.menu = menu;
		this.setFontSize(30);
		this.setTextY(35);
		this.setFill(new ZColor(.6));
		this.setBorder(new ZColor(0.8));
		this.setBorderWidth(1);
	}

	/** @return See {@link #menu} */
	public SavesMenu getMenu(){
		return this.menu;
	}
	
}
