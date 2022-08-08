package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zusass.ZUSASSData;
import zusass.menu.savesmenu.SavesMenu;

/** A button to load the selected save file in the saves menu */
public class SavesLoadButton extends SavesMenuButton{

	/** Create the {@link SavesLoadButton}
	 * 
	 * @param menu See {@link #getMenu()}
	 * @param game The {@link Game} associated with this button
	 */
	public SavesLoadButton(SavesMenu menu, Game<ZUSASSData> game){
		super(205, 600, "Load", menu, game);
	}

	@Override
	public void click(Game<ZUSASSData> game){
		LoadSaveButtonList list = this.getMenu().getLoadButtons();
		LoadSaveButton button = list.getSelected();
		if(button == null) return;
		button.attemptLoad(game);
	}
	
}
