package zgame.things.still.tiles;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.material.Material;

/** A basic tile that draws the bottom slab of a tile */
public class BottomSlabColorTile extends ColorTile{
	
	/**
	 * Create a new {@link ColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param frontColor The color to use for drawing the slab
	 * @param material See {@link #material}
	 */
	public BottomSlabColorTile(String id, String origin, ZColor frontColor, Material material){
		super(id, origin, TileHitbox2D.BOTTOM_SLAB, frontColor, material);
	}
	
	@Override
	public void render(Tile2D t, Renderer r){
		r.setColor(this.getColor());
		var b = t.getBounds();
		r.drawRectangle(b.getX(), b.getCenterY(), b.getWidth(), b.getHeight() * 0.5);
	}
}
