package zgame.menu.scroller;

import zgame.core.utils.ZMath;

/** An object used by {@link MenuScroller} to manage the position of its scrolling */
public class ScrollAxis{
	
	/** The percentage scrolled through the axis, always in the range [0, 1] */
	private double amount;
	
	/** Create new {@link ScrollAxis} with no real minimum or maximum */
	public ScrollAxis(){
		this.amount = 0;
	}
	
	/** @return See {@link #amount} */
	public double getAmount(){
		return this.amount;
	}
	
	/**
	 * Set the amount and automatically adjust the position so it stays in the range [0, 1]
	 *
	 * @param amount See {@link #amount}
	 */
	public void setAmount(double amount){
		this.amount = ZMath.minMax(0, 1, amount);
	}
	
	/**
	 * Scroll this {@link ScrollAxis} by the given amount, and automatically adjust the position so it stays in the range [0, 1]
	 *
	 * @param amount The amount to move by
	 */
	public void scroll(double amount){
		this.setAmount(this.getAmount() + amount);
	}
	
}
