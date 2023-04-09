package zgame.menu.scroller;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zgame.menu.MenuThing;

/**
 * A class representing the clickable portion of a {@link MenuScroller} showing how much has been scrolled
 */
public abstract class MenuScrollerButton extends MenuButton{
	
	/** true if the mouse has clicked this button and is anchored down to start moving */
	private boolean anchored;
	
	/** The position, relative to this button, when this button was anchored */
	private double anchorOffset;
	
	/** The {@link MenuScroller} which uses this {@link MenuScrollerButton} */
	private final MenuScroller scroller;
	
	/**
	 * Create a new basic {@link MenuScrollerButton}
	 *
	 * @param scroller See {@link #scroller}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param game The game associated with this thing
	 */
	public MenuScrollerButton(MenuScroller scroller, double w, double h, Game game){
		super(0, 0, w, h, game);
		this.scroller = scroller;
		this.setFill(new ZColor(0));
		this.anchored = false;
		this.anchorOffset = 0;
	}
	
	@Override
	public boolean mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		boolean input = super.mouseAction(game, button, press, shift, alt, ctrl);
		if(press){
			double mx = game.mouseSX();
			double my = game.mouseSY();
			if(this.getBounds().contains(mx, my)){
				this.anchored = true;
				this.anchorOffset = this.mouseOffset(game);
				return true;
			}
		}
		else anchored = false;
		return input;
	}
	
	@Override
	public boolean mouseMove(Game game, double x, double y){
		boolean input = super.mouseMove(game, x, y);
		if(this.anchored) {
			this.scroller.scroll(this.scrollToPercent(this.mouseOffset(game) - this.anchorOffset));
			return true;
		}
		return input;
	}
	
	/**
	 * issue#13 fix a bug where sometimes the highlight doesn't show up if not using a buffer? Or, maybe it's a problem when using a buffer, like not using opacity correctly?
	 * Or maybe it has to do with blending, or maybe the shaders?
	 */
	
	@Override
	public boolean showHighlight(Game game){
		return super.showHighlight(game) || this.anchored;
	}
	
	/**
	 * Based on an amount of distance for this {@link MenuScrollerButton} to move, find the percentage of the total scroll bar that value is
	 *
	 * @param amount The amount of distance
	 * @return The corresponding percentage
	 */
	public double scrollToPercent(double amount){
		return amount / this.scrollAreaSize();
	}
	
	/**
	 * Update the relative position of this and the given thing, based on the current percentage scrolled
	 *
	 * @param thing The thing to update
	 */
	public abstract void updateRelativePosition(MenuThing thing);
	
	/**
	 * Determine the initial position of where the given thing will be when this {@link MenuScroller} has scrolled zero percent
	 *
	 * @param thing The thing to check
	 * @return The base position
	 */
	public abstract double findBasePosition(MenuThing thing);
	
	/** @return The amount of scrollable space available, i.e. the size of the scroll bar minus the size of this button */
	public abstract double scrollAreaSize();
	
	/** @return The distance the mouse is offset from this button */
	public abstract double mouseOffset(Game game);
	
	/** @return See {@link #scroller} */
	public MenuScroller getScroller(){
		return this.scroller;
	}
	
}
