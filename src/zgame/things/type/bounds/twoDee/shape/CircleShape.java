package zgame.things.type.bounds.twoDee.shape;

import zgame.physics.V2D;
import zgame.things.type.bounds.twoDee.bounds.shape.CircleBounds;
import zgame.things.type.bounds.twoDee.bounds.shape.ShapedBounds2D;

/** A formal mathematical circle with no position */
public interface CircleShape extends Shape2D{
	
	/** @return The radius of this circle */
	double getRadius();
	
	@Override
	default ShapedBounds2D atMinPosition(V2D minPos){
		final var c = this;
		return new CircleBounds(){
			@Override
			public V2D getMinPosition(){
				return minPos;
			}
			
			@Override
			public double getRadius(){
				return c.getRadius();
			}
			
			@Override
			public V2D getDimensions(){
				return c.getDimensions();
			}
		};
	}
}
