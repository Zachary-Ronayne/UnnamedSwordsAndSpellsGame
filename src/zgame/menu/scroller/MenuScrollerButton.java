package zgame.menu.scroller;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuThing;

/** A class representing the clickable portion of a {@link MenuScroller} showing how much has been scrolled */
public class MenuScrollerButton<D>extends MenuThing<D>{
	
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
	 * @param w The width to use
	 * @param h The height to use
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
				// TODO make this have options for horizontal or vertical
				this.anchorOffset = my - this.getY();
			}
		}
		else anchored = false;
	}
	
	@Override
	public void mouseMove(Game<D> game, double x, double y){
		super.mouseMove(game, x, y);
		// TODO make this have options for horizontal or vertical
		if(this.anchored)this.scroller.scroll(this.scrollToPercent(y - (this.getY() + this.anchorOffset)));
	}

	/**
	 * Based on an amount of distance for this {@link MenuScrollerButton} to move, find the percentage of the total scroll bar that value is
	 * @param amount The amount of distance
	 * @return The corresponding percentage
	 */
	public double scrollToPercent(double amount){
		// TODO make this have options for horizontal or vertical
		return amount / (this.scroller.getHeight() - this.getHeight());
	}
	
}
