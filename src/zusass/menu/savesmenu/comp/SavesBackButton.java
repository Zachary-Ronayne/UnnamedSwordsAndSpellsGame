package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.mainmenu.MainMenuState;
import zusass.menu.savesmenu.SavesMenu;

/** The {@link SavesMenuButton} that goes back to the main menu */
public class SavesBackButton extends SavesMenuButton{
	
	/**
	 * Create the {@link SavesBackButton}
	 *
	 * @param menu See {@link #getMenu()}
	 * @param zgame The {@link Game} associated with this button
	 */
	public SavesBackButton(SavesMenu menu, ZusassGame zgame){
		super(20, 600, "Back", menu, zgame);
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		game.setCurrentState(new MainMenuState(zgame));
	}
	
}
