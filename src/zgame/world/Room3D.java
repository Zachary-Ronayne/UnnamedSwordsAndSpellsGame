package zgame.world;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZMath;
import zgame.physics.V3D;
import zgame.physics.collision.Collision;
import zgame.physics.collision.Collision3D;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.ThingClickDetector3D;
import zgame.things.entity.EntityThing;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.tiles.threeDee.BaseTiles3D;
import zgame.things.still.tiles.threeDee.Tile3D;
import zgame.things.still.tiles.TileType3D;
import zgame.things.type.bounds.ClickerBounds;
import zgame.things.type.bounds.HitBox;
import zgame.things.type.bounds.HitBox3D;
import zgame.things.type.bounds.RectPrismBounds;

import static zgame.world.Direction3D.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/** A {@link Room} which is made of 3D tiles */
public class Room3D extends Room<V3D> implements RectPrismBounds{
	
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
		this.enabledBoundaries = new boolean[6];
		this.initTiles(tilesX, tilesY, tilesZ, BaseTiles3D.AIR);
		
		this.setAllBoundaries(true);
		this.boundarySizes = new double[6];
		this.setAllBoundaries(4);
		this.setBoundary(DOWN, 0);
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
					this.setTile(x, y, z, t, true);
				}
			}
		}
		// Now compute all tile data once per tile
		for(int x = 0; x < xTiles; x++){
			for(int y = 0; y < yTiles; y++){
				for(int z = 0; z < zTiles; z++){
					this.computeTileData(x, y, z);
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
		return this.boundarySizes[WEST.i()] + this.boundarySizes[EAST.i()];
	}
	
	@Override
	public double getHeight(){
		return this.boundarySizes[UP.i()] + this.boundarySizes[DOWN.i()];
	}
	
	@Override
	public double getLength(){
		return this.boundarySizes[Direction3D.NORTH.i()] + this.boundarySizes[SOUTH.i()];
	}
	
	/**
	 * Enable the given boundary if it is disabled, otherwise disable it
	 *
	 * @param direction The boundary to modify
	 */
	public void toggleBoundary(Direction3D direction){
		this.setBoundary(direction, !this.boundaryEnabled(direction));
	}
	
	/**
	 * Enable or disable the given boundary
	 *
	 * @param direction The boundary to modify
	 * @param enabled true to enable the boundary, false to disable it
	 */
	public void setBoundary(Direction3D direction, boolean enabled){
		this.enabledBoundaries[direction.i()] = enabled;
	}
	
	/**
	 * @param direction The boundary to check for, defined in {@link Direction3D}
	 * @return true if the boundary is enabled, false otherwise
	 */
	public boolean boundaryEnabled(Direction3D direction){
		return this.enabledBoundaries[direction.index];
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
	 * @param direction The boundary to modify
	 * @param size The new size for the boundary
	 */
	public void setBoundary(Direction3D direction, double size){
		this.boundarySizes[direction.i()] = size;
	}
	
	/**
	 * @param direction The boundary to find
	 * @return The size of the given boundary
	 */
	public double getBoundary(Direction3D direction){
		return this.boundarySizes[direction.i()];
	}
	
	/**
	 * Set the dimensions of this room to be evenly split on the two given boundaries, to be evenly divided by the given size
	 *
	 * @param pos The positive direction
	 * @param neg The negative direction
	 * @param totalSize The new size to be split
	 */
	private void setEqualSize(Direction3D pos, Direction3D neg, double totalSize){
		double size = totalSize * 0.5;
		this.setBoundary(pos, size);
		this.setBoundary(neg, size);
	}
	
	/** Set all boundaries so that they match the bounds the tiles take up */
	public void setTileBoundaries(){
		double tileSize = Tile3D.size();
		
		this.setBoundary(WEST, tileSize * this.getTilesX());
		this.setBoundary(EAST, 0);
		
		this.setBoundary(NORTH, tileSize * this.getTilesZ());
		this.setBoundary(SOUTH, 0);
		
		this.setBoundary(UP, tileSize * this.getTilesY());
		this.setBoundary(DOWN, 0);
	}
	
	/**
	 * Set the width of this room i.e. the boundaries on the x axis, to be evenly divided by the given size
	 *
	 * @param width The new width of the room, width/2 will be the boundary size on both axes
	 */
	public void setEqualWidth(double width){
		this.setEqualSize(WEST, EAST, width);
	}
	
	/**
	 * Set the height of this room i.e. the boundaries on the y axis, to be evenly divided by the given size
	 *
	 * @param height The new height of the room, height/2 will be the boundary size on both axes
	 */
	public void setEqualHeight(double height){
		this.setEqualSize(UP, DOWN, height);
	}
	
	/**
	 * Set the length of this room i.e. the boundaries on the z axis, to be evenly divided by the given size
	 *
	 * @param length The new length of the room, length/2 will be the boundary size on both axes
	 */
	public void setEqualLength(double length){
		this.setEqualSize(NORTH, SOUTH, length);
	}
	
	@Override
	public void render(Renderer r){
		// issue#52 make a way to efficiently render tiles, i.e. only render the ones that need to be rendered
		for(int x = 0; x < this.tilesX; x++){
			for(int y = 0; y < this.tilesY; y++){
				for(int z = 0; z < this.tilesZ; z++){
					this.tiles[x][y][z].render(r);
				}
			}
		}
		
		super.render(r);
	}
	
	@Override
	public List<Collision<V3D>> collideInside(EntityThing<V3D> obj){
		double tileSize = Tile3D.size();
		int tilesX = this.getTilesX() - 1;
		int tilesY = this.getTilesY() - 1;
		int tilesZ = this.getTilesZ() - 1;
		
		var minPos = obj.getMinPosition();
		var maxPos = obj.getMinPosition();
		
		double minX = minPos.getX();
		double maxX = maxPos.getX();
		double minY = minPos.getY();
		double maxY = maxPos.getY();
		double minZ = minPos.getZ();
		double maxZ = maxPos.getZ();
		
		int tMinX = (int)ZMath.minMax(0, tilesX, Math.floor(minX / tileSize));
		int tMaxX = (int)ZMath.minMax(0, tilesX, Math.floor(maxX / tileSize));
		int tMinY = (int)ZMath.minMax(0, tilesY, Math.floor(minY / tileSize));
		int tMaxY = (int)ZMath.minMax(0, tilesY, Math.floor(maxY / tileSize));
		int tMinZ = (int)ZMath.minMax(0, tilesZ, Math.floor(minZ / tileSize));
		int tMaxZ = (int)ZMath.minMax(0, tilesZ, Math.floor(maxZ / tileSize));
		
		ArrayList<Collision<V3D>> collisions = new ArrayList<>();
		
		// Go through all tiles, and record all collisions
		for(int x = tMinX; x <= tMaxX; x++){
			for(int y = tMinY; y <= tMaxY; y++){
				for(int z = tMinZ; z <= tMaxZ; z++){
					var t = this.tiles[x][y][z];
					collisions.add(t.collide(obj));
				}
			}
		}
		
		// Check for collisions with all boundaries
		// TODO consider if this should all be consolidated to a single collision object
		// x axis, i.e. east west
		if(this.boundaryEnabled(WEST)){
			double boundary = this.getBoundary(WEST);
			if(minX > boundary){
				collisions.add(new Collision3D(obj,
						// TODO make some kind of convenience method for this to avoid this mess, also probably make it that it's possible to get individual coordinates
						(HitBox<V3D> hitbox) -> {
							var pos = hitbox.getPosition();
							return new V3D(boundary - Math.abs(hitbox.getMaxPosition().getX() - pos.getX()), pos.getY(), pos.getZ());
						},
						true, false, false, this.getBoundaryMaterial(), ZMath.PI_BY_2));
			}
		}
		if(this.boundaryEnabled(EAST)){
			// TODO probably make boundaries absolute coordinates instead of using negative and positive differently
			double boundary = -this.getBoundary(EAST);
			if(maxX < boundary){
				collisions.add(new Collision3D(obj,
						(HitBox<V3D> hitbox) -> {
							var pos = hitbox.getPosition();
							return new V3D(boundary + Math.abs(hitbox.getMinPosition().getX() - pos.getX()), pos.getY(), pos.getZ());
						},
						true, false, false, this.getBoundaryMaterial(), ZMath.PI_BY_2));
			}
		}
		
		// z axis, i.e. north south
		if(this.boundaryEnabled(NORTH)){
			double boundary = this.getBoundary(NORTH);
			if(minZ > boundary){
				collisions.add(new Collision3D(obj,
						(HitBox<V3D> hitbox) -> {
							var pos = hitbox.getPosition();
							return new V3D(pos.getX(), pos.getY(), boundary + Math.abs(hitbox.getMaxPosition().getZ() - pos.getZ()));
						},
						true, false, false, this.getBoundaryMaterial(), 0));
			}
		}
		if(this.boundaryEnabled(SOUTH)){
			double boundary = -this.getBoundary(SOUTH);
			if(maxZ < boundary){
				collisions.add(new Collision3D(obj,
						(HitBox<V3D> hitbox) -> {
							var pos = hitbox.getPosition();
							return new V3D(pos.getX(), pos.getY(), boundary - Math.abs(hitbox.getMinPosition().getZ() - pos.getZ()));
						},
						true, false, false, this.getBoundaryMaterial(), 0));
			}
		}
		
		// y axis, i.e. up down
		if(this.boundaryEnabled(Direction3D.UP)){
			double boundary = this.getBoundary(Direction3D.UP);
			if(minY > boundary){
				collisions.add(new Collision3D(obj,
						(HitBox<V3D> hitbox) -> {
							var pos = hitbox.getPosition();
							return new V3D(pos.getX(), boundary + Math.abs(hitbox.getMaxPosition().getY() - pos.getY()), pos.getZ());
						},
						false, true, false, this.getBoundaryMaterial(), 0));
			}
		}
		if(this.boundaryEnabled(Direction3D.DOWN)){
			double boundary = -this.getBoundary(Direction3D.DOWN);
			if(maxY < boundary){
				collisions.add(new Collision3D(obj,
						(HitBox<V3D> hitbox) -> {
							var pos = hitbox.getPosition();
							return new V3D(pos.getX(), boundary - Math.abs(hitbox.getMinPosition().getY() - pos.getY()), pos.getZ());
						},
						false, false, true, this.getBoundaryMaterial(), 0));
			}
		}
		
		return collisions;
	}
	
	/**
	 * Have the given thing attempt to click on this room. The closest thing to the given clicker will be clicked, or nothing if there is nothing within clicking range
	 *
	 * @param clicker The thing doing the clicking
	 * @return true if something was clicked, false otherwise
	 */
	public boolean attemptClick(ClickerBounds clicker){
		// If no clickable things, do nothing
		var clickables = this.getAllThings().get(ThingClickDetector3D.class);
		if(clickables == null) return false;
		
		// Find the closest clickable
		double closestDistance = -1;
		ThingClickDetector3D closestClickable = null;
		for(var c : clickables){
			double distance = c.findClickDistance(clicker);
			// Skip if the clickable is the given clicker
			if(clicker == c) continue;
			if((closestClickable == null || distance < closestDistance) && c.canClick(clicker.getClickRange(), distance)){
				closestDistance = distance;
				closestClickable = c;
			}
		}
		// If nothing was clicked, do nothing
		if(closestClickable == null) return false;
		
		// If something was clicked, find the closest tile that could be clicked by the clicker, and if it's closer than the clickable, then do nothing
		double tileDistance = findTileClickDistance(clicker);
		if(tileDistance >= 0 && tileDistance < closestDistance) return false;
		
		// Perform the actual click
		closestClickable.handlePress(this);
		return true;
	}
	
	/**
	 * Find the distance from the nearest tile that the given clicker will be able to click on
	 *
	 * @param clicker The clicker to check
	 * @return The distance, or a negative number if nothing can be clicked on
	 */
	public double findTileClickDistance(ClickerBounds clicker){
		// If max distance is zero or negative for some reason, then there is no click
		double maxDistance = clicker.getClickRange();
		if(maxDistance <= 0) return -1;
		
		// Track the current position on the ray
		double rx = clicker.getClickX();
		double ry = clicker.getClickY();
		double rz = clicker.getClickZ();
		
		// Find the current tile
		int tx = Tile3D.tileIndex(rx);
		int ty = Tile3D.tileIndex(ry);
		int tz = Tile3D.tileIndex(rz);
		
		// Find the direction the ray is going
		var clickDirection = new V3D(clicker.getClickYaw(), clicker.getClickPitch(), 1, false);
		double dx = clickDirection.getX();
		double dy = clickDirection.getY();
		double dz = clickDirection.getZ();
		boolean xPositive = dx > 0;
		boolean yPositive = dy > 0;
		boolean zPositive = dz > 0;
		
		// Continue to go through tiles until the max click range is hit
		double distanceTravelled = 0;
		while(distanceTravelled < maxDistance){
			double tileDistance;
			// If all indexes are inside the range of the tiles, then find the intersection distance to the tile
			if(
					ZMath.in(0, tx, this.getTilesX() - 1) &&
					ZMath.in(0, ty, this.getTilesY() - 1) &&
					ZMath.in(0, tz, this.getTilesZ() - 1)
			){
				var t = this.tiles[tx][ty][tz];
				tileDistance = t.getType().getHitbox().clickDistance(t, clicker);
			}
			else tileDistance = -1;
			
			// If the distance to the tile is more than the max distance, then there is no click
			if(tileDistance > maxDistance) return -1;
			
			// If a tile is hit, return that distance
			if(tileDistance >= 0) return tileDistance;
			
			// Otherwise, move onto the next tile
			
			// Find which axis needs to be moved for the next tile
			boolean xTileMove = false;
			boolean yTileMove = false;
			boolean zTileMove = false;
			
			double txMin = tx * Tile3D.size();
			double txMax = (tx + 1) * Tile3D.size();
			double tyMin = ty * Tile3D.size();
			double tyMax = (ty + 1) * Tile3D.size();
			double tzMin = tz * Tile3D.size();
			double tzMax = (tz + 1) * Tile3D.size();
			
			// Find the closest exit point for each axis
			// Check x axis
			var minMax = ZMath.rayIntersectionRectPrismMinMax(rx, dx, txMin, txMax, null);
			double xDist = minMax == null ? -1 : minMax[1];
			
			// Check y axis
			minMax = ZMath.rayIntersectionRectPrismMinMax(ry, dy, tyMin, tyMax, null);
			double yDist = minMax == null ? -1 : minMax[1];
			
			// Check z axis
			minMax = ZMath.rayIntersectionRectPrismMinMax(rz, dz, tzMin, tzMax, null);
			double zDist = minMax == null ? -1 : minMax[1];
			
			// Whichever distance is smallest is the axis to move on, as long as the distance was not negative
			if(xDist >= 0 && xDist < yDist && xDist < zDist) xTileMove = true;
			else if(yDist >= 0 && yDist < zDist) yTileMove = true;
			else if(zDist >= 0) zTileMove = true;
			
			// The smallest distance will be the tile axis that changes
			/*
			 If moving out of bounds for tiles, i.e. no more tiles can ever be clicked, then quit early,
			 i.e. if negative or greater than max, and already moving in that direction, quit early, there is no intersection
			*/
			if(xTileMove){
				if(xPositive){
					tx++;
					if(tx >= this.getTilesX()) return -1;
				}
				else{
					tx--;
					if(tx < 0) return -1;
				}
			}
			else if(yTileMove){
				if(yPositive){
					ty++;
					if(ty >= this.getTilesY()) return -1;
				}
				else{
					ty--;
					if(ty < 0) return -1;
				}
			}
			else if(zTileMove){
				if(zPositive){
					tz++;
					if(tz >= this.getTilesZ()) return -1;
				}
				else{
					tz--;
					if(tz < 0) return -1;
				}
			}
			// Should be impossible, but being safe
			else break;
			
			txMin = tx * Tile3D.size();
			txMax = (tx + 1) * Tile3D.size();
			tyMin = ty * Tile3D.size();
			tyMax = (ty + 1) * Tile3D.size();
			tzMin = tz * Tile3D.size();
			tzMax = (tz + 1) * Tile3D.size();
			
			// Find the new total distance moved from the original ray to the next tile
			distanceTravelled = ZMath.rayDistanceToRectPrism(rx, ry, rz, dx, dy, dz, txMin, tyMin, tzMin, txMax, tyMax, tzMax);
			// Technically it should be impossible for this to return negative, but just in case
			if(distanceTravelled < 0) break;
		}
		
		// If no tile was hit and max distance was reached, then nothing was clicked
		return -1;
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
	 * @param z The z index
	 * @param t The new tile type
	 */
	public void setTile(int x, int y, int z, TileType3D t){
		this.setTile(x, y, z, t, false);
	}
	
	/**
	 * Set the tile at the given indexes
	 *
	 * @param x The x index
	 * @param y The y index
	 * @param z The z index
	 * @param t The new tile type
	 * @param skipRecompute true to skip recomputing the {@link Tile3D#getCollisionFaces()} values, false for default behavior
	 */
	public void setTile(int x, int y, int z, TileType3D t, boolean skipRecompute){
		this.tiles[x][y][z] = new Tile3D(x, y, z, t);
		
		if(!skipRecompute){
			/*
			 There is a lot of room for optimization here, for example only the touching faces of updated tiles need to be recomputed, not the whole tile,
			 but this is fine for now
			 */
			this.computeTileData(x, y, z);
			this.computeTileData(x - 1, y, z);
			this.computeTileData(x + 1, y, z);
			this.computeTileData(x, y - 1, z);
			this.computeTileData(x, y + 1, z);
			this.computeTileData(x, y, z - 1);
			this.computeTileData(x, y, z + 1);
		}
	}
	
	/**
	 * Compute any associated data with the given tile
	 *
	 * @param x The x index
	 * @param y The y index
	 * @param z The z index
	 */
	public void computeTileData(int x, int y, int z){
		if(x < 0 || x >= this.getTilesX() || y < 0 || y >= this.getTilesY() || z < 0 || z >= this.getTilesZ()) return;
		
		var collisionFaces = this.tiles[x][y][z].getCollisionFaces();
		// Collision will be enabled if either the next tile would be out of bounds and the boundary is disabled, or if the adjacent tile is not already collideable
		collisionFaces[EAST.i()] = x - 1 < 0 || !this.tiles[x - 1][y][z].canCollide(EAST);
		collisionFaces[WEST.i()] = (x + 1 >= this.getTilesX()) || !this.tiles[x + 1][y][z].canCollide(WEST);
		
		collisionFaces[DOWN.i()] = (y - 1 < 0) || !this.tiles[x][y - 1][z].canCollide(DOWN);
		collisionFaces[UP.i()] = (y + 1 >= this.getTilesY()) || !this.tiles[x][y + 1][z].canCollide(UP);
		
		collisionFaces[SOUTH.i()] = (z - 1 < 0) || !this.tiles[x][y][z - 1].canCollide(SOUTH);
		collisionFaces[NORTH.i()] = (z + 1 >= this.getTilesZ()) || !this.tiles[x][y][z + 1].canCollide(NORTH);
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
	
	/**
	 * Render the given function, using a tint the given clickable can be clicked by the clicker
	 *
	 * @param r The renderer used
	 * @param render The function to render the object, true if something was rendered, false otherwise
	 * @param clickable The thing which can be clicked on
	 * @param clicker The thing that may be able to click on the clickable
	 * @return The result of render
	 */
	public boolean renderObjectSelectable(Renderer r, Supplier<Boolean> render, ThingClickDetector3D clickable, ClickerBounds clicker){
		double clickDistance = clickable.findClickDistance(clicker);
		double maxClickRange = clicker.getClickRange();
		
		// Check for tiles
		double tileDistance = -1;
		if(clickDistance >= 0) tileDistance = this.findTileClickDistance(clicker);
		
		boolean canClick = clickDistance <= maxClickRange && clickDistance >= 0 && (tileDistance < 0 || tileDistance > clickDistance);
		if(canClick){
			r.pushTextureTintShader();
			r.setColor(new ZColor(0.7));
		}
		boolean success = render.get();
		if(canClick) r.popShader();
		return success;
	}
	
}
