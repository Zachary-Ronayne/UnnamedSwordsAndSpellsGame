package zgame.core.utils;

import java.awt.geom.Rectangle2D;

/** A convenience class that just extends Rectangle2D.Double, making it easier to code with */
public class ZRect extends Rectangle2D.Double{
	
	public ZRect(){
		super();
	}
	
	public ZRect(double x, double y, double w, double h){
		super(x, y, w, h);
	}
	
	/**
	 * Create a new rectangle with the given amount of padding
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()()}
	 * @param h See {@link #getHeight()()}
	 * @param padding An amount to add around the rectangle on all sides
	 */
	public ZRect(double x, double y, double w, double h, double padding){
		super(x - padding, y - padding, w + padding * 2, h + padding * 2);
	}
	
	/**
	 * Create a new rectangle with the same size as the given one, but with the given amount of padding
	 * @param r The base rectangle
	 * @param padding An amount to add around the rectangle on all sides
	 */
	public ZRect(ZRect r, double padding){
		super(r.getX() - padding, r.getY() - padding, r.getWidth() + padding * 2, r.getHeight() + padding * 2);
	}
	
}