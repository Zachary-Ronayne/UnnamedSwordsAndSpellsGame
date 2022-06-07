package zgame.physics.collision;

import java.awt.geom.Rectangle2D;

/** A class containing methods for calculating where objects should move when colliding */
public final class ZCollision{
	
	/**
	 * Given the rectangular bounds of an unmoving object, and the rectangular bounds of an object to collide with the unmoving object, determine how the latter object should
	 * collide
	 * 
	 * All coordinates are treated as the upper left hand corner of the bounds
	 * 
	 * @param cx The x coordinate of the unmoving bounds
	 * @param cy The y coordinate of the unmoving bounds
	 * @param cw The width of the unmoving bounds
	 * @param ch The height of the unmoving bounds
	 * @param x The x coordinate of the bounds to collide
	 * @param y The y coordinate of the bounds to collide
	 * @param w The width of the bounds to collide
	 * @param h The height of the bounds to collide
	 * @return A {@link CollisionResponse} representing the collision
	 */
	public static CollisionResponse rectToRectBasic(double cx, double cy, double cw, double ch, double x, double y, double w, double h){
		// TODO do something with angles to better determine movement, maybe that should be its own method

		// If the rectangles do not intersect, then there was no collision
		Rectangle2D.Double unmoving = new Rectangle2D.Double(cx, cy, cw, ch);
		if(!unmoving.intersects(x, y, w, h)){
			var r = new CollisionResponse();
			return r;
		};
		
		// Initial Variable values
		double xDis = 0;
		double yDis = 0;
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bottom = false;
		
		// Determining the position of the colliding object relative to the unmoving object
		boolean toLeft = x < cx;
		boolean toRight = x + w > cx + cw;
		boolean above = y < cy;
		boolean below = y + h > cy + ch;

		// The colliding object is to the left of the unmoving object
		if(toLeft) xDis = x + w - cx;
		// The colliding object is to the right of the unmoving object
		else if(toRight) xDis = cx + cw - x;

		// The colliding object is above the unmoving object
		if(above) yDis = y + h - cy;
		// The colliding object is below the unmoving object
		else if(below) yDis = cy + ch - y;
		
		// Prioritize moving on the axis which has moved more, if the x axis moved more, move on the y axis
		if(Math.abs(yDis) < Math.abs(xDis)){
			// The floor was collided with
			if(above){
				yDis = cy - h - y;
				bottom = true;
			}
			// The ceiling was collided with
			else if(below){
				yDis = cy + ch - y;
				top = true;
			}
			if(unmoving.intersects(x, y + yDis, w, h)){
				if(toLeft){
					xDis = cx - w - x;
					right = true;
				}
				else if(toRight){
					xDis = cx + cw - x;
					left = true;
				}
			}
			else xDis = 0;
		}
		// The y axis moved more
		else{
			// The right wall was collided with
			if(toLeft){
				xDis = cx - w - x;
				right = true;
			}
			// The left wall was hit
			else if(toRight){
				xDis = cx + cw - x;
				left = true;
			}
			if(unmoving.intersects(x + xDis, y, w, h)){
				if(above){
					yDis = cy - h - y;
					bottom = true;
				}
				else if(below){
					yDis = cy + ch - y;
					top = true;
				}
			}
			else yDis = 0;
		}

		return new CollisionResponse(xDis, yDis, left, right, top, bottom);
	}
	
	/** Cannot instantiate {@link ZCollision} */
	private ZCollision(){
	}
	
}
