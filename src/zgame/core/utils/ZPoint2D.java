package zgame.core.utils;

import java.awt.geom.Point2D;

/** An object that literally just wraps Point2D.Double to make it less annoying to work with */
public class ZPoint2D extends Point2D.Double{
	
	/**
	 * Constructs and initializes a {@link ZPoint2D} with coordinates (0,&nbsp;0).
	 */
	public ZPoint2D(){
		super(0, 0);
	}
	
	/**
	 * Constructs and initializes a {@code Point2D} with the specified coordinates.
	 *
	 * @param x the X coordinate of the newly constructed {@link ZPoint2D}
	 * @param y the Y coordinate of the newly constructed {@link ZPoint2D}
	 */
	public ZPoint2D(double x, double y){
		super(x, y);
	}
	
	/** @return A point with the same x and y values as this point, but as a different object */
	public ZPoint2D copy(){
		return new ZPoint2D(this.getX(), this.getY());
	}
	
}
