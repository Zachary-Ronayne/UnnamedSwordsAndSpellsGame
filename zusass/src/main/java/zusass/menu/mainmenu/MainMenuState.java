package zusass.menu.mainmenu;

import zgame.core.state.MenuState;
import zusass.ZusassGame;
import zusass.utils.ZusassMusic;

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
		var zgame = ZusassGame.get();
		
		// Unload the currently loaded game
		zgame.unloadGame();
		
		// Update the music to the menu music
		zgame.playMusic(ZusassMusic.MENU_SONG);
	}
}
