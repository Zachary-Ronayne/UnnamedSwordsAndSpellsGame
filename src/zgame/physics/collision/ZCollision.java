package zgame.physics.collision;

import java.awt.geom.Line2D;

import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint;
import zgame.core.utils.ZRect;
import zgame.physics.material.Material;

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
	 * @param m The {@link Material} which was collided with
	 * @return A {@link CollisionResponse} representing the collision
	 */
	public static CollisionResponse rectToRectBasic(double cx, double cy, double cw, double ch, double x, double y, double w, double h, Material m){
		// If the rectangles do not intersect, then there was no collision
		ZRect unmoving = new ZRect(cx, cy, cw, ch);
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
		if(top || bottom){
			left = false;
			right = false;
		}
		return new CollisionResponse(xDis, yDis, left, right, top, bottom, m);
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
	 * @param m The {@link Material} which was collided with
	 * @return A {@link CollisionResponse} representing the collision
	 */
	public static CollisionResponse rectToRect(double cx, double cy, double cw, double ch, double x, double y, double w, double h, double px, double py, Material m){
		// If the current and previous positions of the colliding bounds are the same, then use the basic algorithm
		boolean onlyX = x == px;
		boolean onlyY = y == py;
		if(onlyX && onlyY) return rectToRectBasic(cx, cy, cw, ch, x, y, w, h, m);
		
		// If the rectangles do not intersect, then there was no collision
		ZRect unmoving = new ZRect(cx, cy, cw, ch);
		if(!unmoving.intersects(x, y, w, h)) return new CollisionResponse();
		// Initial Variable values
		double xDis = 0;
		double yDis = 0;
		boolean[] b = orientation(x, y, w, h, cx, cy, cw, ch);
		boolean left = b[0];
		boolean right = b[1];
		boolean top = b[2];
		boolean bottom = b[3];
		
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
			double angle = ZMath.lineAngle(x, y, px, py);
			double reverseAngle = angle + Math.PI;
			
			// Find the corner in the new bounds which should be compared, based on the direction of movement
			// Default to the upper left hand corner, i.e. movement is in the second quadrant
			double cornerX = x;
			double cornerY = y;
			double cornerPX = px;
			double cornerPY = py;
			// Upper right hand corner i.e. movement is in the first quadrant
			if(0 < angle && angle < ZMath.PI_BY_4){
				cornerX = x + w;
				cornerPX = px + w;
				xDis = -w;
			}
			// Lower right hand corner i.e. movement is in the fourth quadrant
			else if(-ZMath.PI_BY_4 < angle && angle < 0){
				cornerX = x + w;
				cornerY = y + h;
				cornerPX = px + w;
				cornerPY = py + h;
				xDis = -w;
				yDis = -h;
			}
			// Lower left hand corner i.e. movement is in the third quadrant
			else if(-ZMath.PI_BY_2 < angle && angle < -ZMath.PI_BY_4){
				cornerY = y + h;
				cornerPY = py + h;
				yDis = -h;
			}
			// Find the line between the corners of the current and previous bounds
			Line2D.Double moveLine = new Line2D.Double(cornerPX, cornerPY, cornerX, cornerY);
			
			// Using that line as a ray with the position (cornerX, cornerY) and moving in the opposite direction, find where that ray intersects the unmoving bounds
			// That point is the new corner of the new bounds
			ZPoint movePoint;
			// Left line
			movePoint = rectToRectHelper(new Line2D.Double(cx, cy, cx, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Right line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx + cw, cy, cx + cw, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Top line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx, cy, cx + w, cy), moveLine, cornerX, cornerY, reverseAngle);
			// Bot line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx, cy + h, cx + w, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Should never happen, but covering bases
			if(movePoint == null) movePoint = new ZPoint(x, y);
			
			// Find the distance to move
			xDis += movePoint.x - x;
			yDis += movePoint.y - y;
		}
		// Return response
		return new CollisionResponse(xDis, yDis, left, right, top, bottom, m);
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
	private static ZPoint rectToRectHelper(Line2D.Double line, Line2D.Double moveLine, double cornerX, double cornerY, double reverseAngle){
		// Find the intersection point for each line
		ZPoint intersection = ZMath.lineIntersection(line, moveLine);
		if(intersection == null) return null;
		
		// Determine which of those points is on the rectangle
		// Find that the angle to the point is in the direction of the ray, and the same coordinate as the unmoving bounds
		double angle = ZMath.lineAngle(cornerX, cornerY, intersection.x, intersection.y);
		boolean angleCorrect = angle == reverseAngle;
		boolean in = line.y1 <= intersection.y && intersection.y <= line.y2;
		if(angleCorrect && in) return intersection;
		return null;
	}
	
	/** @return {@link #rectToRectAprox(double, double, double, double, double, double, double, double, double, double, Material) with a default of 5 iterations. */
	public static CollisionResponse rectToRectAprox(double cx, double cy, double cw, double ch, double x, double y, double w, double h, double px, double py, Material m){
		return rectToRectAprox(cx, cy, cw, ch, x, y, w, h, px, py, 5, m);
	}
	
	/**
	 * Given the rectangular bounds of an unmoving object, and the rectangular bounds of an object to collide with the unmoving object, determine how the latter object should
	 * collide. This method approximates how the collision should occur by splitting the distance between the current location of the colliding bounds and a position where that
	 * bounds would not collide with the colliding bounds, and moving it to a place near, but not colliding with the colliding bounds.
	 * This method does not account for the case where the previous position and new position both do not touch the colliding bounds, but would touch the colliding bounds, had the
	 * moving bounds properly went through the entire path it moved.
	 * I.e., the moving bounds will teleport through the colliding bounds the moving bounds moves too fast
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
	 * @param iterations The number of times to apply the algorithm to approximate the new position
	 * @param m The {@link Material} which was collided with
	 * @return A {@link CollisionResponse} representing the collision
	 */
	public static CollisionResponse rectToRectAprox(double cx, double cy, double cw, double ch, double x, double y, double w, double h, double px, double py, int iterations, Material m){
		// If the new and old positions are the same, use the basic collision
		if(x == px && y == py) return rectToRectBasic(cx, cy, cw, ch, x, y, w, h, m);
		
		// Find the bounds
		ZRect moving = new ZRect(x, y, w, h);
		ZRect prevMoving = new ZRect(px, py, w, h);
		ZRect colliding = new ZRect(cx, cy, cw, ch);
		
		// If the colliding and moving bounds do not touch, then return an empty response
		if(!moving.intersects(colliding)) return new CollisionResponse();
		
		// Initial Variable values
		double xDis = 0;
		double yDis = 0;
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bottom = false;
		ZRect newMoving;
		double dist;
		double angle = ZMath.lineAngle(x, y, px, py);
		double sinA = Math.sin(angle);
		double cosA = Math.cos(angle);
		
		// If the previous bounds intersects the colliding bounds, the bounds will be moved from the current position to somewhere guaranteed to not intersect
		if(prevMoving.intersects(colliding)){
			// If the moving bounds entirely contained by the colliding bounds, then base the new position on the colliding bounds
			if(moving.contains(colliding)) dist = ZMath.hypot(cw, ch);
			// Otherwise, base it on the moving bounds
			else dist = ZMath.hypot(w, h);
			// Find the new moving bounds
			newMoving = new ZRect(x + cosA * dist, y + sinA * dist, w, h);
		}
		// Otherwise, use the previous bounds as the starting point
		else newMoving = prevMoving;
		// For the number of given iterations, move the new moving bounds closer to the colliding bounds, ensuring they don't touch at the end of the method
		ZRect nearBounds = moving;
		ZRect farBounds = newMoving;
		ZRect newBounds;
		for(int i = 0; i < iterations; i++){
			/// Finding half the distance between the bounds
			dist = ZMath.hypot(nearBounds.x - farBounds.x, nearBounds.y - farBounds.y) * .5;
			newBounds = new ZRect(nearBounds.x + cosA * dist, nearBounds.y + sinA * dist, w, h);
			if(colliding.intersects(newBounds)) nearBounds = newBounds;
			else farBounds = newBounds;
		}
		// Find the distance between the original moving bounds and the new position for the bounds
		xDis = farBounds.x - moving.x;
		yDis = farBounds.y - moving.y;
		
		// Determine which walls were touched
		boolean[] b = orientation(x, y, w, h, cx, cy, cw, ch);
		boolean toLeft = b[0];
		boolean toRight = b[1];
		boolean above = b[2];
		boolean below = b[3];
		if(Math.abs(sinA) > .5){
			if(above) bottom = true;
			if(below) top = true;
		}
		if(Math.abs(cosA) > .5){
			if(toLeft) left = true;
			if(toRight) right = true;
		}
		// Return response
		return new CollisionResponse(xDis, yDis, left, right, top, bottom, m);
		
	}
	
	/**
	 * Determine the relative orientation of two rectangles
	 * 
	 * @param x The upper left hand x coordinate of the first rectangle
	 * @param y The upper left hand y coordinate of the first rectangle
	 * @param w The width of the first rectangle
	 * @param h The height of the first rectangle
	 * @param cx The upper left hand x coordinate of the second rectangle
	 * @param cy The upper left hand x coordinate of the second rectangle
	 * @param cw The width of the second rectangle
	 * @param ch The height of the second rectangle
	 * @return An array of 4 booleans, indexed as:
	 *         0: first is left of second
	 *         1: first is right of second
	 *         2: first is above second
	 *         3: first is below second
	 */
	public static boolean[] orientation(double x, double y, double w, double h, double cx, double cy, double cw, double ch){
		boolean[] b = new boolean[]{toLeft(x, cx), toRight(x, w, cx, cw), above(y, cy), below(y, h, cy, ch)};
		return b;
	}
	
	/**
	 * Determine if a bounds is to the left of another
	 * See {@link #big(double, double, double, double)} for details
	 */
	public static boolean toLeft(double x, double cx){
		return small(x, cx);
	}
	
	/**
	 * Determine if a bounds is to the right of another
	 * See {@link #big(double, double, double, double)} for details
	 */
	public static boolean toRight(double x, double w, double cx, double cw){
		return big(x, w, cx, cw);
	}
	
	/**
	 * Determine if a bounds is above another
	 * See {@link #big(double, double, double, double)} for details
	 */
	public static boolean above(double y, double cy){
		return small(y, cy);
	}
	
	/**
	 * Determine if a bounds is below another
	 * See {@link #big(double, double, double, double)} for details
	 */
	public static boolean below(double y, double h, double cy, double ch){
		return big(y, h, cy, ch);
	}
	
	/**
	 * Treat n and cn as the lower coordinate of two rectangle's axis, and determine if n is smaller than cn.
	 * Used for determining if the relative position of rectangles.
	 * Essentially, check if n is to the left or above c
	 * 
	 * @param n The coordinate of the rectangle to check
	 * @param cn The coordinate of the rectangle to check against
	 * @return true if n is smaller, false otherwise
	 */
	private static boolean small(double n, double cn){
		return n < cn;
	}
	
	/**
	 * Treat n and cn as the lower coordinate of two rectangle's axis, and s and cs as the sizes, and determine if n is larger than cn.
	 * Used for determining if the relative position of rectangles
	 * Essentially, check if n is to the right or below cn
	 * 
	 * @param n The coordinate of the rectangle to check
	 * @param cn The coordinate of the rectangle to check against
	 * @param s The size of the rectangle to check
	 * @param cs The size of the rectangle to check against
	 * @return true if n is smaller, false otherwise
	 */
	private static boolean big(double n, double s, double cn, double cs){
		return n + s > cn + cs;
	}
	
	/** Cannot instantiate {@link ZCollision} */
	private ZCollision(){
	}
	
}
