package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResult2D;
import zgame.physics.material.Material;
import zgame.things.type.bounds.HitBox2D;

/** A simple tile which has a constant material */
public abstract class TileType2D extends TileType<HitBox2D, Tile2D, TileHitbox2D, CollisionResult2D>{
	
	/**
	 * Create a new {@link TileType2D} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #getMaterial()}
	 */
	public TileType2D(String id, String origin, TileHitbox2D hitbox, Material material){
		super(id, origin, hitbox, material);
	}
	
}
