package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zgame.menu.MenuTextBox;
import zusass.ZusassData;
import zusass.menu.mainmenu.comp.NewGameButton;

/** A {@link MenuTextBox} for typing in the name of a new game */
public class NewGameTextBox extends ZusassTextBox{
	
	/**
	 * Initialize the {@link NewGameTextBox}
	 * 
	 * @param button The {@link NewGameButton} used by this menu
	 * @param game The ZUSASSGame used by this thing
	 */
	public NewGameTextBox(Game<ZusassData> game){
		super(500, 400, 470, 50, game);
		this.setSelected(true);
		this.setHint("Save name...");
	}
	
}
