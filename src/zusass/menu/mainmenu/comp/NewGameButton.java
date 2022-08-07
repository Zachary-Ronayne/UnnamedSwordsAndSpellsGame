package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZUSASSData;
import zusass.game.MainPlay;

/** The {@link MenuButton} in the main menu for creating a new game */
public class NewGameButton extends MainMenuButton{
	
	/** Create the {@link NewGameButton} */
	public NewGameButton(Game<ZUSASSData> game){
		super(50, 50, 500, 150, "New Game", game);
		this.setFill(new ZColor(.4));
	}
	
	@Override
	public void click(Game<ZUSASSData> game){
		game.setPlayState(new MainPlay(game));
		game.setData(new ZUSASSData());
		game.enterPlayState();
	}
	
}
