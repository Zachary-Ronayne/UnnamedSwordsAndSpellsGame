package zgame.things.entity.movement;

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
		// TODO account for negatives?
		this.getWalk().updateWalkingForce(newWalkForce);
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
	 * @param flying true if this object is flying, false otherwise
	 */
	default void handleMovementControls(double dt, double angleH, double angleV, boolean left, boolean right, boolean forward, boolean backward, boolean up, boolean down, boolean flying){
		
		// TODO make the camera actually change movement direction
		
		// Check for walking
		// TODO implement strafing and moving backwards
		var walk = this.getWalk();
		walk.setWalkingAngle(angleH);
		if(flying) walk.setTryingToMove(left || right || forward || backward);
		else walk.setTryingToMove(left || right || forward || backward || up || down);
		
		// Check for jumping
		if(!flying){
			// Jump if holding the jump button
			if(up) this.jump(dt);
			// For not holding the button
			else this.checkPerformOrStopJump(dt);
		}
		
		// TODO make angleV used for flying
		
		// TODO implement flying based on look angle
		
		// TODO implement flying based on just moving along the x, y, z, axes
	
		// TODO remove old code, keeping for reference until full3D movement is implemented
//		double xSpeed = 0;
//		double ySpeed = 0;
//		double zSpeed = 0;
//
//		// Determining movement direction
//		var ang = this.getRotY();
//		if(left && forward || right && backward) ang -= Math.PI * 0.25;
//		if(right && forward || left && backward) ang += Math.PI * 0.25;
//
//		if(forward) {
//			xSpeed = Math.sin(ang);
//			zSpeed = -Math.cos(ang);
//		}
//		else if(backward) {
//			xSpeed = -Math.sin(ang);
//			zSpeed = Math.cos(ang);
//		}
//		else{
//			if(left){
//				ang -= Math.PI * 0.5;
//				xSpeed = Math.sin(ang);
//				zSpeed = -Math.cos(ang);
//			}
//			else if(right){
//				ang += Math.PI * 0.5;
//				xSpeed = Math.sin(ang);
//				zSpeed = -Math.cos(ang);
//			}
//		}
//		if(flying){
//			if(up && !down) ySpeed = 0.5;
//			else if(down) ySpeed = -.5;
//		}
//		else{
//			// Jump if holding the jump button
//			if(up) this.jump(dt);
//			// For not holding the button
//			else this.checkPerformOrStopJump(dt);
//		}
//
//		var speed = this.getMoveSpeed();
//		this.addX(dt * speed * xSpeed);
//		this.addY(dt * speed * ySpeed);
//		this.addZ(dt * speed * zSpeed);
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
	
	// TODO remove old code after implement movement in 3D
//	/** @return The number of units per second this object moves while moving in a straight line */
//	double getMoveSpeed();

}
