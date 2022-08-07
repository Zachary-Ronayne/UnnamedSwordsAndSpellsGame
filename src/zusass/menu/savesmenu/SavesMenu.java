package zusass.menu.savesmenu;

import zgame.core.Game;
import zusass.ZUSASSData;
import zusass.menu.ZUSASSMenu;
import zusass.menu.savesmenu.comp.SavesBackButton;

/** A {@link ZUSASSMenu} for managing game saves */
public class SavesMenu extends ZUSASSMenu{
	
	/**
	 * Create a new blank {@link SavesMenu}
	 * @param game The game that uses this menu
	 */
	public SavesMenu(Game<ZUSASSData> game){
		super("Saves");
		this.addThing(new SavesBackButton(game));
		this.setTitleX(50);
	}
	
}

// TODO add a system to have menus display on top of others, have the bottom still render, but only the top one tick and take input
