package zgame.things.still.tiles;

import zgame.physics.V2D;
import zgame.physics.material.Material;
import zgame.things.still.tiles.twoDee.Tile2D;

/** A simple tile which has a constant material */
public non-sealed abstract class TileType2D extends TileType<V2D>{
	
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
	
	/**
	 * Build a {@link Tile2D} that uses this type as the primary front tile
	 * @param x The x index of the tile's array position
	 * @param y The y index of the tile's array position
	 * @param backType The type of tile to put as the back of the tile
	 * @return The built tile
	 */
	public abstract Tile2D build(int x, int y, TileType2D backType);
	
}
