package zgame.physics.collision;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import zgame.core.utils.ZMathUtils;

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
		// If the rectangles do not intersect, then there was no collision
		Rectangle2D.Double unmoving = new Rectangle2D.Double(cx, cy, cw, ch);
		if(!unmoving.intersects(x, y, w, h)){
			var r = new CollisionResponse();
			return r;
		}
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
	 * @param px The x coordinate of the location of the bounds in the previous instance of time
	 * @param py The y coordinate of the location of the bounds in the previous instance of time
	 * @return A {@link CollisionResponse} representing the collision
	 */
	public static CollisionResponse rectToRect(double cx, double cy, double cw, double ch, double x, double y, double w, double h, double px, double py){
		// If the current and previous positions of the colliding bounds are the same, then use the basic algorithm
		boolean onlyX = x == px;
		boolean onlyY = y == py;
		if(onlyX && onlyY) return rectToRectBasic(cx, cy, cw, ch, x, y, w, h);
		
		// TODO abstract this block out
		// If the rectangles do not intersect, then there was no collision
		Rectangle2D.Double unmoving = new Rectangle2D.Double(cx, cy, cw, ch);
		if(!unmoving.intersects(x, y, w, h)){
			var r = new CollisionResponse();
			return r;
		}
		// Initial Variable values
		double xDis = 0;
		double yDis = 0;
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bottom = false;

		// TODO Abstract this out
		boolean toLeft = x < cx;
		boolean toRight = x + w > cx + cw;
		boolean above = y < cy;
		boolean below = y + h > cy + ch;
		if(toLeft) left = true;
		if(toRight) right = true;
		if(above) bottom = true;
		if(below) top = true;
		
		// Handle purely horizontal and purely vertical cases, i.e. only need to reposition based on one axis
		// If the x values are the same, it is only horizontal motion
		if(onlyY){
			// Need to move to the left
			if(px < x) xDis = cx - (x + w);
			// Need to move to the right
			else xDis = cx + cw - x;
		}
		// If the y values are the same, it is only vertical motion
		else if(onlyX){
			// Need to move up
			if(py < y) yDis = cy - (y + h);
			// Need to move down
			else yDis = cy + ch - y;
		}
		else{
			// Find the movement angle
			double angle = ZMathUtils.lineAngle(x, y, px, py);
			double reverseAngle = angle + Math.PI;
			
			// Find the corner in the new bounds which should be compared, based on the direction of movement
			// Default to the upper left hand corner, i.e. movement is in the second quadrant
			double cornerX = x;
			double cornerY = y;
			double cornerPX = px;
			double cornerPY = py;
			// Upper right hand corner i.e. movement is in the first quadrant
			if(0 < angle && angle < ZMathUtils.PI_BY_4){
				cornerX = x + w;
				cornerPX = px + w;
				xDis = -w;
			}
			// Lower right hand corner i.e. movement is in the fourth quadrant
			else if(-ZMathUtils.PI_BY_4 < angle && angle < 0){
				cornerX = x + w;
				cornerY = y + h;
				cornerPX = px + w;
				cornerPY = py + h;
				xDis = -w;
				yDis = -h;
			}
			// Lower left hand corner i.e. movement is in the third quadrant
			else if(-ZMathUtils.PI_BY_2 < angle && angle < -ZMathUtils.PI_BY_4){
				cornerY = y + h;
				cornerPY = py + h;
				yDis = -h;
			}
			// Find the line between the corners of the current and previous bounds
			Line2D.Double moveLine = new Line2D.Double(cornerPX, cornerPY, cornerX, cornerY);
			
			// Using that line as a ray with the position (cornerX, cornerY) and moving in the opposite direction, find where that ray intersects the unmoving bounds
			// That point is the new corner of the new bounds
			Point2D.Double movePoint;
			// Left line
			movePoint = rectToRectHelper(new Line2D.Double(cx, cy, cx, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Right line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx + cw, cy, cx + cw, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Top line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx, cy, cx + w, cy), moveLine, cornerX, cornerY, reverseAngle);
			// Bot line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx, cy + h, cx + w, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Should never happen, but covering bases
			if(movePoint == null) movePoint = new Point2D.Double(x, y);

			// Find the distance to move
			xDis += movePoint.x - x;
			yDis += movePoint.y - y;
		}
		// Return response
		return new CollisionResponse(xDis, yDis, left, right, top, bottom);
	}
	
	/**
	 * A helper method for {@link #rectToRect(double, double, double, double, double, double, double, double, double, double)} for handling comparing lines of a rectangle
	 * 
	 * @param line The line of the rectangle
	 * @param moveLine The line which the colliding object is moving on
	 * @param cornerX The x coordinate value of the corner of the colliding object
	 * @param cornerY The y coordinate value of the corner of the colliding object
	 * @param reverseAngle The opposite angle of how the colliding object moved
	 * @return The point to reposition the object if the line is a valid line for where the colliding object should be repositioned, otherwise null
	 */
	private static Point2D.Double rectToRectHelper(Line2D.Double line, Line2D.Double moveLine, double cornerX, double cornerY, double reverseAngle){
		// Find the intersection point for each line
		Point2D.Double intersection = ZMathUtils.lineIntersection(line, moveLine);
		if(intersection == null) return null;
		
		// Determine which of those points is on the rectangle
		// Find that the angle to the point is in the direction of the ray, and the same coordinate as the unmoving bounds
		double angle = ZMathUtils.lineAngle(cornerX, cornerY, intersection.x, intersection.y);
		boolean angleCorrect = angle == reverseAngle;
		boolean in = line.y1 <= intersection.y && intersection.y <= line.y2;
		if(angleCorrect && in) return intersection;
		return null;
	}
	
	/** Cannot instantiate {@link ZCollision} */
	private ZCollision(){
	}
	
}
