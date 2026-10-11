package zgame.things.still.tiles.twoDee;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.V2D;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.still.tiles.Tile;
import zgame.things.still.tiles.TileType;
import zgame.things.still.tiles.TileType2D;

/** A {@link TileType} which renders tiles using a solid color */
public abstract class ColorTile extends TileType2D{
	
	/** The color to draw this tile */
	private ZColor color;
	
	/**
	 * Create a new {@link ColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param shapeOffset See {@link #shapeOffset}
	 * @param color See {@link #color}
	 */
	public ColorTile(String id, String origin, V2D shapeOffset, ZColor color){
		this(id, origin, shapeOffset, color, Materials.DEFAULT);
	}
	
	/**
	 * Create a new {@link ColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param shapeOffset See {@link #shapeOffset}
	 * @param color See {@link #color}
	 * @param material See {@link #material}
	 */
	public ColorTile(String id, String origin, V2D shapeOffset, ZColor color, Material material){
		super(id, origin, shapeOffset, material);
		this.color = color;
	}
	
	/** @return See {@link #color} */
	public ZColor getColor(){
		return this.color;
	}
	
	/**
	 * Set the color used to render this tile. Note: changing the color of this tile will change the color of all tiles of the same type
	 *
	 * @param color see {@link #color}
	 */
	public void setColor(ZColor color){
		this.color = color;
	}
	
	@Override
	public void render(Tile<V2D> t, Renderer r){
		// Render a rectangle of the bounds
		r.setColor(this.getColor());
		var pos = t.getMinPosition();
		var dims = t.getDimensions();
		r.drawRectangle(pos.getX(), pos.getY(), dims.getWidth(), dims.getHeight());
	}
	
}
