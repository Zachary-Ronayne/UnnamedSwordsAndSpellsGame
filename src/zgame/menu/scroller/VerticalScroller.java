package zgame.menu.scroller;

import zgame.core.Game;

/**
 * An implementation of {@link MenuScroller} for a vertical scroll bar
 */
public class VerticalScroller extends MenuScroller{
	
	/**
	 * Create a new {@link MenuScroller} at the specified location and min and max
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param amount See {@link #getAmount()}
	 * @param game The game associated with this thing
	 */
	public VerticalScroller(double x, double y, double w, double h, double amount, Game game){
		super(x, y, w, h, amount, game);
	}
	
	@Override
	public MenuScrollerButton generateButton(Game game){
		return new VerticalScrollerButton(this, this.getWidth(), this.getWidth() * 2, game);
	}
	
}
