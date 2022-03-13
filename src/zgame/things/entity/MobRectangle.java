package zgame.things.entity;

import zgame.things.RectangleHitBox;

import java.awt.geom.Rectangle2D;

/** A {@link MobThing} which has a rectangular hit box */
public abstract class MobRectangle extends MobThing implements RectangleHitBox{
	
	/** The width of the mob */
	public double width;
	/** The height of the mob */
	public double height;
	
	/**
	 * Create a new {@link MobRectangle} at the given position
	 * 
	 * @param x The x coordinate of the mob
	 * @param y The y coordinate of the mob
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 */
	public MobRectangle(double x, double y, double width, double height){
		super(x, y);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public boolean keepLeft(double x){
		if(this.getX() + this.getWidth() <= x) return false;
		this.setX(x - this.getWidth());
		return true;
	}
	
	@Override
	public boolean keepRight(double x){
		if(this.getX() >= x) return false;
		this.setX(x);
		return true;
	}
	
	@Override
	public boolean keepAbove(double y){
		if(this.getY() + this.getHeight() <= y) return false;
		this.setY(y - this.getHeight());
		return true;
	}
	
	@Override
	public boolean keepBelow(double y){
		if(this.getY() >= y) return false;
		this.setY(y);
		return true;
	}
	
	/** @return The center x coordinate of this {@link MobRectangle} */
	public double getCenterX(){
		return this.getX() + this.getWidth() * 0.5;
	}
	
	/** @return The center y coordinate of this {@link MobRectangle} */
	public double getCenterY(){
		return this.getY() + this.getHeight() * 0.5;
	}
	
	@Override
	/** @return See {@link #width} */
	public double getWidth(){
		return this.width;
	}
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	@Override
	/** @return See {@link #height} */
	public double getHeight(){
		return this.height;
	}
	
	/** @param height See {@link #height} */
	public void setHeight(double height){
		this.height = height;
	}
	
	@Override
	public boolean intersects(double x, double y, double w, double h){
		return new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight()).intersects(x, y, w, h);
	}
	
}
