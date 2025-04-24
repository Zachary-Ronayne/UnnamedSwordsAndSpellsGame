package zusass.menu.pause.comp;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;

/** The pause menu button for returning to the game */
public class PauseReturnButton extends PauseMenuButton{
	
	/**
	 * Make a new button
	 *
	 * @param menu The menu which uses this button
	 */
	public PauseReturnButton(PauseMenu menu){
		super(0, 175, "Return", menu);
	}
	
	@Override
	public void click(Game game){
		getMenu().exitMenu((ZusassGame)game);
	}
	
}
