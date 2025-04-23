package zgame.core.graphics;

import zgame.core.utils.ZRect3D;

/** An object holding data for drawing a 3D rectangular prism */
public class RectRender3D extends ZRect3D{
	
	/** The rotations to apply to this rendered object */
	private RotRender3D rot;
	
	/**
	 * Create a {@link RectRender3D} with the given values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 * @param l See {@link #length}
	 * @param rot See {@link #rot}
	 */
	public RectRender3D(double x, double y, double z, double w, double h, double l, RotRender3D rot){
		super(x, y, z, w, h, l);
		this.rot = rot;
	}
	
	/**
	 * Create a {@link RectRender3D} with no rotation
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 * @param l See {@link #length}
	 */
	public RectRender3D(double x, double y, double z, double w, double h, double l){
		this(x, y, z, w, h, l, new RotRender3D());
	}
	
	/**
	 * Create a {@link RectRender3D} with rotation on the y axis, centered at the bottom middle
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 * @param l See {@link #length}
	 * @param rotY The rotation for the y axis
	 */
	public RectRender3D(double x, double y, double z, double w, double h, double l, double rotY){
		this(x, y, z, w, h, l, RotRender3D.axis(0, rotY, 0));
	}
	
	public RectRender3D(ZRect3D r){
		this(r.getX(), r.getY(), r.getZ(), r.getWidth(), r.getHeight(), r.getLength());
	}
	
	/**
	 * Create a {@link RectRender3D} with everything set to 0
	 */
	public RectRender3D(){
		this(0, 0, 0, 0, 0, 0);
	}
	
	/** @return See {@link #rot} */
	public RotRender3D getRot(){
		return this.rot;
	}
	
	/** @param rot See {@link #rot} */
	public void setRot(RotRender3D rot){
		this.rot = rot;
	}
}
