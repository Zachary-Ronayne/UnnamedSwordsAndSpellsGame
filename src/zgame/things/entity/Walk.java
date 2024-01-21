package zgame.things.entity;

import zgame.physics.ZVector;

/** A data object used for storing values related to {@link Movement2D} */
public class Walk{
	
	/** The string used to identify the force used to make this walk */
	public static final String FORCE_NAME_WALKING = "walking";
	/** The string used to identify the force used to make this jump */
	public static final String FORCE_NAME_JUMPING = "jumping";
	/** The string used to identify the force used to make this stop jumping */
	public static final String FORCE_NAME_JUMPING_STOP = "jumpingStop";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** true if this is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	/** true if this mob is currently jumping, false otherwise */
	private boolean jumping;
	
	/** true if this mob is able to wall jump, i.e. it has touched the ground since its last wall jump */
	private boolean wallJumpAvailable;
	
	/** The force of jumping on this */
	private ZVector jumpingForce;
	
	/** The amount of time, in seconds, this has built up their jump height */
	private double jumpTimeBuilt;
	
	/** true if this is currently building up a jump, false otherwise */
	private boolean buildingJump;
	
	/** true if this is currently stopping its jump, false otherwise */
	private boolean stoppingJump;
	
	/** The vector keeping track of the force of this walking */
	private ZVector walkingForce;
	
	/** The direction this is walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	private int walkingDirection;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The {@link EntityThing} using this walk object */
	private final EntityThing thing;
	
	/**
	 * Create a new walk object for use in {@link Movement2D}
	 * @param thing See {@link #thing}
	 */
	public Walk(EntityThing thing){
		this.thing = thing;
		
		this.canJump = false;
		this.jumping = false;
		this.stoppingJump = false;
		this.jumpTimeBuilt = 0;
		this.wallJumpAvailable = false;
		
		this.walkingForce = thing.setForce(FORCE_NAME_WALKING, new ZVector());
		this.jumpingForce = thing.setForce(FORCE_NAME_JUMPING, new ZVector());
	}
	
	/** @return See {@link #thing} */
	public EntityThing getThing(){
		return this.thing;
	}
	
	/** @return true if this is in a position where it is allowed to jump, false otherwise */
	public boolean isCanJump(){
		return this.canJump;
	}
	
	/** @param canJump See {@link #canJump} */
	public void setCanJump(boolean canJump){
		this.canJump = canJump;
	}
	
	/** @return See true if this mob is currently jumping, false otherwise */
	public boolean isJumping(){
		return jumping;
	}
	
	/** @param jumping See {@link #jumping} */
	public void setJumping(boolean jumping){
		this.jumping = jumping;
	}
	
	/** @return See {@link #stoppingJump} */
	public boolean isStoppingJump(){
		return this.stoppingJump;
	}
	
	/** @param stoppingJump See {@link #stoppingJump} */
	public void setStoppingJump(boolean stoppingJump){
		this.stoppingJump = stoppingJump;
	}
	
	/** @return See {@link #jumpTimeBuilt} */
	public double getJumpTimeBuilt(){
		return this.jumpTimeBuilt;
	}
	
	/** @param jumpTimeBuilt See {@link #jumpTimeBuilt} */
	public void setJumpTimeBuilt(double jumpTimeBuilt){
		this.jumpTimeBuilt = jumpTimeBuilt;
	}
	
	/** @param jumpTimeBuilt The amount to add to see {@link #jumpTimeBuilt} */
	public void addJumpTimeBuilt(double jumpTimeBuilt){
		this.setJumpTimeBuilt(this.getJumpTimeBuilt() + jumpTimeBuilt);
	}
	
	/** @return See {@link #buildingJump} */
	public boolean isBuildingJump(){
		return this.buildingJump;
	}
	
	/** @param buildingJump See {@link #buildingJump} */
	public void setBuildingJump(boolean buildingJump){
		this.buildingJump = buildingJump;
	}
	
	/** @return See {@link #wallJumpAvailable} */
	public boolean isWallJumpAvailable(){
		return this.wallJumpAvailable;
	}
	
	/** @param wallJumpAvailable See {@link #wallJumpAvailable} */
	public void setWallJumpAvailable(boolean wallJumpAvailable){
		this.wallJumpAvailable = wallJumpAvailable;
	}
	
	/** @return The direction this is walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	public int getWalkingDirection(){
		return this.walkingDirection;
	}
	
	/** @param direction The new value for {@link #getWalkingDirection()} */
	public void setWalkingDirection(int direction){
		this.walkingDirection = direction;
	}
	
	/** @return See {@link #jumpingForce} */
	public ZVector getJumpingForce(){
		return this.jumpingForce;
	}
	
	/** @param jumpingForce See {@link #jumpingForce} */
	public void setJumpingForce(ZVector jumpingForce){
		this.jumpingForce = jumpingForce;
	}
	
	/** @return See {@link #walkingForce} */
	public ZVector getWalkingForce(){
		return this.walkingForce;
	}
	
	// TODO move this to Movement2D or move the ones in that interface to here
	/** @param movement The amount of force applied to the x axis when this mob is walking */
	public void setWalkingForce(double movement){
		this.walkingForce = this.getThing().setForce(FORCE_NAME_WALKING, movement, 0);
	}
}
