package zgame.things.still.tiles;

import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.things.type.bounds.HitBox;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox<V extends ZVector<V>>{
	
	/**
	 * Based on the given hitbox, determine the collision resulting from that hitbox colliding with the given tile
	 *
	 * @param t The tile to collide
	 * @param obj The object with a hitbox which collides with the given tile
	 * @return The collision to occur
	 */
	Collision<V> collide(Tile<V> t, HitBox<V> obj);
	
}
