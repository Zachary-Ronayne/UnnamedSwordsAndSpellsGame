package zusass.menu.player;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.menu.MenuHolder;
import zgame.menu.format.PixelFormatter;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.ArrayList;

/** An object holding the list of spells to display */
public class SpellList extends MenuHolder{
	
	/** The menu holding this list */
	private final SpellListMenu menu;
	
	/**
	 * Create the new basic spell list menu thing
	 *
	 * @param menu See {@link #menu}
	 */
	public SpellList(SpellListMenu menu){
		super();
		this.getAllThings().addClass(SpellListButton.class);
		
		this.menu = menu;
		
		this.setWidth(1);
		this.setHeight(1);
		this.setFormatter(new PixelFormatter(SpellListMenu.BORDER_SIZE * 1.5, SpellListMenu.BORDER_SIZE * 2.5, SpellListMenu.DRAGGABLE_HEIGHT + SpellListMenu.BORDER_SIZE * 3, null));
		this.invisible();
	}
	
	/**
	 * Generate the buttons used by this spell list, from the given mob and game
	 *
	 * @param mob The mob, must not be null
	 */
	public void generateButtons(ZusassMob mob){
		var existingButtons = new ArrayList<>(this.getAllThings().get(SpellListButton.class));
		for(var e : existingButtons) this.removeThing(e);
		
		var spells = mob.getSpells().getSpellList();
		var size = spells.size();
		SpellListButton firstButton = null;
		for(int i = 0; i < size; i++){
			var b = new SpellListButton(spells.get(i), i, this);
			if(i == 0) firstButton = b;
			this.addThing(b);
			if(i == size - 1) this.setHeight(b.getY() + b.getHeight() - firstButton.getY());
		}
	}
	
	/** @return See {@link #menu} */
	public SpellListMenu getMenu(){
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
		b.y += SpellListMenu.SCROLLER_POSITION;
		b.height -= SpellListMenu.SCROLLER_POSITION;
		r.pushLimitedBoundsIntersection(b);
		super.drawThings(game, r, reposition);
		r.popLimitedBounds();
	}
	
}
