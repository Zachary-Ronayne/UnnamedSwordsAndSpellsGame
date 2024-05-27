package zgame.things.entity.movement;

import zgame.physics.ZVector2D;
import zgame.things.entity.EntityThing;
import zgame.things.entity.EntityThing2D;
import zgame.things.entity.Walk2D;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room2D;

/** A class that handles an {@link EntityThing} moving by walking and jumping */
public interface Movement2D extends Movement<HitBox2D, EntityThing2D, ZVector2D, Room2D>{
	
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
		// -1 or 1 mean trying to walk in a particular direction, 0 means not tryiing to walk
		return this.getWalk().getWalkingDirection() == 0;
	}
	
	@Override
	default void applyWalkForce(double newWalkForce){
		var dir = this.getWalk().getWalkingDirection();
		if(dir == 0) this.getWalk().setWalkingForce(0);
		else this.getWalk().setWalkingForce(dir == 1 ? newWalkForce : -newWalkForce);
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
	
	@Override
	default boolean jumpingInverted(){
		return true;
	}
}
