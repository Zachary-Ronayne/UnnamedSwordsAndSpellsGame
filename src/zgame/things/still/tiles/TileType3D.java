package zgame.things.still.tiles;

import zgame.things.type.bounds.HitBox3D;

/** A simple tile which has a constant material */
public abstract class TileType3D extends TileType<HitBox3D, Tile3D, TileHitbox3D>{
	
	/**
	 * Create a new {@link TileType3D} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 */
	public TileType3D(String id, String origin, TileHitbox3D hitbox){
		super(id, origin, hitbox);
	}
	
}
