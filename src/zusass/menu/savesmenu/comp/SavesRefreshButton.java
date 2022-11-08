package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zusass.menu.savesmenu.SavesMenu;

/** A {@link SavesMenuButton} for refreshing the loaded save files of a {@link SavesMenu} */
public class SavesRefreshButton extends SavesMenuButton{
	
	/** Create the {@link SavesBackButton}
	 * 
	 * @param menu See {@link #getMenu()}
	 * @param game The {@link Game} associated with this button
	 */
	public SavesRefreshButton(SavesMenu menu, Game game){
		super(20, 550, "Refresh", menu, game);
	}
	
	@Override
	public void click(Game game){
		this.getMenu().getLoadButtons().populate(game);
	}
	
}
