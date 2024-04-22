package zgame.things.entity;

import zgame.physics.ZVector;
import zgame.things.entity.movement.Movement;
import zgame.things.entity.movement.Movement2D;
import zgame.things.type.bounds.HitBox;
import zgame.world.Room;

/**
 * A data object used for storing values related to {@link Movement}
 *
 * @param <H> The type of hitbox which uses this class
 * @param <E> The type of entity which uses this class
 * @param <V> The type of vectors using this class
 */
public abstract class Walk<H extends HitBox<H>, E extends EntityThing<H, E, V, R>, V extends ZVector<V>, R extends Room<H, E, V, R>>{
	
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
	private V jumpingForce;
	
	/** The amount of time, in seconds, this has built up their jump height */
	private double jumpTimeBuilt;
	
	/** true if this is currently building up a jump, false otherwise */
	private boolean buildingJump;
	
	/** true if this is currently stopping its jump, false otherwise */
	private boolean stoppingJump;
	
	/** The {@link EntityThing} using this walk object */
	private final E entity;
	
	/**
	 * Create a new walk object for use in {@link Movement2D}
	 *
	 * @param entity See {@link #entity}
	 */
	public Walk(E entity){
		this.entity = entity;
		
		this.canJump = false;
		this.jumping = false;
		this.stoppingJump = false;
		this.jumpTimeBuilt = 0;
		this.wallJumpAvailable = false;
		
		this.jumpingForce = entity.setForce(FORCE_NAME_JUMPING, this.entity.zeroVector());
	}
	
	/** @return See {@link #entity} */
	public E getEntity(){
		return this.entity;
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
	
	/** @return See {@link #jumpingForce} */
	public V getJumpingForce(){
		return this.jumpingForce;
	}
	
	/** @param jumpForce The amount of force applied to the y axis while this mob is jumping */
	public void setJumpingForce(double jumpForce){
		this.jumpingForce = this.getEntity().setVerticalForce(FORCE_NAME_JUMPING, jumpForce);
	}
	
	/** @param force The amount of force moving during walking */
	public abstract void updateWalkingForce(double force);
	
}
