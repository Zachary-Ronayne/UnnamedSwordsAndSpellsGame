package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.material.Material;

/** A basic tile that draws a circle inscribed by a tile */
public class CircleColorTile extends ColorTile{
	
	/**
	 * Create a new {@link ColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param frontColor The color to use for drawing the slab
	 * @param material See {@link #material}
	 */
	public CircleColorTile(String id, String origin, ZColor frontColor, Material material){
		super(id, origin, TileHitbox2D.CIRCLE, frontColor, material);
	}
	
	@Override
	public void render(Tile2D t, Game g, Renderer r){
		r.setColor(this.getColor());
		var b = t.getBounds();
		r.drawCircle(b.getCenterX(), b.getCenterY(), b.getWidth() * 0.5);
	}
}
