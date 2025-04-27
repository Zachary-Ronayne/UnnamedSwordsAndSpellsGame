package zusass.menu.pause.comp;

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
	public void click(){
		ZusassGame.get().getPlayState().enterHub();
		this.getMenu().exitMenu();
	}
	
}
