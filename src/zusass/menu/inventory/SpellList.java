package zusass.menu.inventory;

import zgame.menu.MenuHolder;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;

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
		this.addThing(new SpellListButton(0, this, zgame));
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
