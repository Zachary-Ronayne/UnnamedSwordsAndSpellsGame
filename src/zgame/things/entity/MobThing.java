package zgame.things.entity;

import zgame.physics.ZVector;

/** An {@link EntityThing} which represents some kind of creature which can walk around, i.e. the player, an enemy, an animal, a monster, any NPC, etc. */
public abstract class MobThing extends EntityThing{
	
	/** The velocity added during a jump */
	private double jumpPower;
	
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
		this.jumpPower = 300;
		this.walkingForce = new ZVector(0, 0);
		this.addForce(this.walkingForce);
	}
	
	/** Cause this mob to jump upwards, if the mob is in a position to jump */
	public void jump(){
		if(!canJump) return;
		
		canJump = false;
		this.addVelocityY(-this.getJumpPower());
	}
	
	@Override
	public void touchFloor(){
		super.touchFloor();
		this.canJump = true;
	}

	@Override
	public void touchWall(){
		super.touchWall();
		// TODO make this based on a bounce amount based on the thing collided with
		this.addVelocityX(-this.getVelocity().getX() * 1.2);
	}
	
	/** @return See {@link #jumpPower} */
	public double getJumpPower(){
		return this.jumpPower;
	}
	
	/** @param jumpPower See {@link #jumpPower} */
	public void setJumpPower(double jumpPower){
		this.jumpPower = jumpPower;
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
