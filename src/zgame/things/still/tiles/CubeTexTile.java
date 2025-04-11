package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.physics.material.Material;

/** A simple tile which has a constant material */
public class CubeTexTile extends TileType3D{
	
	/** The {@link Material} of this {@link CubeTexTile} */
	private final Material material;
	
	/** The file name where the texture of this tile comes from */
	private final String fileName;
	
	/**
	 * Create a new {@link CubeTexTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param fileName See {@link #fileName}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #material}
	 */
	public CubeTexTile(String id, String origin, String fileName, TileHitbox3D hitbox, Material material){
		super(id, origin, hitbox, material);
		this.material = material;
		this.fileName = fileName;
	}
	
	/** @return See {@link #fileName} */
	public String getFileName(){
		return this.fileName;
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
		r.drawRectPrismTex(new RectRender3D(t.getX(), t.getY(), t.getZ(), t.getWidth(), t.getHeight(), t.getLength()), g.getImage(this.getFileName()));
	}
}
