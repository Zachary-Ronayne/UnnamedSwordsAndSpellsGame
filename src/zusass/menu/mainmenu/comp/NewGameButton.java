package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZUSASSData;
import zusass.menu.mainmenu.comp.newgamemenu.NewGamePopup;

/** The {@link MenuButton} in the main menu for creating a new game */
public class NewGameButton extends MainMenuButton{
	
	/**
	 * Create the {@link NewGameButton}
	 * 
	 * @param game The ZUSASS game associated with this button
	 * @param state see {@link #state}
	 */
	public NewGameButton(Game<ZUSASSData> game){
		super(50, 350, "New Game", game);
		this.setFill(new ZColor(.4));
	}
	
	@Override
	public void click(Game<ZUSASSData> game){
		game.getCurrentState().popupMenu(new NewGamePopup(this, game));
	}
	
}
