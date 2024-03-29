package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.savesmenu.SavesMenu;

/** A button to load the selected save file in the saves menu */
public class SavesLoadButton extends SavesMenuButton{
	
	/**
	 * Create the {@link SavesLoadButton}
	 *
	 * @param menu See {@link #getMenu()}
	 * @param zgame The {@link Game} associated with this button
	 */
	public SavesLoadButton(SavesMenu menu, ZusassGame zgame){
		super(205, 600, "Load", menu, zgame);
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		LoadSaveButtonList list = this.getMenu().getLoadButtons();
		LoadSaveButton button = list.getSelected();
		if(button == null) return;
		button.attemptLoad(zgame);
	}
	
}
