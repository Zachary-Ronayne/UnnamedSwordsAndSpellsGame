package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;

/** The {@link MenuButton} in the main menu for continuing the last opened game */
public class ContinueGameButton extends MainMenuButton{
	
	/** Create the {@link ContinueGameButton} */
	public ContinueGameButton(Game game){
		super(50, 250, 500, 150, "Continue", game);
		this.setFill(new ZColor(.5));
	}
	
	@Override
	public void click(Game game){
		game.enterPlayState();
	}
}
