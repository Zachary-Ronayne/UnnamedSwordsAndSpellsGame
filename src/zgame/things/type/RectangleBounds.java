package zgame.things.type;

/** An object which has a non rotated rectangular bounds, i.e. width and height */
public interface RectangleBounds extends Bounds{
	
	/** @return The width of this object */
	public double getWidth();
	
	/** @return The height of this object */
	public double getHeight();
	
}
