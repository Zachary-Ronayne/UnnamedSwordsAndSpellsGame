package zusass.menu.inventory;

import zgame.core.Game;
import zgame.menu.format.PercentFormatter;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** A button for a {@link SpellList} which displays a single selectable spell */
public class SpellListButton extends ZusassButton{
	
	/** The spell list containing this button */
	private final SpellList spellList;
	
	/**
	 * Create a {@link ZusassButton} with the appropriate parameters
	 *
	 * @param y The y coordinate of this list
	 * @param list See {@link #spellList}
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public SpellListButton(double y, SpellList list, ZusassGame zgame){
		super(0, y, list.getWidth(),32, "Magic spell with a magic name", zgame);
		this.spellList = list;
		
		this.setFormatter(new PercentFormatter(1.0, null, 0.5, null));
		this.setFontSize(24);
		this.updateTextPosition();
	}
	
	@Override
	public void click(Game game){
		super.click(game);
		var m = this.spellList.getMenu().getMob();
		if(m == null) return;
		m.getSpells().nextSpell();
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
		super.onDragEnd(sideDrag);
		if(!sideDrag) return;
		this.updateTextPosition();
	}
}
