package zgame.menu.scroller;

import zgame.core.Game;

/**
 * An implementation of {@link MenuScroller} for a vertical scroll bar
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public class VerticalScroller<D> extends MenuScroller<D>{
	
	/**
	 * Create a new {@link MenuScroller} at the specified location and min and max
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param See {@link #getHeight()}
	 * @param amount See {@link #getAmount()}
	 * @param game The game associated with this thing
	 */
	public VerticalScroller(double x, double y, double w, double h, double amount, Game<D> game){
		super(x, y, w, h, amount, game);
	}

	@Override
	public MenuScrollerButton<D> generateButton(Game<D> game){
		return new VerticalScrollerButton<D>(this, this.getWidth(), this.getWidth() * 2, game);
	}
	
}
