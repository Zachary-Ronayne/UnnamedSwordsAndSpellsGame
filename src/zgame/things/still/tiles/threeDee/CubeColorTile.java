package zgame.things.still.tiles.threeDee;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.material.Material;

/** A simple tile which has a constant material */
public class CubeColorTile extends CubeTile{
	
	/** The initial color to use for rendering this tile before any modifications */
	private ZColor baseColor;
	
	/**
	 * Create a new {@link CubeColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param material See {@link #material}
	 */
	public CubeColorTile(String id, String origin, ZColor baseColor, Material material){
		super(id, origin, material);
		this.baseColor = baseColor;
	}
	
	/** @return See {@link #baseColor} */
	public ZColor getBaseColor(){
		return this.baseColor;
	}
	
	/** @param baseColor See {@link #baseColor} */
	public void setBaseColor(ZColor baseColor){
		this.baseColor = baseColor;
	}
	
	@Override
	public void render(Tile3D t, Renderer r){
		var c = this.getBaseColor();
		// issue#46 render tiles with transparency properly, maybe this as is, is good enough, just only render them if they are fully opaque
		if(c.alpha() < 1) return;
		
		// issue#47 render with ambient occlusion or at least some similar effect to make every face not the same color
		
		// issue#48 only render the necessary faces
		
		// TODO return vectors for the coordinates and dimensions
		var pos = t.getBottomCenter();
		var dims = t.getDimensions();
		r.drawRectPrism(new RectRender3D(
				pos.getX(), pos.getY(), pos.getZ(),
				dims.getWidth(), dims.getHeight(), dims.getLength()
		), c, c, c, c, c, c);
	}
}
