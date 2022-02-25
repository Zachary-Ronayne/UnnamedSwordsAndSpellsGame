package zgame.things;

import java.util.ArrayList;
import java.util.Collection;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;

/** An object which represents a location in a game, i.e. something that holds the player, NPCs, the tiles, etc. */
public class Room implements GameTickable{
	
	/** The index for {@link #wallSolid} that represents the left wall */
	public static final int WALL_LEFT = 0;
	/** The index for {@link #wallSolid} that represents the right wall */
	public static final int WALL_RIGHT = 1;
	/** The index for {@link #wallSolid} that represents the ceiling (top wall) */
	public static final int WALL_CEILING = 2;
	/** The index for {@link #wallSolid} that represents the floor (bottom wall) */
	public static final int WALL_FLOOR = 3;
	
	/** All of the {@link GameThing} objects which exist in in the game */
	private Collection<GameThing> things;
	/** All of the {@link HitBox} objects which exist in in the game */
	private Collection<HitBox> hitBoxThings;
	/** All of the {@link GameTickable} objects which exist in in the game */
	private Collection<GameTickable> tickableThings;
	
	/** The upper left hand x coordinate of the room. Defaults to 0 */
	private double x;
	/** The upper left hand y coordinate of the room. Defaults to 0 */
	private double y;
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
	 * @param width See {@link #width}
	 * @param width See {@link #height}
	 */
	public Room(double width, double height){
		things = new ArrayList<GameThing>();
		hitBoxThings = new ArrayList<HitBox>();
		tickableThings = new ArrayList<GameTickable>();
		
		this.x = 0;
		this.y = 0;
		this.width = width;
		this.height = height;
		
		this.wallSolid = new boolean[]{true, true, true, true};
	}
	
	/**
	 * Add a {@link GameThing} to this {@link Room}
	 * 
	 * @param thing The {@link GameThing} to add
	 */
	public void addThing(GameThing thing){
		this.things.add(thing);
		if(thing instanceof GameTickable) this.tickableThings.add((GameTickable)thing);
		if(thing instanceof HitBox) this.hitBoxThings.add((HitBox)thing);
	}
	
	/**
	 * Remove a {@link GameTickable} from this {@link Room}
	 * 
	 * @param thing The {@link GameTickable} to remove
	 */
	public void removeThing(GameThing thing){
		this.things.remove(thing);
		if(thing instanceof GameTickable) this.tickableThings.remove((GameTickable)thing);
		if(thing instanceof HitBox) this.hitBoxThings.remove((HitBox)thing);
	}
	
	@Override
	/**
	 * Update this {@link Room}
	 * 
	 * @param game The {@link Game} which this {@link Room} should update relative to
	 * @param dt The amount of time passed in this update
	 */
	public void tick(Game game, double dt){
		// Update all updatable objects
		for(GameTickable t : this.tickableThings) t.tick(game, dt);
		
		// Keep all objects inside the game bounds, if the walls are enabled
		for(HitBox hb : this.hitBoxThings){
			if(this.isSolid(WALL_LEFT)){
				if(hb.keepRight(this.getX())){
					hb.touchWall();
				}
			}
			if(this.isSolid(WALL_RIGHT)){
				if(hb.keepLeft(this.getX() + this.getWidth())){
					hb.touchWall();
				}
			}
			if(this.isSolid(WALL_CEILING)){
				if(hb.keepBelow(this.getY())){
					hb.touchCeiling();
				}
			}
			if(this.isSolid(WALL_FLOOR)){
				if(hb.keepAbove(this.getY() + this.getHeight())){
					hb.touchFloor();
				}
			}
		}
	}
	
	/**
	 * Draw this {@link Room} to the given {@link Renderer}
	 * 
	 * @param game The {@link Game} to draw this {@link Room} relative to
	 * @param r The {@link Renderer} to draw this {@link Room} on
	 */
	public void render(Game game, Renderer r){
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

	
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}

	/** @return See {@link #width} */
	public double getWidth(){
		return this.width;
	}
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	/** @return See {@link #height} */
	public double getHeight(){
		return this.height;
	}
	
	/** @param height See {@link #height} */
	public void setHeight(double height){
		this.height = height;
	}
}
