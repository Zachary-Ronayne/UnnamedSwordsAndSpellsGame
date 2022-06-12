package zgame.things;

import java.util.ArrayList;
import java.util.Collection;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZArrayUtils;
import zgame.core.utils.ZLambdaUtils;
import zgame.core.utils.ZMathUtils;
import zgame.things.entity.EntityThing;
import zgame.things.tiles.BaseTiles;
import zgame.things.tiles.Tile;
import zgame.things.tiles.TileType;

/** An object which represents a location in a game, i.e. something that holds the player, NPCs, the tiles, etc. */
public class Room implements RectangleBounds{
	
	/** The index for {@link #wallSolid} that represents the left wall */
	public static final int WALL_LEFT = 0;
	/** The index for {@link #wallSolid} that represents the right wall */
	public static final int WALL_RIGHT = 1;
	/** The index for {@link #wallSolid} that represents the ceiling (top wall) */
	public static final int WALL_CEILING = 2;
	/** The index for {@link #wallSolid} that represents the floor (bottom wall) */
	public static final int WALL_FLOOR = 3;
	
	/** All of the {@link GameThing} objects which exist in in the game */
	private ArrayList<GameThing> things;
	/** All of the {@link EntityThing} objects which exist in in the game */
	private Collection<EntityThing> entities;
	/** All of the {@link HitBox} objects which exist in in the game */
	private Collection<HitBox> hitBoxThings;
	/** All of the {@link GameTickable} objects which exist in in the game */
	private Collection<GameTickable> tickableThings;
	
	/** All of the {@link GameThing} objects which will be removed on the next game tick */
	private Collection<GameThing> thingsToRemove;
	
	// The 2D grid of {@link Tile} objects defining this {@link Room}
	private Tile[][] tiles;
	
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
		this.width = xTiles * Tile.TILE_SIZE;
		this.height = yTiles * Tile.TILE_SIZE;
		this.tiles = new Tile[xTiles][yTiles];
		for(int i = 0; i < xTiles; i++){ for(int j = 0; j < yTiles; j++){ this.tiles[i][j] = new Tile(i, j, t); } }
	}
	
	/** @return See {@link #things}. This is the actual collection holding the things, not a copy */
	public Collection<GameThing> getThings(){
		return this.things;
	}
	
	/** @return See {@link #entities}. This is the actual collection holding the things, not a copy */
	public Collection<EntityThing> getEntities(){
		return this.entities;
	}
	
	/** @return See {@link #tickableThings}. This is the actual collection holding the things, not a copy */
	public Collection<GameTickable> getTickableThings(){
		return this.tickableThings;
	}
	
	/** @return See {@link #hitBoxThings}. This is the actual collection holding the things, not a copy */
	public Collection<HitBox> getHitBoxThings(){
		return this.hitBoxThings;
	}
	
	/** @return See {@link #tiles} */
	public Tile[][] getTiles(){
		return this.tiles;
	}
	
	/**
	 * Iterate over a subsection of {@link #tiles} which intersect the given bounds
	 * 
	 * @param x The x coordinate of the upper left hand corner of the bounds
	 * @param y The y coordinate of the upper left hand corner of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @param func The function to run for each tile. The parameters are the x and y indexes of {@link Tile} of that iteration
	 */
	public void applyToTileBounds(double x, double y, double w, double h, ZLambdaUtils.TwoInt func){
		int x1 = (int)ZMathUtils.minMax(0, this.tiles.length, Math.floor(x * Tile.inverseSize()));
		int y1 = (int)ZMathUtils.minMax(0, this.tiles[0].length, Math.floor(y * Tile.inverseSize()));
		int x2 = (int)ZMathUtils.minMax(0, this.tiles.length, Math.ceil((x + w) * Tile.inverseSize()));
		int y2 = (int)ZMathUtils.minMax(0, this.tiles[0].length, Math.ceil((y + h) * Tile.inverseSize()));
		for(int lx = x1; lx < x2; lx++) for(int ly = y1; ly < y2; ly++) func.run(lx, ly);
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
	 * Update this {@link Room}
	 * 
	 * @param game The {@link Game} which this {@link Room} should update relative to
	 * @param dt The amount of time passed in this update
	 */
	public void tick(Game game, double dt){
		// Update all updatable objects
		for(GameTickable t : this.tickableThings) t.tick(game, dt);
		
		// Collide things with hitboxes against tiles
		for(EntityThing obj : this.entities){
			// Find touching tiles and collide with them
			this.applyToTileBounds(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), (x, y) -> {
				// Collide the entity with the tile
				obj.collide(tiles[x][y].collideRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), obj.getPX(), obj.getPY()));
			});
		}
		// Keep all objects inside the game bounds, if the walls are enabled
		for(HitBox hb : this.hitBoxThings){
			if(this.isSolid(WALL_LEFT) && hb.keepRight(this.leftEdge())) hb.touchWall();
			if(this.isSolid(WALL_RIGHT) && hb.keepLeft(this.rightEdge())) hb.touchWall();
			if(this.isSolid(WALL_CEILING) && hb.keepBelow(this.topEdge())) hb.touchCeiling();
			if(this.isSolid(WALL_FLOOR) && hb.keepAbove(this.bottomEdge())) hb.touchFloor();
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
		int xTiles = this.tiles.length;
		int yTiles = this.tiles[0].length;
		int startX = Math.max(0, (int)Math.floor(game.getScreenLeft() / Tile.size()));
		int endX = Math.min(xTiles, (int)Math.ceil(game.getScreenRight() / Tile.size()));
		int startY = Math.max(0, (int)Math.floor(game.getScreenTop() / Tile.size()));
		int endY = Math.min(yTiles, (int)Math.ceil(game.getScreenBottom() / Tile.size()));
		// Draw all the tiles
		for(int i = startX; i < endX; i++) for(int j = startY; j < endY; j++) this.tiles[i][j].render(game, r);
		
		// Draw all the things
		for(GameThing t : this.things) t.render(game, r);
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
	
	@Override
	public double leftEdge(){
		return 0;
	}
	
	@Override
	public double rightEdge(){
		return this.getWidth();
	}
	
	@Override
	public double topEdge(){
		return 0;
	}
	
	@Override
	public double bottomEdge(){
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
	
}
