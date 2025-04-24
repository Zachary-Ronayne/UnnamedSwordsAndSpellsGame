package zgame.menu.scroller;

import zgame.core.Game;
import zgame.menu.MenuThing;

/**
 * An implementation of {@link MenuScrollerButton} for a vertical scroll bar
 */
public class VerticalScrollerButton extends MenuScrollerButton{
	
	/**
	 * Create a basic {@link VerticalScrollerButton} with the given values
	 *
	 * @param scroller See {@link #scroller}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public VerticalScrollerButton(MenuScroller scroller, double w, double h){
		super(scroller, w, h);
	}
	
	@Override
	public void updateRelativePosition(MenuThing thing){
		MenuScroller scroller = this.getScroller();
		thing.setRelY(scroller.getBasePosition() + scroller.getScrolledAmount());
		this.setRelY(scroller.getPercent() * (scroller.getHeight() - this.getHeight()));
	}
	
	@Override
	public double findBasePosition(MenuThing thing){
		return thing.getRelY();
	}
	
	@Override
	public double scrollAreaSize(){
		MenuScroller scroller = this.getScroller();
		return scroller.getHeight() - this.getHeight();
	}
	
	@Override
	public double mouseOffset(Game game){
		return game.mouseSY() - this.getY();
	}
}
