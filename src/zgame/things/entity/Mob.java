package zgame.things.entity;

/** An {@link Entity} which represents some kind of creature which can walk around, i.e. the player, an enemy, an animal, a monster, any NPC, etc. */
public abstract class Mob extends Entity{
	
	/** The velocity added during a jump */
	private double jumpPower;
	
	/** true if this {@link Mob} is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	/**
	 * Create a new {@link Mob} at the given position
	 * 
	 * @param x The x coordinate of the mob
	 * @param y The y coordinate of the mob
	 */
	public Mob(double x, double y){
		super(x, y);
		this.canJump = false;
		this.jumpPower = 300;
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
	
	/** @return See {@link #jumpPower} */
	public double getJumpPower(){
		return this.jumpPower;
	}
	
	/** @param jumpPower See {@link #jumpPower} */
	public void setJumpPower(double jumpPower){
		this.jumpPower = jumpPower;
	}
	
}
