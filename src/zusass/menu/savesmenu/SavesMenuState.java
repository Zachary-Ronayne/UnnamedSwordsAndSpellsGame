package zusass.menu.savesmenu;

import zgame.core.state.MenuState;
import zusass.ZusassGame;

/** The {@link MenuState} for the menu that handles managing save files */
public class SavesMenuState extends MenuState{
	
	/** Initialize the menu */
	public SavesMenuState(ZusassGame zgame){
		super(zgame, new SavesMenu(zgame));
		this.setUseCamera(false);
	}
}
