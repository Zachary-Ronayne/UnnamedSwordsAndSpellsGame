package zgame.core.utils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/** A class containing misc methods for math stuff */
public final class ZMath{
	
	/** One fourth of the mathematical value for pi */
	public static final double PI_BY_4 = Math.PI * 0.25;
	/** One half of the mathematical value for pi */
	public static final double PI_BY_2 = Math.PI * 0.5;
	/** Twice the mathematical value for pi */
	public static final double TAU = Math.PI * 2.0;
	
	/**
	 * Convert an integer to an array of boolean values representing it's binary
	 * 
	 * @param n The number to convert
	 * @return The array. true = 1, false = 0,
	 *         Index 0 is the most significant bit, the last index is the least significant bit,
	 *         No trailing zeros are ever used.
	 *         Should only be used for integers greater than or equal to zero
	 */
	public static boolean[] intToBoolArr(int n){
		int cnt = 0;
		int l = n;
		do{
			cnt++;
			l /= 2;
		}while(l > 0);
		boolean[] arr = new boolean[cnt];
		l = n;
		for(int i = 0; i < cnt; i++){
			arr[arr.length - i - 1] = l % 2 == 1;
			l /= 2;
		}
		return arr;
	}
	
	/**
	 * Find the minimum of a list of numbers
	 * 
	 * @param nums The numbers. This is assumed to have at least one element
	 * @return The minimum number
	 */
	public static double min(double ... nums){
		double n = nums[0];
		for(int i = 1; i < nums.length; i++) n = Math.min(n, nums[i]);
		return n;
	}
	
	/**
	 * Find the maximum of a list of numbers
	 * 
	 * @param nums The numbers. This is assumed to have at least one element
	 * @return The maximum number
	 */
	public static double max(double ... nums){
		double n = nums[0];
		for(int i = 1; i < nums.length; i++) n = Math.max(n, nums[i]);
		return n;
	}
	
	/**
	 * Return x, but if x is less than a, return a, and if x is greater than b, return b
	 * 
	 * @param a The lowest number this method should return
	 * @param b The highest number this method should return
	 * @param x The number to compare to a and b
	 * @return The number
	 */
	public static double minMax(double a, double b, double x){
		return Math.max(a, Math.min(b, x));
	}

	/**
	 * Determine if a number is between two other numbers
	 * @param a The lower number to check
	 * @param b The middle number
	 * @param c The higher number
	 * @return true if b is between or equal to a and c, false otherwise
	 */
	public static boolean in(int a, int b, int c){
		return a <= b && b <= c;
	}

	/**
	 * Determine if two numbers have the same sign.
	 * Behavior of this method is not guaranteed for weird values, i.e. infinity and NaN
	 * @param a The first number
	 * @param b The second number
	 * @return true if they have the same sign, false otherwise.
	 */
	public static boolean sameSign(double a, double b){
		return a == b || (a < 0 && b < 0) || (a > 0 && b > 0);
	}

	/**
	 * Find the angle from the point (x, y) to the point (px, py)
	 * 
	 * @param x The x coordinate of the base of the angle
	 * @param y The x coordinate of the base of the angle
	 * @param px The x coordinate of where to go to find the angle
	 * @param py The y coordinate of where to go to find the angle
	 * @return The angle, in radians
	 */
	public static double lineAngle(double x, double y, double px, double py){
		return Math.atan2(py - y, px - x);
	}
	
	/**
	 * Find the intersection point of 2 lines
	 * 
	 * @param l1 The first line
	 * @param l2 The second line
	 * @return The intersection point, or null if the lines are parallel, i.e. they don't intersect
	 */
	public static Point2D.Double lineIntersection(Line2D.Double l1, Line2D.Double l2){
		// Find slopes and check if the lines are parallel
		double m1 = slope(l1);
		double m2 = slope(l2);
		boolean nan1 = Double.isNaN(m1);
		boolean nan2 = Double.isNaN(m2);
		if(nan1 && nan2 || m1 == m2) return null;
		
		// Handle the case of a vertical line
		if(nan1 || nan2){
			Line2D.Double nanL = nan1 ? l1 : l2;
			Line2D.Double line = nan1 ? l2 : l1;
			double m = nan1 ? m2 : m1;
			double b = yIntercept(line);
			return new Point2D.Double(nanL.x1, m * nanL.x1 + b);
		}

		// Find y intercepts
		double b1 = yIntercept(l1, m1);
		double b2 = yIntercept(l2, m2);
		
		// Find the intersection
		double x = (b2 - b1) / (m1 - m2);
		double y = m1 * x + b1;
		return new Point2D.Double(x, y);
	}
	
	/**
	 * @param line The line to check
	 * @return The slope of the line, or NaN if the line is vertical
	 */
	public static double slope(Line2D.Double line){
		double xs = (line.x1 - line.x2);
		if(xs == 0) return Double.NaN;
		return (line.y1 - line.y2) / xs;
	}
	
	/**
	 * @param line The line to check
	 * @return The y intercept of the line
	 */
	public static double yIntercept(Line2D.Double line){
		return yIntercept(line, slope(line));
	}
	
	/**
	 * Find the y intercept, given a line and its slope
	 * 
	 * @param line The line to check
	 * @param slope The slope of the line. Nan for a vertical line
	 * @return The y intercept of the line
	 */
	public static double yIntercept(Line2D.Double line, double slope){
		return line.y1 - slope * line.x1;
	}

	/**
	 * Given two lengths for the non hypotenuse sides of a right triangle, return the length of the hypotenuse
	 * 
	 * @param w The first size
	 * @param h The second size
	 * @return The hypotenuse
	 */
	public static double hypot(double w, double h){
		return Math.sqrt(w * w + h * h);
	}
	
	/** Cannot instantiate {@link ZMath} */
	private ZMath(){
	}
	
}
