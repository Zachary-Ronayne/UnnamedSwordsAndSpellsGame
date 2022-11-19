package zgame.core.utils;

import java.awt.geom.Rectangle2D;

/** A convenience class that just extends Rectangle2D.Double, making it easier to code with */
public class ZRect extends Rectangle2D.Double{
	
	public ZRect(){
		super();
	}
	
	/**
	 * Create a new rectangle with the given size
	 * 
	 * @param x The x coordinate of the upper left hand corner
	 * @param y The y coordinate of the upper left hand corner
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 */
	public ZRect(double x, double y, double w, double h){
		super(x, y, w, h);
	}
	
	/**
	 * Create a new rectangle with the given amount of padding
	 * 
	 * @param x The x coordinate of the upper left hand corner
	 * @param y The y coordinate of the upper left hand corner
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @param padding An amount to add around the rectangle on all sides
	 */
	public ZRect(double x, double y, double w, double h, double padding){
		super(x - padding, y - padding, w + padding * 2, h + padding * 2);
	}
	
	/**
	 * Create a new rectangle with the same size as the given one, but with the given amount of padding
	 * 
	 * @param r The base rectangle
	 * @param padding An amount to add around the rectangle on all sides
	 */
	public ZRect(ZRect r, double padding){
		this(r.getX(), r.getY(), r.getWidth(), r.getHeight(), padding * 2);
	}

	/**
	 * @param padding The distance to add in every direction of this {@link ZRect}
	 * @return The resulting rectangle
	 */
	public ZRect pad(double padding){
		return new ZRect(this, padding);
	}
	
}