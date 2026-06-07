package zgame.things.type;

import zgame.physics.ZVector;

/** An object that tracks some position */
public interface Position<V extends ZVector<V>>{
	
	// TODO does it make sense to have this method as a part of the bounds interface? This probably should only exist at the entity level
	/** @return The current position of this object */
	V getPosition();
	
}
