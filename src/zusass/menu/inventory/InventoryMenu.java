package zusass.menu.inventory;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
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
	
	/** The size of the border of this menu */
	public static final double BORDER_SIZE = 10;
	/** The height of the draggable area at the top of the menu for moving it around the screen */
	public static final double DRAGGABLE_HEIGHT = 30;
	/** The position, relative to this menu, for the maximum height of the scroller */
	public static final double SCROLLER_POSITION = InventoryMenu.DRAGGABLE_HEIGHT + InventoryMenu.BORDER_SIZE * 2;
	/** The color used for the border */
	private static final ZColor BORDER_COLOR = new ZColor(.1, 0, 0, .8);
	
	/** The formatter used to position this menu */
	private final MenuFormatter defaultFormatter;
	
	/** the mob which uses the contents of this menu */
	private ZusassMob mob;
	
	/** The menu thing holding the list of spells to display */
	private final SpellList spellList;
	
	/** The scroller used for this menu for scrolling through spells */
	private final VerticalScroller spellScroller;
	
	/**
	 * Create a new {@link InventoryMenu} for displaying the inventory of something
	 *
	 * @param zgame The game which will use this menu
	 */
	public InventoryMenu(ZusassGame zgame){
		super(zgame, "");
		this.setDefaultDestroyRemove(false);
		
		// issue#28 why does making this use a buffer make the menu extra transparent?
//		this.setBuffer(true);
//		this.getBuffer().setAlphaMode(AlphaMode.BUFFER);
		
		this.makeDraggable(BORDER_SIZE, DRAGGABLE_HEIGHT);
		this.setBorder(BORDER_COLOR);
		this.setFill(new ZColor(.3, 0, 0, .8));
		this.setDraggableColor(new ZColor(.8, .3));
		this.setMinWidth(120.0);
		this.setMinHeight(75.0);
		
		this.defaultFormatter = new MultiFormatter(new PixelFormatter(null, 10.0, null, null), new PercentFormatter(null, 0.8, null, 0.5));
		this.defaultPosition(zgame);
		
		this.spellList = new SpellList(this, zgame);
		this.addThing(this.spellList);
		
		// issue#31 Stop the weird glitchy movement with the scroller when resizing the menu
		this.spellScroller = new VerticalScroller(1, 1, 10, 100, 200, zgame);
		this.spellScroller.setScrollWheelEnabled(true);
		this.spellScroller.setScrollWheelInverse(true);
		this.spellScroller.setFormatter(new PixelFormatter(null, BORDER_SIZE, DRAGGABLE_HEIGHT * 1.4, 20.0));
		// TODO allow scroll strength to be based on the distance that can be scrolled, instead of the distance the scroll bar can be moved, both as a percent and constant
		this.spellScroller.setScrollWheelAsPercent(true);
		this.spellScroller.setScrollWheelStrength(.02);
		this.addThing(this.spellScroller);
		
		this.spellScroller.setMovingThing(this.spellList);
		this.mob = null;
		
		this.updateScrollAmount();
		
		this.spellList.format();
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
	public void onDragEnd(Game game, boolean sideDrag){
		super.onDragEnd(game, sideDrag);
		this.updateScrollAmount();
	}
	
	@Override
	public void regenerateBuffer(){
		super.regenerateBuffer();
		this.spellList.regenerateBuffer();
	}
	
	/** Update the values in {@link #spellScroller} based on the current size of the menu */
	public void updateScrollAmount(){
		this.spellScroller.setAmount(Math.max(0, this.spellList.getHeight() - (this.getHeight() - DRAGGABLE_HEIGHT - BORDER_SIZE * 5)));
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
	
	/** @return See {@link #mob} */
	public ZusassMob getMob(){
		return this.mob;
	}
	
	/** @param mob See {@link #mob} */
	public void setMob(ZusassMob mob){
		this.mob = mob;
	}
}
