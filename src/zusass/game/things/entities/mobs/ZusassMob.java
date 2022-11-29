package zusass.game.things.entities.mobs;

import zgame.things.entity.MobThing;
import zgame.things.type.RectangleHitBox;

/** A generic mob in the Zusass game */
public abstract class ZusassMob extends MobThing implements RectangleHitBox {
	
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
	public ZusassMob(double x, double y, double width, double height){
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
	
	/**
	 * @return This object, as an {@link Npc}, or null if it cannot be an {@link Npc}
	 *         The return value of this method should equal this object, not another version or reference, i.e. (this == this.asNpc()) should evaluate to true
	 */
	public Npc asNpc(){
		return null;
	}
	
	/**
	 * @return This object, as a {@link ZusassPlayer}, or null if it cannot be a {@link ZusassPlayer}
	 *         The return value of this method should equal this object, not another version or reference, i.e. (this == this.asPlayer()) should evaluate to true
	 */
	public ZusassPlayer asPlayer(){
		return null;
	}
	
}
