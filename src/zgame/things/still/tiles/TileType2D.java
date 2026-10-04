package zgame.things.still.tiles;

import zgame.physics.V2D;
import zgame.physics.material.Material;
import zgame.things.type.bounds.Shape2D;

/** A simple tile which has a constant material */
public non-sealed abstract class TileType2D extends TileType<V2D> implements Shape2D{
	
	/**
	 * Create a new {@link TileType2D} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param material See {@link #getMaterial()}
	 */
	public TileType2D(String id, String origin, Material material){
		super(id, origin, material);
	}
	
}
