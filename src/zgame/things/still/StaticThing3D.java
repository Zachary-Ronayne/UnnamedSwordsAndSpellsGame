package zgame.things.still;

import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.Clickable3D;

/** A 3D thing which does not move as an entity would, and generally doesn't move, but can be at an arbitrary position */
public abstract class StaticThing3D extends StaticThing implements Bounds3D, Clickable3D{
	
	/** The bottom middle x coordinate of this thing */
	private double x;
	/** The bottom middle y coordinate of this thing */
	private double y;
	/** The bottom middle z coordinate of this thing */
	private double z;
	/** The width of this thing */
	private double width;
	/** The height of this thing */
	private double height;
	/** The length of this thing */
	private double length;
	
	/**
	 * Create a new entity with the given values
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 * @param l See {@link #length}
	 */
	public StaticThing3D(double x, double y, double z, double w, double h, double l){
		super();
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setWidth(w);
		this.setHeight(h);
		this.setLength(l);
	}
	
	/** @return See {@link #x} */
	@Override
	public double getX(){
		return this.x;
	}
	
	public double getPX(){
		return this.getX();
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/** @return See {@link #y} */
	@Override
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}
	
	/** @return See {@link #z} */
	@Override
	public double getZ(){
		return this.z;
	}
	
	/** @param z See {@link #z} */
	public void setZ(double z){
		this.z = z;
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
	
	/** @return See {@link #length} */
	@Override
	public double getLength(){
		return this.length;
	}
	
	/** @param length See {@link #length} */
	public void setLength(double length){
		this.length = length;
	}
	
}