package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.type.*;
import zgame.things.type.bounds.HitBox;
import zgame.things.type.bounds.HitBox2D;
import zgame.things.type.bounds.RectangleHitBox;

/** A {@link GameThing} with a rectangular hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public class Tile extends PositionedRectangleThing implements RectangleHitBox{
	
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
		super(x * size(), y * size(), size(), size());
		this.xIndex = x;
		this.yIndex = y;
		this.backType = backType;
		this.frontType = frontType;
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
	
	/** See {@link TileHitbox#intersectsTile(Tile, HitBox2D)} */
	@Override
	public boolean intersects(HitBox<HitBox2D> obj){
		return this.getFrontType().getHitbox().intersectsTile(this, obj.get());
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
	
	// Tiles do not move or collide
	@Override
	public void collide(CollisionResponse r){}
	
	@Override
	public void touchFloor(Material touched){}
	
	@Override
	public void leaveFloor(){}
	
	@Override
	public void touchCeiling(Material touched){}
	
	@Override
	public void leaveCeiling(){}
	
	@Override
	public void touchWall(Material touched){}
	
	@Override
	public void leaveWall(){}
	
	@Override
	public boolean isOnGround(){
		return false;
	}
	
	@Override
	public boolean isOnCeiling(){
		return false;
	}
	
	@Override
	public boolean isOnWall(){
		return false;
	}
	
	@Override
	public double getPX(){
		return this.getX();
	}
	
	@Override
	public double getPY(){
		return this.getY();
	}
	
	/** @return Always an empty string, tiles do not use uuids */
	@Override
	public String getUuid(){
		return "";
	}
}
