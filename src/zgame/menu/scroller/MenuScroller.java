package zgame.menu.scroller;

import zgame.core.Game;
import zgame.menu.MenuThing;

/**
 * A {@link MenuThing} that can move other {@link MenuThing}s around. The specified child element of this {@link MenuScroller} will have its positions moved when this scroller
 * moves This object can only have one child element. Adding additional elements will replace the current one
 */
public abstract class MenuScroller extends MenuThing{
	
	/** The amount scrolled */
	private final ScrollAxis axis;
	
	/** The amount this scroller can move on its axis {@link MenuThing}s to */
	private double amount;
	
	/** The position of the child element of this {@link MenuScroller} */
	private double basePosition;
	
	/** The {@link MenuThing} which will be moved by this {@link MenuScroller} */
	private MenuThing movingThing;
	
	/** The button to use for scrolling */
	private final MenuScrollerButton button;
	
	/** The degree to which the scroll wheel will scroll. Also see {@link #scrollWheelAsPercent} */
	private double scrollWheelStrength;
	/** true if {@link #scrollWheelStrength} is the percentage of the scroll that should move per mouse wheel, false if it should be a distance */
	private boolean scrollWheelAsPercent;
	/** true if {@link #scrollWheelAsPercent} and {@link #scrollWheelStrength} should be based on the size of the scroll bar, false if it should be based on {@link #amount} */
	private boolean scrollByBar;
	/** true if the scroll wheel's direction should be inverted, false otherwise */
	private boolean scrollWheelInverse;
	/** true if this scroll wheel should interact, false otherwise */
	private boolean scrollWheelEnabled;
	
	/** true if the direction the scroller moves should be inverted, false otherwise */
	private boolean inverseMove;
	
	/**
	 * Create a new {@link MenuScroller} at the specified location and min and max
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param amount See {@link #amount}
	 * @param game The game associated with this thing
	 */
	public MenuScroller(double x, double y, double w, double h, double amount, Game game){
		super(x, y);
		this.scrollWheelStrength = 0.1;
		this.scrollWheelAsPercent = true;
		this.scrollByBar = false;
		this.scrollWheelInverse = true;
		this.scrollWheelEnabled = true;
		this.setWidth(w);
		this.setHeight(h);
		this.amount = amount;
		this.axis = new ScrollAxis();
		this.button = this.generateButton(game);
		super.addThing(button);
		this.movingThing = null;
		
		this.inverseMove = false;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.button.destroy();
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		MenuThing thing = this.getMovingThing();
		if(thing != null) this.button.updateRelativePosition(thing);
	}
	
	@Override
	public boolean mouseWheelMove(Game game, double amount){
		boolean input = super.mouseWheelMove(game, amount);
		
		if(!this.isScrollWheelEnabled()) return input;
		amount *= this.getScrollWheelStrength();
		if(this.isScrollWheelInverse()) amount = -amount;
		if(!this.isScrollWheelAsPercent()) {
			if(this.isScrollByBar()) amount = this.button.scrollToPercent(amount);
			else amount = amount / this.getAmount();
		}
		this.scroll(amount);
		return true;
	}
	
	/** @param thing See {@link #movingThing} */
	public void setMovingThing(MenuThing thing){
		this.movingThing = thing;
		// Update the position of this scroller
		this.basePosition = this.button.findBasePosition(thing);
	}
	
	/** @return See {@link #movingThing} */
	public MenuThing getMovingThing(){
		return this.movingThing;
	}
	
	/**
	 * @param game The game associated with this thing
	 * @return A MenuScrollerButton implemented to move the scroll button around in the desired way
	 */
	public abstract MenuScrollerButton generateButton(Game game);
	
	/** @param amount The amount to scroll this menu scroller by, as a percentage */
	public void scroll(double amount){
		this.axis.scroll(amount);
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
	public MenuScrollerButton getButton(){
		return this.button;
	}
	
	/** @return The amount of distance the child of this {@link MenuScroller} currently has scrolled */
	public double getScrolledAmount(){
		return (this.isInverseMove() ? -1 : 1) * this.getPercent() * this.getAmount();
	}
	
	/** @return The percentage of the way down this {@link MenuScroller} has moved, in the range [0, 1] */
	public double getPercent(){
		return this.axis.getAmount();
	}
	
	/** @param perc The percentage of the way this scroller should be scrolled */
	public void setPercent(double perc){
		this.axis.setAmount(perc);
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
	
	/** @return See {@link #scrollByBar} */
	public boolean isScrollByBar(){
		return this.scrollByBar;
	}
	
	/** @param scrollByBar See {@link #scrollByBar} */
	public void setScrollByBar(boolean scrollByBar){
		this.scrollByBar = scrollByBar;
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
	
	/** @return See {@link #inverseMove} */
	public boolean isInverseMove(){
		return this.inverseMove;
	}
	
	/** @param inverseMove See {@link #inverseMove} */
	public void setInverseMove(boolean inverseMove){
		this.inverseMove = inverseMove;
	}
}
