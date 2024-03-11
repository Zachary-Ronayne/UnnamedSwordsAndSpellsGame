package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.type.*;
import zgame.things.type.bounds.Bounds2D;
import zgame.things.type.bounds.HitBox2D;

/** A {@link GameThing} with a rectangular hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public class Tile extends GameThing implements Bounds2D, Materialable{
	
	/** The default size of tiles */
	public static final double TILE_SIZE = 64;
	
	/** The inverse of {@link #TILE_SIZE} */
	public static final double TILE_SIZE_INVERSE = 1.0 / TILE_SIZE;
	
	/** The index of this tile on the x axis */
	private final int xIndex;
	/** The index of this tile on the y axis */
	private final int yIndex;
	
	/** The type of this tile used for rendering the background of this tile. This is only used for rendering */
	private TileType backType;
	
	/** The type of this tile used for rendering the front, foreground, of this tile */
	private TileType frontType;
	
	// TODO make these coordinates non mutable? Or somehow be able to recalculate
	/** The x coordinate of this tile */
	private double x;
	/** The y coordinate of this tile */
	private double y;
	
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
	 * Make a new tile at the given index of the default color
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 * @param type See {@link #frontType}
	 */
	public Tile(int x, int y, TileType type){
		this(x, y, BaseTiles.AIR, type);
	}
	
	/**
	 * Make a new tile at the given index and of the given color
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 * @param frontType See {@link #frontType}
	 */
	public Tile(int x, int y, TileType backType, TileType frontType){
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
	public TileType getBackType(){
		return this.backType;
	}
	
	/** @param backType See {@link #backType} */
	public void setBackType(TileType backType){
		this.backType = backType;
	}
	
	/** @return See {@link #frontType} */
	public TileType getFrontType(){
		return this.frontType;
	}
	
	/** @param frontType See {@link #frontType} */
	public void setFrontType(TileType frontType){
		this.frontType = frontType;
	}
	
	@Override
	public Material getMaterial(){
		return this.getFrontType().getMaterial();
	}
	
	/** See {@link TileHitbox#collide(Tile, HitBox2D)} */
	public CollisionResponse collide(HitBox2D obj){
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
