package zgame.things;

/** An interface which describes something with a width and height */
public interface RectangleHitBox extends HitBox{
	
	/** @return The width of this object */
	public double getWidth();

	/** @return The height of this object */
	public double getHeight();

}
