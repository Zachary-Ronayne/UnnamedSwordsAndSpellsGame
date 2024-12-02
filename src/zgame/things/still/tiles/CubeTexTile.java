package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.material.Material;

/** A simple tile which has a constant material */
public class CubeTexTile extends TileType3D{
	
	/** The {@link Material} of this {@link CubeTexTile} */
	private final Material material;
	
	// TODO should this be saving an opengl id? Where should this be determined
	private final String textureId;
	
	/**
	 * Create a new {@link CubeTexTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #material}
	 */
	public CubeTexTile(String id, String origin, TileHitbox3D hitbox, String textureId, Material material){
		super(id, origin, hitbox, material);
		this.textureId = textureId;
		this.material = material;
	}
	
	/** @return See {@link #textureId} */
	public String getTextureId(){
		return this.textureId;
	}
	
	@Override
	public Material getMaterial(){
		return this.material;
	}
	
	@Override
	public void render(Tile3D t, Game g, Renderer r){
		// issue#46 render tiles with transparency properly, in this scenario textured ones, maybe this as is, is good enough, just only render them if they are fully opaque
		
		// issue#47 render with ambient occlusion or at least some similar effect to make every face not the same color
		// issue#48 only render the necessary faces
		r.setColor(1, 1, 1, 1);
		r.drawRectPrismTex(t.getX(), t.getY(), t.getZ(), t.getWidth(), t.getHeight(), t.getLength(), 0, 0, 0, 0, 0, 0, g.getImage(this.getTextureId()));
	}
}
