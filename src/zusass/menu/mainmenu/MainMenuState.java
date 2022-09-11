package zusass.menu.mainmenu;

import zgame.core.Game;
import zgame.core.state.MenuState;
import zusass.ZusassData;

/** The {@link MenuState} for the main menu of the game, i.e. the first thing the player sees */
public class MainMenuState extends MenuState<ZusassData>{
	
	/** Initialize the main menu */
	public MainMenuState(Game<ZusassData> game){
		super(new MainMenu(game));
		this.setUseCamera(false);
	}
	
}
