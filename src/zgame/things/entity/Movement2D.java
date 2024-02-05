package zgame.things.entity;

/** A class that handles an {@link EntityThing} moving by walking and jumping */
public interface Movement2D extends Movement{
	
	@Override
	Walk2D getWalk();
	
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
	default boolean isWalking(){
		return this.getWalk().getWalkingDirection() == 0;
	}
	
	@Override
	default void applyWalkForce(double dt, double newWalkForce){
		if(this.getWalk().getWalkingDirection() == 0){
			this.getWalk().setWalkingForce(0);
			return;
		}
		this.getWalk().setWalkingForce(this.getWalk().getWalkingDirection() == 1 ? newWalkForce : -newWalkForce);
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
