package zgame.things;

/** A {@link PositionedThing} which also has a rectangular hitbox */
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
	public double leftEdge(){
		return this.getX();
	}
	
	@Override
	public double rightEdge(){
		return this.getX() + this.getWidth();
	}
	
	@Override
	public double topEdge(){
		return this.getY();
	}
	
	@Override
	public double bottomEdge(){
		return this.getY() + this.getHeight();
	}
	
	@Override
	public double centerX(){
		return this.getX() + this.getWidth() * 0.5;
	}
	
	@Override
	public double centerY(){
		return this.getY() + this.getHeight() * 0.5;
	}
}
