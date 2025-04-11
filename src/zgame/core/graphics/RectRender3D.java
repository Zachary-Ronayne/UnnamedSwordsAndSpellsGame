package zgame.core.graphics;

import zgame.core.utils.ZRect3D;

/** An object holding data for drawing a 3D rectangular prism */
public class RectRender3D extends ZRect3D{
	
	/** The rotation on the x axis */
	private double rotX;
	/** The rotation on the y axis */
	private double rotY;
	/** The rotation on the z axis */
	private double rotZ;
	
	/** The yaw rotation angle */
	private double yaw;
	/** The pitch rotation angle */
	private double pitch;
	/** The roll rotation angle */
	private double roll;
	
	/** true to use {@link #rotX}, {@link #rotY}, and {@link #rotZ} for applying rotations, false for {@link #yaw}, {@link #pitch}, and {@link #roll} */
	private boolean coordinateRotation;
	
	/** The point, relative to the point to draw this object, to rotate on the x axis */
	private double xa;
	/** The point, relative to the point to draw this object, to rotate on the y axis */
	private double ya;
	/** The point, relative to the point to draw this object, to rotate on the z axis */
	private double za;
	
	/**
	 * Create a {@link RectRender3D} with the given values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 * @param l See {@link #length}
	 * @param rotX See {@link #rotX}
	 * @param rotY See {@link #rotY}
	 * @param rotZ See {@link #rotZ}
	 * @param xa See {@link #xa}
	 * @param ya See {@link #ya}
	 * @param za See {@link #za}
	 */
	public RectRender3D(double x, double y, double z, double w, double h, double l, double rotX, double rotY, double rotZ, double xa, double ya, double za){
		super(x, y, z, w, h, l);
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.xa = xa;
		this.ya = ya;
		this.za = za;
		
		this.yaw = 0;
		this.pitch = 0;
		this.roll = 0;
		this.coordinateRotation = true;
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
		this(x, y, z, w, h, l, 0, 0, 0, 0, 0, 0);
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
	 * @param rotY See {@link #rotY}
	 */
	public RectRender3D(double x, double y, double z, double w, double h, double l, double rotY){
		this(x, y, z, w, h, l, 0, rotY, 0, 0, 0, 0);
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
	
	/** @return See {@link #rotX} */
	public double xRot(){
		return this.rotX;
	}
	
	/** @return See {@link #rotY} */
	public double yRot(){
		return this.rotY;
	}
	
	/** @return See {@link #rotZ} */
	public double zRot(){
		return this.rotZ;
	}
	
	/** @return See {@link #xa} */
	public double xA(){
		return this.xa;
	}
	
	/** @return See {@link #ya} */
	public double yA(){
		return this.ya;
	}
	
	/** @return See {@link #za} */
	public double zA(){
		return this.za;
	}
	
	/** @param rotX See {@link #rotX} */
	public void setRotX(double rotX){
		this.rotX = rotX;
	}
	
	/** @param rotY See {@link #rotY} */
	public void setRotY(double rotY){
		this.rotY = rotY;
	}
	
	/** @param rotZ See {@link #rotZ} */
	public void setRotZ(double rotZ){
		this.rotZ = rotZ;
	}
	
	/** @param xa See {@link #xa} */
	public void setXa(double xa){
		this.xa = xa;
	}
	
	/** @param ya See {@link #ya} */
	public void setYa(double ya){
		this.ya = ya;
	}
	
	/** @param za See {@link #za} */
	public void setZa(double za){
		this.za = za;
	}
	
	/** @return See {@link #yaw} */
	public double yaw(){
		return this.yaw;
	}
	
	/** @param yaw See {@link #yaw} */
	public void setYaw(double yaw){
		this.yaw = yaw;
	}
	
	/** @return See {@link #pitch} */
	public double pitch(){
		return this.pitch;
	}
	
	/** @param pitch See {@link #pitch} */
	public void setPitch(double pitch){
		this.pitch = pitch;
	}
	
	/** @return See {@link #roll} */
	public double roll(){
		return this.roll;
	}
	
	/** @param roll See {@link #roll} */
	public void setRoll(double roll){
		this.roll = roll;
	}
	
	/** @return See {@link #coordinateRotation} */
	public boolean isCoordinateRotation(){
		return this.coordinateRotation;
	}
	
	/** @param coordinateRotation See {@link #coordinateRotation} */
	public void setCoordinateRotation(boolean coordinateRotation){
		this.coordinateRotation = coordinateRotation;
	}
}
