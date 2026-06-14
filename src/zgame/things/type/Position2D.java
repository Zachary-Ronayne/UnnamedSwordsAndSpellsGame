package zgame.things.type;

import zgame.physics.V2D;

/** An object that tracks an x and y position */
public interface Position2D extends Position<V2D>{
	
	/** @return The x coordinate of this object. If this object has a bounds, it's the minimum x coordinate */
	default double getX(){
		return this.getPosition().getX();
	}
	
	/** @return The y coordinate of this object. If this object has a bounds, it's the minimum y coordinate */
	default double getY(){
		return this.getPosition().getY();
	}
	
}
