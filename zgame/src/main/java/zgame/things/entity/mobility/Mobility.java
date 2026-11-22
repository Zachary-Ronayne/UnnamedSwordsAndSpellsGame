package zgame.things.entity.mobility;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector;
import zgame.things.entity.MobilityState;

/**
 * The base interface for defining how things move, walk, fly, etc
 *
 * @param <V> The type of vector which uses this class
 */
public interface Mobility<V extends ZVector<V>>{
	
	/** @return The {@link MobilityState} object holding the data for this interface */
	MobilityState<V> getMobilityState();
	
	/**
	 * Perform the game update actions handling mobility related actions
	 */
	default void mobilityTick(){
		this.getMobilityState().getType().tick(this);
	}
	
	/**
	 * Perform all actions needed to happen in a tick to make this thing walk
	 */
	default void walkingTick(){
		// After doing the normal tick and update with this entity's position and velocity and adding the jump velocity, reset the jump force to 0
		this.getMobilityState().clearForce(MobilityState.FORCE_JUMPING);
		
		// Determine the new walking force
		this.updateWalkForce();
		
		// Update the state of the jumping force
		this.updateJumpState();
	}
	
	/** @return The mass of the thing using this mobility */
	double getMass();
	
	/**
	 * Perform all actions needed to happen in a tick to make this thing fly
	 */
	default void flyingTick(){
		this.updateFlyForce();
	}
	
	/** @return true if this object represents currently walking or running, false otherwise i.e. not moving */
	boolean isTryingToMove();
	
	/** Tell this entity to stop walking */
	default void stopWalking(){
		this.getMobilityState().clearForce(MobilityState.FORCE_WALKING);
	}
	
	/**
	 * Calculate and then update the current walking force based on the next instance of time
	 */
	default void updateWalkForce(){
		var state = this.getMobilityState();
		double dt = state.getTickTime();
		
		// issue#55 fix having too much control of movement while in the air, changing walking direction should decelerate and accelerate
		double mass = this.getMass();
		double walkForce = this.getWalkPower() / dt;
		// See if trying to walk before doing any modifications to the walk force
		boolean walking = walkForce != 0;
		
		double maxSpeed = this.getWalkSpeedMax();
		// If the thing is walking, its max speed should be multiplied by the ratio
		if(this.isSprinting()) maxSpeed *= this.getSprintingRatio();
		
		// If the current velocity is greater than the max speed, and entity is trying to walk in the same direction as the current velocity, walk force will always be zero
		var currentVel = state.getVelocity();
		var currentVelMag = currentVel.getHorizontal();
		double tryRatio = this.getMobilityTryingRatio();
		if(currentVelMag > maxSpeed && walkForce > 0 && tryRatio > 0) {
			walkForce = 0;
		}
		
		// If the entity is not on the ground, it's movement force is modified by the air control
		if(!state.isOnGround()) walkForce *= this.getWalkAirControl();
		
		// Only check the walking speed if there is any walking force
		if(walking && this.isTryingToMove()){
			// Find the total velocity if the new walking force is applied on the next tick
			double newVel = currentVelMag + walkForce * dt / mass;
			
			// If at or above max speed, just set the angle, and apply no force
			if(currentVelMag >= maxSpeed){
				walkForce = 0;
				// TODO figure out how this should work formally in a state system
				state.attemptSetVelocity(this.createTryingToMoveVectorHorizontal(currentVelMag).modifyVerticalValue(currentVel.getVerticalValue()));
			}
			// If the new velocity would exceed or meet the maximum speed, hard set the velocity and angle, and apply no force
			else if(Math.abs(newVel) >= maxSpeed){
				walkForce = 0;
				// TODO figure out how this should work formally in a state system
				state.attemptSetVelocity(this.createTryingToMoveVectorHorizontal(maxSpeed).modifyVerticalValue(currentVel.getVerticalValue()));
			}
		}
		
		// Set the amount the entity is walking
		this.applyWalkForce(walkForce);
	}
	
	/**
	 * Apply the given amount of force to this thing's walking force
	 *
	 * @param newWalkForce The amount of force to apply in the forwards direction of this thing
	 */
	void applyWalkForce(double newWalkForce);
	
	/**
	 * Calculate and then update the current flying force based on the next instance of time
	 */
	default void updateFlyForce(){
		var state = this.getMobilityState();
		var dt = state.getTickTime();
		
		var currentVel = state.getVelocity();
		
		double mass = this.getMass();
		double newFlyForce = this.getFlyPower() / dt;
		double maxSpeed = this.getFlySpeedMax();
		double currentVelMag = currentVel.getMagnitude();
		
		boolean tryingToMove = this.isTryingToMove();
		
		// Find the additional velocity if the new flying force is applied on the next tick
		double initialFlyForceVel = newFlyForce * dt / mass;
		
		// If trying to move, then move forward based on the ratio to facing forward
		if(tryingToMove){
			// If at or above max speed, just set the angle, apply no force
			if(currentVelMag >= maxSpeed){
				newFlyForce = 0;
				// TODO figure out how this should work formally in a state system
				state.attemptSetVelocity(this.createTryingToMoveVector(currentVelMag));
			}
			// If the new velocity would exceed or meet the maximum speed, hard set the velocity and angle, and apply no force
			else if(Math.abs(currentVelMag + initialFlyForceVel) >= maxSpeed){
				newFlyForce = 0;
				// TODO figure out how this should work formally in a state system
				state.attemptSetVelocity(this.createTryingToMoveVector(maxSpeed));
			}
			// Otherwise, just apply the already calculated full amount for newFlyForce
		}
		// Otherwise, if moving and not trying to move, try to slow down
		else if(currentVelMag > 0){
			double flyStopPower = this.getFlyStopPower();
			double stopVel = currentVelMag - (flyStopPower / mass);
			
			// If applying the force would move the velocity below 0, then hard set velocity to 0 and apply no force
			if(stopVel < 0){
				state.clearVelocity();
				newFlyForce = 0;
			}
			else{
				// When trying to slow down, must go in the opposite direction
				newFlyForce = -flyStopPower / dt;
			}
		}
		// Otherwise, apply zero force
		else newFlyForce = 0;
		
		// Apply the force
		this.applyFlyForce(newFlyForce, tryingToMove);
	}
	
	/**
	 * Create a vector with the given magnitude, and in the direction this thing is trying to move in
	 *
	 * @param magnitude The magnitude
	 * @return The vector
	 */
	V createTryingToMoveVector(double magnitude);
	
	/**
	 * Create a vector with the given magnitude, and in the direction this thing is trying to move in, exclusively on the horizontal axis
	 *
	 * @param magnitude The magnitude
	 * @return The vector
	 */
	V createTryingToMoveVectorHorizontal(double magnitude);
	
	/**
	 * Apply the given amount of force to this thing's flying force
	 *
	 * @param newFlyForce The amount of force to apply in the direction this thing is flying. Negative means move in the opposite direction
	 * @param applyFacing true if the force should be applied in the direction the thing is facing, false to apply it in the direction it's moving
	 */
	void applyFlyForce(double newFlyForce, boolean applyFacing);
	
	/**
	 * @return A value in the range [-1, 1] representing how close the angle of movement is to the angle of desired movement.
	 * 		A value of 1 means movement is trying to happen in the exact same direction as current movement, -1 means the exact opposite direction
	 */
	double getMobilityTryingRatio();
	
	/** @return true if able to perform a normal jump off the ground based on the amount of time since it last touched a wall, false otherwise */
	default boolean hasTimeToFloorJump(){
		var state = this.getMobilityState();
		if(this.getNormalJumpTime() == -1) return state.getGroundTime() == -1;
		return state.getGroundTime() <= this.getNormalJumpTime();
	}
	
	/** @return true if able to perform a wall jump based on the amount of time since it last touched a wall, false otherwise */
	default boolean hasTimeToWallJump(){
		var state = this.getMobilityState();
		if(this.getWallJumpTime() == -1) return state.getWallTime() == -1;
		return state.getWallTime() <= this.getWallJumpTime();
	}
	
	/** Remove any jump time built up */
	default void cancelJump(){
		var mobilityData = this.getMobilityState();
		mobilityData.setBuildingJump(false);
		mobilityData.setJumpTimeBuilt(0);
	}
	
	/**
	 * Update the value of the jumping force
	 */
	default void updateJumpState(){
		var mobilityData = this.getMobilityState();
		double dt = mobilityData.getTickTime();
		
		// This entity can jump if it's on the ground, or if it can wall jump and is on a wall
		mobilityData.setCanJump(
				(this.hasTimeToFloorJump() && mobilityData.isGroundedSinceLastJump()) || (this.isCanWallJump() && this.hasTimeToWallJump() && mobilityData.isWallJumpAvailable()));
		
		// If building a jump, and able to jump, then add the time
		if(mobilityData.isBuildingJump() && mobilityData.isCanJump()){
			mobilityData.addJumpTimeBuilt(dt);
			// If the jump time threshold has been met, and this entity is set to jump right away, then perform the jump now
			if(mobilityData.getJumpTimeBuilt() >= this.getJumpBuildTime() && this.isJumpAfterBuildUp()) this.jumpFromBuiltUp(dt);
		}
		if(mobilityData.isStoppingJump()){
			// Can only stop jumping if it's allowed
			if(!this.isCanStopJump()) return;
			var state = this.getMobilityState();
			// Only need to stop jumping if this entity is moving up
			double vy = state.getVelocity().getVertical();
			boolean invert = this.jumpingInverted();
			if(invert && vy < 0 || !invert && vy > 0){
				double mass = this.getMass();
				double power = this.getJumpStopPower();
				double newStopJumpVel = power / mass;
				double newStopJumpForce = -power / dt;
				
				/*
				 If the jump force would adjust extra velocity making its total velocity downwards,
				 apply no force and set vertical velocity to 0
				 */
				if(!invert && vy > newStopJumpVel || invert && vy < newStopJumpVel){
					mobilityData.clearForce(MobilityState.FORCE_JUMPING);
					state.clearVelocityVertical();
					return;
				}
				
				// Account for inverted jumping axis
				if(invert) newStopJumpForce = -newStopJumpForce;
				
				mobilityData.attemptSetVerticalForce(MobilityState.FORCE_JUMPING, newStopJumpForce);
			}
			// Otherwise it is no longer stopping its jump, so remove the stopping force amount
			else{
				mobilityData.setStoppingJump(false);
				mobilityData.clearForce(MobilityState.FORCE_JUMPING);
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
		var mobilityData = this.getMobilityState();
		if(!mobilityData.isCanJump() || mobilityData.getJumpTimeBuilt() > 0 || mobilityData.isJumping()) return false;
		
		// If it takes no time to jump, jump right away
		if(this.jumpsAreInstant()){
			this.jumpFromBuiltUp(dt);
		}
		// Otherwise, start building up a jump
		else mobilityData.setBuildingJump(true);
		return true;
	}
	
	/**
	 * Cause this entity to instantly jump with the currently built power, only if it is allowed to jump
	 *
	 * @param dt The amount of time, in seconds, that will pass in one tick after the entity jumps off the ground
	 */
	default void jumpFromBuiltUp(double dt){
		var mobilityData = this.getMobilityState();
		
		if(!mobilityData.isCanJump()) return;
		
		mobilityData.setJumping(true);
		mobilityData.setGroundedSinceLastJump(false);
		
		// If this entity is on a wall and not on the ground, this counts a wall jump
		if(!this.hasTimeToFloorJump() && this.hasTimeToWallJump()) mobilityData.setWallJumpAvailable(false);
		
		// The jump power is either itself if jumping is instant, or multiplied by the ratio of jump time built and the total time to build a jump, keeping it at most 1
		double power = this.getJumpPower() * (this.jumpsAreInstant() ? 1 : Math.min(1, mobilityData.getJumpTimeBuilt() / this.getJumpBuildTime()));
		double jumpAmount = power / dt;
		boolean invert = this.jumpingInverted();
		if(invert) jumpAmount = -jumpAmount;
		
		// If falling downwards, add additional force so that the jump force will counteract the current downwards force
		var state = this.getMobilityState();
		double vy = state.getVelocity().getVertical();
		double mass = this.getMass();
		if(invert && vy > 0 || !invert && vy < 0){
			double adjust = vy / dt * mass;
			if(invert) jumpAmount += adjust;
			else jumpAmount -= adjust;
		}
		// If the current velocity exceeds the velocity a jump will provide, then apply no force
		double velocityForce = Math.abs(mass * vy / dt);
		if(ZMath.sameSign(vy, jumpAmount) && velocityForce > Math.abs(jumpAmount)) {
			jumpAmount = 0;
		}
		
		mobilityData.attemptSetVerticalForce(MobilityState.FORCE_JUMPING, jumpAmount);
		mobilityData.setJumpTimeBuilt(0);
		mobilityData.setBuildingJump(false);
	}
	
	/** Stop all jumping. Does nothing if not currently jumping */
	default void stopJump(){
		var mobilityData = this.getMobilityState();
		
		if(!mobilityData.isJumping()) return;
		mobilityData.clearForce(MobilityState.FORCE_JUMPING);
		mobilityData.setJumping(false);
		mobilityData.setStoppingJump(true);
	}
	
	/**
	 * Attempt to jump or stop the jump, essentially, a "release the jump button" method
	 *
	 * @param dt The amount of time passing in the game tick where the button was pressed
	 */
	default void checkPerformOrStopJump(double dt){
		// if jumps should be instant, or no jump time is being built up, then stop the jump
		if(this.jumpsAreInstant() || this.getMobilityState().getJumpTimeBuilt() == 0){
			this.stopJump();
		}
		// Otherwise, perform the built up jump
		else this.jumpFromBuiltUp(dt);
	}
	
	/** @return true if jumping should decrease the y axis instead of increasing it */
	default boolean jumpingInverted(){
		return false;
	}
	
	/** @return true jumps are instant, false if jumps must be built up */
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
	 * 		Physics wise doesn't make any sense, but it's to give an option to control jump height by holding down or letting go of a jump button.
	 * 		Return 0 to disable
	 */
	double getJumpStopPower();
	
	/** @return true if this entity has the ability to stop jumping while it's in the air, false otherwise */
	default boolean isCanStopJump(){
		return this.getJumpStopPower() != 0;
	}
	
	/** @return The amount of time, in seconds, it takes this entity to build up to a full jump, use 0 to make jumping instant */
	double getJumpBuildTime();
	
	/** @return true if after building up a jump to max power, this entity should immediately jump, false to make it that it has to wait to jump */
	boolean isJumpAfterBuildUp();
	
	/** @return The power of this while walking i.e., how fast it gets to {@link #getWalkSpeedMax()}, in momentum units i.e. mass * velocity */
	double getWalkPower();
	
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
	
	/** @return The percentage speed this should move at while sprinting instead of normal walking, i.e. 1.5 = 150% of normal speed */
	double getSprintingRatio();
	
	/** @return true if this can jump off walls while touching one, otherwise, false */
	boolean isCanWallJump();
	
	/** @return The amount of time, in seconds, after touching the ground that this entity has to jump. -1 to make jumping only allowed while touching the ground */
	double getNormalJumpTime();
	
	/** @return The amount of time, in seconds, after touching a wall that this entity has to jump. -1 to make jumping only allowed while touching a wall */
	double getWallJumpTime();
	
	/** @return true if this is currently sprinting, false for normal movement speed */
	boolean isSprinting();
	
	/** @return The acceleration of this while flying, i.e., how fast it gets to {@link #getFlySpeedMax()}, defaults to {@link #getWalkPower()} */
	default double getFlyPower(){
		return this.getWalkPower();
	}
	
	/**
	 * @return The magnitude of how much this entity can stop its flying speed in units of momentum, i.e. mass * velocity
	 * 		Defaults to {@link #getFlyPower()}
	 */
	default double getFlyStopPower(){
		return this.getWalkPower();
	}
	
	/** @return The maximum walking speed of this entity thing, defaults to {@link #getWalkSpeedMax()} */
	default double getFlySpeedMax(){
		return this.getWalkSpeedMax();
	}
	
	/** This method should be called when the associated entity touches a floor */
	default void mobilityTouchFloor(){
		var mobilityData = this.getMobilityState();
		mobilityData.clearForce(MobilityState.FORCE_JUMPING);
		mobilityData.setJumping(false);
		mobilityData.setWallJumpAvailable(true);
	}
	
	/** This method should be called when the associated entity leaves a floor */
	default void mobilityLeaveFloor(){
		// Any jump that was being built up is no longer being built up after leaving the floor
		this.cancelJump();
	}
	
	/** This method should be called when the associated entity leaves a wall */
	default void mobilityLeaveWall(){
		// Any jump that was being built up is no longer being built up after leaving a wall
		this.cancelJump();
	}
	
	/** @return The friction constant that this thing should have, based on its current walking state */
	default double getWalkFrictionConstant(){
		var state = this.getMobilityState();
		// If not on the ground, use the normal amount of friction, otherwise, if currently trying to move, return walk friction, otherwise, return stop friction
		return !state.isOnGround() ? 1 : (this.isTryingToMove()) ? this.getWalkFriction() : this.getWalkStopFriction();
	}
	
}
