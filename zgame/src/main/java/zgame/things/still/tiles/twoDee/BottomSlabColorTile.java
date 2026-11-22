package zgame.things.still.tiles.twoDee;

import zgame.core.graphics.ZColor;
import zgame.physics.V2D;
import zgame.physics.material.Material;
import zgame.things.type.bounds.twoDee.shape.RectangleShape;

/** A basic tile that draws the bottom slab of a tile */
public class BottomSlabColorTile extends ColorTile implements RectangleShape{
	
	private static final double HEIGHT = Tile2D.size() * 0.5;
	
	/**
	 * Create a new {@link ColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param frontColor The color to use for drawing the slab
	 * @param material See {@link #material}
	 */
	public BottomSlabColorTile(String id, String origin, ZColor frontColor, Material material){
		super(id, origin, new V2D(0, HEIGHT), frontColor, material);
	}
	
	@Override
	public V2D getDimensions(){
		return new V2D(Tile2D.size(), HEIGHT);
	}
	
}
