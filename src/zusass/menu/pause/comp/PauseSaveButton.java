package zusass.menu.pause.comp;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;

/** The pause menu button that saves the current progress of the game */
public class PauseSaveButton extends PauseMenuButton{
	
	/**
	 * Make a new button
	 *
	 * @param menu The menu which uses this button
	 */
	public PauseSaveButton(PauseMenu menu){
		super(0, 340, "Save", menu);
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		this.getMenu().save(zgame);
		this.getMenu().exitMenu(zgame);
	}
	
}
