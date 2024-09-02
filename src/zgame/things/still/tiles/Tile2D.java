package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.collision.CollisionResult;
import zgame.physics.material.Material;
import zgame.things.type.*;
import zgame.things.type.bounds.Bounds2D;
import zgame.things.type.bounds.HitBox2D;

/** A {@link GameThing} with a rectangular hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public class Tile2D extends GameThing implements Tile<HitBox2D>, Bounds2D, Materialable{
	
	// TODO potentially abstract out Tile2D and Tile3D into Tile
	
	/** The default size of tiles */
	public static final double TILE_SIZE = 64;
	
	/** The inverse of {@link #TILE_SIZE} */
	public static final double TILE_SIZE_INVERSE = 1.0 / TILE_SIZE;
	
	/** The index of this tile on the x axis */
	private final int xIndex;
	/** The index of this tile on the y axis */
	private final int yIndex;
	
	/** The type of this tile used for rendering the background of this tile. This is only used for rendering */
	private TileType2D backType;
	
	/** The type of this tile used for rendering the front, foreground, of this tile */
	private TileType2D frontType;
	
	/** The x coordinate of this tile */
	private final double x;
	/** The y coordinate of this tile */
	private final double y;
	
	/**
	 * Make a new tile at the given index as an air tile
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 */
	public Tile2D(int x, int y){
		this(x, y, BaseTiles2D.AIR);
	}
	
	/**
	 * Make a new tile at the given index of the default color
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 * @param type See {@link #frontType}
	 */
	public Tile2D(int x, int y, TileType2D type){
		this(x, y, BaseTiles2D.AIR, type);
	}
	
	/**
	 * Make a new tile at the given index and of the given color
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 * @param frontType See {@link #frontType}
	 */
	public Tile2D(int x, int y, TileType2D backType, TileType2D frontType){
		super();
		this.xIndex = x;
		this.yIndex = y;
		this.backType = backType;
		this.frontType = frontType;
		this.x = x * size();
		this.y = y * size();
	}
	
	/** @return See {@link #xIndex} */
	public int getXIndex(){
		return this.xIndex;
	}
	
	/** @return See {@link #yIndex} */
	public int getYIndex(){
		return this.yIndex;
	}
	
	/** @return See {@link #backType} */
	public TileType2D getBackType(){
		return this.backType;
	}
	
	/** @param backType See {@link #backType} */
	public void setBackType(TileType2D backType){
		this.backType = backType;
	}
	
	/** @return See {@link #frontType} */
	public TileType2D getFrontType(){
		return this.frontType;
	}
	
	/** @param frontType See {@link #frontType} */
	public void setFrontType(TileType2D frontType){
		this.frontType = frontType;
	}
	
	@Override
	public Material getMaterial(){
		return this.getFrontType().getMaterial();
	}
	
	/** See {@link TileHitbox#collide(Tile2D, HitBox2D)} */
	public CollisionResult collide(HitBox2D obj){
		return this.getFrontType().getHitbox().collide(this, obj);
	}
	
	@Override
	public void render(Game game, Renderer r){
		this.getBackType().render(this, game, r);
		this.getFrontType().render(this, game, r);
	}
	
	/** @return The unit size of a tile */
	public static double size(){
		return TILE_SIZE;
	}
	
	/** @return The inverse of {@link #size()} */
	public static double inverseSize(){
		return TILE_SIZE_INVERSE;
	}
	
	@Override
	public double getX(){
		return this.x;
	}
	
	@Override
	public double getY(){
		return this.y;
	}
	
	@Override
	public double getWidth(){
		return size();
	}
	
	@Override
	public double getHeight(){
		return size();
	}
	
	@Override
	public double maxX(){
		return this.getX() + this.getWidth();
	}
	
	@Override
	public double maxY(){
		return this.getY() + this.getHeight();
	}
}
