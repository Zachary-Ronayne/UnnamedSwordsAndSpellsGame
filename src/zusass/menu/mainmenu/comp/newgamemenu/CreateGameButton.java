package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.game.MainPlay;
import zusass.menu.comp.ZusassButton;
import zusass.utils.ZusassConfig;

/** The button used to confirm to create a new game */
public class CreateGameButton extends ZusassButton{
	
	/** The {@link NewGameTextBox} associated with this {@link CreateGameButton} */
	private NewGameTextBox textBox;
	
	/**
	 * Initialize the {@link CreateGameButton}
	 * 
	 * @param game The Zusass game used by this thing
	 */
	public CreateGameButton(NewGameTextBox textBox, Game game){
		super(500, 460, 200, 50, "Create", game);
		this.textBox = textBox;
	}
	
	@Override
	public void click(Game game){
		String text = this.textBox.getText();
		if(text == null || text.isEmpty()) return;
		this.createNewGame(game, text);
	}
	
	public void createNewGame(Game game, String name){
		ZusassData data = new ZusassData();
		data.setLoadedFile(ZusassConfig.createSaveFilePath(name));
		((ZusassGame)game).setData(data);
		
		MainPlay play = new MainPlay(game);
		game.setCurrentState(play);
		data.checkAutoSave(game);
	}
	
}
