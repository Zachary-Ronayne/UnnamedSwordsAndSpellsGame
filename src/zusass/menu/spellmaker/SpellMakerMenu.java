package zusass.menu.spellmaker;

import zgame.core.graphics.ZColor;
import zgame.menu.format.PercentFormatter;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

/** The {@link ZusassMenu} for creating spells */
public class SpellMakerMenu extends ZusassMenu{
	
	/**
	 * Create the menu
	 * @param zgame The game using the menu
	 */
	public SpellMakerMenu(ZusassGame zgame){
		super(zgame, "Spell Creation");
		
		this.setFill(new ZColor(.6, 0, .4, .5));
		this.setBorder(new ZColor(.3, 0, .5, .5));
		this.makeDraggable(10, 30);
		
		// TODO make the ZusassMenu title follow the formatter, like, reposition it when the menu is moved around
		this.format(zgame.getWindow(), new PercentFormatter(.8, .8, .5, .5));
	}
}
