package zgame.menu.scroller;

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
	 */
	public VerticalScroller(double x, double y, double w, double h, double amount){
		super(x, y, w, h, amount);
		this.setInverseMove(true);
	}
	
	@Override
	public MenuScrollerButton generateButton(){
		return new VerticalScrollerButton(this, this.getWidth(), this.getWidth() * 2);
	}
	
}
