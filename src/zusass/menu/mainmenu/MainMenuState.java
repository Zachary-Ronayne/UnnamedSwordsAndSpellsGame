package zusass.menu.mainmenu;

import zgame.core.Game;
import zgame.core.state.MenuState;
import zusass.ZusassGame;

/** The {@link MenuState} for the main menu of the game, i.e. the first thing the player sees */
public class MainMenuState extends MenuState{
	
	/** Initialize the main menu */
	public MainMenuState(ZusassGame zgame){
		super(zgame, new MainMenu(zgame));
		this.setUseCamera(false);
	}
	
	@Override
	public void onSet(Game game){
		super.onSet(game);
		game.unloadGame();
	}
}
