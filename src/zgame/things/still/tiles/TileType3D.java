package zgame.things.still.tiles;

import zgame.physics.V3D;
import zgame.physics.material.Material;
import zgame.things.still.tiles.threeDee.TileHitbox3D;

/** A simple tile which has a constant material */
public non-sealed abstract class TileType3D extends TileType<V3D>{
	
	/**
	 * Create a new {@link TileType3D} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #getMaterial()}
	 */
	public TileType3D(String id, String origin, TileHitbox<V3D> hitbox, Material material){
		super(id, origin, hitbox, material);
	}
	
	@Override
	public TileHitbox3D getHitbox(){
		return (TileHitbox3D)super.getHitbox();
	}
}
