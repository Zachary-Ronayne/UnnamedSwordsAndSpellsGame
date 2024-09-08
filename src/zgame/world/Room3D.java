package zgame.world;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResult3D;
import zgame.physics.material.Materials;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.tiles.*;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.HitBox3D;

import java.util.Arrays;

/** A {@link Room} which is made of 3D tiles */
public class Room3D extends Room<HitBox3D, EntityThing3D, ZVector3D, Room3D, CollisionResult3D> implements Bounds3D{
	
	/** An array of 6 elements representing which of the 6 boundary walls are enabled for collision */
	private final boolean[] enabledBoundaries;
	
	/** An array of 6 elements representing how far along each axis the boundary exists from the origin (0, 0, 0) */
	private final double[] boundarySizes;
	
	// TODO should tiles be able to go into the negatives? Or have a Minecraft like "chunk" system? Need to decide after getting a minimal implementation working
	
	/** All tiles which are used by this room */
	private Tile3D[][][] tiles;
	
	/** The number of tiles in the x axis in the room */
	private int tilesX;
	
	/** The number of tiles in the y axis in the room */
	private int tilesY;
	
	/** The number of tiles in the z axis in the room */
	private int tilesZ;
	
	/**
	 * Create a new empty room in 3D space with the given tile size
	 * @param tilesX See {@link #tilesX}
	 * @param tilesY See {@link #tilesY}
	 * @param tilesZ See {@link #tilesZ}
	 */
	public Room3D(int tilesX, int tilesY, int tilesZ){
		super();
		this.initTiles(tilesX, tilesY, tilesZ, BaseTiles3D.AIR);
		
		this.enabledBoundaries = new boolean[6];
		this.setAllBoundaries(true);
		this.boundarySizes = new double[6];
		this.setAllBoundaries(4);
		this.setBoundary(Directions3D.DOWN, 0);
	}
	
	/**
	 * Initialize {@link #tiles} to the given size
	 *
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 * @param zTiles The number of tiles on the z axis
	 * @param t The type for every tile
	 */
	public void initTiles(int xTiles, int yTiles, int zTiles, TileType3D t){
		this.tilesX = xTiles;
		this.tilesY = yTiles;
		this.tilesZ = zTiles;
		
		this.tiles = new Tile3D[xTiles][yTiles][zTiles];
		for(int x = 0; x < xTiles; x++){
			for(int y = 0; y < yTiles; y++){
				for(int z = 0; z < zTiles; z++){
					this.setTile(x, y, z, t);
				}
			}
		}
	}
	
	/** The position of a room will always be the origin (0, 0, 0) */
	@Override
	public final double getX(){
		return 0;
	}
	
	/** The position of a room will always be the origin (0, 0, 0) */
	@Override
	public final double getY(){
		return 0;
	}
	
	/** The position of a room will always be the origin (0, 0, 0) */
	@Override
	public final double getZ(){
		return 0;
	}
	
	@Override
	public double getWidth(){
		return this.boundarySizes[Directions3D.EAST] + this.boundarySizes[Directions3D.WEST];
	}
	
	@Override
	public double getHeight(){
		return this.boundarySizes[Directions3D.UP] + this.boundarySizes[Directions3D.DOWN];
	}
	
	@Override
	public double getLength(){
		return this.boundarySizes[Directions3D.NORTH] + this.boundarySizes[Directions3D.SOUTH];
	}
	
	/**
	 * Enable the given boundary if it is disabled, otherwise disable it
	 *
	 * @param direction The boundary to modify, defined in {@link Directions3D}
	 */
	public void toggleBoundary(int direction){
		this.setBoundary(direction, !this.boundaryEnabled(direction));
	}
	
	/**
	 * Enable or disable the given boundary
	 *
	 * @param direction The boundary to modify, defined in {@link Directions3D}
	 * @param enabled true to enable the boundary, false to disable it
	 */
	public void setBoundary(int direction, boolean enabled){
		this.enabledBoundaries[direction] = enabled;
	}
	
	/**
	 * @param direction The boundary to check for, defined in {@link Directions3D}
	 * @return true if the boundary is enabled, false otherwise
	 */
	public boolean boundaryEnabled(int direction){
		return this.enabledBoundaries[direction];
	}
	
	/**
	 * Enable or disable all boundaries
	 *
	 * @param enabled true to enabled, false to disable
	 */
	public void setAllBoundaries(boolean enabled){
		Arrays.fill(this.enabledBoundaries, enabled);
	}
	
	/**
	 * Update the size of the every boundary
	 *
	 * @param size The new size for every boundary
	 */
	public void setAllBoundaries(double size){
		Arrays.fill(this.boundarySizes, size);
	}
	
	/**
	 * Update the size of the given boundary
	 *
	 * @param direction The boundary to modify, defined in {@link Directions3D}
	 * @param size The new size for the boundary
	 */
	public void setBoundary(int direction, double size){
		this.boundarySizes[direction] = size;
	}
	
	/**
	 * @param direction The boundary to find, defined in {@link Directions3D}
	 * @return The size of the given boundary
	 */
	public double getBoundary(int direction){
		return this.boundarySizes[direction];
	}
	
	/**
	 * Set the dimensions of this room to be evenly split on the two given boundaries, to be evenly divided by the given size
	 *
	 * @param pos The positive direction
	 * @param neg The negative direction
	 * @param totalSize The new size to be split
	 */
	private void setEqualSize(int pos, int neg, double totalSize){
		double size = totalSize * 0.5;
		this.setBoundary(pos, size);
		this.setBoundary(neg, size);
	}
	
	/**
	 * Set the width of this room i.e. the boundaries on the x axis, to be evenly divided by the given size
	 *
	 * @param width The new width of the room, width/2 will be the boundary size on both axes
	 */
	public void setEqualWidth(double width){
		this.setEqualSize(Directions3D.EAST, Directions3D.WEST, width);
	}
	
	/**
	 * Set the height of this room i.e. the boundaries on the y axis, to be evenly divided by the given size
	 *
	 * @param height The new height of the room, height/2 will be the boundary size on both axes
	 */
	public void setEqualHeight(double height){
		this.setEqualSize(Directions3D.UP, Directions3D.DOWN, height);
	}
	
	/**
	 * Set the length of this room i.e. the boundaries on the z axis, to be evenly divided by the given size
	 *
	 * @param length The new length of the room, length/2 will be the boundary size on both axes
	 */
	public void setEqualLength(double length){
		this.setEqualSize(Directions3D.NORTH, Directions3D.SOUTH, length);
	}
	
	@Override
	public void render(Game game, Renderer r){
		// TODO make a way to efficiently render tiles, i.e. only render the ones that need to be rendered
		for(int x = 0; x < this.tilesX; x++){
			for(int y = 0; y < this.tilesY; y++){
				for(int z = 0; z < this.tilesZ; z++){
					this.tiles[x][y][z].render(game, r);
				}
			}
		}
		
		super.render(game, r);
	}
	
	@Override
	public CollisionResult3D collide(HitBox3D obj){
		// x axis, i.e. east west
		double widthOffset = obj.getWidth() * 0.5;
		if(this.boundaryEnabled(Directions3D.EAST) && obj.getX() > this.getBoundary(Directions3D.EAST) - widthOffset){
			obj.setX(this.getBoundary(Directions3D.EAST) - widthOffset);
			obj.touchWall(Materials.BOUNDARY);
		}
		else if(this.boundaryEnabled(Directions3D.WEST) && obj.getX() < -this.getBoundary(Directions3D.WEST) + widthOffset){
			obj.setX(-this.getBoundary(Directions3D.WEST) + widthOffset);
			obj.touchWall(Materials.BOUNDARY);
		}
		
		// z axis, i.e. north south
		double lengthOffset = obj.getLength() * 0.5;
		if(this.boundaryEnabled(Directions3D.NORTH) && obj.getZ() > this.getBoundary(Directions3D.NORTH) - lengthOffset){
			obj.setZ(this.getBoundary(Directions3D.NORTH) - lengthOffset);
			obj.touchWall(Materials.BOUNDARY);
		}
		else if(this.boundaryEnabled(Directions3D.SOUTH) && obj.getZ() < -this.getBoundary(Directions3D.SOUTH) + lengthOffset){
			obj.setZ(-this.getBoundary(Directions3D.SOUTH) + lengthOffset);
			obj.touchWall(Materials.BOUNDARY);
		}
		
		// y axis, i.e. up down
		double height = obj.getHeight();
		if(this.boundaryEnabled(Directions3D.UP) && obj.getY() > this.getBoundary(Directions3D.UP) - height){
			obj.setY(this.getBoundary(Directions3D.UP) - height);
			obj.touchCeiling(Materials.BOUNDARY);
		}
		else if(this.boundaryEnabled(Directions3D.DOWN) && obj.getY() < -this.getBoundary(Directions3D.DOWN)){
			obj.setY(-this.getBoundary(Directions3D.DOWN));
			obj.touchFloor(Materials.BOUNDARY);
		}
		
		// TODO implement collision with tiles
		// TODO also call this when the thing touches the ground once proper collision is implemented
		else if(obj.isOnGround()) {
			if(obj.getY() != obj.getPX()) obj.leaveFloor();
			else obj.touchFloor(obj.getFloorMaterial());
		}
		
		return new CollisionResult3D();
	}
	
	@Override
	public Class<HitBox3D> getHitBoxType(){
		return HitBox3D.class;
	}
	
	@Override
	public Class<EntityThing3D> getEntityClass(){
		return EntityThing3D.class;
	}
	
	/**
	 * Set the tile at the given indexes
	 * @param x The x index
	 * @param y The y index
	 * @param z The x index
	 * @param t The new tile type
	 */
	public void setTile(int x, int y, int z, TileType3D t){
		this.tiles[x][y][z] = new Tile3D(x, y, z, t);
	}
	
	/** @return See {@link #tilesX} */
	public int getTilesX(){
		return this.tilesX;
	}
	
	/** @return See {@link #tilesY} */
	public int getTilesY(){
		return this.tilesY;
	}
	
	/** @return See {@link #tilesZ} */
	public int getTilesZ(){
		return this.tilesZ;
	}
}
