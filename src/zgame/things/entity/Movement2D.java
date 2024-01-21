package zgame.things.entity;

/** A class that handles an {@link EntityThing} moving by walking and jumping */
public interface Movement2D extends Movement{
	
	// TODO somehow abstract this to 3D
	@Override
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
	
	@Override
	default void stopWalking(){
		this.getWalk().setWalkingDirection(0);
	}
	
	@Override
	default void updateWalkForce(double dt){
		// TODO somehow abstract some of this stuff to the generic Movement interface, and use it for 3D
		
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

	/**
	 * A utility method that handles a simple implementation of moving
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
		else this.checkPerformOrStopJump(dt);
	}
	
}
