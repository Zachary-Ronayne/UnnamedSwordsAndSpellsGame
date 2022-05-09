package zgame.things;

/** An object which has a rectangular bounds, i.e. width and height */
public interface RectangleBounds{
	
	/** @return The width of this object */
	public double getWidth();
	
	/** @return The height of this object */
	public double getHeight();
	
	/** @return The x coordinate of the left edge of this object */
	public double leftEdge();
	
	/** @return The x coordinate of the right edge of this object */
	public double rightEdge();
	
	/** @return The y coordinate of the top edge of this object */
	public double topEdge();
	
	/** @return The y coordinate of the bottom edge of this object */
	public double bottomEdge();

	/** @return The center x coordinate of this object */
	public double centerX();
	
	/** @return The center y coordinate of this object */
	public double centerY();
}
