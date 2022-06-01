package zgame.things.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;

/** A {@link TileType} which renders tiles as a solid color */
public class ColorTile extends TileType{
	
	/** The color to draw this tile */
	private ZColor color;
	
	/**
	 * Create a new {@link ColorTile} using the given data
	 * 
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param color See {@link #color}
	 */
	public ColorTile(String id, String origin, TileHitbox hitbox, ZColor color){
		super(id, origin, hitbox);
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
	public void render(Tile t, Game g, Renderer r){
		r.setColor(this.getColor());
		r.drawRectangle(t.getX(), t.getY(), t.getWidth(), t.getHeight());
	}
	
}
