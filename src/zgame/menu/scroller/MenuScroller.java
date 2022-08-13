package zgame.menu.scroller;

import zgame.core.Game;
import zgame.menu.MenuThing;

/**
 * A {@link MenuThing} that can move other {@link MenuThing}s around.
 * The specified child element of this {@link MenuScroller} will have its positions moved when this scroller moves
 * This object can only have one child element. Adding additional elements will replace the current one
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public abstract class MenuScroller<D>extends MenuThing<D>{
	
	/** The amount scrolled */
	private ScrollAxis scroller;
	
	/** The amount this scroller can move on its axis {@link MenuThing}s to */
	private double amount;
	
	/** The position of the child element of this {@link MenuScroller} */
	private double basePosition;
	
	/** The {@link MenuThing} which will be moved by this {@link MenuScroller} */
	private MenuThing<D> movingThing;
	
	/** The button to use for scrolling */
	private MenuScrollerButton<D> button;
	
	/** The degree to which the scroll wheel will scroll. Also see {@link #scrollWheelAsPercent} */
	private double scrollWheelStrength;
	/** true if {@link #scrollWheelStrength} is the percentage of the scroll bat that should move per mouse wheel, false if it should be a distance */
	private boolean scrollWheelAsPercent;
	/** true if the scroll wheel's direction should be inverted, false otherwise */
	private boolean scrollWheelInverse;
	/** true if this scroll wheel should interact, false otherwise */
	private boolean scrollWheelEnabled;
	
	/**
	 * Create a new {@link MenuScroller} at the specified location and min and max
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param See {@link #getHeight()}
	 * @param amount See {@link #amount}
	 */
	public MenuScroller(double x, double y, double w, double h, double amount){
		super(x, y);
		this.scrollWheelStrength = 0.1;
		this.scrollWheelAsPercent = true;
		this.scrollWheelInverse = true;
		this.scrollWheelEnabled = true;
		this.setWidth(w);
		this.setHeight(h);
		this.amount = amount;
		this.scroller = new ScrollAxis();
		this.button = this.generateButton();
		super.addThing(button);
		this.movingThing = null;
	}
	
	@Override
	public void tick(Game<D> game, double dt){
		super.tick(game, dt);
		MenuThing<D> thing = this.getMovingThing();
		if(thing != null) this.button.updateRelativePosition(thing);
	}
	
	@Override
	public void mouseWheelMove(Game<D> game, double amount){
		super.mouseWheelMove(game, amount);
		
		if(!this.isScrollWheelEnabled()) return;
		amount *= this.getScrollWheelStrength();
		if(this.isScrollWheelInverse()) amount = -amount;
		if(!this.isScrollWheelAsPercent()) amount = this.button.scrollToPercent(amount);
		this.scroll(amount);
	}
	
	/** @param See {@link #movingThing} */
	public void setMovingThing(MenuThing<D> thing){
		this.movingThing = thing;
		// Update the position of this scroller
		this.basePosition = this.button.findBasePosition(thing);
	}
	
	/** @return See {@link #movingThing} */
	public MenuThing<D> getMovingThing(){
		return this.movingThing;
	}
	
	/** @return A MenuScrollerButton implemented to move the scroll button around in the desired way */
	public abstract MenuScrollerButton<D> generateButton();
	
	/** @param amount The amount to scroll this menu scroller by, as a percentage */
	public void scroll(double amount){
		this.scroller.scroll(amount);
	}
	
	/** @return See {@link #amount} */
	public double getAmount(){
		return this.amount;
	}
	
	/** @param amount See {@link #amount} */
	public void setAmount(double amount){
		this.amount = amount;
	}
	
	/** @return See {@link #button} */
	public MenuScrollerButton<D> getButton(){
		return this.button;
	}
	
	/** @return The amount of distance the child of this {@link MenuScroller} currently has scrolled */
	public double getScrolledAmount(){
		return this.getPercent() * this.getAmount();
	}
	
	/** @return The percentage of the way down this {@link MenuScroller} has moved, in the range [0, 1] */
	public double getPercent(){
		return this.scroller.getAmount();
	}
	
	/** @return See {@link #basePosition} */
	public double getBasePosition(){
		return this.basePosition;
	}
	
	/** @return See {@link #scrollWheelStrength} */
	public double getScrollWheelStrength(){
		return this.scrollWheelStrength;
	}
	
	/** @param scrollWheelStrength See {@link #scrollWheelStrength} */
	public void setScrollWheelStrength(double scrollWheelStrength){
		this.scrollWheelStrength = scrollWheelStrength;
	}
	
	/** @return See {@link #scrollWheelAsPercent} */
	public boolean isScrollWheelAsPercent(){
		return this.scrollWheelAsPercent;
	}
	
	/** @param scrollWheelAsPercent See {@link #scrollWheelAsPercent} */
	public void setScrollWheelAsPercent(boolean scrollWheelAsPercent){
		this.scrollWheelAsPercent = scrollWheelAsPercent;
	}
	
	/** @return See {@link #scrollWheelInverse} */
	public boolean isScrollWheelInverse(){
		return this.scrollWheelInverse;
	}
	
	/** @param scrollWheelInverse See {@link #scrollWheelInverse} */
	public void setScrollWheelInverse(boolean scrollWheelInverse){
		this.scrollWheelInverse = scrollWheelInverse;
	}
	
	/** @return See {@link #scrollWheelEnabled} */
	public boolean isScrollWheelEnabled(){
		return this.scrollWheelEnabled;
	}
	
	/** @param enabled See {@link #scrollWheelEnabled} */
	public void setScrollWheelEnabled(boolean enabled){
		this.scrollWheelEnabled = enabled;
	}
	
}
