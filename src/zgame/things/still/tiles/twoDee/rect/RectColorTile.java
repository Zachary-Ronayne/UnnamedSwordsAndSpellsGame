package zgame.things.still.tiles.twoDee.rect;

import zgame.core.graphics.ZColor;
import zgame.physics.V2D;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.still.tiles.TileType;
import zgame.things.still.tiles.twoDee.ColorTile;
import zgame.things.still.tiles.twoDee.Tile2D;
import zgame.things.type.bounds.twoDee.shape.RectangleShape;

/** A {@link TileType} for a solid hitbox tile rendering a solid color */
public class RectColorTile extends ColorTile implements RectangleShape{
	
	/**
	 * Create a new {@link RectColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param color See {@link #color}
	 */
	public RectColorTile(String id, String origin, ZColor color){
		this(id, origin, color, Materials.DEFAULT);
	}
	
	/**
	 * Create a new {@link RectColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param color See {@link #color}
	 * @param material See {@link #material}
	 */
	public RectColorTile(String id, String origin, ZColor color, Material material){
		super(id, origin, null, color, material);
	}
	
	@Override
	public V2D getDimensions(){
		double s = Tile2D.size();
		return new V2D(s, s);
	}
}
