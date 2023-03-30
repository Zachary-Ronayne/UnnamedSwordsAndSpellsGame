package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** The button used to confirm to create a new game */
public class CreateGameButton extends ZusassButton{
	
	/** The {@link NewGameTextBox} associated with this {@link CreateGameButton} */
	private final NewGameTextBox textBox;
	
	/**
	 * Initialize the {@link CreateGameButton}
	 * 
	 * @param zgame The Zusass game used by this thing
	 */
	public CreateGameButton(NewGameTextBox textBox, ZusassGame zgame){
		super(500, 460, 200, 50, "Create", zgame);
		this.textBox = textBox;
	}
	
	@Override
	public void click(Game game){
		String text = this.textBox.getText();
		if(text == null || text.isEmpty()) return;
		ZusassGame zgame = (ZusassGame)game;
		zgame.createNewGame(text);
	}
	
}
