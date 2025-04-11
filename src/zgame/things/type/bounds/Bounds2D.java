package zgame.things.type.bounds;

import zgame.core.utils.ZPoint2D;
import zgame.core.utils.ZRect2D;
import zgame.things.type.Position2D;

/** An object which has a bounds that can be defined in a 2D space */
public interface Bounds2D extends Position2D{
	
	/** @return The maximum x coordinate of this bounds */
	double maxX();
	
	/** @return The maximum y coordinate of this bounds */
	double maxY();
	
	/** @return The width this bounds takes up */
	default double getWidth(){
		return this.maxX() - this.getX();
	}
	
	/** @return The height this bounds takes up */
	default double getHeight(){
		return this.maxX() - this.getX();
	}
	
	/** @return The center x coordinate of this bounds */
	default double centerX(){
		return this.getX() + getWidth() * 0.5;
	}
	
	/** @return The center y coordinate of this bounds */
	default double centerY(){
		return this.getY() + getHeight() * 0.5;
	}
	
	/** @return The center point of this {@link Bounds2D} */
	default ZPoint2D center(){
		return new ZPoint2D(this.centerX(), this.centerY());
	}
	
	/** @return A rectangle representing the full bounds which this {@link Bounds2D} takes up */
	default ZRect2D getBounds(){
		return new ZRect2D(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
}