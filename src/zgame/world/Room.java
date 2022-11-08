package zgame.world;

import java.util.ArrayList;
import java.util.List;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.file.Saveable;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZArrayUtils;
import zgame.core.utils.ZMath;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.entity.EntityThing;
import zgame.things.still.tiles.BaseTiles;
import zgame.things.still.tiles.Tile;
import zgame.things.still.tiles.TileType;
import zgame.things.type.GameThing;
import zgame.things.type.HitBox;
import zgame.things.type.RectangleBounds;

/** An object which represents a location in a game, i.e. something that holds the player, NPCs, the tiles, etc. */
public class Room implements RectangleBounds, Saveable, Destroyable{
	
	/** The index for {@link #wallSolid} that represents the left wall */
	public static final int WALL_LEFT = 0;
	/** The index for {@link #wallSolid} that represents the right wall */
	public static final int WALL_RIGHT = 1;
	/** The index for {@link #wallSolid} that represents the ceiling (top wall) */
	public static final int WALL_CEILING = 2;
	/** The index for {@link #wallSolid} that represents the floor (bottom wall) */
	public static final int WALL_FLOOR = 3;
	
	/** All of the {@link GameThing} objects which exist in in the game */
	private List<GameThing> things;
	/** All of the {@link EntityThing} objects which exist in in the game */
	private List<EntityThing> entities;
	/** All of the {@link HitBox} objects which exist in in the game */
	private List<HitBox> hitBoxThings;
	/** All of the {@link GameTickable} objects which exist in in the game */
	private List<GameTickable> tickableThings;
	
	/** All of the {@link GameThing} objects which will be removed on the next game tick */
	private List<GameThing> thingsToRemove;
	
	// The 2D grid of {@link Tile} objects defining this {@link Room}
	private ArrayList<ArrayList<Tile>> tiles;
	
	// The number of tiles wide this room is, i.e. the number of tiles on the x axis
	private int xTiles;
	
	// The number of tiles high this room is, i.e. the number of tiles on the y axis
	private int yTiles;
	
	/** The width of the room. */
	private double width;
	/** The height of the room */
	private double height;
	
	/**
	 * A 4 element array which determines which of the walls are solid. If a wall is solid, then any entity outside of that wall will be forced back inside the wall
	 * Use {@link #WALL_LEFT}, {@link #WALL_RIGHT}, {@value #WALL_CEILING}, and {@value #WALL_FLOOR} for specifying walls
	 */
	private boolean[] wallSolid;
	
	/** Create a new empty {@link Room} */
	public Room(){
		this(1000, 500);
	}
	
	/**
	 * Create a new empty {@link Room}
	 * 
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 */
	public Room(int xTiles, int yTiles){
		this.things = new ArrayList<GameThing>();
		this.entities = new ArrayList<EntityThing>();
		this.hitBoxThings = new ArrayList<HitBox>();
		this.tickableThings = new ArrayList<GameTickable>();
		
		this.thingsToRemove = new ArrayList<GameThing>();
		
		this.initTiles(xTiles, yTiles);
		
		this.wallSolid = new boolean[]{true, true, true, true};
	}

	@Override
	public void destroy(){
		for(int i = 0; i < this.things.size(); i++) this.things.get(i).destroy();
	}
	
	/**
	 * Initialize {@link #tiles} to the given size where every tile is the default tile
	 * 
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 */
	public void initTiles(int xTiles, int yTiles){
		this.initTiles(xTiles, yTiles, BaseTiles.AIR);
	}
	
	/**
	 * Initialize {@link #tiles} to the given size
	 * 
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 * @param c The color for every tile
	 */
	public void initTiles(int xTiles, int yTiles, TileType t){
		this.xTiles = xTiles;
		this.yTiles = yTiles;
		this.width = xTiles * Tile.TILE_SIZE;
		this.height = yTiles * Tile.TILE_SIZE;
		this.tiles = new ArrayList<ArrayList<Tile>>(xTiles);
		for(int i = 0; i < xTiles; i++){
			ArrayList<Tile> col = new ArrayList<Tile>(yTiles);
			this.tiles.add(col);
			for(int j = 0; j < yTiles; j++){ col.add(new Tile(i, j, t)); }
		}
	}
	
	/** @return See {@link #things}. This is the actual collection holding the things, not a copy */
	public List<GameThing> getThings(){
		return this.things;
	}
	
	/** @return See {@link #entities}. This is the actual collection holding the things, not a copy */
	public List<EntityThing> getEntities(){
		return this.entities;
	}
	
	/** @return See {@link #tickableThings}. This is the actual collection holding the things, not a copy */
	public List<GameTickable> getTickableThings(){
		return this.tickableThings;
	}
	
	/** @return See {@link #hitBoxThings}. This is the actual collection holding the things, not a copy */
	public List<HitBox> getHitBoxThings(){
		return this.hitBoxThings;
	}
	
	/**
	 * Add a {@link GameThing} to this {@link Room}
	 * 
	 * @param thing The {@link GameThing} to add
	 */
	public void addThing(GameThing thing){
		ZArrayUtils.insertSorted(this.things, thing);
		if(thing instanceof EntityThing) this.entities.add((EntityThing)thing);
		if(thing instanceof GameTickable) this.tickableThings.add((GameTickable)thing);
		if(thing instanceof HitBox) this.hitBoxThings.add((HitBox)thing);
	}
	
	/**
	 * Remove a {@link GameTickable} from this {@link Room}
	 * 
	 * @param thing The {@link GameTickable} to remove
	 */
	public void removeThing(GameThing thing){
		this.thingsToRemove.add(thing);
	}
	
	/**
	 * Collide the given {@link EntityThing} with this room. Essentially, attempt to move the given object so that it no longer intersects with anything in this room.
	 * 
	 * @param obj The object to collide
	 * 
	 * @return The CollisionResponse representing the final collision that took place, where the collision material is the floor collision, if one took place
	 */
	public CollisionResponse collide(HitBox obj){
		// Find touching tiles and collide with them
		int minX = this.tileX(obj.getX());
		int minY = this.tileY(obj.getY());
		int maxX = this.tileX(obj.maxX());
		int maxY = this.tileY(obj.maxY());
		
		boolean wasOnGround = obj.isOnGround();
		boolean wasOnCeiling = obj.isOnCeiling();
		boolean wasOnWall = obj.isOnWall();
		double mx = 0;
		double my = 0;
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bot = false;
		Material material = null;
		
		for(int x = minX; x <= maxX; x++){
			for(int y = minY; y <= maxY; y++){
				Tile t = this.tiles.get(x).get(y);
				CollisionResponse res = t.collide(obj);
				// Keep track of if a tile was touched
				boolean currentCollided = res.x() != 0 || res.y() != 0;
				
				mx += res.x();
				my += res.y();
				if(res.left()) left = true;
				if(res.right()) right = true;
				if(res.ceiling()) top = true;
				if(res.floor()) bot = true;
				// TODO try making it do only one final collision operation at the end
				obj.collide(res);
				
				// Record the material collided with, only if this tile was collided with
				if(currentCollided){
					// Set the material if there is none yet, or the floor
					if(material == null || bot) material = res.material();
				}
			}
		}
		// Determine the final collision
		CollisionResponse res = new CollisionResponse(mx, my, left, right, top, bot, material);
		
		// Keep the object inside the game bounds, if the walls are enabled
		if(this.isSolid(WALL_LEFT) && obj.keepRight(this.getX())){
			left = true;
			obj.touchWall(Materials.BOUNDARY);
		}
		if(this.isSolid(WALL_RIGHT) && obj.keepLeft(this.maxX())){
			right = true;
			obj.touchWall(Materials.BOUNDARY);
		}
		if(this.isSolid(WALL_CEILING) && obj.keepBelow(this.getY())){
			top = true;
			obj.touchCeiling(Materials.BOUNDARY);
		}
		if(this.isSolid(WALL_FLOOR) && obj.keepAbove(this.maxY())){
			bot = true;
			obj.touchFloor(Materials.BOUNDARY);
		}
		// If no tiles were touched on the ground and the object was on the ground, the entity has left the floor
		if(!bot && wasOnGround) obj.leaveFloor();
		// Same thing, but for the walls and for the ceiling
		if(!top && wasOnCeiling) obj.leaveCeiling();
		if(!left && !right && wasOnWall) obj.leaveWall();
		
		return res;
	}
	
	/**
	 * Update this {@link Room}
	 * 
	 * @param game The {@link Game} which this {@link Room} should update relative to
	 * @param dt The amount of time passed in this update
	 */
	public void tick(Game game, double dt){
		// Update all updatable objects
		for(int i = 0; i < this.tickableThings.size(); i++){
			GameTickable t = this.tickableThings.get(i);
			t.tick(game, dt);
		}
		
		// Remove all things that need to be removed
		for(GameThing thing : this.thingsToRemove){
			this.things.remove(thing);
			if(thing instanceof EntityThing) this.entities.remove((EntityThing)thing);
			if(thing instanceof GameTickable) this.tickableThings.remove((GameTickable)thing);
			if(thing instanceof HitBox) this.hitBoxThings.remove((HitBox)thing);
		}
		this.thingsToRemove.clear();
	}
	
	/**
	 * Draw this {@link Room} to the given {@link Renderer}
	 * 
	 * @param game The {@link Game} to draw this {@link Room} relative to
	 * @param r The {@link Renderer} to draw this {@link Room} on
	 */
	public void render(Game game, Renderer r){
		// Determine the indexes of the tiles that need to be rendered
		int startX = Math.max(0, (int)Math.floor(game.getScreenLeft() / Tile.size()));
		int endX = Math.min(this.getXTiles(), (int)Math.ceil(game.getScreenRight() / Tile.size()));
		int startY = Math.max(0, (int)Math.floor(game.getScreenTop() / Tile.size()));
		int endY = Math.min(this.getYTiles(), (int)Math.ceil(game.getScreenBottom() / Tile.size()));
		// Draw all the tiles
		for(int i = startX; i < endX; i++) for(int j = startY; j < endY; j++) this.tiles.get(i).get(j).renderWithCheck(game, r);
		
		// Draw all the things
		for(int i = 0; i < this.things.size(); i++) this.things.get(i).renderWithCheck(game, r);
	}
	
	/** Cause every wall to be solid. See {@link #wallSolid} for details */
	public void makeWallsSolid(){
		for(int i = 0; i < this.wallSolid.length; i++) this.makeWallSolid(i);
	}
	
	/** Cause every wall to be not solid. See {@link #wallSolid} for details */
	public void makeWallsNotSolid(){
		for(int i = 0; i < this.wallSolid.length; i++) this.makeWallNotSolid(i);
	}
	
	/**
	 * Determine if the given wall id is a valid wall id
	 * 
	 * @param wall The wall id
	 * @return true if the id is valid, false otherwise
	 */
	public boolean isValidWall(int wall){
		return wall >= 0 && wall <= 3;
	}
	
	/**
	 * Make the specified wall either solid or not solid
	 * 
	 * @param wall The wall to make solid. See {@link #wallSolid} for details.
	 * @param solid true to make the wall solid, false to make it not solid
	 * @return If wall is invalid, this method does nothing and returns false, otherwise it returns true
	 */
	public boolean makeWallState(int wall, boolean solid){
		if(!this.isValidWall(wall)) return false;
		this.wallSolid[wall] = solid;
		return true;
	}
	
	/**
	 * Make the specified wall solid
	 * 
	 * @param wall The wall to make solid. See {@link #wallSolid} for details.
	 * @return If wall is invalid, this method does nothing and returns false, otherwise it returns true
	 */
	public boolean makeWallSolid(int wall){
		return this.makeWallState(wall, true);
	}
	
	/**
	 * Make the specified wall not solid
	 * 
	 * @param wall The wall to make not solid. See {@link #wallSolid} for details.
	 * @return If wall is invalid, this method does nothing and returns false, otherwise it returns true
	 */
	public boolean makeWallNotSolid(int wall){
		return this.makeWallState(wall, false);
	}
	
	/**
	 * Determine if the given wall is solid
	 * 
	 * @param wall The wall. See {@link #wallSolid} for details.
	 * @return true if the given wall is valid and solid, false otherwise
	 */
	public boolean isSolid(int wall){
		if(!this.isValidWall(wall)) return false;
		return this.wallSolid[wall];
	}
	
	/** @return See {@link #width} */
	@Override
	public double getWidth(){
		return this.width;
	}
	
	/** @return See {@link #height} */
	@Override
	public double getHeight(){
		return this.height;
	}
	
	/** @return See {@link #xTiles} */
	public int getXTiles(){
		return this.xTiles;
	}
	
	/** @return See {@link #yTiles} */
	public int getYTiles(){
		return this.yTiles;
	}
	
	@Override
	public double getX(){
		return 0;
	}
	
	@Override
	public double maxX(){
		return this.getWidth();
	}
	
	@Override
	public double getY(){
		return 0;
	}
	
	@Override
	public double maxY(){
		return this.getHeight();
	}
	
	@Override
	public double centerX(){
		return this.getWidth() * 0.5;
	}
	
	@Override
	public double centerY(){
		return this.getHeight() * 0.5;
	}

	/**
	 * Get the tile at the specified index
	 * @param x The tile index on the x axis
	 * @param y The tile on the y axis
	 * @return The tile, or null if the tile is outside of the range of the grid
	 */
	public Tile getTile(int x, int y){
		if(!ZMath.in(0, x, this.tiles.size() - 1) || !ZMath.in(0, y, this.tiles.get(x).size() - 1)) return null;
		return this.tiles.get(x).get(y);
	}
	
	/**
	 * Set the tile at the specified index
	 * 
	 * @param x The x index
	 * @param y The y index
	 * @param t The type of tile to set
	 * @return true if the tile was set, false if it was not i.e. the indexes were outside the grid
	 */
	public boolean setTile(int x, int y, TileType t){
		if(!this.inTiles(x, y)) return false;
		this.tiles.get(x).set(y, new Tile(x, y, t));
		return true;
	}
	
	/**
	 * Find the index in the x axis of {@link #tiles} which contains the given coordinate
	 * 
	 * @param x The coordinate
	 * @return The index, or if the coordinate is outside the bounds, then the index of the closest tile to the side the coordinate is out of bounds on
	 */
	public int tileX(double x){
		return (int)ZMath.minMax(0, this.getXTiles() - 1, Math.floor(x * Tile.inverseSize()));
	}
	
	/**
	 * Find the index in the y axis of {@link #tiles} which contains the given coordinate
	 * 
	 * @param y The coordinate
	 * @return The index, or if the coordinate is outside the bounds, then the index of the closest tile to the side the coordinate is out of bounds on
	 */
	public int tileY(double y){
		return (int)ZMath.minMax(0, this.getYTiles() - 1, Math.floor(y * Tile.inverseSize()));
	}
	
	/**
	 * Determine if the given index is within the tile bounds
	 * 
	 * @param x The index to check
	 * @return true if the index is in the x axis range of the tile grid
	 */
	public boolean inTilesX(int x){
		return x >= 0 && x < this.getXTiles();
	}
	
	/**
	 * Determine if the given index is within the tile bounds
	 * 
	 * @param y The index to check
	 * @return true if the index is in the y axis range of the tile grid
	 */
	public boolean inTilesY(int y){
		return y >= 0 && y < this.getYTiles();
	}
	
	/**
	 * Determine if the given indexes are within the tile bounds
	 * 
	 * @param x The x index to check
	 * @param y The y index to check
	 * @return true if the index is in the y axis range of the tile grid
	 */
	public boolean inTiles(int x, int y){
		return this.inTilesX(x) && this.inTilesY(y);
	}
	
}
