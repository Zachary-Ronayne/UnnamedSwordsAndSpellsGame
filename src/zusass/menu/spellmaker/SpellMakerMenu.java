package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.format.PercentFormatter;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

/** The {@link ZusassMenu} for creating spells */
public class SpellMakerMenu extends ZusassMenu{
	
	/**
	 * Create the menu
	 *
	 * @param zgame The game using the menu
	 */
	public SpellMakerMenu(ZusassGame zgame){
		super(zgame, "Spell Creation");
		
		this.setFill(new ZColor(.6, 0, .4, .8));
		this.setBorder(new ZColor(.3, 0, .5, .5));
		this.makeDraggable(10, 30);
		
		this.format(zgame.getWindow(), new PercentFormatter(.8, .95, .5, .5));
		this.reformat(zgame);
	}
	
	/**
	 * Format the components of this menu based on its current size
	 * @param zgame The game to reformat to
	 */
	private void reformat(ZusassGame zgame){
		var t = this.getTitleThing();
		t.format(new PercentFormatter(1.0, 1.0, .5, .5));
		t.centerTextHorizontal();
	}
	
	@Override
	public void onDragEnd(Game game, boolean sideDrag){
		// TODO fix the title buffer not being fully cleared when it's resized
		super.onDragEnd(game, sideDrag);
		this.reformat((ZusassGame)game);
	}
}
