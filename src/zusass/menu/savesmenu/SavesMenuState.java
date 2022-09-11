package zusass.menu.savesmenu;

import zgame.core.Game;
import zgame.core.state.MenuState;
import zusass.ZusassData;

/** The {@link MenuState} for the menu that handles managing save files */
public class SavesMenuState extends MenuState<ZusassData>{
	
	/** Initialize the menu */
	public SavesMenuState(Game<ZusassData> game){
		super(new SavesMenu(game));
		this.setUseCamera(false);
	}
}
