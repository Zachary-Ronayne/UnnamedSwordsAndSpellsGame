package zusass.menu.inventory;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZRect;
import zgame.menu.format.PercentFormatter;
import zusass.ZusassGame;
import zusass.game.magic.Spell;
import zusass.menu.comp.ZusassButton;

/** A button for a {@link SpellList} which displays a single selectable spell */
public class SpellListButton extends ZusassButton{
	
	/** The height of each button */
	private static final double HEIGHT = 32;
	/** The space between each button */
	private static final double HEIGHT_SPACE = 2;
	
	/** The spell list containing this button */
	private final SpellList spellList;
	
	/** The index of the spell which this button represents */
	private final int spellIndex;
	
	/**
	 * Create a {@link ZusassButton} with the appropriate parameters
	 *
	 * @param spell The spell to display
	 * @param index The index of this button in spellList
	 * @param list See {@link #spellList}
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public SpellListButton(Spell spell, int index, SpellList list, ZusassGame zgame){
		super(0, index * (HEIGHT + HEIGHT_SPACE), list.getWidth(), HEIGHT, spell.nameAndCost(), zgame);
		this.spellList = list;
		this.spellIndex = index;
		
		this.setFormatter(new PercentFormatter(1.0, null, 0.5, null));
		this.setFontSize(24);
		this.updateTextPosition();
	}
	
	@Override
	public void click(Game game){
		super.click(game);
		var m = this.spellList.getMenu().getMob();
		if(m == null) return;
		m.getSpells().setSelectedSpellIndex(this.spellIndex);
	}
	
	/** Regenerate the text buffer and reposition the text based on the current width, height, and text */
	public void updateTextPosition(){
		this.setTextX(7);
		this.centerTextVertical();
		this.setTextY(this.getTextY() - 5);
		this.regenerateBuffer();
	}
	
	@Override
	public void onDragEnd(boolean sideDrag){
		// This needs to be in here and not in the parent InventoryMenu, so that the main menu doesn't need to use a buffer to be regularly regenerated
		super.onDragEnd(sideDrag);
		if(!sideDrag) return;
		this.updateTextPosition();
	}
	
	@Override
	public ZRect getTextLimitBounds(){
		return super.getTextLimitBounds().pad(-this.getBorderWidth());
	}

	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		super.render(game, r, bounds);
		// TODO remove
//		r.setColor(1, 0, 1, .5);
//		r.fill();
	}
}
