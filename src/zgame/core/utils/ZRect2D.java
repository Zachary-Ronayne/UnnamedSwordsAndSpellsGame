package zgame.core.utils;

import java.awt.geom.Rectangle2D;

/** A convenience class that just extends Rectangle2D.Double, making it easier to code with */
public class ZRect2D extends Rectangle2D.Double{
	
	/** Create a new blank rectangle */
	public ZRect2D(){
		super();
	}
	
	/** @param r A rectangle to make a copy of */
	public ZRect2D(Rectangle2D r){
		super(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Create a new rectangle with the given size
	 *
	 * @param x The x coordinate of the upper left hand corner
	 * @param y The y coordinate of the upper left hand corner
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 */
	public ZRect2D(double x, double y, double w, double h){
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
	public ZRect2D(double x, double y, double w, double h, double padding){
		super(x - padding, y - padding, w + padding * 2, h + padding * 2);
	}
	
	/**
	 * Create a new rectangle with the same size as the given one, but with the given amount of padding
	 *
	 * @param r The base rectangle
	 * @param padding An amount to add around the rectangle on all sides
	 */
	public ZRect2D(ZRect2D r, double padding){
		this(r.getX(), r.getY(), r.getWidth(), r.getHeight(), padding);
	}
	
	/**
	 * Create a new rectangle shifted by the given amount
	 *
	 * @param r The base rectangle
	 * @param xShift An amount to move on the x axis
	 * @param yShift An amount to move on the y axis
	 */
	public ZRect2D(ZRect2D r, double xShift, double yShift){
		this(r.getX() + xShift, r.getY() + yShift, r.getWidth(), r.getHeight());
	}
	
	/**
	 * @param padding The distance to add in every direction of this {@link ZRect2D}
	 * @return The resulting rectangle as a new object
	 */
	public ZRect2D pad(double padding){
		return new ZRect2D(this, padding);
	}
	
	/**
	 * @param x The new x coordinate for a rectangle
	 * @return The resulting rectangle as a new object
	 */
	public ZRect2D x(double x){
		var r = new ZRect2D(this);
		r.x = x;
		return r;
	}
	
	/**
	 * @param y The new x coordinate for a rectangle
	 * @return The resulting rectangle as a new object
	 */
	public ZRect2D y(double y){
		var r = new ZRect2D(this);
		r.y = y;
		return r;
	}
	
	/**
	 * @param w The new width for a rectangle
	 * @return The resulting rectangle as a new object
	 */
	public ZRect2D width(double w){
		var r = new ZRect2D(this);
		r.width = w;
		return r;
	}
	
	/**
	 * @param h The new height for a rectangle
	 * @return The resulting rectangle as a new object
	 */
	public ZRect2D height(double h){
		var r = new ZRect2D(this);
		r.height = h;
		return r;
	}
	
}