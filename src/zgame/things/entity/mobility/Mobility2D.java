package zgame.things.entity.mobility;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector2D;
import zgame.things.entity.EntityThing;
import zgame.things.entity.EntityThing2D;
import zgame.things.entity.MobilityData2D;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room2D;

/** A class that handles an {@link EntityThing} moving by walking and jumping */
public interface Mobility2D extends Mobility<HitBox2D, EntityThing2D, ZVector2D, Room2D>{
	
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
		// TODO implement
	}
	
	@Override
	default double getMobilityTryingRatio(){
		// TODO implement this for both dimensions when flying?
		
		var mobilityData = this.getMobilityData();
		double walkingDirection = mobilityData.getWalkingDirection();
		if(walkingDirection == 0) return 0;
		double currentVel = this.getThing().getHorizontalVel();
		return ZMath.sameSign(currentVel, walkingDirection) ? 1 : -1;
	}
	
	/**
	 * A utility method that handles a simple implementation of moving
	 *
	 * @param moveLeft true if movement should be to the left, false otherwise
	 * @param moveRight true if movement should be to the right, false otherwise
	 * @param jump true if jumping should occur, false otherwise
	 * @param dt The amount of time that passed during this instance of time
	 */
	default void handleMobilityControls(boolean moveLeft, boolean moveRight, boolean jump, double dt){
		// TODO implement flying
		
		// Move left and right
		if(moveLeft) this.walkLeft();
		else if(moveRight) this.walkRight();
		else this.stopWalking();
		
		// Jump if holding the jump button
		if(jump) this.jump(dt);
		// For not holding the button
		else this.checkPerformOrStopJump(dt);
	}
	
	@Override
	default boolean jumpingInverted(){
		return true;
	}
	
	@Override
	default ZVector2D createTryingToMoveVector(double magnitude){
		if(this.walkingLeft()) return new ZVector2D(magnitude, Math.PI, false);
		else if(this.walkingRight()) return new ZVector2D(magnitude, 0, false);
		return new ZVector2D();
	}
}
