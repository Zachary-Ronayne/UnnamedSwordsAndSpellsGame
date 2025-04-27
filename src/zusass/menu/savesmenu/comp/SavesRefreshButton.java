package zusass.menu.savesmenu.comp;

import zusass.menu.savesmenu.SavesMenu;

/** A {@link SavesMenuButton} for refreshing the loaded save files of a {@link SavesMenu} */
public class SavesRefreshButton extends SavesMenuButton{
	
	/**
	 * Create the {@link SavesBackButton}
	 *
	 * @param menu See {@link #getMenu()}
	 */
	public SavesRefreshButton(SavesMenu menu){
		super(20, 550, "Refresh", menu);
	}
	
	@Override
	public void click(){
		this.getMenu().getLoadButtons().populate();
	}
	
}
