package zgame.things.entity;

import zgame.things.type.bounds.RectangleHitBox;

/** A 2D entity with a rectangle hitbox */
public abstract class EntityThingRect2D extends EntityThing2D implements RectangleHitBox{
	
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
	public EntityThingRect2D(double x, double y, double w, double h, double mass){
		super(x, y, 0);
		this.setWidth(w);
		this.setHeight(h);
	}
	
	/** @return See {@link #width} */
	@Override
	public double getWidth(){
		return this.width;
	}
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	/** @return See {@link #height} */
	@Override
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
