package zusass.menu.player;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuThing;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.PixelFormatter;
import zgame.menu.scroller.VerticalScroller;
import zusass.ZusassGame;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.menu.ZusassMenu;

/** A menu which displays on top of the game for displaying information to the player, which can be dragged around */
public abstract class DraggableMenu extends ZusassMenu{
	
	/** The size of the border of this menu */
	public static final double BORDER_SIZE = 10;
	/** The height of the draggable area at the top of the menu for moving it around the screen */
	public static final double DRAGGABLE_HEIGHT = 30;
	/** The position, relative to this menu, for the maximum height of the scroller */
	public static final double SCROLLER_POSITION = DraggableMenu.DRAGGABLE_HEIGHT + DraggableMenu.BORDER_SIZE * 2;
	/** The color used for the border */
	private static final ZColor BORDER_COLOR = new ZColor(.1, 0, 0, .8);
	
	/** The formatter used to position this menu */
	private MenuFormatter defaultFormatter;
	
	/** the mob which uses the contents of this menu */
	private ZusassMob mob;
	
	/** The scroller used for this menu for scrolling through the contents of this menu */
	private VerticalScroller menuScroller;
	
	/**
	 * Create a new {@link DraggableMenu} for displaying the contents of something
	 *
	 * @param zgame The game which will use this menu
	 */
	public DraggableMenu(ZusassGame zgame){
		super(zgame, "");
		this.setDefaultDestroyRemove(false);
		this.setSendToTopOnClick(true);
		
		// issue#28 why does making this use a buffer make the menu extra transparent?
//		this.setBuffer(true);
//		this.getBuffer().setAlphaMode(AlphaMode.BUFFER);
		
		this.makeDraggable(BORDER_SIZE, DRAGGABLE_HEIGHT);
		this.setBorder(BORDER_COLOR);
		this.setFill(new ZColor(.3, 0, 0, .8));
		this.setDraggableColor(new ZColor(.8, .3));
		this.setMinWidth(120.0);
		this.setMinHeight(75.0);
	}
	
	/**
	 * Initialize the state of all menu things for this menu
	 *
	 * @param zgame The game to init the things in
	 */
	public void initMenuThings(ZusassGame zgame){
		this.defaultFormatter = this.getDefaultFormatter();
		this.defaultPosition(zgame);
		
		// issue#31 Stop the weird glitchy movement with the scroller when resizing the menu
		this.menuScroller = new VerticalScroller(1, 1, 10, 100, 200, zgame);
		this.menuScroller.setScrollWheelEnabled(true);
		this.menuScroller.setScrollWheelInverse(true);
		this.menuScroller.setFormatter(new PixelFormatter(null, BORDER_SIZE, DRAGGABLE_HEIGHT * 1.4, 20.0));
		this.menuScroller.setScrollWheelAsPercent(false);
		this.menuScroller.setScrollWheelStrength(this.getScrollWheelStrength());
		
		var movingThing = this.getScrollableMovingThing(zgame);
		this.addThing(movingThing);
		this.menuScroller.setMovingThing(movingThing);
		this.mob = null;
		
		this.addThing(this.menuScroller);
		this.updateScrollAmount();
		
		movingThing.format();
	}
	
	/**
	 * @param zgame The game to use for creating the thing
	 * @return The menu thing which moves with the scroll wheel
	 */
	public abstract MenuThing getScrollableMovingThing(ZusassGame zgame);
	
	/** @return Create a new formatter for the default position and size of this menu */
	public abstract MenuFormatter getDefaultFormatter();
	
	/** @return The strength of the scroll wheel */
	public abstract double getScrollWheelStrength();
	
	/** @return The size, i.e. height, of the entire scrollable area */
	public abstract double getFullScrollableSize();
	
	/**
	 * Regenerate the state of all the things used by this menu, based on the current value of {@link #mob}, should not call unless {@link #mob} is not null
	 *
	 * @param zgame The game to regenerate with
	 */
	public abstract void regenerateThings(ZusassGame zgame);
	
	@Override
	public void onDragEnd(Game game, boolean sideDrag){
		super.onDragEnd(game, sideDrag);
		this.updateScrollAmount();
	}
	
	/** Update the values in {@link #menuScroller} based on the current size of the menu */
	public void updateScrollAmount(){
		this.menuScroller.setAmount(Math.max(0, this.getFullScrollableSize() - (this.getHeight() - DRAGGABLE_HEIGHT - BORDER_SIZE * 5)));
	}
	
	/**
	 * Bring this menu to its default position
	 *
	 * @param zgame The game used to position the menu
	 */
	public void defaultPosition(ZusassGame zgame){
		var w = zgame.getWindow();
		this.format(w, this.defaultFormatter);
	}
	
	/** @return See {@link #mob} */
	public ZusassMob getMob(){
		return this.mob;
	}
	
	/**
	 * @param zgame The game where the mob is set in
	 * @param mob See {@link #mob}
	 */
	public void setMob(ZusassGame zgame, ZusassMob mob){
		this.mob = mob;
		this.regenerateThings(zgame);
	}
	
}
