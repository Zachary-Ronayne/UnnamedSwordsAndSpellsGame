package zgame.things.still.tiles;

import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.things.core.GameThing;
import zgame.things.type.bounds.HitBox;

/** A {@link GameThing} with a hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public interface Tile<V extends ZVector<V>> extends HitBox<V>{
	
	/** See {@link TileHitbox#collide(Tile, HitBox)}  */
	Collision<V> collide(HitBox<V> obj);
	
	/**
	 * Helper method for converting this tile to the correct type, implement as returning this cast to the correct type
	 * @param clazz The type of tile to convert to
	 * @return This, but as the correct type
	 * @param <T> The type to convert to
	 */
	<T extends Tile<V>> T asTile(Class<T> clazz);
	
}
