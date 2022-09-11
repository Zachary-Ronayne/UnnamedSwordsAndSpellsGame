package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.ZusassData;
import zusass.menu.comp.ZusassButton;

/** A {@link ZusassButton} that cancels creating a new game */
public class CancelGameButton extends ZusassButton{
	
	/**
	 * Initialize the {@link CancelGameButton}
	 * 
	 * @param game The ZUSASSGame used by this thing
	 */
	public CancelGameButton(Game<ZusassData> game){
		super(720, 460, 200, 50, "Cancel", game);
	}

	@Override
	public void click(Game<ZusassData> game){
		game.getCurrentState().removeTopMenu();
	}
	
}
