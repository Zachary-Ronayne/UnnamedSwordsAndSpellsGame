package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZusassGame;
import zusass.menu.mainmenu.comp.newgamemenu.NewGamePopup;

/** The {@link MenuButton} in the main menu for creating a new game */
public class NewGameButton extends MainMenuButton{
	
	/**
	 * Create the {@link NewGameButton}
	 *
	 * @param zgame The Zusass game associated with this button
	 */
	public NewGameButton(ZusassGame zgame){
		super(50, 350, "New Game", zgame);
		this.setFill(new ZColor(.4));
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		game.getCurrentState().popupMenu(new NewGamePopup(this, zgame));
	}
	
}
