package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResult;
import zgame.things.type.bounds.HitBox;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox<H extends HitBox<H>, T extends Tile<H>> {
	
	/**
	 * Based on the given hitbox, determine the new position of that hitbox when it collides with the given tile
	 *
	 * @param t The tile to collide
	 * @param obj The object with a hitbox which collides with the given tile
	 * @return A point to reposition the rectangle to
	 */
	CollisionResult collide(T t, H obj);
	
	/**
	 * Determine if a hitbox hits the given tile
	 *
	 * @param obj The hitbox to check
	 * @return true if they intersect, false otherwise
	 */
	boolean intersectsTile(T t, H obj);
	
}
