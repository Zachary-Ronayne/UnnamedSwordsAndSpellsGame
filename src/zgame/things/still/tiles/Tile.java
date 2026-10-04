package zgame.things.still.tiles;

import zgame.physics.ZVector;
import zgame.things.core.GameThing;
import zgame.things.type.Materialable;
import zgame.things.type.bounds.Bounds;

/** A {@link GameThing} with a bounds based on an index in an array. The indexes of this object should directly correlate to its position */
public abstract class Tile<V extends ZVector<V>> extends GameThing implements Bounds<V>, Materialable{
	
	// TODO figure out of this method is actually needed
	/**
	 * Helper method for converting this tile to the correct type, implement as returning this cast to the correct type
	 * @param clazz The type of tile to convert to
	 * @return This, but as the correct type
	 * @param <T> The type to convert to
	 */
	public abstract <T extends Tile<V>> T asTile(Class<T> clazz);
	
}
