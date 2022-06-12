package zgame.things.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.collision.CollisionResponse;
import zgame.things.GameThing;
import zgame.things.PositionedRectangleThing;

/** A {@link GameThing} with a rectangular hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public class Tile extends PositionedRectangleThing{
	
	/** The default size of tiles */
	public static final double TILE_SIZE = 64;

	/** The inverse of {@link #TILE_SIZE} */
	public static final double TILE_SIZE_INVERSE = 1.0 / TILE_SIZE;

	/** The index of this tile on the x axis */
	private int xIndex;
	/** The index of this tile on the y axis */
	private int yIndex;
	
	/** The type of this tile */
	private TileType type;
	
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
	public CollisionResponse collideRect(double x, double y, double w, double h, double px, double py){
		return this.getType().getHitbox().collideRect(this, x, y, w, h, px, py);
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
