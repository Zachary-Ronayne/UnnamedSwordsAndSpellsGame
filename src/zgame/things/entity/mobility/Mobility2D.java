package zgame.things.entity.mobility;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector2D;
import zgame.physics.collision.CollisionResult2D;
import zgame.things.entity.EntityThing;
import zgame.things.entity.EntityThing2D;
import zgame.things.entity.MobilityData2D;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room2D;

/** A class that handles an {@link EntityThing} moving by walking and jumping */
public interface Mobility2D extends Mobility<HitBox2D, EntityThing2D, ZVector2D, Room2D, CollisionResult2D>{
	
	@Override
	MobilityData2D getMobilityData();
	
	@Override
	default boolean isTryingToMove(){
		return this.getMobilityData().getWalkingDirection() != 0;
	}
	
	/** Tell this entity to start walking to the left */
	default void walkLeft(){
		this.getMobilityData().setWalkingDirection(-1);
	}
	
	/** Tell this entity to start walking to the right */
	default void walkRight(){
		this.getMobilityData().setWalkingDirection(1);
	}
	
	/** @return true {@link #getThing()} is walking to the left, false otherwise */
	default boolean walkingLeft(){
		return this.getMobilityData().getWalkingDirection() < 0;
	}
	
	/** @return true {@link #getThing()} is walking to the right, false otherwise */
	default boolean walkingRight(){
		return this.getMobilityData().getWalkingDirection() > 0;
	}
	
	@Override
	default void stopWalking(){
		this.getMobilityData().setWalkingDirection(0);
	}
	
	@Override
	default boolean isWalking(){
		// -1 or 1 mean trying to walk in a particular direction, 0 means not tryiing to walk
		return this.getMobilityData().getWalkingDirection() == 0;
	}
	
	@Override
	default void applyWalkForce(double newWalkForce){
		var dir = this.getMobilityData().getWalkingDirection();
		if(dir == 0) this.getMobilityData().setWalkingForce(0);
		else this.getMobilityData().setWalkingForce(dir == 1 ? newWalkForce : -newWalkForce);
	}
	
	@Override
	default void applyFlyForce(double newFlyForce, boolean applyFacing){
		this.getMobilityData().updateFlyingForce(newFlyForce, applyFacing);
	}
	
	@Override
	default double getMobilityTryingRatio(){
		var mobilityData = this.getMobilityData();
		var mobilityType = mobilityData.getType();
		if(mobilityType == MobilityType.FLYING || mobilityType == MobilityType.FLYING_AXIS){
			var thing = this.getThing();
			var velocity = thing.getVelocity();
			double angleDiff = ZMath.angleDiff(mobilityData.getFlyingAngle(), velocity.getAngle());
			return angleDiff / ZMath.PI_BY_2 - 1;
		}
		else if(mobilityType == MobilityType.WALKING){
			double walkingDirection = mobilityData.getWalkingDirection();
			if(walkingDirection == 0) return 0;
			double currentVel = this.getThing().getHorizontalVel();
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
		var data = this.getMobilityData();
		var mobilityType = data.getType();
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
			if(tryingToMove) data.setFlyingAngle(ZMath.atan2Normalized(yDir, xDir));
			else data.setFlyingAngle(this.getThing().getVelocity().getAngle());
			// 0 for not moving, 1 for moving
			data.setWalkingDirection(tryingToMove ? 1 : 0);
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
	default ZVector2D createTryingToMoveVector(double magnitude){
		var data = this.getMobilityData();
		var mobilityType = data.getType();
		if(mobilityType == MobilityType.FLYING || mobilityType == MobilityType.FLYING_AXIS){
			return new ZVector2D(data.getFlyingAngle(), magnitude, false);
		}
		else if(mobilityType == MobilityType.WALKING){
			if(this.walkingLeft()) return new ZVector2D(Math.PI, magnitude, false);
			else if(this.walkingRight()) return new ZVector2D(0, magnitude, false);
		}
		
		return new ZVector2D();
	}
	
	@Override
	default ZVector2D createTryingToMoveVectorHorizontal(double magnitude){
		if(this.walkingLeft()) return new ZVector2D(-magnitude, 0);
		else if(this.walkingRight()) return new ZVector2D(magnitude, 0);
		return new ZVector2D();
	}
}
