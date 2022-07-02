package zgame.things.entity;

import zgame.core.Game;
import zgame.physics.ZVector;

/** An {@link EntityThing} which represents some kind of creature which can walk around, i.e. the player, an enemy, an animal, a monster, any NPC, etc. */
public abstract class MobThing extends EntityThing{
	
	/** The default value of {@link #jumpPower} */
	public static final double DEFAULT_JUMP_POWER = 600;
	/** The default value of {@link #walkAcceleration} */
	public static final double DEFAULT_WALK_ACCELERATION = 200000;
	/** The default value of {@link #walkSpeedMax} */
	public static final double DEFAULT_WALK_SPEED_MAX = 300;
	/** The default value of {@link #walkAirControl} */
	public static final double DEFAULT_WALK_AIR_CONTROL = 0.5;
	
	/** The velocity added during a jump */
	private double jumpPower;
	
	/** The acceleration of this {@link MobThing} while walking, i.e., how fast it gets to #walkSpeedMax */
	private double walkAcceleration;
	
	/** The maximum walking speed of this {@link MobThing} */
	private double walkSpeedMax;
	
	/** The ratio of speed this {@link MobThing} can use to walk when it is airborne, i.e. not on the ground */
	private double walkAirControl;
	
	/** true if this {@link MobThing} is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	/** The vector keeping track of the force of this {@link MobThing} walking */
	private ZVector walkingForce;
	
	/**
	 * Create a new {@link MobThing} at the given position
	 * 
	 * @param x The x coordinate of the mob
	 * @param y The y coordinate of the mob
	 */
	public MobThing(double x, double y){
		super(x, y);
		this.canJump = false;
		
		this.jumpPower = DEFAULT_JUMP_POWER;
		this.walkAcceleration = DEFAULT_WALK_ACCELERATION;
		this.walkSpeedMax = DEFAULT_WALK_SPEED_MAX;
		this.walkAirControl = DEFAULT_WALK_AIR_CONTROL;
		
		this.walkingForce = new ZVector(0, 0);
		this.addForce(this.walkingForce);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		if(!this.isOnGround()) this.canJump = false;
	}
	
	/** Cause this mob to jump upwards, if the mob is in a position to jump */
	public void jump(){
		if(!canJump) return;
		
		canJump = false;
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
	
	/** @return See {@link #walkingForce} */
	public ZVector getWalkingForce(){
		return this.walkingForce;
	}
	
	/** @param movement The amount of force applied to the x axis when this mob is walking */
	public void setWalkingForce(double movement){
		this.walkingForce = this.replaceForce(this.walkingForce, movement, 0);
	}
	
}
