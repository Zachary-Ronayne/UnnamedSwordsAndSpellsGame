package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.collision.CollisionResult;
import zgame.physics.material.Material;
import zgame.things.type.Materialable;
import zgame.things.type.bounds.HitBox;

/** An enum that defines tiles that can exist. Extend this enum to add new tile types */
public abstract class TileType<H extends HitBox<H, C>, T extends Tile<H, C>, TH extends TileHitbox<H, T, C>, C extends CollisionResult<C>> implements Materialable{
	
	/** The hitbox of this tile type */
	private final TH hitbox;
	
	/** The unique string that identifies this {@link TileType} from others with the same {@link #origin} */
	private final String id;
	
	/**
	 * The name of the place where this {@link TileType} comes from. Use "base" for tiles inherent to the base game engine, and create a custom origin for new sets of
	 * tiles. For instance, individual games might want to use different origin names
	 */
	private final String origin;
	
	/** The {@link Material} of this {@link TileType} */
	private final Material material;
	
	/**
	 * Create a new tile type
	 *
	 * @param id See {@link #id}
	 * @param hitbox See {@link #hitbox}
	 */
	protected TileType(String id, String origin, TH hitbox, Material material){
		this.id = id;
		this.origin = origin;
		this.hitbox = hitbox;
		this.material = material;
	}
	
	/** @return The unique identifier for this {@link TileType} */
	public String getId(){
		return this.id;
	}
	
	/** @return See {@link #origin} */
	public String getOrigin(){
		return this.origin;
	}
	
	/** @return The full name of this TileType in the format of origin.id. If origin is myGame, and id is stone, then this method will return "myGame.stone" */
	public String getName(){
		return String.join(".", this.getOrigin(), this.getId());
	}
	
	/** @return See {@link TileHitbox} */
	public TH getHitbox(){
		return this.hitbox;
	}
	
	/**
	 * Draw the given tile using this {@link TileType}
	 *
	 * @param t The tile to draw
	 * @param g The game where the tile is drawn
	 * @param r The renderer to use for drawing
	 */
	public abstract void render(T t, Game g, Renderer r);
	
	@Override
	public Material getMaterial(){
		return this.material;
	}
	
}
