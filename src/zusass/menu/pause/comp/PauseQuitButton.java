package zusass.menu.pause.comp;

import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;

/** The pause menu button that exists out of the application */
public class PauseQuitButton extends PauseMenuButton{
	
	/**
	 * Make a new button
	 *
	 * @param menu The menu which uses this button
	 */
	public PauseQuitButton(PauseMenu menu){
		super(0, 285, "Close Game", menu);
	}
	
	@Override
	public void click(){
		ZusassGame.get().stop();
	}
	
}
