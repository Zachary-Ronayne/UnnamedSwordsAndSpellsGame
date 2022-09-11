package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.ZusassData;
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
	public CreateGameButton(NewGameTextBox textBox, Game<ZusassData> game){
		super(500, 460, 200, 50, "Create", game);
		this.textBox = textBox;
	}
	
	@Override
	public void click(Game<ZusassData> game){
		String text = this.textBox.getText();
		if(text == null || text.isEmpty()) return;
		this.createNewGame(game, text);
	}
	
	public void createNewGame(Game<ZusassData> game, String name){
		ZusassData data = new ZusassData();
		data.setLoadedFile(ZusassConfig.createSaveFilePath(name));
		game.setData(data);
		
		MainPlay play = new MainPlay(game);
		game.setCurrentState(play);
		data.checkAutoSave(game);
	}
	
}
