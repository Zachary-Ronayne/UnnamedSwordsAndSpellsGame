package zgame.menu.scroller;

import zgame.core.Game;
import zgame.menu.MenuThing;

/** An implementation of {@link MenuScrollerButton} for a horizontal scroll bar */
public class HorizontalScrollerButton<D>extends MenuScrollerButton<D>{
	
	/**
	 * Create a basic {@link HorizontalScrollerButton} with the given values
	 * 
	 * @param scroller See {@link #scroller}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public HorizontalScrollerButton(MenuScroller<D> scroller, double w, double h){
		super(scroller, w, h);
	}
	
	@Override
	public void updateRelativePosition(MenuThing<D> thing){
		MenuScroller<D> scroller = this.getScroller();
		thing.setRelX(scroller.getBasePosition() + scroller.getScrolledAmount());
		this.setRelX(scroller.getPercent() * (scroller.getWidth() - this.getWidth()));
	}
	
	@Override
	public double findBasePosition(MenuThing<D> thing){
		return thing.getRelX();
	}
	
	@Override
	public double scrollAreaSize(){
		MenuScroller<D> scroller = this.getScroller();
		return scroller.getWidth() - this.getWidth();
	}
	
	@Override
	public double mouseOffset(Game<D> game){
		return game.mouseSX() - this.getX();
	}
}
