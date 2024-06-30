package zgame.things.entity.mobility;

import zgame.core.Game;
import zgame.physics.ZVector;
import zgame.physics.material.Material;
import zgame.things.entity.EntityThing;
import zgame.things.entity.MobilityData;
import zgame.things.type.bounds.HitBox;
import zgame.world.Room;

/**
 * The base interface for defining how things move, walk, fly, etc
 *
 * @param <H> The type of hitbox which uses this class
 * @param <E> The type of entity which uses this class
 * @param <V> The type of vector which uses this class
 * @param <R> The type of room which E uses
 */
public interface Mobility<H extends HitBox<H>, E extends EntityThing<H, E, V, R>, V extends ZVector<V>, R extends Room<H, E, V, R>>{
	
	/** @return The {@link EntityThing} using this object */
	default E getThing(){
		return this.getMobilityData().getEntity();
	}
	
	/** @return The {@link MobilityData} object holding the data for this interface */
	MobilityData<H, E, V, R> getMobilityData();
	
	/**
	 * Perform the game update actions handling {@link #getThing()}'s walking and jumping
	 *
	 * @param game The {@link Game} where {@link #getThing()} is in
	 * @param dt The amount of time that passed in the update
	 */
	default void mobilityTick(Game game, double dt){
		var mobilityData = this.getMobilityData();
		// TODO is an enum with a switch statement the best way to do this?
		switch(mobilityData.getType()){
			case WALKING -> {
				// After doing the normal tick and update with this entity's position and velocity and adding the jump velocity, reset the jump force to 0
				this.getMobilityData().setJumpingForce(0);
				
				// Determine the new walking force
				this.updateWalkForce(dt);
				
				// Update the state of the jumping force
				this.updateJumpState(dt);
			}
			case FLYING, FLYING_AXIS -> this.updateFlyForce(dt);
		}
	}
	
	/** @return true if this object represents currently walking or running, false otherwise i.e. not moving */
	boolean isTryingToMove();
	
	/** @return The current speed that this thing is walking at */
	double getCurrentWalkingSpeed();
	
	/** Tell this entity to stop walking */
	void stopWalking();
	
	/**
	 * Calculate and then update the current walking force based on the next instance of time
	 *
	 * @param dt The amount of time that will pass in the next tick when{@link #getThing()} walks
	 */
	default void updateWalkForce(double dt){
		var entity = this.getThing();
		double mass = entity.getMass();
		double acceleration = this.getWalkAcceleration();
		double walkForce = acceleration * mass;
		double maxSpeed = this.getWalkSpeedMax();
		// If the thing is walking, its max speed should be reduced by the ratio
		if(this.isWalking()) maxSpeed *= this.getWalkingRatio();
		
		// If the current velocity is greater than the max speed, and thing is trying to walk in the same direction as the current velocity, walk force will always be zero
		var vx = Math.abs(this.getCurrentWalkingSpeed());
		if(vx > 0 && walkForce > 0 && vx > maxSpeed) walkForce = 0;
		
		boolean walking = walkForce != 0;
		
		// If the entity is not on the ground, it's movement force is modified by the air control
		if(!entity.isOnGround()) walkForce *= this.getWalkAirControl();
		
		// Only check the walking speed if there is any walking force
		if(walking){
			// Find the total velocity if the new walking force is applied on the next tick
			double newVel = vx + walkForce * dt / mass;
			
			/*
			 * issue#14 fix this, it's not always setting it to the exact max speed, usually slightly below it, probably related to the friction issue
			 * This probably needs to account for the change in frictional force
			 * Why can you still move a bit after landing on a high friction force until you stop moving?
			 */
			
			// If that velocity is greater than the maximum speed, then apply a force such that it will bring the velocity exactly to the maximum speed
			if(newVel > maxSpeed) walkForce = (maxSpeed - vx) / dt * mass;
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
	 *
	 * @param dt The amount of time that will pass in the next tick when{@link #getThing()} flies
	 */
	default void updateFlyForce(double dt){
		var entity = this.getThing();
		double mass = entity.getMass();
		double acceleration = this.getFlyAcceleration();
		double newFlyForce = acceleration * mass;
		double maxSpeed = this.getFlySpeedMax();
		var totalVel = entity.getVelocity();
		
		double currentVel = totalVel.getMagnitude();
		double angleRatio = this.getMobilityTryingRatio();
		
		boolean tryingToMove = this.isTryingToMove();
		
		// If moving in the current facing way would exceed max speed
		if(currentVel * angleRatio >= maxSpeed){
			// If trying to move, then move forward based on the ratio to facing forward
			
			// Find the total velocity if the new flying force is applied on the next tick
			double newVel = currentVel + (angleRatio * newFlyForce * dt / mass);
			
			// Likely related to issue#14
			// If that velocity is greater than the maximum speed, then apply a force such that it will bring the velocity exactly to the maximum speed
			// Use max speed if moving in the same direction, or 0 if the opposite direction
			if(angleRatio > 0){
				if(newVel > maxSpeed) newFlyForce = (maxSpeed - currentVel) / dt * mass;
			}
			else{
				if(newVel < 0) newFlyForce = currentVel / dt * mass;
				// If moving in the opposite direction, must put the force in the opposite direction
				newFlyForce = -newFlyForce;
			}
		}
		// Otherwise, if not trying to move, also try to slow down
		else if(!tryingToMove){
			double flyStopPower = this.getFlyStopPower();
			double newVel = currentVel - (flyStopPower * dt / mass);
			
			// Likely related to issue#14
			if(newVel < 0) newFlyForce = currentVel / dt * mass;
			// When trying to slow down, must go in the opposite direction
			newFlyForce = -newFlyForce;
		}
		// Otherwise, apply the full amount of force
		
		// Apply the force
		this.applyFlyForce(newFlyForce, tryingToMove);
	}
	
	/**
	 * Apply the given amount of force to this thing's flying force
	 *
	 * @param newFlyForce The amount of force to apply in the direction this thing is flying. Negative means move in the opposite direction
	 * @param applyFacing true if the force should be applied in the direction the thing is facing, false to apply it in the direction it's moving
	 */
	void applyFlyForce(double newFlyForce, boolean applyFacing);
	
	/**
	 * @return A value in the range [-1, 1] representing how close the angle of movement is to the angle of desired movement.
	 * A value of 1 means movement is trying to happen in the exact same direction as current movement, -1 means the exact opposite direction
	 */
	double getMobilityTryingRatio();
	
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
	
	/** Remove any jump time built up */
	default void cancelJump(){
		var mobilityData = this.getMobilityData();
		mobilityData.setBuildingJump(false);
		mobilityData.setJumpTimeBuilt(0);
	}
	
	/**
	 * Update the value of {@link MobilityData#jumpingForce} based on the current state of {@link #getThing()}
	 *
	 * @param dt The amount of time, in seconds, that will pass in the next tick when {@link #getThing()} stops jumping
	 */
	default void updateJumpState(double dt){
		var mobilityData = this.getMobilityData();
		
		// This entity can jump if it's on the ground, or if it can wall jump and is on a wall
		mobilityData.setCanJump((this.hasTimeToFloorJump() || this.isCanWallJump() && this.hasTimeToWallJump() && mobilityData.isWallJumpAvailable()) && mobilityData.isGroundedSinceLastJump());
		
		// If building a jump, and able to jump, then add the time
		if(mobilityData.isBuildingJump() && mobilityData.isCanJump()){
			mobilityData.addJumpTimeBuilt(dt);
			// If the jump time threshold has been met, and this entity is set to jump right away, then perform the jump now
			if(mobilityData.getJumpTimeBuilt() >= this.getJumpBuildTime() && this.isJumpAfterBuildUp()) this.jumpFromBuiltUp(dt);
		}
		if(mobilityData.isStoppingJump()){
			// Can only stop jumping if it's allowed
			if(!this.isCanStopJump()) return;
			var entity = this.getThing();
			// Only need to stop jumping if this entity is moving up
			double vy = entity.getVerticalVel();
			boolean invert = this.jumpingInverted();
			if(invert && vy < 0 || !invert && vy > 0){
				double mass = entity.getMass();
				double power = this.getJumpStopPower();
				double newStopJumpVel = power / mass;
				double newStopJumpForce = -power / dt;
				
				// If the jump force would adjust extra velocity making its total velocity downwards,
				// then the jump stop force should be such that the y velocity will be 0 on the next tick
				if(invert && vy > newStopJumpVel || !invert && vy < newStopJumpVel){
					newStopJumpForce = -vy * mass / dt;
				}
				
				// Account for inverted jumping axis
				if(invert) newStopJumpForce = -newStopJumpForce;
				
				mobilityData.setJumpingForce(newStopJumpForce);
			}
			// Otherwise it is no longer stopping its jump, so remove the stopping force amount
			else{
				mobilityData.setStoppingJump(false);
				mobilityData.setJumpingForce(0);
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
		var mobilityData = this.getMobilityData();
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
		var mobilityData = this.getMobilityData();
		
		if(!mobilityData.isCanJump()) return;
		var entity = this.getThing();
		
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
		double vy = entity.getVerticalVel();
		if(invert && vy > 0 || !invert && vy < 0){
			double adjust = vy / dt * entity.getMass();
			if(invert) jumpAmount += adjust;
			else jumpAmount -= adjust;
		}
		
		mobilityData.setJumpingForce(jumpAmount);
		mobilityData.setJumpTimeBuilt(0);
		mobilityData.setBuildingJump(false);
	}
	
	/** Cause this {@link #getThing()} to stop jumping. Does nothing if this entity is not currently jumping */
	default void stopJump(){
		var mobilityData = this.getMobilityData();
		
		if(!mobilityData.isJumping()) return;
		mobilityData.setJumpingForce(0);
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
		if(this.jumpsAreInstant() || this.getMobilityData().getJumpTimeBuilt() == 0){
			this.stopJump();
		}
		// Otherwise, perform the built up jump
		else this.jumpFromBuiltUp(dt);
	}
	
	/** @return true if jumping should decrease the y axis instead of increasing it */
	default boolean jumpingInverted(){
		return false;
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
	
	/** @return The acceleration of this while flying, i.e., how fast it gets to {@link #getFlySpeedMax()}, defaults to {@link #getWalkAcceleration()} */
	default double getFlyAcceleration(){
		return this.getWalkAcceleration();
	}
	
	/**
	 * @return The magnitude of how much this entity can stop its flying speed in units of momentum, i.e. mass * velocity
	 * 		Defaults to {@link #getFlyAcceleration()}
	 */
	default double getFlyStopPower(){
		return this.getWalkAcceleration();
	}
	
	/** @return The maximum walking speed of this entity thing, defaults to {@link #getWalkSpeedMax()} */
	default double getFlySpeedMax(){
		return this.getWalkSpeedMax();
	}
	
	/** This method should be called when the associated entity touches a floor */
	default void mobilityTouchFloor(Material m){
		var mobilityData = this.getMobilityData();
		mobilityData.setJumpingForce(0);
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
		var entity = this.getThing();
		// If not on the ground, use the normal amount of friction, otherwise, if currently walking, return walk friction, otherwise, return stop friction
		return !entity.isOnGround() ? 1 : (this.isWalking()) ? this.getWalkFriction() : this.getWalkStopFriction();
	}
	
}
