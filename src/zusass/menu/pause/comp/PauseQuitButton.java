package zusass.menu.pause.comp;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;

/** The pause menu button that exists out of the application */
public class PauseQuitButton extends PauseMenuButton{
	
	/**
	 * Make a new button
	 *
	 * @param zgame The game which uses the button
	 */
	public PauseQuitButton(PauseMenu menu, ZusassGame zgame){
		super(0, 285, "Close Game", menu, zgame);
	}
	
	@Override
	public void click(Game game){
		game.stop();
	}
	
}
