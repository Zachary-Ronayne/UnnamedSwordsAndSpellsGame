package zgame.things.type;

import zgame.core.utils.ZRect;

/** An object which has a bounds that can be defined in a 2D space */
public interface Bounds extends Position{
	
	/** @return The maximum x coordinate of this bounds */
	public double maxX();
	
	/** @return The maximum y coordinate of this bounds */
	public double maxY();
	
	/** @return The width this bounds takes up */
	public default double getWidth(){
		return this.maxX() - this.getX();
	}
	
	/** @return The height this bounds takes up */
	public default double getHeight(){
		return this.maxX() - this.getX();
	}
	
	/** @return The center x coordinate of this bounds */
	public default double centerX(){
		return this.getX() + getWidth() * 0.5;
	}
	
	/** @return The center y coordinate of this bounds */
	public default double centerY(){
		return this.getY() + getHeight() * 0.5;
	}
	
	/** @return A rectangle representing the full bounds which this {@link Bounds} takes up */
	public default ZRect getBounds(){
		return new ZRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
}