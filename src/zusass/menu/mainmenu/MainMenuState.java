package zusass.menu.mainmenu;

import zgame.core.state.MenuState;
import zusass.ZusassGame;

/** The {@link MenuState} for the main menu of the game, i.e. the first thing the player sees */
public class MainMenuState extends MenuState{
	
	/** Initialize the main menu */
	public MainMenuState(){
		super(new MainMenu());
		this.setUseCamera(false);
	}
	
	@Override
	public void onSet(){
		super.onSet();
		ZusassGame.get().unloadGame();
	}
}
