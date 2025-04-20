package zgame.things.still.tiles;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.image.GameImage;
import zgame.physics.material.Material;

/** A variation of {@link CubeTexTile} which renders textures with a tint for a tile */
public class CubeTexTintTile extends CubeTexTile{
	
	/** The color of the tint of this tile */
	private ZColor tint;
	
	/**
	 * Create a new {@link CubeTexTintTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param fileName See {@link #getFileName()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #material}
	 * @param tint See {@link #tint}
	 */
	public CubeTexTintTile(String id, String origin, String fileName, TileHitbox3D hitbox, Material material, ZColor tint){
		super(id, origin, fileName, hitbox, material);
		this.tint = tint;
	}
	
	@Override
	public ZColor getColor(){
		return this.getTint();
	}
	
	@Override
	protected void renderTile(RectRender3D rect, GameImage image, Renderer r){
		r.pushTextureTintShader();
		r.drawRectPrismTex(rect, image);
		r.popShader();
	}
	
	/** @param tint See {@link #tint} */
	public void setTint(ZColor tint){
		this.tint = tint;
	}
	
	/** @return See {@link #tint} */
	public ZColor getTint(){
		return this.tint;
	}
}
