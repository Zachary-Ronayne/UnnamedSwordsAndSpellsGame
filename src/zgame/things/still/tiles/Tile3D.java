package zgame.things.still.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.collision.CollisionResult3D;
import zgame.physics.material.Material;
import zgame.things.type.GameThing;
import zgame.things.type.Materialable;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.things.type.bounds.RectPrismBounds;

/** A {@link GameThing} with a cube hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public class Tile3D extends GameThing implements Tile<HitBox3D, CollisionResult3D>, Bounds3D, RectPrismBounds, Materialable{
	
	/** The default size of tiles */
	public static final double TILE_SIZE = 1;
	
	/** The inverse of {@link #TILE_SIZE} */
	public static final double TILE_SIZE_INVERSE = 1.0 / TILE_SIZE;
	
	/** The index of this tile on the x axis */
	private final int xIndex;
	/** The index of this tile on the y axis */
	private final int yIndex;
	/** The index of this tile on the z axis */
	private final int zIndex;
	
	/** The type of tile in all contexts */
	private TileType3D type;
	
	/** The x coordinate of this tile */
	private final double x;
	/** The y coordinate of this tile */
	private final double y;
	/** The z coordinate of this tile */
	private final double z;
	
	/**
	 * Make a new tile at the given index as an air tile
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 * @param z See {@link #zIndex}
	 */
	public Tile3D(int x, int y, int z){
		this(x, y, z, BaseTiles3D.AIR);
	}
	
	/**
	 * Make a new tile at the given index and of the given color
	 *
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 * @param z See {@link #zIndex}
	 * @param type See {@link #type}
	 */
	public Tile3D(int x, int y, int z, TileType3D type){
		super();
		this.xIndex = x;
		this.yIndex = y;
		this.zIndex = z;
		this.type = type;
		double size = size();
		this.x = (x + 0.5) * size;
		this.y = y * size();
		this.z = (z + 0.5) * size;
	}
	
	/** @return See {@link #xIndex} */
	public int getXIndex(){
		return this.xIndex;
	}
	
	/** @return See {@link #yIndex} */
	public int getYIndex(){
		return this.yIndex;
	}
	
	/** @return See {@link #zIndex} */
	public int getzIndex(){
		return this.zIndex;
	}
	
	/** @return See {@link #type} */
	public TileType3D getType(){
		return this.type;
	}
	
	/** @param type See {@link #type} */
	public void setType(TileType3D type){
		this.type = type;
	}
	
	@Override
	public Material getMaterial(){
		return this.getType().getMaterial();
	}
	
	@Override
	public CollisionResult3D collide(HitBox3D obj){
		return this.getType().getHitbox().collide(this, obj);
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
	
	@Override
	public double getX(){
		return this.x;
	}
	
	@Override
	public double getY(){
		return this.y;
	}
	
	/** @return See {@link #z} */
	@Override
	public double getZ(){
		return this.z;
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
	public double getLength(){
		return size();
	}
	
}
