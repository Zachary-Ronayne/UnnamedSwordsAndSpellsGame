package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.menu.comp.ZusassButton;

/** A {@link ZusassButton} that cancels creating a new game */
public class CancelGameButton extends ZusassButton{
	
	/**
	 * Initialize the {@link CancelGameButton}
	 * 
	 * @param game The Zusass game used by this thing
	 */
	public CancelGameButton(Game game){
		super(720, 460, 200, 50, "Cancel", game);
	}
	
	@Override
	public void click(Game game){
		game.getCurrentState().removeTopMenu();
	}
	
}
