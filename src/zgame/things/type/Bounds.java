package zgame.things.type;

import zgame.core.utils.ZPoint;
import zgame.core.utils.ZRect;

/** An object which has a bounds that can be defined in a 2D space */
public interface Bounds extends Position{
	
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
	
	/** @return The center point of this {@link Bounds} */
	default ZPoint center(){
		return new ZPoint(this.centerX(), this.centerY());
	}
	
	/** @return A rectangle representing the full bounds which this {@link Bounds} takes up */
	default ZRect getBounds(){
		return new ZRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
}