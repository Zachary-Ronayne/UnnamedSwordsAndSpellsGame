package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** A {@link ZusassButton} that cancels creating a new game */
public class CancelGameButton extends ZusassButton{
	
	/**
	 * Initialize the {@link CancelGameButton}
	 * 
	 * @param zgame The Zusass game used by this thing
	 */
	public CancelGameButton(ZusassGame zgame){
		super(0, 550, 200, 50, "Cancel", zgame);
	}
	
	@Override
	public void click(Game game){
		game.getCurrentState().removeTopMenu(game);
	}
	
}
