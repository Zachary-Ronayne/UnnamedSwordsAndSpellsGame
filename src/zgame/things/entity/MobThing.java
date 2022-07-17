package zgame.things.entity;

import zgame.core.Game;
import zgame.core.utils.ZMathUtils;
import zgame.physics.ZVector;
import zgame.physics.material.Material;

/** An {@link EntityThing} which represents some kind of creature which can walk around, i.e. the player, an enemy, an animal, a monster, any NPC, etc. */
public abstract class MobThing extends EntityThing{
	
	/** The string used to identify the force used to make this {@link MobThing} walk */
	public static final String FORCE_NAME_WALKING = "walking";
	/** The string used to identify the force used to make this {@link MobThing} jump */
	public static final String FORCE_NAME_JUMPING = "jumping";
	/** The string used to identify the force used to make this {@link MobThing} stop jumping */
	public static final String FORCE_NAME_JUMPING_STOP = "jumpingStop";
	
	/** The default value of {@link #jumpPower} */
	public static final double DEFAULT_JUMP_POWER = 60000;
	/** The default value of {@link #jumpStopPower} */
	public static final double DEFAULT_JUMP_STOP_POWER = 5000;
	/** The default value of {@link #canStopJump} */
	public static final boolean DEFAULT_CAN_STOP_JUMP = true;
	/** The default value of {@link #walkAcceleration} */
	public static final double DEFAULT_WALK_ACCELERATION = 2000;
	/** The default value of {@link #walkSpeedMax} */
	public static final double DEFAULT_WALK_SPEED_MAX = 300;
	/** The default value of {@link #walkAirControl} */
	public static final double DEFAULT_WALK_AIR_CONTROL = 0.5;
	/** The default value of {@link #walkFriction} */
	public static final double DEFAULT_WALK_FRICTION = 1;
	/** The default value of {@link #walkStopFriction} */
	public static final double DEFAULT_WALK_STOP_FRICTION = 10;
	
	/**
	 * The magnitude of how much a mob can jump in units of momentum, i.e. mass * velocity,
	 * i.e, higher mass makes for lower jumps, lower mass makes for higher jumps
	 */
	private double jumpPower;
	
	/** In the same units as {@link #jumpPower}, the power at which this {@link MobThing} is able to stop jumping while in the air */
	private double jumpStopPower;

	/** true if this mob has the ability to stop jumping while it's in the air, false otherwise */
	private boolean canStopJump;
	
	/** The acceleration of this {@link MobThing} while walking, i.e., how fast it gets to #walkSpeedMax */
	private double walkAcceleration;
	
	/** The maximum walking speed of this {@link MobThing} */
	private double walkSpeedMax;
	
	/** The ratio of speed this {@link MobThing} can use to walk when it is airborne, i.e. not on the ground */
	private double walkAirControl;
	
	/**
	 * The frictional constant used to slow down this {@link MobThing} when it is trying to move.
	 * This value represents the amount of the surface's friction which is applied.
	 * Zero means no friction is applied while walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction
	 * Generally should be 1.
	 */
	private double walkFriction;
	
	/**
	 * The frictional constant used to slow down this {@link MobThing} when it is trying to stop moving
	 * This value represents the amount of the surface's friction which is applied.
	 * Zero means no friction is applied while not walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction.
	 * Use a high value to make stopping walking happen quickly, use a low value to make stopping walking slow, and use zero to make it impossible to stop.
	 * This friction only applies while on the ground. A value of 1 is used while in the air, regardless of if this MobThing is walking or not.
	 */
	private double walkStopFriction;
	
	/** true if this {@link MobThing} is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	/** true if this job is currently jumping, false otherwise */
	private boolean jumping;
	
	/** The force of jumping on this {@link MobThing} */
	private ZVector jumpingForce;

	/**
	 * The force used to make you stop jumping. Physics wise doesn't make any sense, but it's to give an option to control jump height by holding down or letting go of a jump
	 * button
	 */
	private ZVector jumpingStopForce;
	
	/** true if this {@link MobThing} is currently stopping its jump, false otherwise */
	private boolean stoppingJump;
	
	/** The vector keeping track of the force of this {@link MobThing} walking */
	private ZVector walkingForce;
	
	/** The direction this {@link MobThing} is walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	private int walkingDirection;
	
	/**
	 * Create a new {@link MobThing} at the given position
	 * 
	 * @param x The x coordinate of the mob
	 * @param y The y coordinate of the mob
	 */
	public MobThing(double x, double y){
		super(x, y);
		this.canJump = false;
		this.jumping = false;
		this.stoppingJump = false;
		this.stopWalking();
		
		this.jumpingForce = new ZVector();
		this.addForce(FORCE_NAME_JUMPING, this.jumpingForce);
		this.jumpingStopForce = new ZVector();
		this.addForce(FORCE_NAME_JUMPING_STOP, this.jumpingStopForce);
		
		this.jumpPower = DEFAULT_JUMP_POWER;
		this.jumpStopPower = DEFAULT_JUMP_STOP_POWER;
		this.canStopJump = DEFAULT_CAN_STOP_JUMP;
		this.walkAcceleration = DEFAULT_WALK_ACCELERATION;
		this.walkSpeedMax = DEFAULT_WALK_SPEED_MAX;
		this.walkAirControl = DEFAULT_WALK_AIR_CONTROL;
		this.walkFriction = DEFAULT_WALK_FRICTION;
		this.walkStopFriction = DEFAULT_WALK_STOP_FRICTION;
		
		this.walkingForce = new ZVector();
		this.addForce(FORCE_NAME_WALKING, this.walkingForce);
	}
	
	@Override
	public void tick(Game game, double dt){
		// Determine the new walking force
		this.updateWalkForce(dt);
		
		// Being off the ground means the mob cannot jump
		if(!this.isOnGround()) this.canJump = false;
		
		// Update the state of the jumping force
		this.updateJumpForce(dt);
		
		// Do the normal game update
		super.tick(game, dt);
	}
	
	/** @return See {@link #walkingDirection} */
	public int getWalkingDirection(){
		return this.walkingDirection;
	}
	
	/** Tell this mob to start walking to the left */
	public void walkLeft(){
		this.walkingDirection = -1;
	}
	
	/** Tell this mob to start walking to the right */
	public void walkRight(){
		this.walkingDirection = 1;
	}
	
	/** Tell this mob to stop walking */
	public void stopWalking(){
		this.walkingDirection = 0;
	}
	
	/**
	 * Update the value of {@link #walkingForce} based on the current state of this {@link MobThing}
	 * 
	 * @param dt The amount of time that will pass in the next tick when this {@link MobThing} walks
	 */
	public void updateWalkForce(double dt){
		// First handle mob movement
		double mass = this.getMass();
		double acceleration = this.getWalkAcceleration();
		double walkForce = acceleration * mass * this.getWalkingDirection();
		boolean walking = walkForce != 0;
		
		// If the mob is not on the ground, it's movement force is modified by the air control
		if(!this.isOnGround()) walkForce *= this.getWalkAirControl();
		
		// Only make the walking happen if there is any walking force
		if(walking){
			// If already moving at or beyond maximum walking speed, and walking would increase the x axis speed, don't continue to walk
			double vx = this.getVX();
			// TODO this amount of force should be such that on the next update, it will move the velocity to exactly max speed, need to add dt
			if(Math.abs(vx) > this.getWalkSpeedMax() && ZMathUtils.sameSign(vx, walkForce)) walkForce = 0;
		}
		// Set the amount the mob is walking
		this.setWalkingForce(walkForce);
	}
	
	@Override
	public double getFrictionConstant(){
		// If not on the ground, use the normal amount of friction, otherwise, if currently walking, return walk friction, otherwise, return stop friction
		return !this.isOnGround() ? 1 : (this.getWalkingDirection() != 0) ? getWalkFriction() : getWalkStopFriction();
	}
	
	/** @return See {@link #jumping} */
	public boolean isJumping(){
		return this.jumping;
	}
	
	/** @return See {@link #stoppingJump} */
	public boolean isStoppingJump(){
		return this.stoppingJump;
	}
	
	/**
	 * Update the value of {@link #jumpingForce} and {@link #jumpingStopForce} based on the current state of this {@link MobThing}
	 * 
	 * @param dt The amount of time that will pass in the next tick when this {@link MobThing} stops jumping
	 */
	public void updateJumpForce(double dt){
		if(!this.isOnGround()) this.jumpingForce = this.replaceForce(FORCE_NAME_JUMPING, 0, 0);
		
		if(this.isStoppingJump()){
			// Can only stop jumping if it's allowed
			if(!this.isCanStopJump()){
				this.jumpingStopForce = this.replaceForce(FORCE_NAME_JUMPING_STOP, 0, 0);
				return;
			}

			// Only need to stop jumping if the mob is moving up
			double vy = this.getVY();
			if(vy < 0){
				double mass = this.getMass();
				double newStopJumpVel = this.getJumpStopPower() / mass;
				double newStopJumpForce = this.getJumpStopPower() / dt;
				// If the jump force would add extra velocity making its total velocity downwards,
				// then the jump stop force should be such that the y velocity will be 0 on the next tick
				if(vy + newStopJumpVel > 0) newStopJumpForce = -vy * mass / dt;

				this.jumpingStopForce = this.replaceForce(FORCE_NAME_JUMPING_STOP, 0, newStopJumpForce);
			}
			// Otherwise it is no longer stopping its jump, so remove the stopping force amount
			else{
				this.stoppingJump = false;
				this.jumpingStopForce = this.replaceForce(FORCE_NAME_JUMPING_STOP, 0, 0);
			}
		}
	}
	
	/**
	 * Cause this mob to jump upwards, if the mob is in a position to jump
	 * 
	 * @param dt The amount of time that will pass in one tick after the mob jumps off the ground
	 */
	public void jump(double dt){
		if(!canJump) return;
		
		this.jumping = true;
		canJump = false;
		double jumpAmount = -this.jumpPower / dt;
		this.jumpingForce = this.replaceForce(FORCE_NAME_JUMPING, 0, jumpAmount);
	}
	
	/** Cause this {@link MobThing} to stop jumping.  */
	public void stopJump(){
		this.jumpingForce = this.replaceForce(FORCE_NAME_JUMPING, 0, 0);
		this.jumping = false;
		this.stoppingJump = true;
	}
	
	@Override
	public void touchFloor(Material m){
		super.touchFloor(m);
		this.canJump = true;
		this.jumpingForce = this.replaceForce(FORCE_NAME_JUMPING, 0, 0);
		this.jumping = false;
	}
	
	/** @return See {@link #jumpPower} */
	public double getJumpPower(){
		return this.jumpPower;
	}
	
	/** @param jumpPower See {@link #jumpPower} */
	public void setJumpPower(double jumpPower){
		this.jumpPower = jumpPower;
	}
	
	/** @return See {@link #jumpStopPower} */
	public double getJumpStopPower(){
		return this.jumpStopPower;
	}
	
	/** @param jumpStopPower See {@link #jumpStopPower} */
	public void setJumpStopPower(double jumpStopPower){
		this.jumpStopPower = jumpStopPower;
	}
	
	/** @return See {@link #canStopJump} */
	public boolean isCanStopJump(){
		return this.canStopJump;
	}
	
	/** @param canStopJump See {@link #canStopJump} */
	public void setCanStopJump(boolean canStopJump){
		this.canStopJump = canStopJump;
	}
	
	/** @return See {@link #walkAcceleration} */
	public double getWalkAcceleration(){
		return this.walkAcceleration;
	}
	
	/** @param See {@link #walkAcceleration} */
	public void setWalkAcceleration(double walkAcceleration){
		this.walkAcceleration = walkAcceleration;
	}
	
	/** @return See {@link #walkSpeedMax} */
	public double getWalkSpeedMax(){
		return this.walkSpeedMax;
	}
	
	/** @param See {@link #walkSpeedMax} */
	public void setWalkSpeedMax(double walkSpeedMax){
		this.walkSpeedMax = walkSpeedMax;
	}
	
	/** @return See {@link #walkAirControl} */
	public double getWalkAirControl(){
		return this.walkAirControl;
	}
	
	/** @param See {@link #walkAirControl} */
	public void setWalkAirControl(double walkAirControl){
		this.walkAirControl = walkAirControl;
	}
	
	/** @return See {@link #walkFriction} */
	public double getWalkFriction(){
		return walkFriction;
	}
	
	/** @param See {@link #walkFriction} */
	public void setWalkFriction(double walkFriction){
		this.walkFriction = walkFriction;
	}
	
	/** @return See {@link #walkStopFriction} */
	public double getWalkStopFriction(){
		return walkStopFriction;
	}
	
	/** @param See {@link #walkStopFriction} */
	public void setWalkStopFriction(double walkStopFriction){
		this.walkStopFriction = walkStopFriction;
	}
	
	/** @return See {@link #walkingForce} */
	public ZVector getWalkingForce(){
		return this.walkingForce;
	}
	
	/** @param movement The amount of force applied to the x axis when this mob is walking */
	public void setWalkingForce(double movement){
		this.walkingForce = this.replaceForce(FORCE_NAME_WALKING, movement, 0);
	}
	
}
