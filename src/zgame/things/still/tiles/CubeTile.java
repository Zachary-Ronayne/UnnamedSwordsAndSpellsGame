package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.material.Material;

/** A simple tile which has a constant material */
public class CubeTile extends TileType3D{
	
	/** The {@link Material} of this {@link CubeTile} */
	private final Material material;
	
	// TODO make this not need a color for all tiles, only color tiles should need a color
	/** The initial color to use for rendering this tile before any modifications */
	private ZColor baseColor;
	
	/**
	 * Create a new {@link CubeTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #material}
	 */
	public CubeTile(String id, String origin, TileHitbox3D hitbox, ZColor baseColor, Material material){
		super(id, origin, hitbox, material);
		this.baseColor = baseColor;
		this.material = material;
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
	public Material getMaterial(){
		return this.material;
	}
	
	@Override
	public void render(Tile3D t, Game g, Renderer r){
		var c = this.getBaseColor();
		// issue#46 render tiles with transparency properly, maybe this as is, is good enough, just only render them if they are fully opaque
		if(c.alpha() < 1) return;
		
		// issue#47 render with ambient occlusion or at least some similar effect to make every face not the same color
		
		// issue#48 only render the necessary faces
		
		// TODO make this an abstract method where implementations decide how to render the tile
		r.drawRectPrism(new RectRender3D(t.getX(), t.getY(), t.getZ(), t.getWidth(), t.getHeight(), t.getLength()), c, c, c, c, c, c);
	}
}
