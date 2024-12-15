package zgame.core.graphics;

/** An object holding data for drawing a 3D rectangular prism */
public class RectRender3D{
	
	/** The bottom middle x coordinate of the rect */
	private double x;
	/** The bottom middle y coordinate of the rect */
	private double y;
	/** The bottom middle z coordinate of the rect */
	private double z;
	
	/** The width, x axis, of the rect */
	private double w;
	/** The height, y axis, of the rect */
	private double h;
	/** The length, z axis, of the rect */
	private double l;
	
	/** The rotation on the x axis */
	private double rotX;
	/** The rotation on the y axis */
	private double rotY;
	/** The rotation on the z axis */
	private double rotZ;
	
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
	 * @param w See {@link #w}
	 * @param h See {@link #h}
	 * @param l See {@link #l}
	 * @param rotX See {@link #rotX}
	 * @param rotY See {@link #rotY}
	 * @param rotZ See {@link #rotZ}
	 * @param xa See {@link #xa}
	 * @param ya See {@link #ya}
	 * @param za See {@link #za}
	 */
	public RectRender3D(double x, double y, double z, double w, double h, double l, double rotX, double rotY, double rotZ, double xa, double ya, double za){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.l = l;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.xa = xa;
		this.ya = ya;
		this.za = za;
	}
	
	/**
	 * Create a {@link RectRender3D} with no rotation
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param w See {@link #w}
	 * @param h See {@link #h}
	 * @param l See {@link #l}
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
	 * @param w See {@link #w}
	 * @param h See {@link #h}
	 * @param l See {@link #l}
	 * @param rotY See {@link #rotY}
	 */
	public RectRender3D(double x, double y, double z, double w, double h, double l, double rotY){
		this(x, y, z, w, h, l, 0, rotY, 0, 0, 0, 0);
	}
	
	/**
	 * Create a {@link RectRender3D} with everything set to 0
	 */
	public RectRender3D(){
		this(0, 0, 0, 0, 0, 0);
	}
	
	/** @return See {@link #x} */
	public double x(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	public double y(){
		return this.y;
	}
	
	/** @return See {@link #z} */
	public double z(){
		return this.z;
	}
	
	/** @return See {@link #w} */
	public double w(){
		return this.w;
	}
	
	/** @return See {@link #h} */
	public double h(){
		return this.h;
	}
	
	/** @return See {@link #l} */
	public double l(){
		return this.l;
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
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}
	
	/** @param z See {@link #z} */
	public void setZ(double z){
		this.z = z;
	}
	
	/** @param w See {@link #w} */
	public void setW(double w){
		this.w = w;
	}
	
	/** @param h See {@link #h} */
	public void setH(double h){
		this.h = h;
	}
	
	/** @param l See {@link #l} */
	public void setL(double l){
		this.l = l;
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
}
