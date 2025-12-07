package zgame.physics;

/** A data class holding a forward vector with pitch and yaw vector components */
public class ForwardVector{
	
	/** The x component of the yaw direction */
	private double yawX;
	/** The y component of the yaw direction */
	private double yawY;
	/** The z component of the yaw direction */
	private double yawZ;
	
	/** The x component of the pitch direction */
	private double pitchX;
	/** The y component of the pitch direction */
	private double pitchY;
	/** The z component of the pitch direction */
	private double pitchZ;
	
	/** Create a vector with 0 for all components */
	public ForwardVector(){
		this(0, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Create a new forward vector based on the given yaw and pitch
	 *
	 * @param yaw The yaw angle for computing the components
	 * @param pitch The pitch angle for computing the components
	 */
	public ForwardVector(double yaw, double pitch){
		double cosP = Math.cos(pitch);
		double cosY = Math.cos(yaw);
		double sinP = Math.sin(pitch);
		double sinY = Math.sin(yaw);
		
		this.yawX = cosP * cosY;
		this.yawY = sinP;
		this.yawZ = cosP * sinY;
		this.pitchX = -(this.yawX * this.yawY) / cosP;
		this.pitchY =  cosP;
		this.pitchZ = -(this.yawY * this.yawZ) / cosP;
	}
	
	/**
	 * Create a new forward vector using all given components
	 *
	 * @param yawX See {@link #yawX}
	 * @param yawY See {@link #yawY}
	 * @param yawZ See {@link #yawZ}
	 * @param pitchX See {@link #pitchX}
	 * @param pitchY See {@link #pitchY}
	 * @param pitchZ See {@link #pitchZ}
	 */
	public ForwardVector(double yawX, double yawY, double yawZ, double pitchX, double pitchY, double pitchZ){
		this.yawX = yawX;
		this.yawY = yawY;
		this.yawZ = yawZ;
		this.pitchX = pitchX;
		this.pitchY = pitchY;
		this.pitchZ = pitchZ;
	}
	
	/** @return See {@link #yawX} */
	public double getYawX(){
		return this.yawX;
	}
	
	/** @param yawX See {@link #yawX} */
	public void setYawX(double yawX){
		this.yawX = yawX;
	}
	
	/** @return See {@link #yawY} */
	public double getYawY(){
		return this.yawY;
	}
	
	/** @param yawY See {@link #yawY} */
	public void setYawY(double yawY){
		this.yawY = yawY;
	}
	
	/** @return See {@link #yawZ} */
	public double getYawZ(){
		return this.yawZ;
	}
	
	/** @param yawZ See {@link #yawZ} */
	public void setYawZ(double yawZ){
		this.yawZ = yawZ;
	}
	
	/** @return See {@link #pitchX} */
	public double getPitchX(){
		return this.pitchX;
	}
	
	/** @param pitchX See {@link #pitchX} */
	public void setPitchX(double pitchX){
		this.pitchX = pitchX;
	}
	
	/** @return See {@link #pitchY} */
	public double getPitchY(){
		return this.pitchY;
	}
	
	/** @param pitchY See {@link #pitchY} */
	public void setPitchY(double pitchY){
		this.pitchY = pitchY;
	}
	
	/** @return See {@link #pitchZ} */
	public double getPitchZ(){
		return this.pitchZ;
	}
	
	/** @param pitchZ See {@link #pitchZ} */
	public void setPitchZ(double pitchZ){
		this.pitchZ = pitchZ;
	}
}
