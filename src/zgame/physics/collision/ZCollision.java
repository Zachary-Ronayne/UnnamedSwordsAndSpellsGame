package zgame.physics.collision;

import java.awt.geom.Line2D;

import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint2D;
import zgame.core.utils.ZRect2D;
import zgame.physics.material.Material;

/** A class containing methods for calculating where objects should move when colliding */
public final class ZCollision{
	
	/**
	 * Given the rectangular bounds of an unmoving object, and the rectangular bounds of an object to collide with the unmoving object, determine how the latter object should
	 * collide
	 * <p>
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
	 * @return A {@link CollisionResult2D} representing the collision
	 */
	public static CollisionResult2D rectToRectBasic(double cx, double cy, double cw, double ch, double x, double y, double w, double h, Material m){
		// If the rectangles do not intersect, then there was no collision
		ZRect2D unmoving = new ZRect2D(cx, cy, cw, ch);
		if(!unmoving.intersects(x, y, w, h)) return new CollisionResult2D();
		// Initial Variable values
		double xDis;
		double yDis;
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bottom = false;
		
		// Determining the position of the colliding object relative to the unmoving object
		boolean toLeft = x < cx;
		boolean toRight = x + w > cx + cw;
		boolean above = y < cy;
		boolean below = y + h > cy + ch;
		
		boolean leftCenter = x + w * 0.5 < cx + cw * 0.5;
		boolean aboveCenter = y + h * 0.5 < cy + cy * 0.5;
		
		// The colliding object is to the left of the unmoving object
		if(toLeft || !toRight && leftCenter){
			xDis = x + w - cx;
		}
		// The colliding object is to the right of the unmoving object
		else xDis = cx + cw - x;
		
		// The colliding object is above the unmoving object
		if(above || !below && aboveCenter){
			yDis = y + h - cy;
		}
		// The colliding object is below the unmoving object
		else yDis = cy + ch - y;
		
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
		return new CollisionResult2D(xDis, yDis, left, right, top, bottom, m);
	}
	
	/**
	 * Given the rectangular bounds of an unmoving object, and the rectangular bounds of an object to collide with the unmoving object, determine how the latter object should
	 * collide
	 * <p>
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
	 * @return A {@link CollisionResult2D} representing the collision
	 */
	public static CollisionResult2D rectToRect(double cx, double cy, double cw, double ch, double x, double y, double w, double h, double px, double py, Material m){
		// If the current and previous positions of the colliding bounds are the same, then use the basic algorithm
		boolean onlyX = x == px;
		boolean onlyY = y == py;
		if(onlyX && onlyY) return rectToRectBasic(cx, cy, cw, ch, x, y, w, h, m);
		
		// If the rectangles do not intersect, then there was no collision
		ZRect2D unmoving = new ZRect2D(cx, cy, cw, ch);
		if(!unmoving.intersects(x, y, w, h)) return new CollisionResult2D();
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
			ZPoint2D movePoint;
			// Left line
			movePoint = rectToRectHelper(new Line2D.Double(cx, cy, cx, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Right line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx + cw, cy, cx + cw, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Top line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx, cy, cx + w, cy), moveLine, cornerX, cornerY, reverseAngle);
			// Bot line
			if(movePoint == null) movePoint = rectToRectHelper(new Line2D.Double(cx, cy + h, cx + w, cy + h), moveLine, cornerX, cornerY, reverseAngle);
			// Should never happen, but covering bases
			if(movePoint == null) movePoint = new ZPoint2D(x, y);
			
			// Find the distance to move
			xDis += movePoint.x - x;
			yDis += movePoint.y - y;
		}
		// Return response
		return new CollisionResult2D(xDis, yDis, left, right, top, bottom, m);
	}
	
	/**
	 * A helper method for {@link #rectToRect(double, double, double, double, double, double, double, double, double, double, Material)} for handling comparing lines of a
	 * rectangle
	 *
	 * @param line The line of the rectangle
	 * @param moveLine The line which the colliding object is moving on
	 * @param cornerX The x coordinate value of the corner of the colliding object
	 * @param cornerY The y coordinate value of the corner of the colliding object
	 * @param reverseAngle The opposite angle of how the colliding object moved
	 * @return The point to reposition the object if the line is a valid line for where the colliding object should be repositioned, otherwise null
	 */
	private static ZPoint2D rectToRectHelper(Line2D.Double line, Line2D.Double moveLine, double cornerX, double cornerY, double reverseAngle){
		// Find the intersection point for each line
		ZPoint2D intersection = ZMath.lineIntersection(line, moveLine);
		if(intersection == null) return null;
		
		// Determine which of those points is on the rectangle
		// Find that the angle to the point is in the direction of the ray, and the same coordinate as the unmoving bounds
		double angle = ZMath.lineAngle(cornerX, cornerY, intersection.x, intersection.y);
		boolean angleCorrect = angle == reverseAngle;
		boolean in = line.y1 <= intersection.y && intersection.y <= line.y2;
		if(angleCorrect && in) return intersection;
		return null;
	}
	
	/** @return {@link #rectToRectAprox(double, double, double, double, double, double, double, double, double, double, int, Material)}  with a default of 5 iterations. */
	public static CollisionResult2D rectToRectAprox(double cx, double cy, double cw, double ch, double x, double y, double w, double h, double px, double py, Material m){
		return rectToRectAprox(cx, cy, cw, ch, x, y, w, h, px, py, 5, m);
	}
	
	/**
	 * Given the rectangular bounds of an unmoving object, and the rectangular bounds of an object to collide with the unmoving object, determine how the latter object should
	 * collide. This method approximates how the collision should occur by splitting the distance between the current location of the colliding bounds and a position where
	 * that bounds would not collide with the colliding bounds, and moving it to a place near, but not colliding with the colliding bounds. This method does not account for
	 * the case where the previous position and new position both do not touch the colliding bounds, but would touch the colliding bounds, had the moving bounds properly went
	 * through the entire path it moved. I.e., the moving bounds will teleport through the colliding bounds the moving bounds moves too fast
	 * <p>
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
	 * @return A {@link CollisionResult2D} representing the collision
	 */
	public static CollisionResult2D rectToRectAprox(double cx, double cy, double cw, double ch, double x, double y, double w, double h, double px, double py, int iterations, Material m){
		// If the new and old positions are the same, use the basic collision
		if(x == px && y == py) return rectToRectBasic(cx, cy, cw, ch, x, y, w, h, m);
		
		// Find the bounds
		ZRect2D moving = new ZRect2D(x, y, w, h);
		ZRect2D prevMoving = new ZRect2D(px, py, w, h);
		ZRect2D colliding = new ZRect2D(cx, cy, cw, ch);
		
		// If the colliding and moving bounds do not touch, then return an empty response
		if(!moving.intersects(colliding)) return new CollisionResult2D();
		
		// Initial Variable values
		double xDis;
		double yDis;
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bottom = false;
		ZRect2D newMoving;
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
			newMoving = new ZRect2D(x + cosA * dist, y + sinA * dist, w, h);
		}
		// Otherwise, use the previous bounds as the starting point
		else newMoving = prevMoving;
		// For the number of given iterations, move the new moving bounds closer to the colliding bounds, ensuring they don't touch at the end of the method
		ZRect2D nearBounds = moving;
		ZRect2D farBounds = newMoving;
		ZRect2D newBounds;
		for(int i = 0; i < iterations; i++){
			/// Finding half the distance between the bounds
			dist = ZMath.hypot(nearBounds.x - farBounds.x, nearBounds.y - farBounds.y) * .5;
			newBounds = new ZRect2D(nearBounds.x + cosA * dist, nearBounds.y + sinA * dist, w, h);
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
		return new CollisionResult2D(xDis, yDis, left, right, top, bottom, m);
		
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
	 * @return An array of 4 booleans, indexed as: 0: first is left of second 1: first is right of second 2: first is above second 3: first is below second
	 */
	public static boolean[] orientation(double x, double y, double w, double h, double cx, double cy, double cw, double ch){
		return new boolean[]{toLeft(x, cx), toRight(x, w, cx, cw), above(y, cy), below(y, h, cy, ch)};
	}
	
	/**
	 * Determine if a bounds is to the left of another See {@link #big(double, double, double, double)} for details
	 */
	public static boolean toLeft(double x, double cx){
		return small(x, cx);
	}
	
	/**
	 * Determine if a bounds is to the right of another See {@link #big(double, double, double, double)} for details
	 */
	public static boolean toRight(double x, double w, double cx, double cw){
		return big(x, w, cx, cw);
	}
	
	/**
	 * Determine if a bounds is above another See {@link #big(double, double, double, double)} for details
	 */
	public static boolean above(double y, double cy){
		return small(y, cy);
	}
	
	/**
	 * Determine if a bounds is below another See {@link #big(double, double, double, double)} for details
	 */
	public static boolean below(double y, double h, double cy, double ch){
		return big(y, h, cy, ch);
	}
	
	/**
	 * Treat n and cn as the lower coordinate of two rectangles axis, and determine if n is smaller than cn. Used for determining if the relative position of rectangles.
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
	 * Treat n and cn as the lower coordinate of two rectangles axis, and s and cs as the sizes, and determine if n is larger than cn. Used for determining if the relative
	 * position of rectangles Essentially, check if n is to the right or below cn
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
	
	/**
	 * Given the rectangular bounds of an unmoving object, and the circular bounds of an object to collide with the rectangular bounds, determine how the latter object should
	 * collide
	 * <p>
	 *
	 * @param rx The upper left hand x coordinate of the unmoving bounds
	 * @param ry The upper left hand y coordinate of the unmoving bounds
	 * @param rw The width of the unmoving bounds
	 * @param rh The height of the unmoving bounds
	 * @param circleX The center x coordinate of the circle to collide
	 * @param circleY The center y coordinate of the circle to collide
	 * @param radius The radius of the circle to collide
	 * @param m The {@link Material} which was collided with
	 * @return A {@link CollisionResult2D} representing the collision
	 */
	public static CollisionResult2D rectToCircleBasic(double rx, double ry, double rw, double rh, double circleX, double circleY, double radius, Material m){
		// If the shapes do not intersect, then there was no collision
		if(!ZMath.circleIntersectsRect(circleX, circleY, radius, rx, ry, rw, rh)) return new CollisionResult2D();
		
		// Initial Variable values
		double xDis;
		double yDis;
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bottom = false;
		
		// Determining the position of the colliding object relative to the unmoving object
		boolean toLeft = circleX - radius < rx;
		boolean toRight = circleX + radius > rx + rw;
		boolean above = circleY - radius < ry;
		boolean below = circleY + radius > ry + rh;
		
		boolean toLeftCentered = circleX < rx;
		boolean toRightCentered = circleX > rx + rw;
		boolean aboveCentered = circleY < ry;
		boolean belowCentered = circleY > ry + rh;
		
		boolean leftCenter = circleX < rx + rw * 0.5;
		boolean aboveCenter = circleY < ry + ry * 0.5;
		
		// The colliding object is to the left of the unmoving object
		if(toLeft || !toRight && leftCenter){
			xDis = rx - (circleX + radius);
		}
		// The colliding object is to the right of the unmoving object
		else{
			xDis = (rx + rw) - (circleX - radius);
		}
		
		// The colliding object is above the unmoving object
		if(above || !below && aboveCenter){
			yDis = ry - (circleY + radius);
		}
		// The colliding object is below the unmoving object
		else{
			yDis = (ry + rh) - (circleY - radius);
		}
		
		// Prioritize moving on the axis which has moved more, if the x axis moved more, move on the y axis
		if(Math.abs(yDis) < Math.abs(xDis)){
			// The floor was collided with
			if(aboveCentered){
				if(toLeftCentered){
					yDis = ry - circleLineIntersection(circleX, circleY, radius, rx, true, false);
					right = true;
				}
				else if(toRightCentered){
					yDis = ry - circleLineIntersection(circleX, circleY, radius, rx + rw, true, false);
					left = true;
				}
				
				bottom = true;
			}
			// The ceiling was collided with
			else if(belowCentered){
				if(toLeftCentered){
					yDis = ry + rh - circleLineIntersection(circleX, circleY, radius, rx, true, true);
					right = true;
				}
				else if(toRightCentered){
					yDis = ry + rh - circleLineIntersection(circleX, circleY, radius, rx + rw, true, true);
					left = true;
				}
				top = true;
			}
			xDis = 0;
		}
		// The y axis moved more
		else{
			// The right wall was collided with
			if(toLeftCentered){
				if(aboveCentered){
					xDis = rx - circleLineIntersection(circleX, circleY, radius, ry, false, false);
					bottom = true;
				}
				else if(belowCentered){
					xDis = rx - circleLineIntersection(circleX, circleY, radius, ry + rh, false, false);
					top = true;
				}
				right = true;
			}
			// The left wall was hit
			else if(toRightCentered){
				if(aboveCentered){
					xDis = rx + rw - circleLineIntersection(circleX, circleY, radius, ry, false, true);
					bottom = true;
				}
				else if(belowCentered){
					xDis = rx + rw - circleLineIntersection(circleX, circleY, radius, ry + rh, false, true);
					top = true;
				}
				left = true;
			}
			yDis = 0;
		}
		if(top || bottom){
			left = false;
			right = false;
		}
		return new CollisionResult2D(xDis, yDis, left, right, top, bottom, m);
	}
	
	/**
	 * Determine the coordinate of an intersection between a circle and a vertical or horizontal line
	 *
	 * @param rx The center x coordinate of the circle
	 * @param ry The center y coordinate of the circle
	 * @param r The radius of the circle
	 * @param line The coordinate of the line
	 * @param vertical true if the line is vertical and line represents an x coordinate, false if it is horizontal and represents a y coordinate
	 * @param returnLower true if the lower of the two intersection points should be returned, false for the higher value
	 * @return The intersection point. It will be a y coordinate if vertical is true, or an x coordinate if vertical is false
	 * 		If the line and circle do not intersect, NaN is returned
	 */
	public static double circleLineIntersection(double rx, double ry, double r, double line, boolean vertical, boolean returnLower){
		// (x - rx)^2 + (y - ry)^2 = r^2
		// (x - rx)^2 = r^2 - (y - ry)^2
		// x - rx = +-sqrt(r^2 - (y - ry)^2)
		// x = +-sqrt(r^2 - (y - ry)^2) + rx
		
		// (x - ry)^2 + (y - ry)^2 = r^2
		// (y - ry)^2 = r^2 - (x - rx)^2
		// y - ry = +-sqrt(r^2 - (x - rx)^2)
		// y = +-sqrt(r^2 - (x - rx)^2) + ry
		
		// line is an x coordinate
		if(vertical){
			var x = line - rx;
			var d = r * r - x * x;
			// If the sqrt value is negative, there's no intersection
			if(d < 0) return Double.NaN;
			var y = Math.sqrt(d);
			// If y is negative, non negated is the lower value
			if(y < 0) return (returnLower ? y : -y) + ry;
			else return (returnLower ? -y : y) + ry;
		}
		// Line is a y coordinate
		var y = line - ry;
		var d = r * r - y * y;
		// If the sqrt value is negative, there's no intersection
		if(d < 0) return Double.NaN;
		var x = Math.sqrt(d);
		// If x is negative, non negated is the lower value
		if(x < 0) return (returnLower ? x : -x) + rx;
		else return (returnLower ? -x : x) + rx;
	}
	
	/**
	 * Given the circle bounds of an unmoving object, and the circle bounds of an object to collide with the unmoving object, determine how the latter object should collide
	 *
	 * @param cx The x center of the unmoving object
	 * @param cy The y center of the unmoving object
	 * @param cr The radius of the unmoving object
	 * @param x The x center of the moving object
	 * @param y The y center of the moving object
	 * @param r The radius of the moving object
	 * @param m The material of the object collided with
	 * @return The response
	 */
	public static CollisionResult2D circleToCircleBasic(double cx, double cy, double cr, double x, double y, double r, Material m){
		var dist = Math.sqrt((cx - x) * (cx - x) + (cy - y) * (cy - y));
		var radi = cr + r;
		if(radi < dist) return new CollisionResult2D();
		
		var offset = radi - dist;
		var angle = ZMath.lineAngle(cx, cy, x, y);
		var cos = Math.cos(angle);
		var sin = Math.sin(angle);
		
		return new CollisionResult2D(cos * offset, sin * offset, cos > 0, cos < 0, sin > 0, sin < 0, m);
	}
	
	/**
	 * Given the rectangular prism bounds of an unmoving object, and the bounds of a cylinder, determine how the later should move colliding with the former
	 *
	 * @param rx The bottom middle x coordinate of the rectangular prism
	 * @param ry The bottom middle y coordinate of the rectangular prism
	 * @param rz The bottom middle z coordinate of the rectangular prism
	 * @param rw The total width of the rectangular prism
	 * @param rh The total height of the rectangular prism
	 * @param rl The total length of the rectangular prism
	 * @param cx The bottom middle x coordinate of the cylinder
	 * @param cy The bottom middle y coordinate of the cylinder
	 * @param cz The bottom middle z coordinate of the cylinder
	 * @param cr The radius of the rectangular prism
	 * @param ch The total height of the rectangular prism
	 * @param m The material of the rectangular prism
	 * @return A collision result representing how the cylinder should move
	 */
	public static CollisionResult3D rectToCylinderBasic(double rx, double ry, double rz, double rw, double rh, double rl, double cx, double cy, double cz, double cr, double ch, Material m){
		// With no intersection, there is no collision
		if(!rectIntersectsCylinder(rx, ry, rz, rw, rh, rl, cx, cy, cz, cr, ch)) return new CollisionResult3D();
		
		double minDist = -1;
		double moveDist = 0;
		
		boolean moveX = false;
		boolean moveY = false;
		boolean moveZ = false;
		
		boolean touchWall = false;
		boolean touchFloor = false;
		boolean touchCeiling = false;
		
		double hrw = rw * 0.5;
		double hrl = rl * 0.5;
		
		// Check for collision with all 6 sides of the rectangular prism, and find the distance to move on each side, or a negative number if it is too far
		
		// Check the x axis, left and right
		double rxl = rx - hrw;
		double leftDist = circleDistanceLineSegment(rz - hrl, rz + hrl, rxl, cz, rxl + (rxl - cx), cr, true);
		if(leftDist > 0 && leftDist < rw){
			moveDist = -leftDist;
			minDist = leftDist;
			moveX = true;
			touchWall = true;
		}
		double rxr = rx + hrw;
		double rightDist = circleDistanceLineSegment(rz + hrl, rz + hrl, rxr, cz, cx, cr, true);
		if(rightDist > 0 && rightDist < rw && (minDist < 0 || rightDist < minDist)){
			moveDist = rightDist;
			minDist = rightDist;
			moveX = true;
			touchWall = true;
		}
		
		// Check the z axis, front and back
		double rzb = rz + hrl;
		double backDist = circleDistanceLineSegment(rx - hrw, rx + hrw, rzb, cx, rzb + (cz - rzb), cr, true);
		if(backDist > 0 && backDist < rl && (minDist < 0 || backDist < minDist)){
			moveDist = backDist;
			minDist = backDist;
			moveZ = true;
			moveX = false;
			touchWall = true;
		}
		double rzf = rz - hrl;
		double frontDist = circleDistanceLineSegment(rx - hrw, rx + hrw, rzf, cx, cz, cr, false);
		if(frontDist > 0 && frontDist < rl && (minDist < 0 || frontDist < minDist)){
			moveDist = -frontDist;
			minDist = frontDist;
			moveZ = true;
			moveX = false;
			touchWall = true;
		}
		
		// Check the y axis, top and bottom
		double topDist = (ry + rh) - cy;
		if(topDist > 0 && topDist < rh && (minDist < 0 || topDist < minDist)){
			moveDist = topDist;
			minDist = topDist;
			moveY = true;
			moveX = false;
			moveZ = false;
			touchFloor = true;
			touchWall = false;
		}
		double bottomDist = (cy + ch) - ry;
		if(bottomDist > 0 && bottomDist < rh && (minDist < 0 || bottomDist < minDist)){
			moveDist = -bottomDist;
			minDist = bottomDist;
			moveY = true;
			moveX = false;
			moveZ = false;
			touchCeiling = true;
			touchFloor = false;
			touchWall = false;
		}
		
		// If no movement is needed, there is no collision, though this should always be false at this point
		if(minDist < 0) return new CollisionResult3D();
		
		// Otherwise, the result will be only for the side with the smallest change
		double dx = moveX ? moveDist : 0;
		double dy = moveY ? moveDist : 0;
		double dz = moveZ ? moveDist : 0;
		
		double wallAngle;
		// x axis wall
		if(moveX) wallAngle = ZMath.PI_BY_2;
		// z axis wall
		else if(moveZ) wallAngle = 0;
		// No wall hit
		else wallAngle = 0;
		
		// Set the flags appropriately for which sides were touched and return the result
		return new CollisionResult3D(dx, dy, dz, touchWall, touchCeiling, touchFloor, m, wallAngle);
	}
	
	/**
	 * Determine the distance that the given circle needs to move to no longer collide with the given line segment. This assumes the given values are for a line in 2D
	 * aligned to the y axis where the circle would need to move down, i.e. increase y, to collide with the wall
	 *
	 * @param lx1 The smaller of the two x coordinates representing the line to collide with
	 * @param lx2 The larger of the two x coordinates representing the line to collide with
	 * @param ly The y coordinate of the line
	 * @param cx The center x coordinate of the circle
	 * @param cy The center y coordinate of the circle
	 * @param cr The radius of the circle
	 * @param invert true if the direction of the comparison will be inverted on the y axis, i.e. the circle needs to be moved in the opposite direction
	 * @return The distance, as a magnitude, the circle needs to move down to align with the line, or a negative value if there's no intersection
	 */
	private static double circleDistanceLineSegment(double lx1, double lx2, double ly, double cx, double cy, double cr, boolean invert){
		// Find the point on the line touching the circle
		var intersectionX = circleLineIntersection(cx, cy, cr, ly, false, false);
		// No intersection
		if(Double.isNaN(intersectionX)) return -1;
		
		// Find the y coordinate on the circle where the endpoints of the line segment touch the circle, based on the x coordinate line segments
		// y = +-sqrt(r^2 - (x - rx)^2) + ry
		Double leftY = null;
		Double rightY = null;
		double radiusSquared = cr * cr;
		double lx1Diff = cx - lx1;
		double lx1DiffSquared = lx1Diff * lx1Diff;
		double yDistSquared = (cy - ly) * (cy - ly);
		// Only consider the endpoint if it is within the circle, and the center of the circle is outside the line
		if(yDistSquared + lx1DiffSquared <= radiusSquared && cx < lx1){
			double leftYToRoot = radiusSquared - lx1DiffSquared;
			if(leftYToRoot >= 0){
				double rootVal = Math.sqrt(leftYToRoot);
				if(invert) leftY = cy - rootVal;
				else leftY = cy + rootVal;
			}
		}
		double lx2Diff = cx - lx2;
		double lx2DiffSquared = lx2Diff * lx2Diff;
		// Only consider the endpoint if it is within the circle
		if(yDistSquared + lx2DiffSquared <= radiusSquared && lx2 < cx){
			double rightYToRoot = radiusSquared - lx2DiffSquared;
			if(rightYToRoot >= 0){
				double rootVal = Math.sqrt(rightYToRoot);
				if(invert) rightY = cy - rootVal;
				else rightY = cy + rootVal;
			}
		}
		
		double circleCheckPosY;
		
		// If neither points touch, then use the y coordinate of the line
		if(leftY == null && rightY == null){
			if(invert) circleCheckPosY = cy - cr;
			else circleCheckPosY = cy + cr;
		}
		// If one of those endpoints touch the circle, then move based on that position
		else if(leftY != null && rightY == null) circleCheckPosY = leftY;
		else if(leftY == null) circleCheckPosY = rightY;
			// Otherwise, if both touch, use the smaller distance
		else{
			if(leftY > rightY) circleCheckPosY = rightY;
			else circleCheckPosY = leftY;
		}
		
		// Return the positive distance from the line to the top of the circle
		return Math.abs(circleCheckPosY - ly);
	}
	
	/**
	 * Determine if the rectangular prism and the bounds of a cylinder intersect
	 *
	 * @param rx The bottom middle x coordinate of the rectangular prism
	 * @param ry The bottom middle y coordinate of the rectangular prism
	 * @param rz The bottom middle z coordinate of the rectangular prism
	 * @param rw The total width of the rectangular prism
	 * @param rh The total height of the rectangular prism
	 * @param rl The total length of the rectangular prism
	 * @param cx The bottom middle x coordinate of the cylinder
	 * @param cy The bottom middle y coordinate of the cylinder
	 * @param cz The bottom middle z coordinate of the cylinder
	 * @param cr The radius of the rectangular prism
	 * @param ch The total height of the rectangular prism
	 * @return true if they intersect, false otherwise
	 */
	public static boolean rectIntersectsCylinder(double rx, double ry, double rz, double rw, double rh, double rl, double cx, double cy, double cz, double cr, double ch){
		// If neither of the y bounds of the rectangular prism are inside the cylinder, then there is no collision
		if(// Check if the rectangular prism is inside the cylinder
				!ZMath.inExclusive(cy, ry, cy + ch) && !ZMath.inExclusive(cy, ry + rh, cy + ch) &&
				// Do the same check in reverse
				!ZMath.inExclusive(ry, cy, ry + rh) && !ZMath.inExclusive(ry, cy + ch, ry + rh)
			/////////////////////////////////////////////////////////////////////////
		) return false;
		
		// If the circular bounds doesn't intersect the rectangle, there will be no collision
		return ZMath.circleIntersectsRect(cx, cz, cr, rx - rw * 0.5, rz - rl * 0.5, rw, rl);
	}
	
	/** Cannot instantiate {@link ZCollision} */
	private ZCollision(){
	}
	
}
