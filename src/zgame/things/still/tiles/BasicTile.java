package zgame.things.still.tiles;

import zgame.physics.material.Material;

/** A simple tile which has a constant material */
public abstract class BasicTile extends TileType2D{
	
	/** The {@link Material} of this {@link BasicTile} */
	private final Material material;
	
	/**
	 * Create a new {@link ColorTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param hitbox See {@link #getHitbox()}
	 * @param material See {@link #material}
	 */
	public BasicTile(String id, String origin, TileHitbox2D hitbox, Material material){
		super(id, origin, hitbox);
		this.material = material;
	}
	
	@Override
	public Material getMaterial(){
		return this.material;
	}
	
}
