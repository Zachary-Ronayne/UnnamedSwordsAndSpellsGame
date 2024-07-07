package zgame.things.entity.mobility;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector3D;
import zgame.things.entity.EntityThing3D;
import zgame.things.entity.MobilityData3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** An interface used to control movement in 3D */
public interface Mobility3D extends Mobility<HitBox3D, EntityThing3D, ZVector3D, Room3D>{
	
	@Override
	MobilityData3D getMobilityData();
	
	@Override
	default void applyWalkForce(double newWalkForce){
		this.getMobilityData().updateWalkingForce(newWalkForce);
	}
	
	@Override
	default void applyFlyForce(double newFlyForce, boolean applyFacing){
		this.getMobilityData().updateFlyingForce(newFlyForce, applyFacing);
	}
	
	@Override
	default double getMobilityTryingRatio(){
		var mobilityData = this.getMobilityData();
		double movingH = mobilityData.getMovingHorizontalAngle();
		double movingV = mobilityData.getMovingVerticalAngle();
		
		var thing = this.getThing();
		var totalVel = thing.getVelocity();
		double threshold = thing.getClampVelocity();
		double currentH = (Math.abs(totalVel.getHorizontal()) > threshold) ? totalVel.getAngleH() + ZMath.PI_BY_2 : movingH;
		double currentV = (Math.abs(totalVel.getVertical()) > threshold) ? totalVel.getAngleV() : movingV;
		
		double diffH = ZMath.angleDiff(movingH, currentH);
		double diffV = ZMath.angleDiff(movingV, currentV);
		
		return (ZMath.PI_BY_2 - diffH) * (ZMath.PI_BY_2 - diffV) / (ZMath.PI_BY_2 * ZMath.PI_BY_2);
	}
	
	/**
	 * Move this object based on the given parameters
	 *
	 * @param dt The amount of time, in seconds, that passed
	 * @param angleH The angle this thing is looking at on the horizontal axis, i.e. x z plane
	 * @param angleV The angle this thing is looking at on the vertical axis
	 * @param left true if this object is moving to its left, false otherwise
	 * @param right true if this object is moving to its right, false otherwise
	 * @param forward true if this object is moving forward, false otherwise
	 * @param backward true if this object is moving backward, false otherwise
	 * @param up true if this object is moving up, false otherwise. Only does anything if flying is true
	 * @param down true if this object is moving down, false otherwise. Only does anything if flying is true
	 */
	default void handleMobilityControls(double dt, double angleH, double angleV, boolean left, boolean right, boolean forward, boolean backward, boolean up, boolean down){
		var mobilityData = this.getMobilityData();
		mobilityData.setFacingHorizontalAngle(angleH);
		mobilityData.setFacingVerticalAngle(angleV);

		var mobilityType = mobilityData.getType();
		if(mobilityType == MobilityType.FLYING || mobilityType == MobilityType.FLYING_AXIS){
			mobilityData.setTryingToMove(left != right || up != down || forward != backward);
			double verticalAngle;
			double horizontalAngle = angleH;
			
			if(mobilityType == MobilityType.FLYING_AXIS){
				// Go straight up and down
				if(up) verticalAngle = ZMath.PI_BY_2;
				else if(down) verticalAngle = ZMath.PI_BY_2 + Math.PI;
				else verticalAngle = 0;
			}
			else{
				verticalAngle = angleV;
				
				// Account for strafing up and down
				if(up || down){
					double modifier = up ? ZMath.PI_BY_2 : -ZMath.PI_BY_2;
					verticalAngle = verticalAngle + modifier;
				}
			}
			// Account for strafing
			horizontalAngle += this.calculateStrafeModifier(left, right, forward, backward);
			
			// Only the vertical axis needs to be inverted here because only one axis needs to be inverted
			if(backward && !forward) verticalAngle = verticalAngle + Math.PI;
			
			mobilityData.setMovingHorizontalAngle(ZMath.angleNormalized(horizontalAngle));
			mobilityData.setMovingVerticalAngle(ZMath.angleNormalized(verticalAngle));
		}
		else if(mobilityType == MobilityType.WALKING){
			mobilityData.setTryingToMove(left != right || forward != backward);
			double horizontalAngle = angleH;
			
			if(mobilityData.isTryingToMove()){
				// Account for moving backwards
				if(backward && !forward) horizontalAngle = ZMath.angleNormalized(horizontalAngle + Math.PI);
				
				// Account for strafing
				horizontalAngle += this.calculateStrafeModifier(left, right, forward, backward);
				mobilityData.setMovingHorizontalAngle(horizontalAngle);
			}
			
			// Jump if holding the jump button
			if(up) {
				this.jump(dt);
			}
			// For not holding the button
			else this.checkPerformOrStopJump(dt);
		}
	}
	
	/**
	 * Determine the modifier value to use when strafing, depending on which directions are being moved in
	 * @param left True if moving left, false otherwise
	 * @param right True if moving right, false otherwise
	 * @param forward True if moving forward, false otherwise
	 * @param backward True if moving backwards, false otherwise
	 * @return The modifier, can be 0 if there is no strafing
	 */
	default double calculateStrafeModifier(boolean left, boolean right, boolean forward, boolean backward){
		if(left || right){
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
		return this.getMobilityData().isTryingToMove();
	}
	
}
