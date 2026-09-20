package zgame.things.still.tiles.twoDee.empty;

import zgame.core.graphics.ZColor;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.still.tiles.TileType;
import zgame.things.still.tiles.TileType2D;
import zgame.things.still.tiles.twoDee.ColorTile;
import zgame.things.still.tiles.twoDee.rect.RectTile;
import zgame.things.still.tiles.twoDee.Tile2D;

/** A {@link TileType} with no hitbox, rendering a solid color */
public class EmptyColorTile extends ColorTile{
	
	/**
	 * Create a new {@link EmptyColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param color See {@link #color}
	 */
	public EmptyColorTile(String id, String origin, ZColor color){
		this(id, origin, color, Materials.DEFAULT);
	}
	
	/**
	 * Create a new {@link EmptyColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param color See {@link #color}
	 * @param material See {@link #material}
	 */
	public EmptyColorTile(String id, String origin, ZColor color, Material material){
		super(id, origin, color, material);
	}
	
	@Override
	public Tile2D build(int x, int y, TileType2D backType){
		// TODO should back type be used?
		return new RectTile(x, y, this);
	}
}
