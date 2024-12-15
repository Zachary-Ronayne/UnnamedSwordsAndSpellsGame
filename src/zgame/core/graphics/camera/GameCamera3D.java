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
	
	// Rotation values
	/** The rotation of the camera on the x axis in radians */
	private double rotX;
	/** The rotation of the camera on the y axis in radians */
	private double rotY;
	/** The rotation of the camera on the z axis in radians */
	private double rotZ;
	
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
	
	/*
	 issue#42 this also account for a camera distance from where it rotates around?
	  Like imagine the (x, y, z) point is where the camera's swivel point is,
	  but the actual point where the camera shows stuff is determined by this distance.
	  Also, this probably is where third person should be handled
	 */
	
	/** Create a base camera at (0, 0, 0), with no rotation on any axis */
	public GameCamera3D(){
		this.setX(0);
		this.setY(0);
		this.setZ(0);
		this.setRotX(0);
		this.setRotY(0);
		this.setRotZ(0);
		
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
	
	/** @param z The amount to add to {@link #z} */
	public void addZ(double z){
		this.setZ(this.z + z);
	}
	
	/** @return See {@link #rotX} */
	public double getRotX(){
		return this.rotX;
	}
	
	/** @param rotX See {@link #rotX} */
	public void setRotX(double rotX){
		this.rotX = this.isEnableLookLimit() ? ZMath.minMax(-Math.PI * 0.5, Math.PI * 0.5, rotX) : rotX;
	}
	
	/** @param rotX The amount to add to {@link #rotX} */
	public void addRotX(double rotX){
		this.setRotX(this.rotX + rotX);
	}
	
	/** @return See {@link #rotY} */
	public double getRotY(){
		return this.rotY;
	}
	
	/** @param rotY See {@link #rotY} */
	public void setRotY(double rotY){
		this.rotY = rotY;
	}
	
	/** @param rotY The amount to add to {@link #rotY} */
	public void addRotY(double rotY){
		this.setRotY(this.rotY + rotY);
	}
	
	/** @return See {@link #rotZ} */
	public double getRotZ(){
		return this.rotZ;
	}
	
	/** @param rotZ See {@link #rotZ} */
	public void setRotZ(double rotZ){
		this.rotZ = rotZ;
	}
	
	/** @param rotZ The amount to add to {@link #rotZ} */
	public void addRotZ(double rotZ){
		this.setRotZ(this.rotZ + rotZ);
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
