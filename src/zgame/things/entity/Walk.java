package zgame.things.entity;

import zgame.core.Game;
import zgame.physics.ZVector;
import zgame.physics.material.Material;

/** A class that handles an {@link EntityThing} moving by walking and jumping */
public class Walk{
	
	/** The string used to identify the force used to make {@link #thing} walk */
	public static final String FORCE_NAME_WALKING = "walking";
	/** The string used to identify the force used to make {@link #thing} jump */
	public static final String FORCE_NAME_JUMPING = "jumping";
	/** The string used to identify the force used to make {@link #thing} stop jumping */
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
	/** The default value of {@link #walking} */
	public static final boolean DEFAULT_WALKING = false;
	/** The default value of {@link #walkingRatio} */
	public static final double DEFAULT_WALKING_RATIO = 0.5;
	/** The default value of {@link #canWallJump} */
	public static final boolean DEFAULT_CAN_WALL_JUMP = false;
	/** The default value of {@link #normalJumpTime} */
	public static final double DEFAULT_NORMAL_JUMP_TIME = .1;
	/** The default value of {@link #wallJumpTime} */
	public static final double DEFAULT_WALL_JUMP_TIME = .25;
	
	/**
	 * The magnitude of how much a mob can jump in units of momentum, i.e. mass * velocity,
	 * i.e, higher mass makes for lower jumps, lower mass makes for higher jumps
	 */
	private double jumpPower;
	
	/** In the same units as {@link #jumpPower}, the power at which {@link #thing} is able to stop jumping while in the air */
	private double jumpStopPower;
	
	/** true if this mob has the ability to stop jumping while it's in the air, false otherwise */
	private boolean canStopJump;
	
	/** The amount of time, in seconds, it takes the mob to build up to a full jump, use 0 to make jumping instant */
	private double jumpBuildTime;
	
	/** true if after building up a jump to max power, the mob should immediately jump, false to make it that it has to wait to jump */
	private boolean jumpAfterBuildUp;
	
	/** The acceleration of {@link #thing} while walking, i.e., how fast it gets to #walkSpeedMax */
	private double walkAcceleration;
	
	/** The maximum walking speed of {@link #thing} */
	private double walkSpeedMax;
	
	/** The ratio of speed {@link #thing} can use to walk when it is airborne, i.e. not on the ground */
	private double walkAirControl;
	
	/**
	 * The frictional constant used to slow down {@link #thing} when it is trying to move.
	 * This value represents the amount of the surface's friction which is applied.
	 * Zero means no friction is applied while walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction
	 * Generally should be 1.
	 */
	private double walkFriction;
	
	/**
	 * The frictional constant used to slow down {@link #thing} when it is trying to stop moving
	 * This value represents the amount of the surface's friction which is applied.
	 * Zero means no friction is applied while not walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction.
	 * Use a high value to make stopping walking happen quickly, use a low value to make stopping walking slow, and use zero to make it impossible to stop.
	 * This friction only applies while on the ground. A value of 1 is used while in the air, regardless of if this MobThing is walking or not.
	 */
	private double walkStopFriction;

	/** true if {@link #thing} is currently running, false for walking */
	private boolean walking;

	/** The percentage speed {@link #thing} should move at while walking instead of running. i.e. 0.5 = 50% */
	private double walkingRatio;
	
	/** true if {@link #thing} can jump off walls while touching one, otherwise, false */
	private boolean canWallJump;
	
	/** The amount of time, in seconds, after touching the ground that {@link #thing} has to jump. -1 to make jumping only allowed while touching the ground */
	private double normalJumpTime;
	
	/** The amount of time, in seconds, after touching a wall that {@link #thing} has to jump. -1 to make jumping only allowed while touching a wall */
	private double wallJumpTime;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** true if {@link #thing} is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	/** true if this mob is currently jumping, false otherwise */
	private boolean jumping;
	
	/** true if this mob is able to wall jump, i.e. it has touched the ground since its last wall jump */
	private boolean wallJumpAvailable;
	
	/** The force of jumping on {@link #thing} */
	private ZVector jumpingForce;
	
	/** The amount of time, in seconds, {@link #thing} has built up their jump height */
	private double jumpTimeBuilt;
	
	/** true if {@link #thing} is currently building up a jump, false otherwise */
	private boolean buildingJump;
	
	/** true if {@link #thing} is currently stopping its jump, false otherwise */
	private boolean stoppingJump;
	
	/**
	 * The force used to make you stop jumping. Physics wise doesn't make any sense, but it's to give an option to control jump height by holding down or letting go of a jump
	 * button
	 */
	private ZVector jumpingStopForce;
	
	/** The vector keeping track of the force of {@link #thing} walking */
	private ZVector walkingForce;
	
	/** The direction {@link #thing} is walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	private int walkingDirection;
	
	/** The {@link EntityThing} that uses this {@link Walk} */
	private final EntityThing thing;
	
	/**
	 * Make a new {@link Walk} with the given entity
	 * @param thing See {@link #thing}
	 */
	public Walk(EntityThing thing){
		this.thing = thing;
		
		this.canJump = false;
		this.jumping = false;
		this.stoppingJump = false;
		this.jumpTimeBuilt = 0;
		this.wallJumpAvailable = false;
		
		this.stopWalking();
		
		this.jumpingForce = new ZVector();
		thing.setForce(FORCE_NAME_JUMPING, this.jumpingForce);
		this.jumpingStopForce = new ZVector();
		thing.setForce(FORCE_NAME_JUMPING_STOP, this.jumpingStopForce);
		
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
		this.walking = DEFAULT_WALKING;
		this.walkingRatio = DEFAULT_WALKING_RATIO;
		this.canWallJump = DEFAULT_CAN_WALL_JUMP;
		this.normalJumpTime = DEFAULT_NORMAL_JUMP_TIME;
		this.wallJumpTime = DEFAULT_WALL_JUMP_TIME;
		
		this.walkingForce = new ZVector();
		thing.setForce(FORCE_NAME_WALKING, this.walkingForce);
	}
	
	/** @return See {@link #thing} */
	public EntityThing getThing(){
		return this.thing;
	}
	
	/**
	 * Perform the game update actions handling {@link #thing}'s walking and jumping
	 * @param game The {@link Game} where {@link #thing} is in
	 * @param dt The amount of time that passed in the update
	 */
	public void tick(Game game, double dt){
		// Determine the new walking force
		this.updateWalkForce(dt);
		
		// Update the state of the jumping force
		this.updateJumpState(dt);
	}
	
	/** @return true if this object represents currently walking or running, false otherwise */
	public boolean isTryingToMove(){
		return this.walkingDirection != 0;
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
	 * Update the value of {@link #walkingForce} based on the current state of {@link #thing}
	 *
	 * @param dt The amount of time that will pass in the next tick when{@link #thing} walks
	 */
	public void updateWalkForce(double dt){
		var entity = this.getThing();
		// First handle mob movement
		double mass = entity.getMass();
		double acceleration = this.getWalkAcceleration();
		double walkForce = acceleration * mass * this.getWalkingDirection();
		boolean walking = walkForce != 0;
		double maxSpeed = this.getWalkSpeedMax();
		
		// If the thing is walking, its max speed should be reduced by the ratio
		if(this.isWalking()) maxSpeed *= this.getWalkingRatio();
		
		// If the mob is not on the ground, it's movement force is modified by the air control
		if(!entity.isOnGround()) walkForce *= this.getWalkAirControl();
		
		// Only check the walking speed if there is any walking force
		if(walking){
			double vx = entity.getVX();
			// Find the total velocity if the new walking force is applied on the next tick
			double sign = walkForce;
			double newVel = vx + walkForce * dt / mass;
			
			/*
			 * issue#14 fix this, it's not always setting it to the exact max speed, usually slightly below it, probably related to the friction issue
			 * This probably needs to account for the change in frictional force
			 * Why can you still move a bit after landing on a high friction force until you stop moving?
			 */
			
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
	
	/** @return true if {@link #thing} is able to perform a normal jump off the ground based on the amount of time since it last touched a wall, false otherwise */
	public boolean hasTimeToFloorJump(){
		var entity = this.getThing();
		if(this.getNormalJumpTime() == -1) return entity.getGroundTime() == -1;
		return entity.getGroundTime() <= this.getNormalJumpTime();
	}
	
	/** @return true if this {@link #thing} is able to perform a wall jump based on the amount of time since it last touched a wall, false otherwise */
	public boolean hasTimeToWallJump(){
		var entity = this.getThing();
		if(this.getWallJumpTime() == -1) return entity.getWallTime() == -1;
		return entity.getWallTime() <= this.getWallJumpTime();
	}
	
	/**
	 * A utility method that handles a simple implementation of moving using keyboard controls
	 * @param moveLeft true if movement should be to the left, false otherwise
	 * @param moveRight true if movement should be to the right, false otherwise
	 * @param jump true if jumping should occur, false otherwise
	 * @param dt The amount of time that passed during this instance of time
	 */
	public void handleMovementControls(boolean moveLeft, boolean moveRight, boolean jump, double dt){
		// Move left and right
		if(moveLeft) this.walkLeft();
		else if(moveRight) this.walkRight();
		else this.stopWalking();
		
		// Jump if holding the jump button
		if(jump) this.jump(dt);
			// For not holding the button
		else{
			// if jumps should be instant, or no jump time is being built up, then stop the jump
			if(this.jumpsAreInstant() || this.getJumpTimeBuilt() == 0) this.stopJump();
				// Otherwise, perform the built up jump
			else this.jumpFromBuiltUp(dt);
			
		}
	}
	
	/** Remove any jump time built up */
	public void cancelJump(){
		this.buildingJump = false;
		this.jumpTimeBuilt = 0;
	}
	
	/**
	 * Update the value of {@link #jumpingForce} and {@link #jumpingStopForce} based on the current state of {@link #thing}
	 *
	 * @param dt The amount of time, in seconds, that will pass in the next tick when {@link #thing} stops jumping
	 */
	public void updateJumpState(double dt){
		// The mob can jump if it's on the ground, or if it can wall jump and is on a wall
		this.canJump = this.hasTimeToFloorJump() || this.isCanWallJump() && this.hasTimeToWallJump() && this.isWallJumpAvailable();
		
		// If building a jump, and able to jump, then add the time
		if(this.isBuildingJump() && this.isCanJump()){
			this.jumpTimeBuilt += dt;
			// If the jump time threshold has been met, and this mob is set to jump right away, then perform the jump now
			if(this.getJumpTimeBuilt() >= this.getJumpBuildTime() && this.isJumpAfterBuildUp()) this.jumpFromBuiltUp(dt);
		}
		if(this.isStoppingJump()){
			// Can only stop jumping if it's allowed
			if(!this.isCanStopJump()) return;
			var entity = this.getThing();
			// Only need to stop jumping if the mob is moving up
			double vy = entity.getVY();
			if(vy < 0){
				double mass = entity.getMass();
				double newStopJumpVel = this.getJumpStopPower() / mass;
				double newStopJumpForce = this.getJumpStopPower() / dt;
				// If the jump force would add extra velocity making its total velocity downwards,
				// then the jump stop force should be such that the y velocity will be 0 on the next tick
				if(vy + newStopJumpVel > 0) newStopJumpForce = -vy * mass / dt;
				
				this.jumpingStopForce = entity.setForce(FORCE_NAME_JUMPING_STOP, 0, newStopJumpForce);
			}
			// Otherwise it is no longer stopping its jump, so remove the stopping force amount
			else{
				this.stoppingJump = false;
				this.jumpingStopForce = entity.setForce(FORCE_NAME_JUMPING_STOP, 0, 0);
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
		var entity = this.getThing();
		
		this.jumping = true;
		
		// If this mob is on a wall and not on the ground, this counts a wall jump
		if(!this.hasTimeToFloorJump() && this.hasTimeToWallJump()) this.wallJumpAvailable = false;
		
		// The jump power is either itself if jumping is instant, or multiplied by the ratio of jump time built and the total time to build a jump, keeping it at most 1
		double power = this.jumpPower * (this.jumpsAreInstant() ? 1 : Math.min(1, this.getJumpTimeBuilt() / this.getJumpBuildTime()));
		double jumpAmount = -power / dt;
		
		// If falling downwards, add additional force so that the jump force will counteract the current downwards force
		double vy = entity.getVY();
		if(vy > 0) jumpAmount -= vy / dt * entity.getMass();
		
		this.jumpingForce = entity.setForce(FORCE_NAME_JUMPING, 0, jumpAmount);
		this.jumpTimeBuilt = 0;
		this.buildingJump = false;
	}
	
	/** Cause this {@link #thing} to stop jumping. Does nothing if the mob is not currently jumping */
	public void stopJump(){
		if(!this.isJumping()) return;
		this.jumpingForce = this.getThing().setForce(FORCE_NAME_JUMPING, 0, 0);
		this.jumping = false;
		this.stoppingJump = true;
	}
	
	/** @return true if {@link #thing} jumps instantly, false if it has to build up a jump */
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
		// Walk acceleration cannot go below 0
		this.walkAcceleration = Math.max(0, walkAcceleration);
	}
	
	/** @return See {@link #walkSpeedMax} */
	public double getWalkSpeedMax(){
		return this.walkSpeedMax;
	}
	
	/** @param walkSpeedMax See {@link #walkSpeedMax} */
	public void setWalkSpeedMax(double walkSpeedMax){
		// Walk speed cannot go below 0
		this.walkSpeedMax = Math.max(0, walkSpeedMax);
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
		// Friction cannot go below 0
		this.walkStopFriction = Math.max(0, walkStopFriction);
	}
	
	/** @return See {@link #walking} */
	public boolean isWalking(){
		return this.walking;
	}
	
	/** @param walking See {@link #walking} */
	public void setWalking(boolean walking){
		this.walking = walking;
	}
	
	/** toggle the state of {@link #walking} */
	public void toggleWalking(){
		this.setWalking(!this.isWalking());
	}
	
	/** @return See {@link #walkingRatio} */
	public double getWalkingRatio(){
		return this.walkingRatio;
	}
	
	/** @param walkingRatio See {@link #walkingRatio} */
	public void setWalkingRatio(double walkingRatio){
		this.walkingRatio = walkingRatio;
	}
	
	/** @return See {@link #canWallJump} */
	public boolean isCanWallJump(){
		return this.canWallJump;
	}
	
	/** @param canWallJump See {@link #canWallJump} */
	public void setCanWallJump(boolean canWallJump){
		this.canWallJump = canWallJump;
	}
	
	/** @return See {@link #normalJumpTime} */
	public double getNormalJumpTime(){
		return this.normalJumpTime;
	}
	
	/** @param normalJumpTime See {@link #normalJumpTime} */
	public void setNormalJumpTime(double normalJumpTime){
		this.normalJumpTime = normalJumpTime;
	}
	
	/** @return See {@link #wallJumpTime} */
	public double getWallJumpTime(){
		return this.wallJumpTime;
	}
	
	/** @param wallJumpTime See {@link #wallJumpTime} */
	public void setWallJumpTime(double wallJumpTime){
		this.wallJumpTime = wallJumpTime;
	}
	
	/** @return See {@link #walkingForce} */
	public ZVector getWalkingForce(){
		return this.walkingForce;
	}
	
	/** @param movement The amount of force applied to the x axis when this mob is walking */
	public void setWalkingForce(double movement){
		this.walkingForce = this.getThing().setForce(FORCE_NAME_WALKING, movement, 0);
	}
	
	/**
	 * Update the position and velocity of {@link #thing} based on its current forces and velocity
	 *
	 * @param game The {@link Game} where the update takes place
	 * @param dt The amount of time, in seconds, which passed in the tick where this update took place
	 */
	public void updatePosition(Game game, double dt){
		// After doing the normal tick and update with the mob's position and velocity and adding the jump velocity, reset the jump force to 0
		this.jumpingForce = this.getThing().setForce(FORCE_NAME_JUMPING, 0, 0);
	}
	
	/** This method should be called when the associated entity touches a floor */
	public void touchFloor(Material m){
		this.jumpingForce = this.getThing().setForce(FORCE_NAME_JUMPING, 0, 0);
		this.jumping = false;
		this.wallJumpAvailable = true;
	}
	
	/** This method should be called when the associated entity leaves a floor */
	public void leaveFloor(){
		// Any jump that was being built up is no longer being built up after leaving the floor
		this.cancelJump();
	}
	
	/** This method should be called when the associated entity leaves a wall */
	public void leaveWall(){
		this.cancelJump();
	}
	
	/** @return The friction constant that this thing should have, based on its current walking state */
	public double getFrictionConstant(){
		var entity = this.getThing();
		// If not on the ground, use the normal amount of friction, otherwise, if currently walking, return walk friction, otherwise, return stop friction
		return !entity.isOnGround() ? 1 : (this.getWalkingDirection() != 0) ? this.getWalkFriction() : this.getWalkStopFriction();
	}
	
}
