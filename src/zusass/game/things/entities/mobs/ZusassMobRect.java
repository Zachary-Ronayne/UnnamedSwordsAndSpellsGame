package zusass.game.things.entities.mobs;

import zgame.things.entity.MobThing;
import zgame.things.type.RectangleHitBox;

/** A generic mob in the Zusass game */
public abstract class ZusassMobRect extends MobThing implements RectangleHitBox {
	
	/** The width of this mob */
	private double width;
	/** The height of this mob */
	private double height;
	
	/**
	 * Create a new mob with the given bounds
	 * 
	 * @param x The upper left hand x coordinate
	 * @param y The upper left hand y coordinate
	 * @param width The mob's width
	 * @param height The mob's height
	 */
	public ZusassMobRect(double x, double y, double width, double height){
		super(x, y);
		this.width = width;
		this.height = height;
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
	
}
