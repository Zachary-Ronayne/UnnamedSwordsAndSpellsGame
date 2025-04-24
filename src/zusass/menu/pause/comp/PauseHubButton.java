package zusass.menu.pause.comp;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;

/** The pause button that returns the player to the hub */
public class PauseHubButton extends PauseMenuButton{
	
	/**
	 * Make a new button
	 *
	 * @param menu The menu which uses this button
	 */
	public PauseHubButton(PauseMenu menu){
		super(0, 395, "Hub", menu);
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		zgame.getPlayState().enterHub(zgame);
		this.getMenu().exitMenu(zgame);
	}
	
}
