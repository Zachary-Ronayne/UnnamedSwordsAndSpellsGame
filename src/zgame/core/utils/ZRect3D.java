package zgame.core.utils;

/** An object representing a 3D rectangular prism with no rotations */
public class ZRect3D extends ZPoint3D{
	
	/** The width, x axis, of this rect */
	private double width;
	/** The height, y axis, of this rect */
	private double height;
	/** The length, z axis, of this rect */
	private double length;
	
	/** Create a new blank rect with no bounds positioned at (0, 0, 0) */
	public ZRect3D(){
		this(0, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Create a new rect at the given position
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 * @param length See {@link #length}
	 */
	public ZRect3D(double x, double y, double z, double width, double height, double length){
		super(x, y, z);
		this.width = width;
		this.height = height;
		this.length = length;
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
	
	/** @return See {@link #length} */
	public double getLength(){
		return this.length;
	}
	
	/** @param length See {@link #length} */
	public void setLength(double length){
		this.length = length;
	}
}