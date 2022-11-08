package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zusass.ZusassGame;
import zusass.menu.savesmenu.SavesMenuState;

/** A Button used to load a saved game, i.e. open the saves menu */
public class LoadGameButton extends MainMenuButton{

	public LoadGameButton(ZusassGame zgame){
		super(50, 200, "Load Game", zgame);
		this.setFill(new ZColor(.45));
	}

	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		game.setCurrentState(new SavesMenuState(zgame));
	}
	
}
