package zgame.things.entity;

import zgame.physics.V2D;
import zgame.things.entity.mobility.Mobility2D;

/** A type of {@link MobilityState} that exists in 2D space */
public class MobilityState2D extends MobilityState<V2D>{
	
	/** The direction walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	private int walkingDirection;
	
	/** The angle, in radians, trying to fly at */
	private double flyingAngle;
	
	/**
	 * Create a new walk object for use in {@link Mobility2D}
	 *
	 * @param gravityAcceleration The acceleration of gravity
	 * @param clampVelocity A velocity where if velocity magnitude reaches a value below this, velocity will be zero
	 */
	public MobilityState2D(double gravityAcceleration, double clampVelocity){
		super(new V2D(), gravityAcceleration, clampVelocity);
		
		this.setWalkingDirection(0);
		this.setFlyingAngle(0);
	}
	
	/** @return See {@link #walkingDirection} */
	public int getWalkingDirection(){
		return this.walkingDirection;
	}
	
	/** @param direction See {@link #walkingDirection} */
	public void setWalkingDirection(int direction){
		this.walkingDirection = direction;
	}
	
	@Override
	public void updateWalkingForce(double force){
		this.attemptSetForce(FORCE_WALKING, new V2D(force, 0));
	}
	
	/** @return See {@link #flyingAngle} */
	public double getFlyingAngle(){
		return this.flyingAngle;
	}
	
	/** @param flyingAngle See {@link #flyingAngle} */
	public void setFlyingAngle(double flyingAngle){
		this.flyingAngle = flyingAngle;
	}
	
	@Override
	public void updateFlyingForce(double force, boolean applyFacing){
		// For 2D, apply facing is irrelevant
		this.attemptSetForce(FORCE_FLYING, new V2D(this.getFlyingAngle(), force, false));
	}
}
