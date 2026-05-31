package zgame.things.type;

import zgame.physics.ZVector;

/** An object that tracks some position */
public interface Position<V extends ZVector<V>>{
	
	/** @return The current position of this object */
	V getPosition();
	
}
