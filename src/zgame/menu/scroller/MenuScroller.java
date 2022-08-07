package zgame.menu.scroller;

import java.util.Collection;
import java.util.List;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuThing;

/**
 * A {@link MenuThing} that can move other {@link MenuThing}s around.
 * The specified child element of this {@link MenuScroller} will have its positions moved when this scroller moves
 * This object can only have one child element. Adding additional elements will replace the current one
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public class MenuScroller<D>extends MenuThing<D>{
	
	/** The amount scrolled */
	private ScrollAxis scroller;
	
	/** The amount this scroller can move on its axis {@link MenuThing}s to */
	private double amount;

	/** The position of the child element of this {@link MenuScroller} */
	private double basePosition;
	
	/**
	 * Create a new {@link MenuScroller} at the specified location and min and max
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param amount See {@link #amount}
	 */
	public MenuScroller(double x, double y, double amount){
		super(x, y);
		// TODO make these options
		this.setWidth(20);
		this.setHeight(350);
		this.amount = amount;
		this.scroller = new ScrollAxis();
	}

	@Override
	public void tick(Game<D> game, double dt){
		super.tick(game, dt);
		for(MenuThing<D> thing : this.getThings()){
			// TODO split this into two classes, one for vertical, one for horizontal
			thing.setRelY(this.getBasePosition() + this.getScrolledAmount());
		}
	}
	
	@Override
	public void render(Game<D> game, Renderer r){
		super.render(game, r);
		// TODO make all of these colors and stuff options
		r.setColor(new ZColor(0));
		r.drawRectangle(this.getX(), this.getY() + (this.getPercent() * (this.getHeight() - this.getWidth())), this.getWidth(), this.getWidth());
	}
	
	@Override
	public void mouseWheelMove(Game<D> game, double amount){
		super.mouseWheelMove(game, amount);
		this.scroller.scroll(amount * -0.1);
	}

	/**
	 * See {@link MenuThing#addThing(MenuThing)}
	 * For a {@link MenuScroller}, adding a new thing will remove all existing things, as this thing can only hold one child thing. 
	 * Successfully adding a new thing will also set the current position of thing as {@link #basePosition}.
	 * From there, that will be the minimum value it will scroll to from this {@link MenuScroller}, and adding {@link #amount} will be the maximum it will scroll to
	 */
	@Override
	public boolean addThing(MenuThing<D> thing){
		// Save the current things
		Collection<MenuThing<D>> things = this.getThings();
		Collection<MenuThing<D>> oldThings = List.copyOf(things);

		// Clear out the old things and add the new thing
		things.clear();
		boolean success = super.addThing(thing);

		// If the new thing failed to add, empty out the things and add the old things back
		if(!success){
			things.clear();
			things.addAll(oldThings);
		}
		else{
			// TODO make options for x or y axis
			this.basePosition = thing.getRelY();
		}

		return success;
	}
	
	/** @return See {@link #amount} */
	public double getAmount(){
		return this.amount;
	}
	
	/** @param amount See {@link #amount} */
	public void setAmount(double amount){
		this.amount = amount;
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

}
