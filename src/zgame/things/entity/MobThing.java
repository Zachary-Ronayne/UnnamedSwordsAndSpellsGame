package zgame.things.entity;

import zgame.core.Game;
import zgame.core.utils.ZMathUtils;
import zgame.physics.ZVector;
import zgame.physics.material.Material;

/** An {@link EntityThing} which represents some kind of creature which can walk around, i.e. the player, an enemy, an animal, a monster, any NPC, etc. */
public abstract class MobThing extends EntityThing{

	/** The string used to identify the force used to make this {@link MobThing} walk */
	public static final String FORCE_NAME_WALKING = "walking";
	
	/** The default value of {@link #jumpPower} */
	public static final double DEFAULT_JUMP_POWER = 600;
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
	
	/** The velocity added during a jump */
	private double jumpPower;
	
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
	
	// TODO make jumping a force? Also make it that you can control how high you jump, options for in air or for holding down the button
	
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
		this.stopWalking();
		
		this.jumpPower = DEFAULT_JUMP_POWER;
		this.walkAcceleration = DEFAULT_WALK_ACCELERATION;
		this.walkSpeedMax = DEFAULT_WALK_SPEED_MAX;
		this.walkAirControl = DEFAULT_WALK_AIR_CONTROL;
		this.walkFriction = DEFAULT_WALK_FRICTION;
		this.walkStopFriction = DEFAULT_WALK_STOP_FRICTION;
		
		this.walkingForce = new ZVector(0, 0);
		this.addForce(FORCE_NAME_WALKING, this.walkingForce);
	}
	
	@Override
	public void tick(Game game, double dt){
		// Determine the new walking force
		this.updateWalkForce();
		
		// Being off the ground means the mob cannot jump
		if(!this.isOnGround()) this.canJump = false;
		
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
	
	/** Update the value of {@link #walkingForce} based on the current state of this {@link MobThing} */
	public void updateWalkForce(){
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
	
	/** Cause this mob to jump upwards, if the mob is in a position to jump */
	public void jump(){
		if(!canJump) return;
		
		canJump = false;
		// TODO make jumping also adjust the size, as if the mob is leaning down and then jumping
		// TODO should this be setting velocity, or just adding?
		this.setVY(-this.getJumpPower());
	}
	
	@Override
	public void touchFloor(Material m){
		super.touchFloor(m);
		this.canJump = true;
	}
	
	/** @return See {@link #jumpPower} */
	public double getJumpPower(){
		return this.jumpPower;
	}
	
	/** @param jumpPower See {@link #jumpPower} */
	public void setJumpPower(double jumpPower){
		this.jumpPower = jumpPower;
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
