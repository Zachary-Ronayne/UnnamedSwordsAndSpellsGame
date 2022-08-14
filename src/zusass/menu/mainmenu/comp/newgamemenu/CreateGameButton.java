package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.ZUSASSData;
import zusass.game.MainPlay;
import zusass.menu.comp.ZUSASSButton;
import zusass.utils.ZUSASSConfig;

/** The button used to confirm to create a new game */
public class CreateGameButton extends ZUSASSButton{
	
	/** The {@link NewGameTextBox} associated with this {@link CreateGameButton} */
	private NewGameTextBox textBox;
	
	/**
	 * Initialize the {@link CreateGameButton}
	 * 
	 * @param game The ZUSASSGame used by this thing
	 */
	public CreateGameButton(NewGameTextBox textBox, Game<ZUSASSData> game){
		super(500, 460, 200, 50, "Create", game);
		this.textBox = textBox;
	}

	@Override
	public void click(Game<ZUSASSData> game){
		String text = this.textBox.getText();
		if(text == null || text.isEmpty()) return;
		this.createNewGame(game, text);
	}
	
	public void createNewGame(Game<ZUSASSData> game, String name){
		ZUSASSData data = new ZUSASSData();
		data.setLoadedFile(ZUSASSConfig.createSaveFilePath(name));
		game.setData(data);
		
		MainPlay play = new MainPlay(game);
		play.enterHub(game);
		game.setPlayState(play);
		game.enterPlayState();
		data.checkAutoSave(game);
	}
	
}
