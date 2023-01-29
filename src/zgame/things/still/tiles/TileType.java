package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.type.Materialable;

/** An enum that defines tiles that can exist. Extend this enum to add new tile types */
public abstract class TileType implements Materialable{
	
	/** The hitbox of this tile type */
	private final TileHitbox hitbox;

	/** The unique string that identifies this {@link TileType} from others with the same {@link #origin} */
	private final String id;

	/**
	 * The name of the place where this {@link TileType} comes from. Use "base" for tiles inherent to the base game engine, and create a custom origin for new sets of
	 * tiles. For instance, individual games might want to use different origin names
	 */
	private final String origin;
	
	/**
	 * Create a new tile type
	 * @param id See {@link #id}
	 * @param hitbox See {@link #hitbox}
	 */
	protected TileType(String id, String origin, TileHitbox hitbox){
		this.id = id;
		this.origin = origin;
		this.hitbox = hitbox;
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
	
	/** @return See {@link zgame.things.type.HitBox} */
	public TileHitbox getHitbox(){
		return this.hitbox;
	}

	/**
	 * Draw the given tile using this {@link TileType}
	 * 
	 * @param t The tile to draw
	 * @param g The game where the tile is drawn
	 * @param r The renderer to use for drawing
	 */
	public abstract void render(Tile t, Game g, Renderer r);

}
