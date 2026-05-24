package zgame.things.entity.mobility;

import zgame.core.utils.ZMath;
import zgame.physics.V2D;
import zgame.things.entity.MobilityState;
import zgame.things.entity.MobilityState2D;

/** A class that handles mobility actions like walking and jumping */
public interface Mobility2D extends Mobility<V2D>{
	
	@Override
	MobilityState2D getMobilityState();
	
	@Override
	default boolean isTryingToMove(){
		return this.getMobilityState().getWalkingDirection() != 0;
	}
	
	/** Tell this entity to start walking to the left */
	default void walkLeft(){
		this.getMobilityState().setWalkingDirection(-1);
	}
	
	/** Tell this entity to start walking to the right */
	default void walkRight(){
		this.getMobilityState().setWalkingDirection(1);
	}
	
	/** @return true if walking to the left, false otherwise */
	default boolean walkingLeft(){
		return this.getMobilityState().getWalkingDirection() < 0;
	}
	
	/** @return true if walking to the right, false otherwise */
	default boolean walkingRight(){
		return this.getMobilityState().getWalkingDirection() > 0;
	}
	
	@Override
	default void stopWalking(){
		this.getMobilityState().setWalkingDirection(0);
	}
	
	@Override
	default boolean isSprinting(){
		// -1 or 1 mean trying to walk in a particular direction, 0 means not trying to walk
		return this.getMobilityState().getWalkingDirection() == 0;
	}
	
	@Override
	default void applyWalkForce(double newWalkForce){
		var dir = this.getMobilityState().getWalkingDirection();
		if(dir == 0) this.getMobilityState().clearForce(MobilityState.FORCE_WALKING);
		else this.getMobilityState().updateWalkingForce(dir == 1 ? newWalkForce : -newWalkForce);
	}
	
	@Override
	default void applyFlyForce(double newFlyForce, boolean applyFacing){
		this.getMobilityState().updateFlyingForce(newFlyForce, applyFacing);
	}
	
	@Override
	default double getMobilityTryingRatio(){
		var state = this.getMobilityState();
		var mobilityType = state.getType();
		if(mobilityType == MobilityType.FLYING || mobilityType == MobilityType.FLYING_AXIS){
			var velocity = state.getVelocity();
			double angleDiff = ZMath.angleDiff(state.getFlyingAngle(), velocity.getAngle());
			return angleDiff / ZMath.PI_BY_2 - 1;
		}
		else if(mobilityType == MobilityType.WALKING){
			double walkingDirection = state.getWalkingDirection();
			if(walkingDirection == 0) return 0;
			double currentVel = state.getVelocity().getX();
			return ZMath.sameSign(currentVel, walkingDirection) ? 1 : -1;
		}
		return 0;
	}
	
	/**
	 * A utility method that handles a simple implementation of moving
	 *
	 * @param moveLeft true if movement should be to the left, false otherwise
	 * @param moveRight true if movement should be to the right, false otherwise
	 * @param moveUp true if movement should happen in the upwards direction, false otherwise
	 * @param moveDown true if movement should happen in the downwards direction, false otherwise
	 * @param jump true if jumping should occur, false otherwise, ignored when flying
	 * @param dt The amount of time that passed during this instance of time
	 */
	default void handleMobilityControls(boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown, boolean jump, double dt){
		var state = this.getMobilityState();
		var mobilityType = state.getType();
		// Flying types don't matter for 2D, just has to be one of them
		if(mobilityType == MobilityType.FLYING || mobilityType == MobilityType.FLYING_AXIS){
			int xDir;
			int yDir;
			if(moveLeft && !moveRight) xDir = -1;
			else if(!moveLeft && moveRight) xDir = 1;
			else xDir = 0;
			
			if(moveDown && !moveUp) yDir = 1;
			else if(!moveDown && moveUp) yDir = -1;
			else yDir = 0;
			
			boolean tryingToMove = moveLeft != moveRight || moveDown != moveUp;
			if(tryingToMove) state.setFlyingAngle(ZMath.atan2Normalized(yDir, xDir));
			else state.setFlyingAngle(state.getVelocity().getAngle());
			// 0 for not moving, 1 for moving
			state.setWalkingDirection(tryingToMove ? 1 : 0);
		}
		else if(mobilityType == MobilityType.WALKING){
			// Move left and right
			if(moveLeft) this.walkLeft();
			else if(moveRight) this.walkRight();
			else this.stopWalking();
			
			// Jump if holding the jump button
			if(jump) this.jump(dt);
			// For not holding the button
			else this.checkPerformOrStopJump(dt);
		}
	}
	
	@Override
	default boolean jumpingInverted(){
		return true;
	}
	
	@Override
	default V2D createTryingToMoveVector(double magnitude){
		var data = this.getMobilityState();
		var mobilityType = data.getType();
		if(mobilityType == MobilityType.FLYING || mobilityType == MobilityType.FLYING_AXIS){
			return new V2D(data.getFlyingAngle(), magnitude, false);
		}
		else if(mobilityType == MobilityType.WALKING){
			if(this.walkingLeft()) return new V2D(Math.PI, magnitude, false);
			else if(this.walkingRight()) return new V2D(0, magnitude, false);
		}
		
		return new V2D();
	}
	
	@Override
	default V2D createTryingToMoveVectorHorizontal(double magnitude){
		if(this.walkingLeft()) return new V2D(-magnitude, 0);
		else if(this.walkingRight()) return new V2D(magnitude, 0);
		return new V2D();
	}
}
