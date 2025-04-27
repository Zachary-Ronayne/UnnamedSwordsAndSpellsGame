package zgame.things.still.tiles;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.image.GameImage;
import zgame.core.graphics.image.ImageManager;
import zgame.physics.material.Material;

/** A simple tile which has a constant material */
public class CubeTexTile extends TileType3D{
	
	/** The {@link Material} of this {@link CubeTexTile} */
	private final Material material;
	
	/** The file name where the texture of this tile comes from */
	private final String fileName;
	
	/** The color used to render tiles be default, only opacity is relevant */
	private static final ZColor DEFAULT_COLOR = new ZColor(1, 1, 1, 1);
	
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
	
	/** @return By default returns see {@link #DEFAULT_COLOR}, override to use a custom color */
	public ZColor getColor(){
		return DEFAULT_COLOR;
	}
	
	@Override
	public void render(Tile3D t, Renderer r){
		r.setColor(this.getColor());
		this.renderTile(new RectRender3D(t.getX(), t.getY(), t.getZ(), t.getWidth(), t.getHeight(), t.getLength()), ImageManager.image(this.getFileName()), r);
	}
	
	/**
	 * Draw the given bounds of a tile as a cube, can override for custom rendering
	 *
	 * @param rect The bounds to render
	 * @param image The image for rendering the bounds
	 * @param r The renderer to use for drawing
	 */
	protected void renderTile(RectRender3D rect, GameImage image, Renderer r){
		// issue#46 render tiles with transparency properly, in this scenario textured ones, maybe this as is, is good enough, just only render them if they are fully opaque
		
		// issue#47 render with ambient occlusion or at least some similar effect to make every face not the same color
		// issue#48 only render the necessary faces
		r.drawRectPrismTex(rect, image);
	}
}
