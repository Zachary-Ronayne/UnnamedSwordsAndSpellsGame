package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZUSASSData;
import zusass.game.MainPlay;
import zusass.utils.ZUSASSConfig;

/** The {@link MenuButton} in the main menu for creating a new game */
public class NewGameButton extends MainMenuButton{
	
	/** Create the {@link NewGameButton} */
	public NewGameButton(Game<ZUSASSData> game){
		super(50, 350, "New Game", game);
		this.setFill(new ZColor(.4));
	}
	
	@Override
	public void click(Game<ZUSASSData> game){
		ZUSASSData data = new ZUSASSData();
		// TODO make an actual way of giving the save files a name, a text box, the nanotime thing is just a simple temporary way to make names
		data.setLoadedFile(ZUSASSConfig.createSaveFilePath(System.nanoTime() + ""));
		game.setData(data);

		MainPlay play = new MainPlay(game);
		play.enterHub(game);
		game.setPlayState(play);
		game.enterPlayState();
	}
	
}
