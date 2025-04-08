package zgame.core.graphics.camera;

import zgame.core.utils.ZMath;

/** A camera meant for 3D graphics */
public class GameCamera3D{
	
	// Position values
	/** The x coordinate of the camera */
	private double x;
	/** The y coordinate of the camera */
	private double y;
	/** The z coordinate of the camera */
	private double z;
	
	/** The distance from the camera's root position to where the camera will actually be displayed */
	private double positionOffset;
	
	// Rotation values
	/** The yaw angle in radians */
	private double yaw;
	/** The pitch angle in radians */
	private double pitch;
	/** The roll angle in radians */
	private double roll;
	
	// Perspective settings
	/** The current field of view of this camera */
	private double fov;
	/** The nearest value for the z buffer for the camera */
	private double nearZ;
	/** The farthest value for the z buffer for the camera */
	private double farZ;
	/** The left vertical clipping plane */
	private double leftClip;
	/** The right vertical clipping plane */
	private double rightClip;
	/** The bottom horizontal clipping plane */
	private double bottomClip;
	/** The top horizontal clipping plane */
	private double topClip;
	/** The distance from the camera to the near clipping plane */
	private double nearClip;
	/** The distance from the camera to the far clipping plane */
	private double farClip;
	
	/** true to limit the camera to only looking at most straight up and straight down, false for no limit */
	private boolean enableLookLimit;
	
	/** Create a base camera at (0, 0, 0), with no rotation on any axis */
	public GameCamera3D(){
		this.setX(0);
		this.setY(0);
		this.setZ(0);
		this.setYaw(0);
		this.setPitch(0);
		this.setRoll(0);
		this.setPositionOffset(0);
		
		this.setFov(1);
		this.setNearZ(0.1);
		this.setFarZ(100.0);
		this.setLeftClip(-1.0);
		this.setRightClip(1.0);
		this.setBottomClip(-1.0);
		this.setTopClip(1.0);
		this.setNearClip(0.01);
		this.setFarClip(1000.0);
		
		this.setEnableLookLimit(true);
	}
	
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/** @param x The amount to add to {@link #x} */
	public void addX(double x){
		this.setX(this.x + x);
	}
	
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}
	
	/** @param y The amount to add to {@link #y} */
	public void addY(double y){
		this.setY(this.y + y);
	}
	
	/** @return See {@link #z} */
	public double getZ(){
		return this.z;
	}
	
	/** @param z See {@link #z} */
	public void setZ(double z){
		this.z = z;
	}
	
	/** @return See {@link #positionOffset} */
	public double getPositionOffset(){
		return this.positionOffset;
	}
	
	/** @param positionOffset See {@link #positionOffset} */
	public void setPositionOffset(double positionOffset){
		this.positionOffset = positionOffset;
	}
	
	/** @param z The amount to add to {@link #z} */
	public void addZ(double z){
		this.setZ(this.z + z);
	}
	
	/** @return See {@link #yaw} */
	public double getYaw(){
		return this.yaw;
	}
	
	/** @param yaw See {@link #yaw} */
	public void setYaw(double yaw){
		this.yaw = yaw;
	}
	
	/** @param yaw The amount to add to {@link #yaw} */
	public void addYaw(double yaw){
		this.setYaw(this.yaw + yaw);
	}
	
	/** @return See {@link #pitch} */
	public double getPitch(){
		return this.pitch;
	}
	
	/** @param pitch See {@link #pitch} */
	public void setPitch(double pitch){
		this.pitch = this.isEnableLookLimit() ? ZMath.minMax(-Math.PI * 0.5, Math.PI * 0.5, pitch) : pitch;
	}
	
	/** @param pitch The amount to add to {@link #pitch} */
	public void addPitch(double pitch){
		this.setPitch(this.pitch + pitch);
	}
	
	/** @return See {@link #roll} */
	public double getRoll(){
		return this.roll;
	}
	
	/** @param roll See {@link #roll} */
	public void setRoll(double roll){
		this.roll = roll;
	}
	
	/** @param roll The amount to add to {@link #roll} */
	public void addRoll(double roll){
		this.setRoll(this.roll + roll);
	}
	
	/** @return See {@link #enableLookLimit} */
	public boolean isEnableLookLimit(){
		return this.enableLookLimit;
	}
	
	/** @param enableLookLimit See {@link #enableLookLimit} */
	public void setEnableLookLimit(boolean enableLookLimit){
		this.enableLookLimit = enableLookLimit;
	}
	
	/** @return See {@link #fov} */
	public double getFov(){
		return this.fov;
	}
	
	/** @param fov See {@link #fov} */
	public void setFov(double fov){
		this.fov = fov;
	}
	
	/** @return See {@link #nearZ} */
	public double getNearZ(){
		return this.nearZ;
	}
	
	/** @param nearZ See {@link #nearZ} */
	public void setNearZ(double nearZ){
		this.nearZ = nearZ;
	}
	
	/** @return See {@link #farZ} */
	public double getFarZ(){
		return this.farZ;
	}
	
	/** @param farZ See {@link #farZ} */
	public void setFarZ(double farZ){
		this.farZ = farZ;
	}
	
	/** @return See {@link #leftClip} */
	public double getLeftClip(){
		return this.leftClip;
	}
	
	/** @param leftClip See {@link #leftClip} */
	public void setLeftClip(double leftClip){
		this.leftClip = leftClip;
	}
	
	/** @return See {@link #rightClip} */
	public double getRightClip(){
		return this.rightClip;
	}
	
	/** @param rightClip See {@link #rightClip} */
	public void setRightClip(double rightClip){
		this.rightClip = rightClip;
	}
	
	/** @return See {@link #bottomClip} */
	public double getBottomClip(){
		return this.bottomClip;
	}
	
	/** @param bottomClip See {@link #bottomClip} */
	public void setBottomClip(double bottomClip){
		this.bottomClip = bottomClip;
	}
	
	/** @return See {@link #topClip} */
	public double getTopClip(){
		return this.topClip;
	}
	
	/** @param topClip See {@link #topClip} */
	public void setTopClip(double topClip){
		this.topClip = topClip;
	}
	
	/** @return See {@link #nearClip} */
	public double getNearClip(){
		return this.nearClip;
	}
	
	/** @param nearClip See {@link #nearClip} */
	public void setNearClip(double nearClip){
		this.nearClip = nearClip;
	}
	
	/** @return See {@link #farClip} */
	public double getFarClip(){
		return this.farClip;
	}
	
	/** @param farClip See {@link #farClip} */
	public void setFarClip(double farClip){
		this.farClip = farClip;
	}
	
}
