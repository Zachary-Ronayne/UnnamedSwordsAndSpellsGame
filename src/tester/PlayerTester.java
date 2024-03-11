package tester;

import zgame.core.Game;
import zgame.physics.material.Material;
import zgame.things.entity.*;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room;

import static org.lwjgl.glfw.GLFW.*;

public abstract class PlayerTester extends EntityThing2D implements Movement2D {
	
	/** The width of this mob */
	private double width;
	/** The height of this mob */
	private double height;
	
	/** true to lock the camera to the center of the player, false otherwise */
	private boolean lockCamera;
	
	/** The {@link Walk} object used by this object's implementation of {@link Movement2D} */
	private final Walk2D walk;
	
	/** See {@link Movement2D#getWalkSpeedMax()} */
	private double walkSpeedMax;
	
	/** See {@link Movement2D#getWalkAcceleration()} */
	private double walkAcceleration;
	
	/** See {@link Movement2D#getWalkStopFriction()} */
	private double walkStopFriction;
	
	/** See {@link Movement2D#getJumpPower()} */
	private double jumpPower;
	
	/** See {@link Movement2D#getJumpStopPower()} */
	private double jumpStopPower;
	
	/** See {@link Movement2D#isCanStopJump()} */
	private boolean canStopJump;
	
	/** See {@link Movement2D#getJumpBuildTime()} */
	private double jumpBuildTime;
	
	/** See {@link Movement2D#isJumpAfterBuildUp()} */
	private boolean jumpAfterBuildUp;
	
	/** See {@link Movement2D#getWalkAirControl()} */
	private double walkAirControl;
	
	/** See {@link Movement2D#getWalkFriction()} */
	private double walkFriction;
	
	/** See {@link Movement2D#getWalkingRatio()} */
	private double walkingRatio;
	
	/** See {@link Movement2D#isCanWallJump()} */
	private boolean canWallJump;
	
	/** See {@link Movement2D#getNormalJumpTime()} */
	private double normalJumpTime;
	
	/** See {@link Movement2D#getWallJumpTime()} */
	private double wallJumpTime;
	
	/** See {@link Movement2D#isWalking()} */
	private boolean walking;
	
	/**
	 * Create a new {@link PlayerTester} of the given size
	 *
	 * @param x The x coordinate of the {@link PlayerTester}
	 * @param y The y coordinate of the {@link PlayerTester}
	 * @param width The width of the {@link PlayerTester} hit box
	 * @param height The height of the {@link PlayerTester} hit box
	 */
	public PlayerTester(double x, double y, double width, double height){
		super(x, y);
		
		this.walkSpeedMax = 300;
		this.walkAcceleration = 2000;
		this.walkStopFriction = 10;
		this.jumpPower = 60000;
		this.jumpStopPower = 3000;
		this.canStopJump = true;
		this.jumpBuildTime = 0;
		this.jumpAfterBuildUp = true;
		this.walkAirControl = .5;
		this.walkFriction = 1;
		this.walkingRatio = .5;
		this.canWallJump = false;
		this.normalJumpTime = .1;
		this.wallJumpTime = .25;
		this.walking = false;
		this.walk = new Walk2D(this);
		
		this.lockCamera = false;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void tick(Game game, double dt){
		this.movementTick(game, dt);
		
		var ki = game.getKeyInput();
		this.handleMovementControls(ki.pressed(GLFW_KEY_LEFT), ki.pressed(GLFW_KEY_RIGHT), ki.pressed(GLFW_KEY_UP), dt);
		
		// Lastly, perform the normal game tick on the player
		super.tick(game, dt);
		
		// Now the camera to the player after repositioning the player
		this.checkCenterCamera(game);
	}
	
	
	/**
	 * If the camera should be locked to this {@link PlayerTester}, then lock the camera, otherwise do nothing
	 *
	 * @param game The game to get the camera from
	 */
	public void checkCenterCamera(Game game){
		if(this.isLockCamera()) this.centerCamera(game);
	}
	
	/** @return See {@link #lockCamera} */
	public boolean isLockCamera(){
		return this.lockCamera;
	}
	
	/** @param lockCamera See {@link #lockCamera} */
	public void setLockCamera(boolean lockCamera){
		this.lockCamera = lockCamera;
	}
	
	@Override
	public void enterRoom(Room<HitBox2D> from, Room<HitBox2D> to, Game game){
		super.enterRoom(from, to, game);
		if(to != null) game.getPlayState().setCurrentRoom(to);
		
		// Center the camera to the player
		this.checkCenterCamera(game);
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
	
	@Override
	public Walk2D getWalk(){
		return this.walk;
	}
	
	/** @return See {@link #walkSpeedMax} */
	@Override
	public double getWalkSpeedMax(){
		return this.walkSpeedMax;
	}
	
	/** @param walkSpeedMax See {@link #walkSpeedMax} */
	public void setWalkSpeedMax(double walkSpeedMax){
		this.walkSpeedMax = walkSpeedMax;
	}
	
	/** @return See {@link #walkAcceleration} */
	@Override
	public double getWalkAcceleration(){
		return this.walkAcceleration;
	}
	
	/** @param walkAcceleration See {@link #walkAcceleration} */
	public void setWalkAcceleration(double walkAcceleration){
		this.walkAcceleration = walkAcceleration;
	}
	
	/** @return See {@link #walkStopFriction} */
	@Override
	public double getWalkStopFriction(){
		return this.walkStopFriction;
	}
	
	/** @param walkStopFriction See {@link #walkStopFriction} */
	public void setWalkStopFriction(double walkStopFriction){
		this.walkStopFriction = walkStopFriction;
	}
	
	@Override
	public double getFrictionConstant(){
		return this.getWalkFrictionConstant();
	}
	
	// TODO make it that this doesn't need to be implemented in this explicit way
	@Override
	public void leaveFloor(){
		super.leaveFloor();
		this.movementLeaveFloor();
	}
	
	@Override
	public void leaveWall(){
		super.leaveWall();
		this.movementLeaveWall();
	}
	
	@Override
	public void touchFloor(Material touched){
		super.touchFloor(touched);
		this.movementTouchFloor(touched);
	}
	
	/** @return See {@link #jumpPower} */
	@Override
	public double getJumpPower(){
		return this.jumpPower;
	}
	
	/** @param jumpPower See {@link #jumpPower} */
	public void setJumpPower(double jumpPower){
		this.jumpPower = jumpPower;
	}
	
	/** @return See {@link #jumpStopPower} */
	@Override
	public double getJumpStopPower(){
		return this.jumpStopPower;
	}
	
	/** @param jumpStopPower See {@link #jumpStopPower} */
	public void setJumpStopPower(double jumpStopPower){
		this.jumpStopPower = jumpStopPower;
	}
	
	/** @return See {@link #canStopJump} */
	@Override
	public boolean isCanStopJump(){
		return this.canStopJump;
	}
	
	/** @param canStopJump See {@link #canStopJump} */
	public void setCanStopJump(boolean canStopJump){
		this.canStopJump = canStopJump;
	}
	
	/** @return See {@link #jumpBuildTime} */
	@Override
	public double getJumpBuildTime(){
		return this.jumpBuildTime;
	}
	
	/** @param jumpBuildTime See {@link #jumpBuildTime} */
	public void setJumpBuildTime(double jumpBuildTime){
		this.jumpBuildTime = jumpBuildTime;
	}
	
	/** @return See {@link #jumpAfterBuildUp} */
	@Override
	public boolean isJumpAfterBuildUp(){
		return this.jumpAfterBuildUp;
	}
	
	/** @param jumpAfterBuildUp See {@link #jumpAfterBuildUp} */
	public void setJumpAfterBuildUp(boolean jumpAfterBuildUp){
		this.jumpAfterBuildUp = jumpAfterBuildUp;
	}
	
	/** @return See {@link #walkAirControl} */
	@Override
	public double getWalkAirControl(){
		return this.walkAirControl;
	}
	
	/** @param walkAirControl See {@link #walkAirControl} */
	public void setWalkAirControl(double walkAirControl){
		this.walkAirControl = walkAirControl;
	}
	
	/** @return See {@link #walkFriction} */
	@Override
	public double getWalkFriction(){
		return this.walkFriction;
	}
	
	/** @param walkFriction See {@link #walkFriction} */
	public void setWalkFriction(double walkFriction){
		this.walkFriction = walkFriction;
	}
	
	/** @return See {@link #walkingRatio} */
	@Override
	public double getWalkingRatio(){
		return this.walkingRatio;
	}
	
	/** @param walkingRatio See {@link #walkingRatio} */
	public void setWalkingRatio(double walkingRatio){
		this.walkingRatio = walkingRatio;
	}
	
	/** @return See {@link #canWallJump} */
	@Override
	public boolean isCanWallJump(){
		return this.canWallJump;
	}
	
	/** @param canWallJump See {@link #canWallJump} */
	public void setCanWallJump(boolean canWallJump){
		this.canWallJump = canWallJump;
	}
	
	/** @return See {@link #normalJumpTime} */
	@Override
	public double getNormalJumpTime(){
		return this.normalJumpTime;
	}
	
	/** @param normalJumpTime See {@link #normalJumpTime} */
	public void setNormalJumpTime(double normalJumpTime){
		this.normalJumpTime = normalJumpTime;
	}
	
	/** @return See {@link #wallJumpTime} */
	@Override
	public double getWallJumpTime(){
		return this.wallJumpTime;
	}
	
	/** @param wallJumpTime See {@link #wallJumpTime} */
	public void setWallJumpTime(double wallJumpTime){
		this.wallJumpTime = wallJumpTime;
	}
	
	@Override
	public boolean isWalking(){
		return this.walking;
	}
	
	@Override
	public double getCurrentWalkingSpeed(){
		return this.getVX();
	}
	
	/** @param walking See {@link #walking} */
	public void setWalking(boolean walking){
		this.walking = walking;
	}
	
	/** toggle the state of {@link #walking} */
	public void toggleWalking(){
		this.setWalking(!this.isWalking());
	}
	
}
