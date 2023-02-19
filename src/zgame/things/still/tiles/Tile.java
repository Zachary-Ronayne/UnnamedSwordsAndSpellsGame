package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.type.GameThing;
import zgame.things.type.HitBox;
import zgame.things.type.Materialable;
import zgame.things.type.PositionedRectangleThing;

/** A {@link GameThing} with a rectangular hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public class Tile extends PositionedRectangleThing implements Materialable{
	
	/** The default size of tiles */
	public static final double TILE_SIZE = 64;
	
	/** The inverse of {@link #TILE_SIZE} */
	public static final double TILE_SIZE_INVERSE = 1.0 / TILE_SIZE;
	
	/** The index of this tile on the x axis */
	private final int xIndex;
	/** The index of this tile on the y axis */
	private final int yIndex;
	
	/** The type of this tile */
	private final TileType type;
	
	/**
	 * Make a new tile at the given index of the default color
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 */
	public Tile(int x, int y){
		this(0, 0, BaseTiles.AIR);
	}
	
	/**
	 * Make a new tile at the given index and of the given color
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 * @param type See {@link #type}
	 */
	public Tile(int x, int y, TileType type){
		super(x * size(), y * size(), size(), size());
		this.xIndex = x;
		this.yIndex = y;
		this.type = type;
	}
	
	/** @return See {@link #xIndex} */
	public int getXIndex(){
		return this.xIndex;
	}
	
	/** @return See {@link #yIndex} */
	public int getYIndex(){
		return this.yIndex;
	}
	
	/** @return See {@link TileType} */
	public TileType getType(){
		return type;
	}
	
	@Override
	public Material getMaterial(){
		return this.getType().getMaterial();
	}
	
	/** See {@link TileHitbox#collide(Tile, HitBox)} */
	public CollisionResponse collide(HitBox obj){
		return this.getType().getHitbox().collide(this, obj);
	}
	
	/** See {@link TileHitbox#intersects(Tile, HitBox)} */
	public boolean intersects(HitBox obj){
		return this.getType().getHitbox().intersects(this, obj);
	}
	
	@Override
	public void render(Game game, Renderer r){
		this.getType().render(this, game, r);
	}
	
	/** @return The unit size of a tile */
	public static double size(){
		return TILE_SIZE;
	}
	
	/** @return The inverse of {@link #size()} */
	public static double inverseSize(){
		return TILE_SIZE_INVERSE;
	}
	
}
