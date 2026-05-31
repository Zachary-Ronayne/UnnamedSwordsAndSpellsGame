package zgame.things.type.bounds;

import zgame.core.utils.ZRect2D;
import zgame.physics.V2D;
import zgame.things.type.Position2D;

/** An object which has a bounds that can be defined in a 2D space */
public interface Bounds2D extends Position2D, Bounds<V2D>{
	
	@Override
	default V2D getMinPosition(){
		return this.getPosition();
	}
	
	@Override
	default V2D getMaxPosition(){
		return new V2D(this.maxX(), this.maxY());
	}
	
	@Override
	default V2D getCenterPosition(){
		return new V2D(this.centerX(), this.centerY());
	}
	
	@Override
	default V2D getDimensions(){
		return new V2D(this.getWidth(), this.getHeight());
	}
	
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
	
	/** @return A rectangle representing the full bounds which this {@link Bounds2D} takes up */
	default ZRect2D getBounds(){
		return new ZRect2D(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
}