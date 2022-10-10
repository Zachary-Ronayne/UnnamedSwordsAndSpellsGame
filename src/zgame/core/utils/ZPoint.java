package zgame.core.utils;

import java.awt.geom.Point2D;

/** An object that literally just wraps Point2D.Double to make it less annoying to work with */
public class ZPoint extends Point2D.Double{
	
	/**
	 * Constructs and initializes a {@code Point2D} with
	 * coordinates (0,&nbsp;0).
	 */
	public ZPoint(){
		super(0, 0);
	}
	
	/**
	 * Constructs and initializes a {@code Point2D} with the
	 * specified coordinates.
	 *
	 * @param x the X coordinate of the newly
	 *        constructed {@code Point2D}
	 * @param y the Y coordinate of the newly
	 *        constructed {@code Point2D}
	 */
	public ZPoint(double x, double y){
		super(x, y);
	}

	/** @return A point with the same x and y values as this point, but as a different object */
	public ZPoint copy(){
		return new ZPoint(this.getX(), this.getY());
	}
	
}
