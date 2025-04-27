package zusass.menu.pause.comp;

import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;

/** The pause menu button that goes to the main menu */
public class PauseMainMenuButton extends PauseMenuButton{
	
	/**
	 * Make a new button
	 *
	 * @param menu The menu which uses this button
	 */
	public PauseMainMenuButton(PauseMenu menu){
		super(0, 230, "Main Menu", menu);
	}
	
	@Override
	public void click(){
		ZusassGame.get().getPlayState().enterMainMenu();
	}
	
}
