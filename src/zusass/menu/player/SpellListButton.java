package zusass.menu.player;

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
	public static final double HEIGHT = 32;
	/** The space between each button */
	public static final double HEIGHT_SPACE = 2;
	
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
		this.setKeepInParent(false);
		
		this.setFormatter(new PercentFormatter(1.0, null, 0.5, null));
		this.setFontSize(24);
		this.bufferWidthToWindow(zgame);
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
	public void onDragEnd(Game game, boolean sideDrag){
		// This needs to be in here and not in the parent InventoryMenu, so that the main menu doesn't need to use a buffer to be regularly regenerated
		super.onDragEnd(game, sideDrag);
		if(!sideDrag) return;
		this.updateTextPosition();
	}
	
	@Override
	public ZRect getTextLimitBounds(){
		var b = super.getTextLimitBounds();
		return b == null ? null : b.pad(-this.getBorderWidth());
	}
	
	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		super.render(game, r, bounds);
		
		// Draw a highlight if this button is the currently selected spell
		var zgame = (ZusassGame)game;
		if(zgame.getPlayer().getSpells().getSelectedSpellIndex() == this.spellIndex){
			r.setColor(.8, .8, 1, 0.5);
			r.drawRectangle(bounds);
		}
	}
}
