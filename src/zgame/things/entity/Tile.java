package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.things.GameThing;
import zgame.things.PositionedRectangleThing;

/** A {@link GameThing} with a rectangular hitbox and a position based on an index in an array. The indexes of this object should directly correlate to its position */
public class Tile extends PositionedRectangleThing{
	
	/** The default size of tiles */
	public static final double TILE_SIZE = 64;
	
	/** The index of this tile on the x axis */
	private int xIndex;
	/** The index of this tile on the y axis */
	private int yIndex;

	/** The color id of this tile */
	private ZColor color;
	
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
		super(x * size(), y * size(), size(), size());
		this.xIndex = x;
		this.yIndex = y;
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
	public void render(Game game, Renderer r){
		r.setColor(color);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	/** @return The unit size of a tile */
	public static double size(){
		return TILE_SIZE;
	}
	
}
