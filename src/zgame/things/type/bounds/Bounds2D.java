package zgame.things.type.bounds;

import zgame.core.utils.ZRect2D;
import zgame.physics.V2D;
import zgame.things.type.Position2D;

/** An object which has a bounds that can be defined in a 2D space */
public interface Bounds2D extends Position2D, Bounds<V2D>{
	
	@Override
	default V2D getMaxPosition(){
		return this.getMinPosition().add(this.getDimensions());
	}
	
	/** @return The minimum x coordinate of this bounds */
	default double minX(){
		return this.getMinPosition().getX();
	}
	
	/** @return The minimum y coordinate of this bounds */
	default double minY(){
		return this.getMinPosition().getY();
	}
	
	/** @return The maximum x coordinate of this bounds */
	default double maxX(){
		return this.getMaxPosition().getX();
	}
	
	/** @return The maximum y coordinate of this bounds */
	default double maxY(){
		return this.getMaxPosition().getY();
	}
	
	/** @return The width this bounds takes up */
	default double getWidth(){
		return this.getDimensions().getWidth();
	}
	
	/** @return The height this bounds takes up */
	default double getHeight(){
		return this.getDimensions().getHeight();
	}
	
	default V2D getCenterPosition(){
		return this.getMinPosition().add(this.getDimensions().scale(0.5));
	}
	
	/** @return The center x coordinate of this bounds */
	default double centerX(){
		return this.getCenterPosition().getX();
	}
	
	/** @return The center y coordinate of this bounds */
	default double centerY(){
		return this.getCenterPosition().getY();
	}
	
	/** @return An axis aligned rectangle representing the full bounds which this {@link Bounds2D} takes up */
	default ZRect2D getRectBounds(){
		var pos = this.getMinPosition();
		return new ZRect2D(pos.getX(), pos.getY(), this.getWidth(), this.getHeight());
	}
	
}