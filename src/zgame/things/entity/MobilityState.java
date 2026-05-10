package zgame.things.entity;

import zgame.physics.ZVector;
import zgame.things.entity.mobility.Mobility;
import zgame.things.entity.mobility.Mobility2D;
import zgame.things.entity.mobility.MobilityType;
import zgame.things.entity.state.EntityState;

/**
 * A data object used for storing values related to {@link Mobility}
 *
 * @param <V> The type of vectors using this class
 */
public abstract class MobilityState<V extends ZVector<V>> extends EntityState<V>{
	
	/** The string used to identify the force used to make this walk */
	public static final String FORCE_WALKING = "walking";
	/** The string used to identify the force used to make this fly */
	public static final String FORCE_FLYING = "flying";
	/** The string used to identify the force used to make this jump */
	public static final String FORCE_JUMPING = "jumping";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** true if this is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	/** true if this mob is currently jumping, false otherwise */
	private boolean jumping;
	
	/** true if this mob is able to wall jump, i.e. it has touched the ground since its last wall jump */
	private boolean wallJumpAvailable;
	
	/** The amount of time, in seconds, this has built up their jump height */
	private double jumpTimeBuilt;
	
	/** true if this is currently building up a jump, false otherwise */
	private boolean buildingJump;
	
	/** true if this is currently stopping its jump, false otherwise */
	private boolean stoppingJump;
	
	/** true if this has hit the ground since it last jumped */
	private boolean groundedSinceLastJump;
	
	/** The current state describing the type of mobility */
	private MobilityType type;
	
	/**
	 * Create a new walk object for use in {@link Mobility2D}
	 *
	 * @param zeroVector A vector with magnitude zero
	 * @param gravityAcceleration The acceleration of gravity
	 * @param clampVelocity A velocity where if velocity magnitude reaches a value below this, velocity will be zero
	 */
	public MobilityState(V zeroVector, double gravityAcceleration, double clampVelocity){
		super(zeroVector, gravityAcceleration, clampVelocity);
		
		this.canJump = false;
		this.jumping = false;
		this.stoppingJump = false;
		this.jumpTimeBuilt = 0;
		this.wallJumpAvailable = false;
		this.groundedSinceLastJump = false;
		
		// Init forces
		this.initForce(FORCE_WALKING);
		this.initForce(FORCE_FLYING);
		this.initForce(FORCE_JUMPING);
		
		this.setType(MobilityType.WALKING);
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
	
	/** @return See {@link #groundedSinceLastJump} */
	public boolean isGroundedSinceLastJump(){
		return this.groundedSinceLastJump;
	}
	
	/** @param groundedSinceLastJump See {@link #groundedSinceLastJump} */
	public void setGroundedSinceLastJump(boolean groundedSinceLastJump){
		this.groundedSinceLastJump = groundedSinceLastJump;
	}
	
	/** @param force The amount of force moving during walking */
	public abstract void updateWalkingForce(double force);
	
	/**
	 * @param force The amount of force moving during flying
	 * @param applyFacing true to apply the force in the facing direction, false for the movement direction
	 */
	public abstract void updateFlyingForce(double force, boolean applyFacing);
	
	/** @return See {@link #type} */
	public MobilityType getType(){
		return this.type;
	}
	
	/** @param type See {@link #type} */
	public void setType(MobilityType type){
		this.type = type;
		this.type.updateForces(this);
	}
	
	/** Update all necessary forces to walk and no other forms of movement */
	public void updateWalkForces(){
		this.clearForce(FORCE_FLYING);
		this.clearForce(FORCE_WALKING);
		this.clearForce(FORCE_JUMPING);
		this.setGravityLevel(1);
	}
	
	/** Update all necessary forces to fly and no other forms of movement */
	public void updateFlyForces(){
		this.clearForce(FORCE_FLYING);
		this.clearForce(FORCE_WALKING);
		this.clearForce(FORCE_JUMPING);
		// TODO probably avoid setting gravity level directly here in case something else affects it, maybe just remove the gravity vector
		this.setGravityLevel(0);
	}
	
}
