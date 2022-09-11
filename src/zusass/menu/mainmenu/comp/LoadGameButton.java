package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zusass.ZusassData;
import zusass.menu.savesmenu.SavesMenuState;

/** A Button used to load a saved game, i.e. open the saves menu */
public class LoadGameButton extends MainMenuButton{

	public LoadGameButton(Game<ZusassData> game){
		super(50, 200, "Load Game", game);
		this.setFill(new ZColor(.45));
	}

	@Override
	public void click(Game<ZusassData> game){
		game.setCurrentState(new SavesMenuState(game));
	}
	
}
