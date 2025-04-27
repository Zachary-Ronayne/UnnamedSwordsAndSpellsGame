package zusass.menu.savesmenu.comp;

import zusass.menu.savesmenu.SavesMenu;

/** A button to load the selected save file in the saves menu */
public class SavesLoadButton extends SavesMenuButton{
	
	/**
	 * Create the {@link SavesLoadButton}
	 *
	 * @param menu See {@link #getMenu()}
	 */
	public SavesLoadButton(SavesMenu menu){
		super(205, 600, "Load", menu);
	}
	
	@Override
	public void click(){
		LoadSaveButtonList list = this.getMenu().getLoadButtons();
		LoadSaveButton button = list.getSelected();
		if(button == null) return;
		button.attemptLoad();
	}
	
}
