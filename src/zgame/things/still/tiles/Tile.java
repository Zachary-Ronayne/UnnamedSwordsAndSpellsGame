package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResult;
import zgame.things.type.GameThing;
import zgame.things.type.bounds.HitBox;

/** A {@link GameThing} with a hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public interface Tile<H extends HitBox<H>>{
	
	/** See {@link TileHitbox#collide(Tile, HitBox)}  */
	CollisionResult collide(H obj);
	
}
