package zusass.menu.savesmenu;

import zgame.core.Game;
import zgame.core.state.MenuState;
import zusass.ZUSASSData;

/** The {@link MenuState} for the menu that handles managing save files */
public class SavesMenuState extends MenuState<ZUSASSData>{
	
	/** Initialize the menu */
	public SavesMenuState(Game<ZUSASSData> game){
		super(new SavesMenu(game));
		this.setUseCamera(false);
	}
}
