package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZUSASSData;

/** The {@link MenuButton} in the main menu for continuing the last opened game */
public class ContinueGameButton extends MainMenuButton{
	
	/** Create the {@link ContinueGameButton} */
	public ContinueGameButton(Game<ZUSASSData> game){
		super(50, 250, 500, 150, "Continue", game);
		this.setFill(new ZColor(.5));
	}
	
	@Override
	public void click(Game<ZUSASSData> game){
		// TODO make this load the most recently saved file
		game.enterPlayState();
	}

	// TODO make another menu to load any save files, it should only load files with the prefix ZUSASS_
}
