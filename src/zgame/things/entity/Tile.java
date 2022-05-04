package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.things.GameThing;
import zgame.things.Positionable;
import zgame.things.RectangleBounds;

/** A {@link GameThing} with a rectangular hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public class Tile extends GameThing implements Positionable, RectangleBounds{
	
	// TODO make this a setting
	/** The default size of tiles */
	public static final double TILE_SIZE = 64;
	
	/** The index of this tile on the x axis */
	private int xIndex;
	/** The index of this tile on the y axis */
	private int yIndex;
	/** The actual position of this tile on the x axis */
	private double x;
	/** The actual position of this tile on the y axis */
	private double y;

	/** The color id of this tile */
	private ZColor color;

	// TODO add methods for rectangles for getting each corner, edge center, and overall center
	
	/**
	 * Make a new tile at the given index of the default color
	 * 
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 */
	public Tile(int x, int y){
		this(0, 0, new ZColor(1));
	}

	/**
	 * Make a new tile at the given index and of the given color
	 * 
	 * @param x See {@link #xIndex}
	 * @param y See {@link #yIndex}
	 * @param color See {@link #color}
	 */
	public Tile(int x, int y, ZColor color){
		this.xIndex = x;
		this.yIndex = y;
		this.x = this.xIndex * TILE_SIZE;
		this.y = this.yIndex * TILE_SIZE;
		this.color = color;
	}
	
	/** @return See {@link #xIndex} */
	public int getXIndex(){
		return this.xIndex;
	}

	/** @return See {@link #yIndex} */
	public int getYIndex(){
		return this.yIndex;
	}
	
	@Override
	public double getX(){
		return this.x;
	}
	
	@Override
	public double getY(){
		return this.y;
	}
	
	@Override
	public double getWidth(){
		return TILE_SIZE;
	}
	
	@Override
	public double getHeight(){
		return TILE_SIZE;
	}
	
	@Override
	public void render(Game game, Renderer r){
		r.setColor(color);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
}
