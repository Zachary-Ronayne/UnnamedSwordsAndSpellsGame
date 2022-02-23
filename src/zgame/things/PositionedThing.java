package zgame.things;

/** A {@link GameThing} which uses x and y coordinates */
public abstract class PositionedThing extends GameThing implements Positionable{
	
	/** The x coordinate of this {@link PositionedThing} */
	private double x;
	/** The y coordinate of this {@link PositionedThing} */
	private double y;

	/**
	 * Create a new {@link PositionedThing} at (0, 0)
	 */
	public PositionedThing(){
		this(0, 0);
	}

	/**
	 * Create a new {@link PositionedThing}
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 */
	public PositionedThing(double x, double y){
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}

	/**
	 * Add the given value to {@link #x}
	 * @param x The value to add
	 */
	public void addX(double x){
		this.setX(this.getX() + x);
	}
	
	@Override
	/** @return See {@link #y} */
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

}
