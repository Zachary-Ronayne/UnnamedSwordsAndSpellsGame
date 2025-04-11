package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResult3D;
import zgame.physics.material.Material;
import zgame.things.type.bounds.HitBox3D;

/** A simple tile which has a constant material */
public abstract class TileType3D extends TileType<HitBox3D, Tile3D, TileHitbox3D, CollisionResult3D>{
	
	/**
	 * Create a new {@link TileType3D} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #getMaterial()}
	 */
	public TileType3D(String id, String origin, TileHitbox3D hitbox, Material material){
		super(id, origin, hitbox, material);
	}
	
}
