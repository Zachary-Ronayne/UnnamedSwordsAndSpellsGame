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
	default void applyFlyForce(double newFlyForce, boolean applyFacing){
		// TODO should this check if newFlyForce is negative? Should the same check be applied for walking force?
		// TODO maybe enforce that all vectors will have a positive value, and the angle exclusively determines direction?
		this.getWalk().updateFlyingForce(newFlyForce, applyFacing);
	}
	
	@Override
	default double getMovementTryingRatio(){
		var walk = this.getWalk();
		// TODO this should not be based on facing, but based on direction trying to move in
		double facingH = walk.getWalkingAngle();
		double facingV = walk.getVerticalAngle();
		
		var totalVel = this.getThing().getVelocity();
		boolean moving = totalVel.getMagnitude() != 0;
		double movingH = moving ? totalVel.getAngleH() : facingH;
		double movingV = moving ? totalVel.getAngleV() : facingV;
		
		double diffH = ZMath.angleDiff(facingH, movingH);
		double diffV = ZMath.angleDiff(facingV, movingV);
		
		// TODO fix infinite horizontal acceleration when flying on axes? Why is there sudden periodic stutter stepping while flying?
		return (Math.PI - diffH) * (Math.PI - diffV) / (Math.PI * Math.PI);
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
		boolean horizontalMove = left || right || forward || backward;
		boolean verticalMove = up || down;
		
		var walk = this.getWalk();

		// TODO is this the best way of changing movement types?
		var walkType = walk.getType();
		if(walkType == WalkType.FLYING || walkType == WalkType.FLYING_AXIS){
			walk.setTryingToMove(left != right || up != down || forward != backward);
			double verticalAngle;
			double horizontalAngle;
			
			if(walkType == WalkType.FLYING_AXIS){
				// Go straight up and down
				if(up) verticalAngle = ZMath.PI_BY_2;
				else if(down) verticalAngle = ZMath.PI_BY_2 + Math.PI;
				else verticalAngle = 0;
				
				// TODO maybe abstract out this strafing thing, with how it's also used with walking
				horizontalAngle = angleH;
				// Account for strafing left and right
				if(left || right){
					double modifier = (forward || backward) ? ZMath.PI_BY_4 : ZMath.PI_BY_2;
					if(left && !backward || right && backward) modifier = -modifier;
					horizontalAngle = horizontalAngle + modifier;
				}
				// TODO explain why the horizontal angle doesn't also need to be inverted
				if(backward) horizontalAngle = horizontalAngle + Math.PI;
			}
			else{
				verticalAngle = angleV;
				horizontalAngle = angleH;
				// TODO explain why the horizontal angle doesn't also need to be inverted
				if(backward) verticalAngle = verticalAngle + Math.PI;
				
				// Account for strafing left and right
				if(left || right){
					double modifier = (forward || backward) ? ZMath.PI_BY_4 : ZMath.PI_BY_2;
					if(left && !backward || right && backward) modifier = -modifier;
					horizontalAngle = horizontalAngle + modifier;
				}
				// Account for strafing up and down
				if(up || down){
					double modifier = up ? ZMath.PI_BY_2 : -ZMath.PI_BY_2;
					verticalAngle = verticalAngle + modifier;
				}
			}
			
			walk.setWalkingAngle(ZMath.angleNormalized(horizontalAngle));
			walk.setVerticalAngle(ZMath.angleNormalized(verticalAngle));
		}
		else if(walkType == WalkType.WALKING){
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
