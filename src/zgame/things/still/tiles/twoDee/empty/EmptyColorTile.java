package zgame.things.still.tiles.twoDee.empty;

import zgame.core.graphics.ZColor;
import zgame.physics.material.Materials;
import zgame.things.still.tiles.TileType;
import zgame.things.still.tiles.twoDee.ColorTile;
import zgame.things.type.bounds.NoShape2D;

/** A {@link TileType} with no hitbox, rendering a solid color */
public class EmptyColorTile extends ColorTile implements NoShape2D{
	
	/**
	 * Create a new {@link EmptyColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param color See {@link #color}
	 */
	public EmptyColorTile(String id, String origin, ZColor color){
		super(id, origin, null, color, Materials.DEFAULT);
	}
}
