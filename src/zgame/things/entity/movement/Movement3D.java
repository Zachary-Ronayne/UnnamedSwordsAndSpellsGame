package zgame.things.entity.movement;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector3D;
import zgame.things.entity.EntityThing3D;
import zgame.things.entity.Walk3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** An interface used to control movement in 3D */
public interface Movement3D extends Movement<HitBox3D, EntityThing3D, ZVector3D, Room3D>{
	
	@Override
	Walk3D getWalk();
	
	@Override
	default void applyWalkForce(double newWalkForce){
		this.getWalk().updateWalkingForce(newWalkForce);
	}
	
	@Override
	default void applyFlyForce(double newFlyForce){
		this.getWalk().updateFlyingForce(newFlyForce);
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
	default void handleMovementControls(double dt, double angleH, double angleV, boolean left, boolean right, boolean forward, boolean backward, boolean up, boolean down){
		
		// Check for walking
		var horizontalMove = left || right || forward || backward;
		var verticalMove = up || down;
		
		var walk = this.getWalk();

		// TODO implement flying based on just moving along the x, y, z, axes
		if(walk.getType() == WalkType.FLYING){
			// TODO move forwards or backwards depending on which is pressed
			walk.setTryingToMove(forward || backward);
			walk.setVerticalAngle(angleV);
			walk.setWalkingAngle(angleH);
		}
		else{
			if(horizontalMove || verticalMove){
				double walkingAngle = angleH;
				
				// Account for moving backwards
				if(backward && !forward) walkingAngle = ZMath.angleNormalized(walkingAngle + Math.PI);
				
				// Account for strafing
				if(left || right){
					double modifier = (forward || backward) ? ZMath.PI_BY_4 : ZMath.PI_BY_2;
					if(left && !backward || right && backward) modifier = -modifier;
					walkingAngle = ZMath.angleNormalized(walkingAngle + modifier);
				}
				
				walk.setWalkingAngle(walkingAngle);
			}
			
			// Check for jumping
			walk.setTryingToMove(horizontalMove);
			// Jump if holding the jump button
			if(up) {
				this.jump(dt);
			}
			// For not holding the button
			else this.checkPerformOrStopJump(dt);
		}
	}
	
	/** @return The rotation of this object on the x axis */
	double getRotX();
	
	/** @return The rotation of this object on the y axis */
	double getRotY();
	
	/** @return The rotation of this object on the z axis */
	double getRotZ();
	
	/** @param x The amount to move on the x axis */
	void addX(double x);
	
	/** @param y The amount to move on the y axis */
	void addY(double y);
	
	/** @param z The amount to move on the z axis */
	void addZ(double z);
	
	@Override
	default boolean isTryingToMove(){
		return this.getWalk().isTryingToMove();
	}
	
}
