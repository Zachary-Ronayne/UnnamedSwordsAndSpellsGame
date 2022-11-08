package zusass.menu.mainmenu;

import zgame.core.Game;
import zgame.core.state.MenuState;

/** The {@link MenuState} for the main menu of the game, i.e. the first thing the player sees */
public class MainMenuState extends MenuState{
	
	/** Initialize the main menu */
	public MainMenuState(Game game){
		super(new MainMenu(game));
		this.setUseCamera(false);
	}
	
}
