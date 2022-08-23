package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZUSASSData;
import zusass.game.MainPlay;
import zusass.utils.ZUSASSConfig;

/** The {@link MenuButton} in the main menu for continuing the last opened game */
public class ContinueGameButton extends MainMenuButton{
	
	/** Create the {@link ContinueGameButton} */
	public ContinueGameButton(Game<ZUSASSData> game){
		super(50, 50, "Continue", game);
		this.setFill(new ZColor(.5));
	}
	
	@Override
	public void click(Game<ZUSASSData> game){
		boolean success = game.loadGame(ZUSASSConfig.getMostRecentSave());
		if(!success) return;
		MainPlay play = new MainPlay(game);
		play.enterHub(game);
		game.setCurrentState(play);
	}
}
