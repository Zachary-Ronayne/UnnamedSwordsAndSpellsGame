package zgame.things.entity;

import zgame.core.Game;
import zgame.core.utils.ZMathUtils;
import zgame.physics.ZVector;

/** An {@link EntityThing} which represents some kind of creature which can walk around, i.e. the player, an enemy, an animal, a monster, any NPC, etc. */
public abstract class MobThing extends EntityThing{
	
	/** The default value of {@link #jumpPower} */
	public static final double DEFAULT_JUMP_POWER = 600;
	/** The default value of {@link #walkAcceleration} */
	public static final double DEFAULT_WALK_ACCELERATION = 2000;
	/** The default value of {@link #walkSpeedMax} */
	public static final double DEFAULT_WALK_SPEED_MAX = 300;
	/** The default value of {@link #walkAirControl} */
	public static final double DEFAULT_WALK_AIR_CONTROL = 0.5;
	/** The default value of {@link #walkFriction} */
	public static final double DEFAULT_WALK_FRICTION = 0;
	/** The default value of {@link #walkStopFriction} */
	public static final double DEFAULT_WALK_STOP_FRICTION = 1;
	
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
	 */
	private double walkFriction;
	
	/**
	 * The frictional constant used to slow down this {@link MobThing} when it is trying to stop moving
	 * This value represents the amount of the surface's friction which is applied.
	 * Zero means no friction is applied while not walking. One means apply the same amount of friction as normal, higher than 1 means apply extra friction
	 */
	private double walkStopFriction;
	
	/** true if this {@link MobThing} is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	// TODO make jumping a force?
	
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
		this.addForce(this.walkingForce);
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
		
		// If the mob is not trying to move and is moving so slowly that the friction would stop it, then set the x velocity to zero
		// TODO make this a friction value in a material? Or should the friction value it be in mob? Or should this check for velocity be in EntityThing?
		if(!walking && Math.abs(this.getVX()) < 0.00000001);// this.setVX(0); // TODO add this back in
		// Otherwise, the mob should walk
		else{
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
		// The first number is the the friction while trying to move, the second is trying to stop
		return (this.getWalkingDirection() != 0) ? getWalkFriction() : getWalkStopFriction();
	}
	
	/** Cause this mob to jump upwards, if the mob is in a position to jump */
	public void jump(){
		if(!canJump) return;
		
		canJump = false;
		// TODO should this be setting velocitym, or just adding?
		this.setVY(-this.getJumpPower());
	}
	
	@Override
	public void touchFloor(){
		super.touchFloor();
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
		this.walkingForce = this.replaceForce(this.walkingForce, movement, 0);
	}
	
}
