package zusass.menu.inventory;

import zgame.menu.MenuHolder;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.game.things.entities.mobs.ZusassMob;

/** An object holding the list of spells to display */
public class SpellList extends MenuHolder{
	
	/** The menu holding this list */
	private final InventoryMenu menu;
	
	/**
	 * Create the new basic spell list menu thing
	 *
	 * @param menu See {@link #menu}
	 * @param zgame The game containing this menu
	 */
	public SpellList(InventoryMenu menu, ZusassGame zgame){
		super();
		this.getAllThings().addClass(SpellListButton.class);
		
		this.menu = menu;
		
		this.setWidth(100);
		this.setHeight(100);
		this.setFormatter(new PixelFormatter(20.0, 20.0, 50.0, null));
		this.invisible();
		
		// TODO allow the list to be scrolled through
	}
	
	/**
	 * Generate the buttons used by this spell list, from the given mob and game
	 *
	 * @param mob The mob, must not be null
	 * @param zgame The game
	 */
	public void generateButtons(ZusassMob mob, ZusassGame zgame){
		var spells = mob.getSpells().getSpellList();
		for(int i = 0; i < spells.size(); i++){
			this.addThing(new SpellListButton(spells.get(i), i, this, zgame));
		}
	}
	
	/** @return See {@link #menu} */
	public InventoryMenu getMenu(){
		return this.menu;
	}
	
	@Override
	public void regenerateBuffer(){
		super.regenerateBuffer();
		var buttons = this.getAllThings().get(SpellListButton.class);
		if(buttons == null) return;
		for(var b : buttons) b.updateTextPosition();
	}
	
}
