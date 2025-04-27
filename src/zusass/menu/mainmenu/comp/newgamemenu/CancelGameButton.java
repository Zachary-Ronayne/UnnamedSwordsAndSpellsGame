package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.menu.comp.ZusassButton;

/** A {@link ZusassButton} that cancels creating a new game */
public class CancelGameButton extends ZusassButton{
	
	/**
	 * Initialize the {@link CancelGameButton}
	 */
	public CancelGameButton(){
		super(0, 550, 200, 50, "Cancel");
	}
	
	@Override
	public void click(){
		Game.get().getCurrentState().removeTopMenu();
	}
	
}
