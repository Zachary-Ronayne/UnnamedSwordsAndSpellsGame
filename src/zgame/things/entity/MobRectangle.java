package zgame.things.entity;

/** A {@link Mob} which has a rectangular hit box */
public abstract class MobRectangle extends Mob implements RectangleHitBox{
	
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
	
}
