package zgame.things.still.tiles;

import zgame.physics.V2D;
import zgame.physics.collision.Collision2D;
import zgame.physics.material.Material;
import zgame.things.still.tiles.twoDee.Tile2D;
import zgame.things.still.tiles.twoDee.TileHitbox2D;
import zgame.things.type.bounds.HitBox2D;

/** A simple tile which has a constant material */
public non-sealed abstract class TileType2D extends TileType<V2D>{
	
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
