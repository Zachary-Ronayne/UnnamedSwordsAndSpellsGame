package zgame.menu.scroller;

import zgame.core.Game;
import zgame.menu.MenuThing;

/**
 * An implementation of {@link MenuScrollerButton} for a horizontal scroll bar
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public class HorizontalScrollerButton<D>extends MenuScrollerButton<D>{
	
	/**
	 * Create a basic {@link HorizontalScrollerButton} with the given values
	 * 
	 * @param scroller See {@link #scroller}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param game The game associated with this thing
	 */
	public HorizontalScrollerButton(MenuScroller<D> scroller, double w, double h, Game<D> game){
		super(scroller, w, h, game);
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
