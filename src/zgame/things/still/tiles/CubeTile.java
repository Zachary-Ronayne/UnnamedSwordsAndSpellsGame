package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.material.Material;

/** A simple tile which has a constant material */
public class CubeTile extends TileType3D{
	
	/** The {@link Material} of this {@link CubeTile} */
	private final Material material;
	
	// TODO make this able to use textures
	/** The initial color to use for rendering this tile before any modifications */
	private final ZColor baseColor;
	
	/**
	 * Create a new {@link CubeTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #material}
	 */
	public CubeTile(String id, String origin, TileHitbox3D hitbox, ZColor baseColor, Material material){
		super(id, origin, hitbox);
		this.baseColor = baseColor;
		this.material = material;
	}
	
	/** @return See {@link #baseColor} */
	public ZColor getBaseColor(){
		return this.baseColor;
	}
	
	@Override
	public Material getMaterial(){
		return this.material;
	}
	
	@Override
	public void render(Tile3D t, Game g, Renderer r){
		var c = this.getBaseColor();
		// TODO render tiles with transparency properly, maybe this as is is good enough, just only render them if they are fully opaque
		if(c.alpha() < 1) return;
		
		// TODO render with ambient occlusion or at least some similar effect to make every face not the same color
		
		// TODO only render the necessary faces
		
		r.drawRectPrism(t.getX(), t.getY(), t.getZ(), t.getWidth(), t.getHeight(), t.getLength(), 0, c, c, c, c, c, c);
	}
}
