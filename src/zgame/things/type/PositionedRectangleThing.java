package zgame.things.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** A {@link PositionedThing} which also has a rectangular bounds */
public abstract class PositionedRectangleThing extends PositionedThing implements RectangleBounds{
	
	/** The width of this thing */
	private double width;
	/** The height of this thing */
	private double height;
	
	/**
	 * Create a new {@link PositionedRectangleThing} with no size and the given position
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 */
	public PositionedRectangleThing(double x, double y){
		this(x, y, 0, 0);
	}
	
	/**
	 * Create a new {@link PositionedRectangleThing} with the given values
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public PositionedRectangleThing(double x, double y, double w, double h){
		super(x, y);
		this.width = w;
		this.height = h;
	}
	
	@Override
	public boolean shouldRender(Game game, Renderer r){
		return r.gameBoundsInScreen(this.getBounds());
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
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	/** @param height See {@link #height} */
	public void setHeight(double height){
		this.height = height;
	}
	
	@Override
	public double maxX(){
		return this.getX() + this.getWidth();
	}
	
	@Override
	public double maxY(){
		return this.getY() + this.getHeight();
	}
	
}
