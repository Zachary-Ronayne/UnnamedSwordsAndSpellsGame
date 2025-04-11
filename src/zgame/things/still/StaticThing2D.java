package zgame.things.still;

import zgame.things.type.bounds.Bounds2D;

/** A 2D thing which does not move as an entity would, and generally doesn't move, but can be at an arbitrary position */
public abstract class StaticThing2D extends StaticThing implements Bounds2D{
	
	/** The upper left hand x coordinate of this thing */
	private double x;
	/** The upper left hand y coordinate of this thing */
	private double y;
	/** The width of this thing */
	private double width;
	/** The height of this thing */
	private double height;
	
	/**
	 * Create a new entity with the given values
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 */
	public StaticThing2D(double x, double y, double w, double h){
		super();
		this.setX(x);
		this.setY(y);
		this.setWidth(w);
		this.setHeight(h);
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
	
	@Override
	public double maxX(){
		return this.getX() + this.getWidth();
	}
	
	@Override
	public double maxY(){
		return this.getY() + this.getHeight();
	}
	
}