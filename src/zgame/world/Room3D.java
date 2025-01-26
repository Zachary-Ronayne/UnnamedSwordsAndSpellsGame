package zgame.world;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZMath;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResult3D;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.ThingClickDetector3D;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.tiles.*;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.ClickerBounds;
import zgame.things.type.bounds.HitBox3D;
import zgame.things.type.bounds.RectPrismBounds;

import java.util.Arrays;

/** A {@link Room} which is made of 3D tiles */
public class Room3D extends Room<HitBox3D, EntityThing3D, ZVector3D, Room3D, CollisionResult3D> implements RectPrismBounds{
	
	/** An array of 6 elements representing which of the 6 boundary walls are enabled for collision */
	private final boolean[] enabledBoundaries;
	
	/** An array of 6 elements representing how far along each axis the boundary exists from the origin (0, 0, 0) */
	private final double[] boundarySizes;
	
	// issue#51 should tiles be able to go into the negatives? Or have a Minecraft like "chunk" system? Need to decide after getting a minimal implementation working
	
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
	 *
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
		this.getAllThings().addClass(ThingClickDetector3D.class);
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
	
	/** Set all boundaries so that they match the bounds the tiles take up */
	public void setTileBoundaries(){
		double tileSize = Tile3D.size();
		
		this.setBoundary(Directions3D.EAST, tileSize * this.getTilesX());
		this.setBoundary(Directions3D.WEST, 0);
		
		this.setBoundary(Directions3D.NORTH, tileSize * this.getTilesZ());
		this.setBoundary(Directions3D.SOUTH, 0);
		
		this.setBoundary(Directions3D.UP, tileSize * this.getTilesY());
		this.setBoundary(Directions3D.DOWN, 0);
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
		// issue#52 make a way to efficiently render tiles, i.e. only render the ones that need to be rendered
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
		boolean wasOnGround = obj.isOnGround();
		boolean wasOnCeiling = obj.isOnCeiling();
		boolean wasOnWall = obj.isOnWall();
		double mx = 0;
		double my = 0;
		double mz = 0;
		boolean wall = false;
		boolean top = false;
		boolean bot = false;
		double wallAngle = 0;
		Material material = null;
		double tileSize = Tile3D.size();
		int tilesX = this.getTilesX() - 1;
		int tilesY = this.getTilesY() - 1;
		int tilesZ = this.getTilesZ() - 1;
		
		int minX = (int)ZMath.minMax(0, tilesX, Math.floor(obj.minX() / tileSize));
		int maxX = (int)ZMath.minMax(0, tilesX, Math.floor(obj.maxX() / tileSize));
		int minY = (int)ZMath.minMax(0, tilesY, Math.floor(obj.minY() / tileSize));
		int maxY = (int)ZMath.minMax(0, tilesY, Math.floor(obj.maxY() / tileSize));
		int minZ = (int)ZMath.minMax(0, tilesZ, Math.floor(obj.minZ() / tileSize));
		int maxZ = (int)ZMath.minMax(0, tilesZ, Math.floor(obj.maxZ() / tileSize));
		
		// TODO fix glitchy walking when walking on top off and between tiles
		for(int x = minX; x <= maxX; x++){
			for(int z = minZ; z <= maxZ; z++){
				for(int y = minY; y <= maxY; y++){
					var t = this.tiles[x][y][z];
					var res = t.collide(obj);
					// Keep track of if a tile was touched
					boolean currentCollided = res.x() != 0 || res.y() != 0;
					
					mx += res.x();
					my += res.y();
					mz += res.z();
					if(res.wall()) {
						wall = true;
						wallAngle = res.wallAngle();
					}
					if(res.ceiling()) top = true;
					if(res.floor()) bot = true;
					// issue#15 try making it do only one final collision operation at the end
					obj.collide(res);
					
					// Record the material collided with, only if this tile was collided with
					if(currentCollided){
						// Set the material if there is none yet, or the floor
						if(material == null || bot) material = res.material();
					}
				}
			}
		}
		// If no material was selected, and the thing was on the ground, us the ground material, same goes for walls and then ceilings
		if(material == null){
			if(wasOnGround) material = obj.getFloorMaterial();
			if(wasOnWall) material = obj.getWallMaterial();
			if(wasOnCeiling) material = obj.getCeilingMaterial();
		}
		
		// Determine the final collision
		var res = new CollisionResult3D(mx, my, mz, wall, top, bot, material, wallAngle);
		
		boolean touchedFloor = false;
		boolean touchedCeiling = false;
		boolean touchedWall = false;
		
		// x axis, i.e. east west
		boolean touchedAxisX = false;
		if(this.boundaryEnabled(Directions3D.EAST)){
			double boundary = this.getBoundary(Directions3D.EAST) - (obj.getWidth() * 0.5);
			double objX = obj.getX();
			if(objX > boundary){
				obj.setX(boundary);
				obj.touchWall(new CollisionResult3D(-Math.abs(boundary - objX), 0, 0, true, false, false, this.getBoundaryMaterial(), ZMath.PI_BY_2));
				touchedAxisX = true;
			}
		}
		if(this.boundaryEnabled(Directions3D.WEST) && !touchedAxisX){
			double boundary = -this.getBoundary(Directions3D.WEST) + (obj.getWidth() * 0.5);
			double objX = obj.getX();
			if(objX < boundary){
				obj.setX(boundary);
				obj.touchWall(new CollisionResult3D(Math.abs(boundary - objX), 0, 0, true, false, false, this.getBoundaryMaterial(), ZMath.PI_BY_2));
				touchedAxisX = true;
			}
		}
		if(touchedAxisX) touchedWall = true;
		
		// z axis, i.e. north south
		boolean touchedAxisZ = false;
		if(this.boundaryEnabled(Directions3D.NORTH)){
			double boundary = this.getBoundary(Directions3D.NORTH) - (obj.getLength() * 0.5);
			double objZ = obj.getZ();
			if(objZ > boundary){
				obj.setZ(boundary);
				obj.touchWall(new CollisionResult3D(0, 0, Math.abs(boundary - objZ), true, false, false, this.getBoundaryMaterial(), 0));
				touchedAxisZ = true;
			}
		}
		if(this.boundaryEnabled(Directions3D.SOUTH) && !touchedAxisZ){
			double boundary = -this.getBoundary(Directions3D.SOUTH) + (obj.getLength() * 0.5);
			double objZ = obj.getZ();
			if(objZ < boundary){
				obj.setZ(boundary);
				obj.touchWall(new CollisionResult3D(0, 0, -Math.abs(boundary - objZ), true, false, false, this.getBoundaryMaterial(), 0));
				touchedAxisZ = true;
			}
		}
		if(touchedAxisZ) touchedWall = true;
		
		// y axis, i.e. up down
		if(this.boundaryEnabled(Directions3D.UP)){
			double boundary = this.getBoundary(Directions3D.UP) - obj.getHeight();
			double objY = obj.getY();
			if(objY > boundary){
				obj.setY(boundary);
				obj.touchCeiling(new CollisionResult3D(0, Math.abs(boundary - objY), 0, false, true, false, this.getBoundaryMaterial(), 0));
				touchedCeiling = true;
			}
		}
		if(this.boundaryEnabled(Directions3D.DOWN) && obj.getY() < -this.getBoundary(Directions3D.DOWN)){
			double boundary = -this.getBoundary(Directions3D.DOWN);
			double objY = obj.getY();
			if(objY < boundary){
				obj.setY(boundary);
				obj.touchFloor(new CollisionResult3D(0, -Math.abs(boundary - objY), 0, false, false, true, this.getBoundaryMaterial(), 0));
				touchedFloor = true;
			}
		}
		
		if(wasOnGround){
			// If the hitbox was on the ground, but no y axis movement happened, then the hitbox is still on the ground, so touch the floor
			if(obj.getPY() == obj.getY() || bot){
				if(!touchedFloor) obj.touchFloor(new CollisionResult3D(0, 0, 0, false, false, true, obj.getFloorMaterial(), res.wallAngle()));
			}
			// Otherwise, leave the floor
			else obj.leaveFloor();
		}
		
		// Same thing, but for the walls and for the ceiling
		if(wasOnCeiling){
			if(obj.getPY() == obj.getY() || top){
				if(!touchedCeiling) obj.touchCeiling(new CollisionResult3D(0, 0, 0, false, true, false, obj.getCeilingMaterial(), res.wallAngle()));
			}
			else obj.leaveCeiling();
		}
		
		if(wasOnWall){
			if(obj.getPX() == obj.getX() || wall){
				if(!touchedWall) obj.touchWall(new CollisionResult3D(0, 0, 0, true, false, false, obj.getWallMaterial(), res.wallAngle()));
			}
			else obj.leaveWall();
		}
		
		return res;
	}
	
	/**
	 * Have the given thing attempt to click on this room. The closest thing to the given clicker will be clicked, or nothing if there is nothing within clicking range
	 *
	 * @param game The game containing this room
	 * @param clicker The thing doing the clicking
	 * @return true if something was clicked, false otherwise
	 */
	public boolean attemptClick(Game game, ClickerBounds clicker){
		var clickables = this.getAllThings().get(ThingClickDetector3D.class);
		if(clickables != null){
			double closestDistance = -1;
			ThingClickDetector3D closestClickable = null;
			for(var c : clickables){
				double distance = c.findClickDistance(clicker);
				if((closestClickable == null || distance < closestDistance) && c.canClick(distance)){
					closestDistance = distance;
					closestClickable = c;
				}
			}
			if(closestClickable != null) closestClickable.handlePress(game, this);
		}
		// TODO account for clicking on tiles
		return false;
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
	 * Get the tile at the specified index
	 *
	 * @param x The tile index on the x axis
	 * @param y The tile index on the y axis
	 * @param z The tile index on the z axis
	 * @return The tile, or null if the tile is outside of the range of the grid
	 */
	public Tile3D getTile(int x, int y, int z){
		if(!ZMath.in(0, x, this.tilesX) || !ZMath.in(0, y, this.tilesY) || !ZMath.in(0, z, this.tilesZ)) return null;
		return this.getTileUnchecked(x, y, z);
	}
	
	/**
	 * Get the tile at the specified index
	 * Will cause an {@link IndexOutOfBoundsException} if the indexes are outside the range of the grid.
	 * Only call this method if the bounds are being checked separately. Use {@link #getTile(int, int, int)} instead to return null if the indexes go out of bounds
	 *
	 * @param x The tile index on the x axis
	 * @param y The tile index on the y axis
	 * @param z The tile index on the z axis
	 * @return The tile
	 */
	public Tile3D getTileUnchecked(int x, int y, int z){
		return this.tiles[x][y][z];
	}
	
	/**
	 * Set the tile at the given indexes
	 *
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
	
	public Material getBoundaryMaterial(){
		return Materials.BOUNDARY;
	}
	
}
