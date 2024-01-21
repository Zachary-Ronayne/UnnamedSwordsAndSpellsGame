package zgame.things.entity;

import zgame.core.Game;
import zgame.physics.material.Material;

/** A class that handles an {@link EntityThing} moving by walking and jumping */
public interface Movement2D{
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** @return The {@link EntityThing} using this object */
	default EntityThing getThing(){
		return this.getWalk().getThing();
	}
	
	/** @return The {@link Walk} object holding the data for this interface */
	Walk getWalk();
	
	/**
	 * Perform the game update actions handling {@link #getThing()}'s walking and jumping
	 *
	 * @param game The {@link Game} where {@link #getThing()} is in
	 * @param dt The amount of time that passed in the update
	 */
	default void movementTick(Game game, double dt){
		// Determine the new walking force
		this.updateWalkForce(dt);
		
		// Update the state of the jumping force
		this.updateJumpState(dt);
	}
	
	/** @return true if this object represents currently walking or running, false otherwise */
	default boolean isTryingToMove(){
		return this.getWalk().getWalkingDirection() != 0;
	}
	
	/** Tell this entity to start walking to the left */
	default void walkLeft(){
		this.getWalk().setWalkingDirection(-1);
	}
	
	/** Tell this entity to start walking to the right */
	default void walkRight(){
		this.getWalk().setWalkingDirection(1);
	}
	
	/** Tell this entity to stop walking */
	default void stopWalking(){
		this.getWalk().setWalkingDirection(0);
	}
	
	/**
	 * Update the value of {@link Walk#getWalkingForce()} based on the current state of {@link #getThing()}
	 *
	 * @param dt The amount of time that will pass in the next tick when{@link #getThing()} walks
	 */
	default void updateWalkForce(double dt){
		var walk = this.getWalk();
		
		var entity = this.getThing();
		// First handle entity movement
		double mass = entity.getMass();
		double acceleration = this.getWalkAcceleration();
		double walkForce = acceleration * mass * walk.getWalkingDirection();
		double maxSpeed = this.getWalkSpeedMax();
		// If the thing is walking, its max speed should be reduced by the ratio
		if(this.isWalking()) maxSpeed *= this.getWalkingRatio();
		
		// If the current velocity is greater than the max speed, and thing is trying to walk in the same direction as the current velocity, walk force will always be zero
		var vx = entity.getVX();
		if(vx > 0 && walkForce > 0 && vx > maxSpeed || vx < 0 && walkForce < 0 && vx < -maxSpeed){
			walkForce = 0;
		}
		
		boolean walking = walkForce != 0;
		
		// If the entity is not on the ground, it's movement force is modified by the air control
		if(!entity.isOnGround()) walkForce *= this.getWalkAirControl();
		
		// Only check the walking speed if there is any walking force
		if(walking){
			
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
		
		// Set the amount the entity is walking
		walk.setWalkingForce(walkForce);
	}
	
	/** @return true if {@link #getThing()} is able to perform a normal jump off the ground based on the amount of time since it last touched a wall, false otherwise */
	default boolean hasTimeToFloorJump(){
		var entity = this.getThing();
		if(this.getNormalJumpTime() == -1) return entity.getGroundTime() == -1;
		return entity.getGroundTime() <= this.getNormalJumpTime();
	}
	
	/** @return true if this {@link #getThing()} is able to perform a wall jump based on the amount of time since it last touched a wall, false otherwise */
	default boolean hasTimeToWallJump(){
		var entity = this.getThing();
		if(this.getWallJumpTime() == -1) return entity.getWallTime() == -1;
		return entity.getWallTime() <= this.getWallJumpTime();
	}
	
	/**
	 * A utility method that handles a simple implementation of moving using keyboard controls
	 *
	 * @param moveLeft true if movement should be to the left, false otherwise
	 * @param moveRight true if movement should be to the right, false otherwise
	 * @param jump true if jumping should occur, false otherwise
	 * @param dt The amount of time that passed during this instance of time
	 */
	default void handleMovementControls(boolean moveLeft, boolean moveRight, boolean jump, double dt){
		// Move left and right
		if(moveLeft) this.walkLeft();
		else if(moveRight) this.walkRight();
		else this.stopWalking();
		
		// Jump if holding the jump button
		if(jump){
			this.jump(dt);
		}
		// For not holding the button
		else{
			// if jumps should be instant, or no jump time is being built up, then stop the jump
			if(this.jumpsAreInstant() || this.getWalk().getJumpTimeBuilt() == 0){
				this.stopJump();
			}
			// Otherwise, perform the built up jump
			else this.jumpFromBuiltUp(dt);
			
		}
	}
	
	/** Remove any jump time built up */
	default void cancelJump(){
		var walk = this.getWalk();
		walk.setBuildingJump(false);
		walk.setJumpTimeBuilt(0);
	}
	
	/**
	 * Update the value of {@link Walk#jumpingForce} based on the current state of {@link #getThing()}
	 *
	 * @param dt The amount of time, in seconds, that will pass in the next tick when {@link #getThing()} stops jumping
	 */
	default void updateJumpState(double dt){
		var walk = this.getWalk();
		
		// This entity can jump if it's on the ground, or if it can wall jump and is on a wall
		walk.setCanJump(this.hasTimeToFloorJump() || this.isCanWallJump() && this.hasTimeToWallJump() && walk.isWallJumpAvailable());
		
		// If building a jump, and able to jump, then add the time
		if(walk.isBuildingJump() && walk.isCanJump()){
			walk.addJumpTimeBuilt(dt);
			// If the jump time threshold has been met, and this entity is set to jump right away, then perform the jump now
			if(walk.getJumpTimeBuilt() >= this.getJumpBuildTime() && this.isJumpAfterBuildUp()) this.jumpFromBuiltUp(dt);
		}
		if(walk.isStoppingJump()){
			// Can only stop jumping if it's allowed
			if(!this.isCanStopJump()) return;
			var entity = this.getThing();
			// Only need to stop jumping if this entity is moving up
			double vy = entity.getVY();
			if(vy < 0){
				double mass = entity.getMass();
				double newStopJumpVel = this.getJumpStopPower() / mass;
				double newStopJumpForce = this.getJumpStopPower() / dt;
				// If the jump force would add extra velocity making its total velocity downwards,
				// then the jump stop force should be such that the y velocity will be 0 on the next tick
				if(vy + newStopJumpVel > 0) newStopJumpForce = -vy * mass / dt;
				
				walk.setJumpingForce(newStopJumpForce);
			}
			// Otherwise it is no longer stopping its jump, so remove the stopping force amount
			else{
				walk.setStoppingJump(false);
				walk.setJumpingForce(0);
			}
		}
	}
	
	/**
	 * Cause this entity to start jumping or instantly jump if {@link #getJumpBuildTime} is 0, or to build up a jump if it is greater than zero.
	 * Only runs if the entity is in a position to jump, has not begun to build up jump time, and is not already jumping
	 *
	 * @param dt The amount of time, in seconds, that will pass in one tick after the entity jumps off the ground
	 * @return true if the jump occurred or started building up, false otherwise
	 */
	default boolean jump(double dt){
		var walk = this.getWalk();
		if(!walk.isCanJump() || walk.getJumpTimeBuilt() > 0 || walk.isJumping()) return false;
		
		// If it takes no time to jump, jump right away
		if(this.jumpsAreInstant()){
			this.jumpFromBuiltUp(dt);
		}
		// Otherwise, start building up a jump
		else walk.setBuildingJump(true);
		return true;
	}
	
	/**
	 * Cause this entity to instantly jump with the currently built power, only if it is allowed to jump
	 *
	 * @param dt The amount of time, in seconds, that will pass in one tick after the entity jumps off the ground
	 */
	default void jumpFromBuiltUp(double dt){
		var walk = this.getWalk();
		
		if(!walk.isCanJump()) return;
		var entity = this.getThing();
		
		walk.setJumping(true);
		
		// If this entity is on a wall and not on the ground, this counts a wall jump
		if(!this.hasTimeToFloorJump() && this.hasTimeToWallJump()) walk.setWallJumpAvailable(false);
		
		// The jump power is either itself if jumping is instant, or multiplied by the ratio of jump time built and the total time to build a jump, keeping it at most 1
		double power = this.getJumpPower() * (this.jumpsAreInstant() ? 1 : Math.min(1, walk.getJumpTimeBuilt() / this.getJumpBuildTime()));
		double jumpAmount = -power / dt;
		
		// If falling downwards, add additional force so that the jump force will counteract the current downwards force
		double vy = entity.getVY();
		if(vy > 0) jumpAmount -= vy / dt * entity.getMass();
		
		walk.setJumpingForce(jumpAmount);
		walk.setJumpTimeBuilt(0);
		walk.setBuildingJump(false);
	}
	
	// TODO fix being able to jump a second time while in the air because of normalJumpTime
	
	/** Cause this {@link #getThing()} to stop jumping. Does nothing if this entity is not currently jumping */
	default void stopJump(){
		var walk = this.getWalk();
		
		if(!walk.isJumping()) return;
		walk.setJumpingForce(0);
		walk.setJumping(false);
		walk.setStoppingJump(true);
	}
	
	/** @return true if {@link #getThing()} jumps instantly, false if it has to build up a jump */
	default boolean jumpsAreInstant(){
		return this.getJumpBuildTime() == 0;
	}
	
	/**
	 * @return The magnitude of how much this entity can jump in units of momentum, i.e. mass * velocity,
	 * 		i.e, higher mass makes for lower jumps, lower mass makes for higher jumps
	 */
	double getJumpPower();
	
	/**
	 * @return In the same units as {@link #getJumpPower}, the power at which this is able to stop jumping while in the air
	 * 		Physics wise doesn't make any sense, but it's to give an option to control jump height by holding down or letting go of a jump button
	 */
	double getJumpStopPower();
	
	/** @return true if this entity has the ability to stop jumping while it's in the air, false otherwise */
	boolean isCanStopJump();
	
	/** @return The amount of time, in seconds, it takes this entity to build up to a full jump, use 0 to make jumping instant */
	double getJumpBuildTime();
	
	/** @return true if after building up a jump to max power, this entity should immediately jump, false to make it that it has to wait to jump */
	boolean isJumpAfterBuildUp();
	
	/** @return The acceleration of this while walking, i.e., how fast it gets to {@link #getWalkSpeedMax()} */
	double getWalkAcceleration();
	
	/** @return The maximum walking speed of this entity thing */
	double getWalkSpeedMax();
	
	/** @return The ratio of speed this can use to walk when it is airborne, i.e. not on the ground */
	double getWalkAirControl();
	
	/**
	 * @return The frictional constant used to slow down this when it is trying to move.
	 * 		This value represents the amount of the surface's friction which is applied.
	 * 		Zero means no friction is applied while walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction
	 * 		Generally should be 1.
	 */
	double getWalkFriction();
	
	/**
	 * @return The frictional constant used to slow down this when it is trying to stop moving
	 * 		This value represents the amount of the surface's friction which is applied.
	 * 		Zero means no friction is applied while not walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction.
	 * 		Use a high value to make stopping walking happen quickly, use a low value to make stopping walking slow, and use zero to make it impossible to stop.
	 * 		This friction only applies while on the ground. A value of 1 is used while in the air, regardless of if this entity is walking or not.
	 */
	double getWalkStopFriction();
	
	/** @return The percentage speed this should move at while walking instead of running. i.e. 0.5 = 50% */
	double getWalkingRatio();
	
	/** @return true if this can jump off walls while touching one, otherwise, false */
	boolean isCanWallJump();
	
	/** @return The amount of time, in seconds, after touching the ground that this entity has to jump. -1 to make jumping only allowed while touching the ground */
	double getNormalJumpTime();
	
	/** @return The amount of time, in seconds, after touching a wall that this entity has to jump. -1 to make jumping only allowed while touching a wall */
	double getWallJumpTime();
	
	/** @return true if this is currently walking, false for running */
	boolean isWalking();
	
	/**
	 * Update the position and velocity of {@link #getThing()} based on its current forces and velocity
	 *
	 * @param game The {@link Game} where the update takes place
	 * @param dt The amount of time, in seconds, which passed in the tick where this update took place
	 */
	default void updateMovementPosVel(Game game, double dt){
		// After doing the normal tick and update with this entity's position and velocity and adding the jump velocity, reset the jump force to 0
		this.getWalk().setJumpingForce(0);
	}
	
	/** This method should be called when the associated entity touches a floor */
	default void movementTouchFloor(Material m){
		var walk = this.getWalk();
		walk.setJumpingForce(0);
		walk.setJumping(false);
		walk.setWallJumpAvailable(true);
	}
	
	/** This method should be called when the associated entity leaves a floor */
	default void movementLeaveFloor(){
		// Any jump that was being built up is no longer being built up after leaving the floor
		this.cancelJump();
	}
	
	/** This method should be called when the associated entity leaves a wall */
	default void movementLeaveWall(){
		this.cancelJump();
	}
	
	/** @return The friction constant that this thing should have, based on its current walking state */
	default double getWalkFrictionConstant(){
		var entity = this.getThing();
		// If not on the ground, use the normal amount of friction, otherwise, if currently walking, return walk friction, otherwise, return stop friction
		return !entity.isOnGround() ? 1 : (this.getWalk().getWalkingDirection() != 0) ? this.getWalkFriction() : this.getWalkStopFriction();
	}
	
}