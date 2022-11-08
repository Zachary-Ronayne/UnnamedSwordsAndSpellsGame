package zusass.menu.savesmenu;

import zgame.core.Game;
import zgame.core.state.MenuState;

/** The {@link MenuState} for the menu that handles managing save files */
public class SavesMenuState extends MenuState{
	
	/** Initialize the menu */
	public SavesMenuState(Game game){
		super(new SavesMenu(game));
		this.setUseCamera(false);
	}
}
