package zgame.things.entity.mobility;

import zgame.core.utils.ZMath;
import zgame.physics.V3D;
import zgame.things.entity.MobilityState;
import zgame.things.entity.MobilityState3D;

/** An interface used to control movement in 3D */
public interface Mobility3D extends Mobility<V3D>{
	
	@Override
	MobilityState3D getMobilityState();
	
	@Override
	default void applyWalkForce(double newWalkForce){
		this.getMobilityState().updateWalkingForce(newWalkForce);
	}
	
	@Override
	default void applyFlyForce(double newFlyForce, boolean applyFacing){
		this.getMobilityState().updateFlyingForce(newFlyForce, applyFacing);
	}
	
	@Override
	default double getMobilityTryingRatio(){
		var state = this.getMobilityState();
		double movingH = state.getMovingYaw();
		double movingV = state.getMovingPitch();
		
		var totalVel = state.getVelocity();
		double threshold = state.getClampVelocity();
		double currentH = (totalVel.getHorizontal() > threshold) ? totalVel.getYaw() : movingH;
		double currentV = (state.isTryingToMoveVertical() && totalVel.getVertical() > threshold) ? totalVel.getPitch() : movingV;
		
		double diffH = ZMath.angleDiff(movingH, currentH);
		double diffV = ZMath.angleDiff(movingV, currentV);

		return (ZMath.PI_BY_2 - diffH) * (ZMath.PI_BY_2 - diffV) / (ZMath.PI_BY_2 * ZMath.PI_BY_2);
	}
	
	/**
	 * Move this object based on the given parameters
	 *
	 * @param dt The amount of time, in seconds, that passed
	 * @param yaw The angle this thing is looking at on the horizontal axis, i.e. x z plane
	 * @param pitch The angle this thing is looking at on the vertical axis
	 * @param left true if this object is moving to its left, false otherwise
	 * @param right true if this object is moving to its right, false otherwise
	 * @param forward true if this object is moving forward, false otherwise
	 * @param backward true if this object is moving backward, false otherwise
	 * @param up true if this object is moving up, false otherwise. Only does anything if flying is true
	 * @param down true if this object is moving down, false otherwise. Only does anything if flying is true
	 */
	default void handleMobilityControls(double dt, double yaw, double pitch, boolean left, boolean right, boolean forward, boolean backward, boolean up, boolean down){
		var mobilityState = this.getMobilityState();
		/*
		This random rotation by half pi doesn't really make sense, and there's probably somewhere in the engine that is effectively
		rotating everything by 90 degrees. The adjustment accounts for the weird offset with the camera, so that
		movement goes in a direction that makes sense
		 */
		double adjustedYaw = yaw - ZMath.PI_BY_2;
		double adjustedPitch = -pitch;
		mobilityState.setFacingYaw(adjustedYaw);
		mobilityState.setFacingPitch(adjustedPitch);
		
		var mobilityType = mobilityState.getType();
		if(mobilityType == MobilityType.FLYING || mobilityType == MobilityType.FLYING_AXIS){
			// issue#37 fix flying feeling borked when trying to move in more than one direction at once, i.e. left, up, and back
			mobilityState.setTryingToMove(left != right || up != down || forward != backward);
			double movingPitch;
			double movingYaw = adjustedYaw;
			
			if(mobilityType == MobilityType.FLYING_AXIS){
				// Go straight up and down
				if(up) movingPitch = ZMath.PI_BY_2;
				else if(down) movingPitch = ZMath.PI_BY_2 + Math.PI;
				else movingPitch = 0;
				if(backward) movingYaw += Math.PI;
			}
			else{
				movingPitch = adjustedPitch;
				
				// Account for strafing up and down
				if(up || down){
					double modifier = up ? ZMath.PI_BY_2 : -ZMath.PI_BY_2;
					movingPitch = movingPitch + modifier;
				}
			}
			// Account for strafing
			movingYaw += this.calculateStrafeModifier(left, right, forward, backward);
			
			// Only the vertical axis needs to be inverted here because only one axis needs to be inverted
			if(backward && !forward) movingPitch = movingPitch + Math.PI;
			
			mobilityState.setMovingYaw(ZMath.angleNormalized(movingYaw));
			mobilityState.setMovingPitch(ZMath.angleNormalized(movingPitch));
		}
		else if(mobilityType == MobilityType.WALKING){
			mobilityState.setTryingToMove(left != right || forward != backward);
			
			if(mobilityState.isTryingToMove()){
				double movingYaw = adjustedYaw;
				// Account for moving backwards
				if(backward && !forward) movingYaw = ZMath.angleNormalized(movingYaw + Math.PI);
				
				// Account for strafing
				movingYaw += this.calculateStrafeModifier(left, right, forward, backward);
				mobilityState.setMovingYaw(movingYaw);
			}
			
			// Jump if holding the jump button
			if(up){
				this.jump(dt);
			}
			// For not holding the button
			else this.checkPerformOrStopJump(dt);
			
			if(!left && !right && !forward && !backward && mobilityState.getForce(MobilityState.FORCE_WALKING).getMagnitude() != 0) this.stopWalking();
		}
	}
	
	/**
	 * Determine the modifier value to use when strafing, depending on which directions are being moved in
	 *
	 * @param left True if moving left, false otherwise
	 * @param right True if moving right, false otherwise
	 * @param forward True if moving forward, false otherwise
	 * @param backward True if moving backwards, false otherwise
	 * @return The modifier, can be 0 if there is no strafing
	 */
	default double calculateStrafeModifier(boolean left, boolean right, boolean forward, boolean backward){
		if(left != right){
			double modifier = (forward || backward) ? ZMath.PI_BY_4 : ZMath.PI_BY_2;
			if(left && !backward || right && backward) modifier = -modifier;
			return modifier;
		}
		return 0;
	}
	
	/** @param x The amount to move on the x axis */
	void addX(double x);
	
	/** @param y The amount to move on the y axis */
	void addY(double y);
	
	/** @param z The amount to move on the z axis */
	void addZ(double z);
	
	@Override
	default boolean isTryingToMove(){
		return this.getMobilityState().isTryingToMove();
	}
	
	@Override
	default V3D createTryingToMoveVector(double magnitude){
		var data = this.getMobilityState();
		return new V3D(data.getMovingYaw(), data.getMovingPitch(), magnitude, false);
	}
	
	@Override
	default V3D createTryingToMoveVectorHorizontal(double magnitude){
		var data = this.getMobilityState();
		double movingAngle = data.getMovingYaw();
		return new V3D(Math.cos(movingAngle) * magnitude, 0, Math.sin(movingAngle) * magnitude);
	}
}
