package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZusassData;
import zusass.game.MainPlay;
import zusass.utils.ZusassConfig;

/** The {@link MenuButton} in the main menu for continuing the last opened game */
public class ContinueGameButton extends MainMenuButton{
	
	/** Create the {@link ContinueGameButton} */
	public ContinueGameButton(Game<ZusassData> game){
		super(50, 50, "Continue", game);
		this.setFill(new ZColor(.5));
	}
	
	@Override
	public void click(Game<ZusassData> game){
		boolean success = game.loadGame(ZusassConfig.getMostRecentSave());
		if(!success) return;
		MainPlay play = new MainPlay(game);
		play.enterHub(game);
		game.setCurrentState(play);
	}
}
