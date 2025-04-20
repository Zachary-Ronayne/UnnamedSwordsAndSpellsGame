package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.menu.MenuTextBox;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassTextBox;

/** A {@link MenuTextBox} for typing in the seed of a new game */
public class SeedTextBox extends ZusassTextBox{
	
	/**
	 * Initialize the {@link SeedTextBox}
	 *
	 * @param zgame The Zusass game used by this thing
	 */
	public SeedTextBox(ZusassGame zgame){
		super(500, 455, 470, 50, zgame);
		this.setHint("Seed...");
	}
	
}
