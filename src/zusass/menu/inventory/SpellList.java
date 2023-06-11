package zusass.menu.inventory;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
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
		
		this.setWidth(1);
		this.setHeight(1);
		this.setFormatter(new PixelFormatter(InventoryMenu.BORDER_SIZE * 1.5, InventoryMenu.BORDER_SIZE * 2.5, InventoryMenu.DRAGGABLE_HEIGHT + InventoryMenu.BORDER_SIZE * 3, null));
		this.invisible();
	}
	
	@Override
	public void onHeightChange(){
		super.onHeightChange();
	}
	
	/**
	 * Generate the buttons used by this spell list, from the given mob and game
	 *
	 * @param mob The mob, must not be null
	 * @param zgame The game
	 */
	public void generateButtons(ZusassMob mob, ZusassGame zgame){
		var spells = mob.getSpells().getSpellList();
		var size = spells.size();
		SpellListButton firstButton = null;
		for(int i = 0; i < size; i++){
			var b = new SpellListButton(spells.get(i), i, this, zgame);
			if(i == 0) firstButton = b;
			this.addThing(b);
			if(i == size - 1) setHeight(b.getY() + b.getHeight() - firstButton.getY());
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
	
	@Override
	public void drawThings(Game game, Renderer r, boolean reposition){
		var b = this.menu.getRelBounds();
		b.y += InventoryMenu.SCROLLER_POSITION;
		b.height -= InventoryMenu.SCROLLER_POSITION;
		r.pushLimitedBoundsIntersection(b);
		super.drawThings(game, r, reposition);
		r.popLimitedBounds();
	}
	
}
