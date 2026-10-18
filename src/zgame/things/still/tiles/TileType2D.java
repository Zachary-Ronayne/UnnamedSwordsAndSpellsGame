package zgame.things.still.tiles;

import zgame.physics.V2D;
import zgame.physics.material.Material;
import zgame.things.type.bounds.twoDee.shape.Shape2D;

/** A standard 2D tile type */
public non-sealed abstract class TileType2D extends TileType<V2D> implements Shape2D{
	
	/**
	 * Create a new {@link TileType2D} using the given data
	 *
	 * @param id See {@link #id}
	 * @param origin See {@link #origin}
	 * @param shapeOffset See {@link #shapeOffset}
	 * @param material See {@link #material}
	 */
	public TileType2D(String id, String origin, V2D shapeOffset, Material material){
		super(id, origin, shapeOffset == null ? new V2D() : shapeOffset, material);
	}
	
}
