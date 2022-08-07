package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zusass.ZUSASSData;
import zusass.menu.mainmenu.MainMenuState;

/** The {@link SavesMenuButton} that goes back to the main menu */
public class SavesBackButton extends SavesMenuButton{
	
	public SavesBackButton(Game<ZUSASSData> game){
		super(50, 600, "Back", game);
	}
	
	@Override
	public void click(Game<ZUSASSData> game){
		game.setCurrentState(new MainMenuState(game));
	}
	
}
