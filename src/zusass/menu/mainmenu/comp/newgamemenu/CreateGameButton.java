package zusass.menu.mainmenu.comp.newgamemenu;

import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

import java.util.Random;

/** The button used to confirm to create a new game */
public class CreateGameButton extends ZusassButton{
	
	/** The {@link NewGamePopup} associated with this {@link CreateGameButton} */
	private final NewGamePopup popup;
	
	/**
	 * Initialize the {@link CreateGameButton}
	 *
	 * @param popup See {@link #popup}
	 */
	public CreateGameButton(NewGamePopup popup){
		super(0, 550, 200, 50, "Create");
		this.popup = popup;
	}
	
	@Override
	public void click(){
		var levelName = this.popup.getLevelNameText();
		if(levelName == null || levelName.isEmpty()) return;
		
		var seedString = this.popup.getSeed();
		long seed;
		// Random seed if none is provided
		if(seedString == null || seedString.isEmpty()) seed = new Random().nextLong();
		else {
			// If a parsable long value is input, use that, otherwise, make a seed based on the string's bytes
			try{
				seed = Long.parseLong(seedString);
			}catch(NumberFormatException e){
				seed = 1;
				var bytes = seedString.getBytes();
				// Arbitrary hash like function
				for(int i = 0; i < bytes.length; i++) seed = seed * 31 + (257 + bytes[i]);
			}
		}
		
		ZusassGame.get().createNewGame(levelName, seed);
	}
	
}
