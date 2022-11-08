package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zusass.menu.mainmenu.MainMenuState;
import zusass.menu.savesmenu.SavesMenu;

/** The {@link SavesMenuButton} that goes back to the main menu */
public class SavesBackButton extends SavesMenuButton{
	
	/** Create the {@link SavesBackButton}
	 * 
	 * @param menu See {@link #getMenu()}
	 * @param game The {@link Game} associated with this button
	 */
	public SavesBackButton(SavesMenu menu, Game game){
		super(20, 600, "Back", menu, game);
	}
	
	@Override
	public void click(Game game){
		game.setCurrentState(new MainMenuState(game));
	}
	
}
