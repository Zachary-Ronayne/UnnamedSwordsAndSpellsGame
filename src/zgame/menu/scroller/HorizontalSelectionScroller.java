package zgame.menu.scroller;

import org.lwjgl.glfw.GLFW;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuThing;
import zgame.menu.format.PercentFormatter;

/** A {@link HorizontalScroller} which is used for selecting a percentage, rather than scrolling space */
public class HorizontalSelectionScroller extends HorizontalScroller{
	
	/** The minimum value usable by this scroller */
	private final double min;
	/** The maximum value usable by this scroller */
	private final double max;
	
	/**
	 * Create a new {@link HorizontalScroller} at the specified location and min and max
	 *
	 * @param min See {@link #min}
	 * @param max See {@link #max}
	 * @param baseThing The thing which will be used as a reference for how big this scroller should be. This thing will take up the entirety of the width of the given thing
	 */
	public HorizontalSelectionScroller(double min, double max, MenuThing baseThing){
		super(0, 0, baseThing.getWidth(), baseThing.getHeight(), baseThing.getWidth());
		this.min = min;
		this.max = max;
		
		this.removeBorder();
		this.invisible();
		this.getButton().setFill(new ZColor(.5, .3));
		this.setScrollerWidth(0.2);
		
		this.setAmount(1);
		this.setScrollWheelEnabled(false);
		this.setDraggableButton(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		
		var moveThing = new MenuThing();
		moveThing.setRelX(0);
		moveThing.setRelY(0);
		moveThing.invisible();
		moveThing.setWidth(this.getWidth());
		moveThing.setHeight(baseThing.getHeight());
		this.setMovingThing(moveThing);
		
		baseThing.addThing(this);
		this.format();
		this.getButton().format();
	}
	
	/** @param value Set the numerical value which this should be scrolled to */
	public void setScrolledValue(double value){
		// Set the scroller amount to the amount of the way through the total range the given value is, divided by the total range
		this.getButton().getScroller().setPercent((value - this.getMin()) / Math.abs(this.getMax() - this.getMin()));
	}
	
	/** @param perc The percentage of the total width of the thing holding this scroller which this scroller should take up */
	public void setScrollerWidth(double perc){
		this.getButton().setFormatter(new PercentFormatter(perc, 1.0, null, 0.5));
	}
	
	@Override
	public void scroll(double amount){
		super.scroll(amount);
		this.onScrollValueChange(this.getMin() + (this.getMax() - this.getMin()) * this.getPercent());
	}
	
	/**
	 * Called when this scroller scrolls. Does nothing by default, override for custom behavior
	 * @param value The value between {@link #min} and {@link #max} where the scroller currently is
	 */
	public void onScrollValueChange(double value){}
	
	/** @return See {@link #min} */
	public double getMin(){
		return this.min;
	}
	
	/** @return See {@link #max} */
	public double getMax(){
		return this.max;
	}
}
