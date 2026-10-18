package zgame.things.still.tiles.twoDee;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.V2D;
import zgame.physics.material.Material;
import zgame.things.still.tiles.Tile;
import zgame.things.type.bounds.twoDee.shape.CircleShape;

/** A basic tile that draws a circle inscribed by a tile */
public class CircleColorTile extends ColorTile implements CircleShape{
	
	/**
	 * Create a new {@link ColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param frontColor The color to use for drawing the slab
	 * @param material See {@link #material}
	 */
	public CircleColorTile(String id, String origin, ZColor frontColor, Material material){
		super(id, origin, null, frontColor, material);
	}
	
	@Override
	public void render(Tile<V2D> t, Renderer r){
		r.setColor(this.getColor());
		var pos = t.getCenterPosition();
		var dims = this.getDimensions();
		r.drawCircle(pos.getX(), pos.getY(), dims.getWidth() * 0.5);
	}
	
	@Override
	public V2D getDimensions(){
		double s = Tile2D.size();
		return new V2D(s, s);
	}
}
