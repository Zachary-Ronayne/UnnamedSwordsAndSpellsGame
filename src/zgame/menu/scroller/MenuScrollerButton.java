package zgame.menu.scroller;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuThing;

/** A class representing the clickable portion of a {@link MenuScroller} showing how much has been scrolled */
public abstract class MenuScrollerButton<D>extends MenuThing<D>{
	
	/** true if the mouse has clicked this button and is anchored down to start moving */
	private boolean anchored;
	
	/** The position, relative to this button, when this button was anchored */
	private double anchorOffset;
	
	/** The {@link MenuScroller} which uses this {@link MenuScrollerButton} */
	private MenuScroller<D> scroller;
	
	/**
	 * Create a new basic {@link MenuScrollerButton}
	 * 
	 * @param scroller See {@link #scroller}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public MenuScrollerButton(MenuScroller<D> scroller, double w, double h){
		super();
		this.scroller = scroller;
		this.setWidth(w);
		this.setHeight(h);
		this.setFill(new ZColor(0));
		this.anchored = false;
		this.anchorOffset = 0;
	}
	
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.mouseAction(game, button, press, shift, alt, ctrl);
		if(press){
			double mx = game.mouseSX();
			double my = game.mouseSY();
			if(this.getBounds().contains(mx, my)){
				this.anchored = true;
				this.anchorOffset = this.mouseOffset(game);
			}
		}
		else anchored = false;
	}
	
	@Override
	public void mouseMove(Game<D> game, double x, double y){
		super.mouseMove(game, x, y);
		if(this.anchored) this.scroller.scroll(this.scrollToPercent(this.mouseOffset(game) - this.anchorOffset));
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
	 * Update the relative position of {@link #getButton()} and the given thing, based on the current percentage scrolled
	 * 
	 * @param thing The thing to update
	 */
	public abstract void updateRelativePosition(MenuThing<D> thing);
	
	/**
	 * Determine the initial position of where the given thing will be when this {@link MenuScroller} has scrolled zero percent
	 * 
	 * @param thing The thing to check
	 * @return The base position
	 */
	public abstract double findBasePosition(MenuThing<D> thing);

	/** @return The amount of scrollable space available, i.e. the size of the scroll bar minus the size of this button */
	public abstract double scrollAreaSize();
	
	/** @return The distance the mouse is offset from this button */
	public abstract double mouseOffset(Game<D> game);

	/** @return See {@link #scroller} */
	public MenuScroller<D> getScroller(){
		return this.scroller;
	}

}
