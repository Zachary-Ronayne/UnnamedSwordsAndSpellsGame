package zgame.things.type.bounds.threeDee.shape;

import zgame.physics.V3D;
import zgame.things.type.bounds.threeDee.bounds.shape.RectPrismBounds;
import zgame.things.type.bounds.threeDee.bounds.shape.ShapedBounds3D;
import zgame.world.Direction3D;

/** An axis aligned rectangular prism with no position */
public interface RectPrismShape extends Shape3D{
	
	/** An array indexed by {@link Direction3D} indicating which faces can be collided with, true for allowing a collision, false  otherwise */
	boolean[] getCollisionFaces();
	
	@Override
	default ShapedBounds3D atMinPosition(V3D minPos){
		final var r = this;
		return new RectPrismBounds(){
			@Override
			public V3D getMinPosition(){
				return minPos;
			}
			
			@Override
			public boolean[] getCollisionFaces(){
				return r.getCollisionFaces();
			}
			
			@Override
			public V3D getDimensions(){
				return r.getDimensions();
			}
		};
	}
}
