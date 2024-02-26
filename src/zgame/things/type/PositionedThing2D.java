package zgame.things.type;

import zgame.core.Game;
import zgame.things.entity.Entity;

/** A {@link GameThing} which uses x and y coordinates */
public abstract class PositionedThing2D extends PositionedThing implements Position2D{
	
	/** The x coordinate of this {@link PositionedThing}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link Entity} */
	private double x;
	/** The y coordinate of this {@link PositionedThing}. Do not use this value to simulate movement via physics, for that, use velocity with an {@link Entity} */
	private double y;
	
	/** Create a new {@link PositionedThing} at (0, 0) */
	public PositionedThing2D(){
		this(0, 0);
	}
	
	/**
	 * Create a new {@link PositionedThing2D}
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 */
	public PositionedThing2D(double x, double y){
		super();
		this.setX(x);
		this.setY(y);
	}
	
	/** @return See {@link #x} */
	@Override
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
	
	/** @return See {@link #y} */
	@Override
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
	 * Center the camera of the given {@link Game} to the center of this object
	 *
	 * @param game The game
	 */
	public void centerCamera(Game game){
		game.centerCamera(this.centerX(), this.centerY());
	}
	
}
