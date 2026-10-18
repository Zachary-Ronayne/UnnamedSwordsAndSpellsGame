package zgame.things.type.bounds.twoDee.shape;

import zgame.physics.V2D;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.twoDee.bounds.CircleBounds;

/** Represents a formal mathematical circle */
public interface CircleShape extends Shape2D{
	
	/** @return The radius of this circle */
	double getRadius();
	
	@Override
	default Bounds<V2D> shapeAsBounds(V2D minPos){
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
	
	@Override
	default ShapeType2D getShapeType(){
		return ShapeType2D.CIRCLE;
	}
}
