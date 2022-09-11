package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZusassData;
import zusass.menu.mainmenu.comp.newgamemenu.NewGamePopup;

/** The {@link MenuButton} in the main menu for creating a new game */
public class NewGameButton extends MainMenuButton{
	
	/**
	 * Create the {@link NewGameButton}
	 * 
	 * @param game The Zusass game associated with this button
	 * @param state see {@link #state}
	 */
	public NewGameButton(Game<ZusassData> game){
		super(50, 350, "New Game", game);
		this.setFill(new ZColor(.4));
	}
	
	@Override
	public void click(Game<ZusassData> game){
		game.getCurrentState().popupMenu(new NewGamePopup(this, game));
	}
	
}
