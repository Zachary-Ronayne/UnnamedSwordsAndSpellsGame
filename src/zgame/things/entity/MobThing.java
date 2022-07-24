package zgame.things.entity;

import zgame.core.Game;
import zgame.physics.ZVector;
import zgame.physics.material.Material;

/** An {@link EntityThing} which represents some kind of creature which can walk around, i.e. the player, an enemy, an animal, a monster, any NPC, etc. */
public abstract class MobThing extends EntityThing{
	
	/** The string used to identify the force used to make this {@link MobThing} walk */
	public static final String FORCE_NAME_WALKING = "walking";
	/** The string used to identify the force used to make this {@link MobThing} jump */
	public static final String FORCE_NAME_JUMPING = "jumping";
	/** The string used to identify the force used to make this {@link MobThing} stop jumping */
	public static final String FORCE_NAME_JUMPING_STOP = "jumpingStop";
	
	/** The default value of {@link #jumpPower} */
	public static final double DEFAULT_JUMP_POWER = 60000;
	/** The default value of {@link #jumpStopPower} */
	public static final double DEFAULT_JUMP_STOP_POWER = 3000;
	/** The default value of {@link #canStopJump} */
	public static final boolean DEFAULT_CAN_STOP_JUMP = true;
	/** The default value of {@link #jumpBuildTime} */
	public static final double DEFAULT_JUMP_BUILD_TIME = 0;
	/** The default value of {@link #jumpAfterBuildUp} */
	public static final boolean DEFAULT_JUMP_AFTER_BUILD_UP = true;
	/** The default value of {@link #walkAcceleration} */
	public static final double DEFAULT_WALK_ACCELERATION = 2000;
	/** The default value of {@link #walkSpeedMax} */
	public static final double DEFAULT_WALK_SPEED_MAX = 300;
	/** The default value of {@link #walkAirControl} */
	public static final double DEFAULT_WALK_AIR_CONTROL = 0.5;
	/** The default value of {@link #walkFriction} */
	public static final double DEFAULT_WALK_FRICTION = 1;
	/** The default value of {@link #walkStopFriction} */
	public static final double DEFAULT_WALK_STOP_FRICTION = 10;
	/** The default value of {@link #canWallJump} */
	public static final boolean DEFAULT_CAN_WALL_JUMP = false;
	
	/**
	 * The magnitude of how much a mob can jump in units of momentum, i.e. mass * velocity,
	 * i.e, higher mass makes for lower jumps, lower mass makes for higher jumps
	 */
	private double jumpPower;
	
	/** In the same units as {@link #jumpPower}, the power at which this {@link MobThing} is able to stop jumping while in the air */
	private double jumpStopPower;
	
	/** true if this mob has the ability to stop jumping while it's in the air, false otherwise */
	private boolean canStopJump;
	
	/** The amount of time, in seconds, it takes the mob to build up to a full jump, use 0 to make jumping instant */
	private double jumpBuildTime;
	
	/** true if after building up a jump to max power, the mob should immediately jump, false to make it that it has to wait to jump */
	private boolean jumpAfterBuildUp;
	
	/** The acceleration of this {@link MobThing} while walking, i.e., how fast it gets to #walkSpeedMax */
	private double walkAcceleration;
	
	/** The maximum walking speed of this {@link MobThing} */
	private double walkSpeedMax;
	
	/** The ratio of speed this {@link MobThing} can use to walk when it is airborne, i.e. not on the ground */
	private double walkAirControl;
	
	/**
	 * The frictional constant used to slow down this {@link MobThing} when it is trying to move.
	 * This value represents the amount of the surface's friction which is applied.
	 * Zero means no friction is applied while walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction
	 * Generally should be 1.
	 */
	private double walkFriction;
	
	/**
	 * The frictional constant used to slow down this {@link MobThing} when it is trying to stop moving
	 * This value represents the amount of the surface's friction which is applied.
	 * Zero means no friction is applied while not walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction.
	 * Use a high value to make stopping walking happen quickly, use a low value to make stopping walking slow, and use zero to make it impossible to stop.
	 * This friction only applies while on the ground. A value of 1 is used while in the air, regardless of if this MobThing is walking or not.
	 */
	private double walkStopFriction;
	
	/** True if this {@link MobThing} can jump off walls while touching one, otherwise, false */
	private boolean canWallJump;
	
	/** true if this {@link MobThing} is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	/** true if this mob is currently jumping, false otherwise */
	private boolean jumping;
	
	/** true if this mob is able to wall jump, i.e. it has touched the ground since its last wall jump */
	private boolean wallJumpAvailable;
	
	/** The force of jumping on this {@link MobThing} */
	private ZVector jumpingForce;
	
	/** The amount of time, in seconds, this {@link MobThing} has built up their jump height */
	private double jumpTimeBuilt;
	
	/** true if this {@link MobThing} is currently building up a jump, false otherwise */
	private boolean buildingJump;
	
	/** true if this {@link MobThing} is currently stopping its jump, false otherwise */
	private boolean stoppingJump;
	
	/**
	 * The force used to make you stop jumping. Physics wise doesn't make any sense, but it's to give an option to control jump height by holding down or letting go of a jump
	 * button
	 */
	private ZVector jumpingStopForce;
	
	/** The vector keeping track of the force of this {@link MobThing} walking */
	private ZVector walkingForce;
	
	/** The direction this {@link MobThing} is walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	private int walkingDirection;
	
	/**
	 * Create a new {@link MobThing} at the given position
	 * 
	 * @param x The x coordinate of the mob
	 * @param y The y coordinate of the mob
	 */
	public MobThing(double x, double y){
		super(x, y);
		this.canJump = false;
		this.jumping = false;
		this.stoppingJump = false;
		this.jumpTimeBuilt = 0;
		this.wallJumpAvailable = false;
		
		this.stopWalking();
		
		this.jumpingForce = new ZVector();
		this.setForce(FORCE_NAME_JUMPING, this.jumpingForce);
		this.jumpingStopForce = new ZVector();
		this.setForce(FORCE_NAME_JUMPING_STOP, this.jumpingStopForce);
		
		this.jumpPower = DEFAULT_JUMP_POWER;
		this.jumpStopPower = DEFAULT_JUMP_STOP_POWER;
		this.canStopJump = DEFAULT_CAN_STOP_JUMP;
		this.jumpBuildTime = DEFAULT_JUMP_BUILD_TIME;
		this.jumpAfterBuildUp = DEFAULT_JUMP_AFTER_BUILD_UP;
		this.walkAcceleration = DEFAULT_WALK_ACCELERATION;
		this.walkSpeedMax = DEFAULT_WALK_SPEED_MAX;
		this.walkAirControl = DEFAULT_WALK_AIR_CONTROL;
		this.walkFriction = DEFAULT_WALK_FRICTION;
		this.walkStopFriction = DEFAULT_WALK_STOP_FRICTION;
		this.canWallJump = DEFAULT_CAN_WALL_JUMP;
		
		this.walkingForce = new ZVector();
		this.setForce(FORCE_NAME_WALKING, this.walkingForce);
	}
	
	@Override
	public void tick(Game game, double dt){
		// Determine the new walking force
		this.updateWalkForce(dt);
		
		// Update the state of the jumping force
		this.updateJumpState(dt);
		
		// Do the normal game update
		super.tick(game, dt);

		// After doing the normal tick and updating the mob's position and velocity,
		// if the mob is not on a surface allowing it to jump, then remove all jump force
		if(!(this.isOnGround() || this.isCanWallJump() && this.isWallJumpAvailable() && this.isOnWall())) this.jumpingForce = this.setForce(FORCE_NAME_JUMPING, 0, 0);
	}
	
	/** @return See {@link #walkingDirection} */
	public int getWalkingDirection(){
		return this.walkingDirection;
	}
	
	/** Tell this mob to start walking to the left */
	public void walkLeft(){
		this.walkingDirection = -1;
	}
	
	/** Tell this mob to start walking to the right */
	public void walkRight(){
		this.walkingDirection = 1;
	}
	
	/** Tell this mob to stop walking */
	public void stopWalking(){
		this.walkingDirection = 0;
	}
	
	/**
	 * Update the value of {@link #walkingForce} based on the current state of this {@link MobThing}
	 * 
	 * @param dt The amount of time that will pass in the next tick when this {@link MobThing} walks
	 */
	public void updateWalkForce(double dt){
		// First handle mob movement
		double mass = this.getMass();
		double acceleration = this.getWalkAcceleration();
		double walkForce = acceleration * mass * this.getWalkingDirection();
		boolean walking = walkForce != 0;
		double maxSpeed = this.getWalkSpeedMax();
		
		// If the mob is not on the ground, it's movement force is modified by the air control
		if(!this.isOnGround()) walkForce *= this.getWalkAirControl();
		
		// Only check the walking speed if there is any walking force
		if(walking){
			double vx = this.getVX();
			// Find the total velocity if the new walking force is applied on the next tick
			double sign = walkForce;
			double newVel = vx + walkForce * dt / mass;
			
			// TODO fix this, it's not always setting it to the exact max speed, usually slightly below it, probably related to the friction issue
			// If that velocity is greater than the maximum speed, then apply a force such that it will bring the velocity exactly to the maximum speed
			if(Math.abs(newVel) > maxSpeed){
				// Need to account for the sign of max speed depending on the direction of the new desired walking force
				walkForce = Math.abs(maxSpeed * ((sign > 0) ? 1 : -1) - vx) / dt * mass;
				// Need to adjust the sign depending on the direction of the original walk force
				if(sign < 0) walkForce *= -1;
			}
		}
		// Set the amount the mob is walking
		this.setWalkingForce(walkForce);
	}
	
	@Override
	public double getFrictionConstant(){
		// If not on the ground, use the normal amount of friction, otherwise, if currently walking, return walk friction, otherwise, return stop friction
		return !this.isOnGround() ? 1 : (this.getWalkingDirection() != 0) ? getWalkFriction() : getWalkStopFriction();
	}
	
	/** @return See {@link #canJump} */
	public boolean isCanJump(){
		return this.canJump;
	}
	
	/** @return See {@link #jumping} */
	public boolean isJumping(){
		return this.jumping;
	}
	
	/** @return See {@link #stoppingJump} */
	public boolean isStoppingJump(){
		return this.stoppingJump;
	}
	
	/** @return See {@link #jumpTimeBuilt} */
	public double getJumpTimeBuilt(){
		return this.jumpTimeBuilt;
	}
	
	/** @return See {@link #buildingJump} */
	public boolean isBuildingJump(){
		return this.buildingJump;
	}
	
	/** @return See {@link #wallJumpAvailable} */
	public boolean isWallJumpAvailable(){
		return this.wallJumpAvailable;
	}
	
	/** Remove any jump time built up */
	public void cancelJump(){
		this.buildingJump = false;
		this.jumpTimeBuilt = 0;
	}
	
	/**
	 * Update the value of {@link #jumpingForce} and {@link #jumpingStopForce} based on the current state of this {@link MobThing}
	 * 
	 * @param dt The amount of time, in seconds, that will pass in the next tick when this {@link MobThing} stops jumping
	 */
	public void updateJumpState(double dt){
		// TODO give wall jump a period of time where the mob can still jump even if it's not touching the wall
		//	make onWall, onFloor, onCeiling stored as numbers, not booleans, then isOnThing methods determine if the values are within a threshold

		// The mob can jump if it's on the ground, or if it can wall jump and is on a wall
		this.canJump = this.isOnGround() || this.isCanWallJump() && this.isOnWall() && this.isWallJumpAvailable();
		
		// If building a jump, and able to jump, then add the time
		if(this.isBuildingJump() && this.isCanJump()){
			this.jumpTimeBuilt += dt;
			// If the jump time threshold has been met, and this mob is set to jump right away, then perform the jump now
			if(this.getJumpTimeBuilt() >= this.getJumpBuildTime() && this.isJumpAfterBuildUp()) this.jumpFromBuiltUp(dt);
		}
		
		if(this.isStoppingJump()){
			// Can only stop jumping if it's allowed
			if(!this.isCanStopJump()) return;
			// Only need to stop jumping if the mob is moving up
			double vy = this.getVY();
			if(vy < 0){
				double mass = this.getMass();
				double newStopJumpVel = this.getJumpStopPower() / mass;
				double newStopJumpForce = this.getJumpStopPower() / dt;
				// If the jump force would add extra velocity making its total velocity downwards,
				// then the jump stop force should be such that the y velocity will be 0 on the next tick
				if(vy + newStopJumpVel > 0) newStopJumpForce = -vy * mass / dt;
				
				this.jumpingStopForce = this.setForce(FORCE_NAME_JUMPING_STOP, 0, newStopJumpForce);
			}
			// Otherwise it is no longer stopping its jump, so remove the stopping force amount
			else{
				this.stoppingJump = false;
				this.jumpingStopForce = this.setForce(FORCE_NAME_JUMPING_STOP, 0, 0);
			}
		}
	}
	
	/**
	 * Cause this mob to start jumping or instantly jump if {@link #jumpBuildTime} is 0, or to build up a jump if it is greater than zero.
	 * Only runs if the mob is in a position to jump, has not begun to build up jump time, and is not already jumping
	 * 
	 * @param dt The amount of time, in seconds, that will pass in one tick after the mob jumps off the ground
	 */
	public void jump(double dt){
		if(!this.canJump || this.getJumpTimeBuilt() > 0 || this.isJumping()) return;
		
		// If it takes no time to jump, jump right away
		if(this.jumpsAreInstant()) this.jumpFromBuiltUp(dt);
		// Otherwise, start building up a jump
		else this.buildingJump = true;
	}
	
	/**
	 * Cause this mob to instantly jump with the currently built power, only if it is allowed to jump
	 * 
	 * @param dt The amount of time, in seconds, that will pass in one tick after the mob jumps off the ground
	 */
	public void jumpFromBuiltUp(double dt){
		if(!this.canJump) return;
		
		this.jumping = true;
		this.canJump = false;
		
		// If this mob is on a wall, this counts a wall jump
		if(this.isOnWall()) this.wallJumpAvailable = false;
		
		// The jump power is either itself if jumping is instant, or multiplied by the ratio of jump time built and the total time to build a jump, keeping it at most 1
		double power = this.jumpPower * (this.jumpsAreInstant() ? 1 : Math.min(1, this.getJumpTimeBuilt() / this.getJumpBuildTime()));
		double jumpAmount = -power / dt;
		this.jumpingForce = this.setForce(FORCE_NAME_JUMPING, 0, jumpAmount);
		this.jumpTimeBuilt = 0;
		this.buildingJump = false;
	}
	
	/** Cause this {@link MobThing} to stop jumping. Does nothing if the mob is not currently jumping */
	public void stopJump(){
		if(!this.isJumping()) return;
		this.jumpingForce = this.setForce(FORCE_NAME_JUMPING, 0, 0);
		this.jumping = false;
		this.stoppingJump = true;
	}
	
	@Override
	public void touchFloor(Material m){
		super.touchFloor(m);
		this.jumpingForce = this.setForce(FORCE_NAME_JUMPING, 0, 0);
		this.jumping = false;
		this.wallJumpAvailable = true;
	}
	
	@Override
	public void leaveFloor(){
		super.leaveFloor();
		
		// Any jump that was being built up is no longer being built up after leaving the floor
		this.cancelJump();
	}
	
	@Override
	public void leaveWall(){
		super.leaveWall();
		
		this.cancelJump();
	}
	
	/** @return true if this {@link MobThing} jumps instantly, false if it has to build up a jump */
	public boolean jumpsAreInstant(){
		return this.jumpBuildTime == 0;
	}
	
	/** @return See {@link #jumpPower} */
	public double getJumpPower(){
		return this.jumpPower;
	}
	
	/** @param jumpPower See {@link #jumpPower} */
	public void setJumpPower(double jumpPower){
		this.jumpPower = jumpPower;
	}
	
	/** @return See {@link #jumpStopPower} */
	public double getJumpStopPower(){
		return this.jumpStopPower;
	}
	
	/** @param jumpStopPower See {@link #jumpStopPower} */
	public void setJumpStopPower(double jumpStopPower){
		this.jumpStopPower = jumpStopPower;
	}
	
	/** @return See {@link #canStopJump} */
	public boolean isCanStopJump(){
		return this.canStopJump;
	}
	
	/** @param canStopJump See {@link #canStopJump} */
	public void setCanStopJump(boolean canStopJump){
		this.canStopJump = canStopJump;
	}
	
	/** @return See {@link #jumpBuildTime} */
	public double getJumpBuildTime(){
		return this.jumpBuildTime;
	}
	
	/** @param jumpBuildTime See {@link #jumpBuildTime} */
	public void setJumpBuildTime(double jumpBuildTime){
		this.jumpBuildTime = jumpBuildTime;
	}
	
	/** @return See {@link #jumpAfterBuildUp} */
	public boolean isJumpAfterBuildUp(){
		return this.jumpAfterBuildUp;
	}
	
	/** @param jumpAfterBuildUp See {@link #jumpAfterBuildUp} */
	public void setJumpAfterBuildUp(boolean jumpAfterBuildUp){
		this.jumpAfterBuildUp = jumpAfterBuildUp;
	}
	
	/** @return See {@link #walkAcceleration} */
	public double getWalkAcceleration(){
		return this.walkAcceleration;
	}
	
	/** @param walkAcceleration See {@link #walkAcceleration} */
	public void setWalkAcceleration(double walkAcceleration){
		this.walkAcceleration = walkAcceleration;
	}
	
	/** @return See {@link #walkSpeedMax} */
	public double getWalkSpeedMax(){
		return this.walkSpeedMax;
	}
	
	/** @param walkSpeedMax See {@link #walkSpeedMax} */
	public void setWalkSpeedMax(double walkSpeedMax){
		this.walkSpeedMax = walkSpeedMax;
	}
	
	/** @return See {@link #walkAirControl} */
	public double getWalkAirControl(){
		return this.walkAirControl;
	}
	
	/** @param walkAirControl See {@link #walkAirControl} */
	public void setWalkAirControl(double walkAirControl){
		this.walkAirControl = walkAirControl;
	}
	
	/** @return See {@link #walkFriction} */
	public double getWalkFriction(){
		return walkFriction;
	}
	
	/** @param walkFriction See {@link #walkFriction} */
	public void setWalkFriction(double walkFriction){
		this.walkFriction = walkFriction;
	}
	
	/** @return See {@link #walkStopFriction} */
	public double getWalkStopFriction(){
		return walkStopFriction;
	}
	
	/** @param walkStopFriction See {@link #walkStopFriction} */
	public void setWalkStopFriction(double walkStopFriction){
		this.walkStopFriction = walkStopFriction;
	}
	
	/** @return See {@link #canWallJump} */
	public boolean isCanWallJump(){
		return this.canWallJump;
	}
	
	/** @param canWallJump See {@link #canWallJump} */
	public void setCanWallJump(boolean canWallJump){
		this.canWallJump = canWallJump;
	}
	
	/** @return See {@link #walkingForce} */
	public ZVector getWalkingForce(){
		return this.walkingForce;
	}
	
	/** @param movement The amount of force applied to the x axis when this mob is walking */
	public void setWalkingForce(double movement){
		this.walkingForce = this.setForce(FORCE_NAME_WALKING, movement, 0);
	}
	
}
