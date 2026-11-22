package zgame.menu.scroller;

/**
 * An implementation of {@link MenuScroller} for a horizontal scroll bar
 */
public class HorizontalScroller extends MenuScroller{
	
	/**
	 * Create a new {@link HorizontalScroller} at the specified location and min and max
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param amount See {@link #amount}
	 */
	public HorizontalScroller(double x, double y, double w, double h, double amount){
		super(x, y, w, h, amount);
	}
	
	@Override
	public MenuScrollerButton generateButton(){
		return new HorizontalScrollerButton(this, this.getHeight() * 2, this.getHeight());
	}
	
}
