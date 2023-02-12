package zusass.menu.pause.comp;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;

/** The pause menu button that goes to the main menu */
public class PauseMainMenuButton extends PauseMenuButton{
	
	/**
	 * Make a new button
	 *
	 * @param zgame The game which uses the button
	 */
	public PauseMainMenuButton(PauseMenu menu, ZusassGame zgame){
		super(0, 230, "Main Menu", menu, zgame);
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		zgame.getPlayState().enterMainMenu(zgame);
	}
	
}
