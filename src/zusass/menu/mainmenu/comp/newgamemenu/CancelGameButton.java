package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.ZUSASSData;
import zusass.menu.comp.ZUSASSButton;

/** A {@link ZUSASSButton} that cancels creating a new game */
public class CancelGameButton extends ZUSASSButton{
	
	/**
	 * Initialize the {@link CancelGameButton}
	 * 
	 * @param game The ZUSASSGame used by this thing
	 */
	public CancelGameButton(Game<ZUSASSData> game){
		super(720, 460, 200, 50, "Cancel", game);
	}

	@Override
	public void click(Game<ZUSASSData> game){
		game.getCurrentState().removeTopMenu();
	}
	
}
