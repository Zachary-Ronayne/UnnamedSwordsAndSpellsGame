package zgame.core.graphics.camera;

import zgame.core.Game;
import zgame.core.utils.ZMath;
import zgame.settings.BooleanTypeSetting;
import zgame.settings.DoubleTypeSetting;

/** A camera meant for 3D graphics */
public class GameCamera3D{
	
	/** The x coordinate of the camera */
	private double x;
	/** The y coordinate of the camera */
	private double y;
	/** The z coordinate of the camera */
	private double z;
	
	/** The rotation of the camera on the x axis in radians */
	private double rotX;
	/** The rotation of the camera on the y axis in radians */
	private double rotY;
	/** The rotation of the camera on the z axis in radians */
	private double rotZ;
	
	/** true to limit the camera to only looking at most straight up and straight down, false for no limit */
	private boolean enableLookLimit;
	
	/*
	 issue#42 this also account for a camera distance from where it rotates around?
	  Like imagine the (x, y, z) point is where the camera's swivel point is,
	  but the actual point where the camera shows stuff is determined by this distance.
	  Also this probably is where third person should be handled
	 */
	
	/** Create a base camera at (0, 0, 0), with no rotation on any axis */
	public GameCamera3D(){
		this.setX(0);
		this.setY(0);
		this.setZ(0);
		this.setRotX(0);
		this.setRotY(0);
		this.setRotZ(0);
		
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
	
	/**
	 * Make this camera look in a direction based on the given mouse coordinates. This will add some amount to {@link #rotX} and {@link #rotY}
	 * based on the previous mouse position and the given mouse position
	 *
	 * @param game The game where the look happened
	 * @param x The new x coordinate of the mouse, obtained from {@link Game#mouseMove(double, double)}
	 * @param y The new y coordinate of the mouse, obtained from {@link Game#mouseMove(double, double)}
	 */
	public void look(Game game, double x, double y){
		// TODO this should be in Mobility3D, split this up to not need game as a parameter or to rely on mouse input here
		
		// issue#43 fix sudden camera jolts when switching between normal and not normal mouse modes
		
		// Axes swapped because of the way that it feels like it should be when moving a mouse around
		var dx = (y - game.getMouseInput().lastY()) * game.get(DoubleTypeSetting.CAMERA_LOOK_SPEED_X);
		var dy = (x - game.getMouseInput().lastX()) * game.get(DoubleTypeSetting.CAMERA_LOOK_SPEED_Y);
		
		// Invert if applicable
		if(game.get(BooleanTypeSetting.CAMERA_LOOK_INVERT_X)) dx = -dx;
		if(game.get(BooleanTypeSetting.CAMERA_LOOK_INVERT_Y)) dy = -dy;
		
		this.addRotX(dx);
		this.addRotY(dy);
	}
	
}
