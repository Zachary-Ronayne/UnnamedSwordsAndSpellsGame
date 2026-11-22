package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.menu.MenuTextBox;
import zusass.menu.comp.ZusassTextBox;

/** A {@link MenuTextBox} for typing in the seed of a new game */
public class SeedTextBox extends ZusassTextBox{
	
	/**
	 * Initialize the {@link SeedTextBox}
	 */
	public SeedTextBox(){
		super(500, 455, 470, 50);
		this.setHint("Seed...");
	}
	
}
