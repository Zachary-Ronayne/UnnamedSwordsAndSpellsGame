package zusass.menu.savesmenu.comp;

import zusass.ZusassGame;
import zusass.menu.mainmenu.MainMenuState;
import zusass.menu.savesmenu.SavesMenu;

/** The {@link SavesMenuButton} that goes back to the main menu */
public class SavesBackButton extends SavesMenuButton{
	
	/**
	 * Create the {@link SavesBackButton}
	 *
	 * @param menu See {@link #getMenu()}
	 */
	public SavesBackButton(SavesMenu menu){
		super(20, 600, "Back", menu);
	}
	
	@Override
	public void click(){
		ZusassGame.get().setCurrentState(new MainMenuState());
	}
	
}
