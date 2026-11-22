package zgame.things.type.bounds.twoDee.shape;

import zgame.physics.V2D;
import zgame.things.type.bounds.twoDee.bounds.shape.RectangleBounds;
import zgame.things.type.bounds.twoDee.bounds.shape.ShapedBounds2D;

/** An axis aligned rectangle with no position */
public interface RectangleShape extends Shape2D{
	
	@Override
	default ShapedBounds2D atMinPosition(V2D minPos){
		final var rect = this;
		return new RectangleBounds(){
			@Override
			public V2D getMinPosition(){
				return minPos;
			}
			
			@Override
			public V2D getDimensions(){
				return rect.getDimensions();
			}
		};
	}
}
