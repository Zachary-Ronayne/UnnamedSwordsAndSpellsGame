package zgame.things.entity;

import zgame.physics.ZVector3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** An interface used to control movement in 3D */
public interface Movement3D extends Movement<HitBox3D, EntityThing3D, ZVector3D, Room3D>{
	
	@Override
	default void applyWalkForce(double dt, double newWalkForce){
		// TODO implement using Walk3D
	}
	
	// TODO make all of this stuff interact with the base Movement class
	/**
	 * Move this object based on the given parameters simulating walking
	 *
	 * @param dt The amount of time, in seconds, that passed
	 * @param left true if this object is moving to its left, false otherwise
	 * @param right true if this object is moving to its right, false otherwise
	 * @param forward true if this object is moving forward, false otherwise
	 * @param backward true if this object is moving backward, false otherwise
	 */
	default void handleMovementControls(double dt, boolean left, boolean right, boolean forward, boolean backward){
		this.handleMovementControls(dt, left, right, forward, backward, false, false, false);
	}
	
	/**
	 * Move this object based on the given parameters
	 *
	 * @param dt The amount of time, in seconds, that passed
	 * @param left true if this object is moving to its left, false otherwise
	 * @param right true if this object is moving to its right, false otherwise
	 * @param forward true if this object is moving forward, false otherwise
	 * @param backward true if this object is moving backward, false otherwise
	 * @param up true if this object is moving up, false otherwise. Only does anything if flying is true
	 * @param down true if this object is moving down, false otherwise. Only does anything if flying is true
	 * @param flying true if this object is flying, false otherwise
	 */
	default void handleMovementControls(double dt, boolean left, boolean right, boolean forward, boolean backward, boolean up, boolean down, boolean flying){
		double xSpeed = 0;
		double ySpeed = 0;
		double zSpeed = 0;
		
		// Determining movement direction
		var ang = this.getRotY();
		if(left && forward || right && backward) ang -= Math.PI * 0.25;
		if(right && forward || left && backward) ang += Math.PI * 0.25;
		
		if(forward) {
			xSpeed = Math.sin(ang);
			zSpeed = -Math.cos(ang);
		}
		else if(backward) {
			xSpeed = -Math.sin(ang);
			zSpeed = Math.cos(ang);
		}
		else{
			if(left){
				ang -= Math.PI * 0.5;
				xSpeed = Math.sin(ang);
				zSpeed = -Math.cos(ang);
			}
			else if(right){
				ang += Math.PI * 0.5;
				xSpeed = Math.sin(ang);
				zSpeed = -Math.cos(ang);
			}
		}
		if(flying){
			if(up && !down) ySpeed = 0.5;
			else if(down) ySpeed = -.5;
		}
		else{
			// Jump if holding the jump button
			if(up) this.jump(dt);
			// For not holding the button
			else this.checkPerformOrStopJump(dt);
		}
		
		var speed = this.getMoveSpeed();
		this.addX(dt * speed * xSpeed);
		this.addY(dt * speed * ySpeed);
		this.addZ(dt * speed * zSpeed);
		
	}
	
	// TODO implement fly look
	
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
	
	/** @return The number of units per second this object moves while moving in a straight line */
	double getMoveSpeed();
	
}
