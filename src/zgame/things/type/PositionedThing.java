package zgame.things.type;

import zgame.core.Game;
import zgame.things.entity.EntityThing;
import zgame.world.Room;

/** A {@link GameThing} which uses x and y coordinates */
public abstract class PositionedThing extends GameThing implements Position{
	
	/** The x coordinate of this {@link PositionedThing}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link EntityThing} */
	private double x;
	/** The y coordinate of this {@link PositionedThing}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link EntityThing} */
	private double y;
	/**
	 * Create a new {@link PositionedThing} at (0, 0)
	 */
	public PositionedThing(){
		this(0, 0);
	}
	
	/**
	 * Create a new {@link PositionedThing}
	 * 
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 */
	public PositionedThing(double x, double y){
		super();
		this.x = x;
		this.y = y;
	}
	
	@Override
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/**
	 * Add the given value to {@link #x}
	 * 
	 * @param x The value to add
	 */
	public void addX(double x){
		this.setX(this.getX() + x);
	}
	
	@Override
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}
	
	/**
	 * Add the given value to {@link #y}
	 * 
	 * @param y The value to add
	 */
	public void addY(double y){
		this.setY(this.getY() + y);
	}
	
	/**
	 * Take this {@link PositionedThing} from the given room, and place it in the other given room
	 * 
	 * @param from The room to move the thing from, i.e. the thing was in this room. Can be null if the thing didn't come from a room
	 * @param to The room to move the thing to, i.e. the thing is now in this room. Can be null if the thing isn't going to a room
	 * @param game The {@link Game} where this thing entered the room
	 */
	public void enterRoom(Room from, Room to, Game game){
		if(from != null) from.removeThing(this);
		if(to != null) to.addThing(this);
	}
	
	/**
	 * Center the camera of the given {@link Game} to the center of this object
	 * 
	 * @param game The game
	 */
	public void centerCamera(Game game){
		game.centerCamera(this.centerX(), this.centerY());
	}
	
}
