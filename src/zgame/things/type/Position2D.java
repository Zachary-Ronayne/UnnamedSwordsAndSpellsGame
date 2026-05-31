package zgame.things.type;

import zgame.physics.V2D;

/** An object that tracks an x and y position */
public interface Position2D extends Position<V2D>{
	
	/** @return The x coordinate of this object. If this object has a bounds, it's the minimum x coordinate */
	double getX();
	
	/** @return The y coordinate of this object. If this object has a bounds, it's the minimum y coordinate */
	double getY();
	
	@Override
	default V2D getPosition(){
		return new V2D(this.getX(), this.getY());
	}
}
