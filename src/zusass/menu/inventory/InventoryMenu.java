package zusass.menu.inventory;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zgame.menu.scroller.VerticalScroller;
import zusass.ZusassGame;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.menu.ZusassMenu;

/** The menu which displays on top of the game */
public class InventoryMenu extends ZusassMenu{
	
	/** The formatter used to position this menu */
	private final MenuFormatter defaultFormatter;
	
	/** the mob which uses the contents of this menu */
	private ZusassMob mob;
	
	/** The menu thing holding the list of spells to display */
	private final SpellList spellList;
	
	// TODO make a Zusass component for this
	/** The scroller used for this menu for scrolling through spells */
	private final VerticalScroller spellScroller;
	
	/**
	 * Create a new {@link InventoryMenu} for displaying the inventory of something
	 *
	 * @param zgame The game which will use this menu
	 */
	public InventoryMenu(ZusassGame zgame){
		super(zgame, "");
		
		// issue#28 why does making this use a buffer make the menu extra transparent?
//		this.setBuffer(true);
//		this.getBuffer().setAlphaMode(AlphaMode.BUFFER);
		
		this.makeDraggable(10, 30);
		this.setBorder(new ZColor(.1, 0, 0, .8));
		this.setFill(new ZColor(.3, 0, 0, .8));
		this.setMinWidth(120.0);
		this.setMinHeight(75.0);
		
		this.defaultFormatter = new MultiFormatter(new PixelFormatter(null, 10.0, null, null), new PercentFormatter(null, 0.8, null, 0.5));
		this.defaultPosition(zgame);
		
		this.spellList = new SpellList(this, zgame);
		this.addThing(this.spellList);
		
		// TODO make the amount for the scroller dynamically change as the height changes, and depend on the number of buttons
		// TODO Avoid the weird glitchy movement with the scroller when resizing the menu
		this.spellScroller = new VerticalScroller(1, 1, 10, 100, 200, zgame);
		this.spellScroller.setScrollWheelEnabled(true);
		this.spellScroller.setScrollWheelInverse(true);
		var d = this.getDraggableArea().getRelBounds();
		this.spellScroller.setFormatter(new PixelFormatter(null, 5.0, d.getHeight() * 1.4, 20.0));
		this.addThing(this.spellScroller);
		
		this.spellScroller.setMovingThing(this.spellList);
		this.mob = null;
	}
	
	/**
	 * Regenerate the state of all the things used by this menu, based on the current value of {@link #mob}, should not call unless {@link #mob} is not null
	 *
	 * @param zgame The game to regenerate with
	 */
	public void regenerateThings(ZusassGame zgame){
		this.spellList.generateButtons(this.getMob(), zgame);
	}
	
	@Override
	public void regenerateBuffer(){
		super.regenerateBuffer();
		this.spellList.regenerateBuffer();
	}
	
	/**
	 * Bring this menu to its default position on the right side
	 *
	 * @param zgame The game used to position the menu
	 */
	public void defaultPosition(ZusassGame zgame){
		var w = zgame.getWindow();
		this.setWidth(200);
		this.format(w, this.defaultFormatter);
	}
	
	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		r.pushLimitedBounds(bounds);
		super.render(game, r, bounds);
		
		// #issue28 If this uses a buffer, the fill is solid, but this value is transparent and should be on top of the solid color, then this part is still transparent. Why?
		r.setColor(new ZColor(.8, .3));
		var d = this.getDraggableArea().getRelBounds();
		r.drawRectangle(d.x(bounds.getX() + d.getX()).y(bounds.getY() + d.getY()));
		r.popLimitedBounds();
	}
	
	/** @return See {@link #mob} */
	public ZusassMob getMob(){
		return this.mob;
	}
	
	/** @param mob See {@link #mob} */
	public void setMob(ZusassMob mob){
		this.mob = mob;
	}
}
