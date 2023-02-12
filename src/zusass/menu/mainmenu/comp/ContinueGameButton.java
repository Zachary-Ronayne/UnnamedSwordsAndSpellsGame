package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZusassGame;
import zusass.game.MainPlay;
import zusass.utils.ZusassConfig;

/** The {@link MenuButton} in the main menu for continuing the last opened game */
public class ContinueGameButton extends MainMenuButton{
	
	/** Create the {@link ContinueGameButton} */
	public ContinueGameButton(ZusassGame zgame){
		super(50, 50, "Continue", zgame);
		this.setFill(new ZColor(.5));
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		boolean success = game.loadGame(ZusassConfig.getMostRecentSave());
		if(!success) return;
		MainPlay play = new MainPlay(zgame);
		game.setCurrentState(play);
	}
}
