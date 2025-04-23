package zgame.core.graphics;

/** An object holding data for handling rotations in a 3D space */
public class RotRender3D{
	
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
	
	/** The point, relative to the point to draw this object, to rotate on the x axis */
	private double xa;
	/** The point, relative to the point to draw this object, to rotate on the y axis */
	private double ya;
	/** The point, relative to the point to draw this object, to rotate on the z axis */
	private double za;
	
	/**
	 * Create a {@link RotRender3D} with no rotation
	 */
	public RotRender3D(){
		this(0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Create a {@link RotRender3D} with the given values
	 *
	 * @param rotX See {@link #rotX}
	 * @param rotY See {@link #rotY}
	 * @param rotZ See {@link #rotZ}
	 * @param yaw See {@link #yaw}
	 * @param pitch See {@link #pitch}
	 * @param roll See {@link #roll}
	 * @param xa See {@link #xa}
	 * @param ya See {@link #ya}
	 * @param za See {@link #za}
	 */
	public RotRender3D(double rotX, double rotY, double rotZ, double yaw, double pitch, double roll, double xa, double ya, double za){
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.xa = xa;
		this.ya = ya;
		this.za = za;
		
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
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
	
	/**
	 * Create a {@link RotRender3D} with axis rotation with the given values
	 *
	 * @param rotX See {@link #rotX}
	 * @param rotY See {@link #rotY}
	 * @param rotZ See {@link #rotZ}
	 */
	public static RotRender3D axis(double rotX, double rotY, double rotZ){
		return new RotRender3D(rotX, rotY, rotZ, 0, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Create a {@link RotRender3D} with axis rotation with the given values
	 *
	 * @param rotX See {@link #rotX}
	 * @param rotY See {@link #rotY}
	 * @param rotZ See {@link #rotZ}
	 * @param xa See {@link #xa}
	 * @param ya See {@link #ya}
	 * @param za See {@link #za}
	 */
	public static RotRender3D axis(double rotX, double rotY, double rotZ, double xa, double ya, double za){
		return new RotRender3D(rotX, rotY, rotZ, 0, 0, 0, xa, ya, za);
	}
	
	/**
	 * Create a {@link RotRender3D} with euler rotations with the given values
	 *
	 * @param yaw See {@link #yaw}
	 * @param pitch See {@link #pitch}
	 * @param roll See {@link #roll}
	 */
	public static RotRender3D euler(double yaw, double pitch, double roll){
		return new RotRender3D(0, 0, 0, yaw, pitch, roll, 0, 0, 0);
	}
	
	/**
	 * Create a {@link RotRender3D} with euler rotations with the given values
	 *
	 * @param yaw See {@link #yaw}
	 * @param pitch See {@link #pitch}
	 * @param roll See {@link #roll}
	 * @param xa See {@link #xa}
	 * @param ya See {@link #ya}
	 * @param za See {@link #za}
	 */
	public static RotRender3D euler(double yaw, double pitch, double roll, double xa, double ya, double za){
		return new RotRender3D(0, 0, 0, yaw, pitch, roll, xa, ya, za);
	}
}
