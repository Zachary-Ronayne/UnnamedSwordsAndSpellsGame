package zgame.things.entity;

import zgame.physics.ZVector;
import zgame.things.entity.movement.Movement;
import zgame.things.entity.movement.Movement2D;
import zgame.things.entity.movement.WalkType;
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
	/** The string used to identify the force used to make this fly */
	public static final String FORCE_NAME_FLYING = "flying";
	/** The string used to identify the force used to make this jump */
	public static final String FORCE_NAME_JUMPING = "jumping";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** true if this is in a position where it is allowed to jump, false otherwise */
	private boolean canJump;
	
	/** true if this mob is currently jumping, false otherwise */
	private boolean jumping;
	
	/** true if this mob is able to wall jump, i.e. it has touched the ground since its last wall jump */
	private boolean wallJumpAvailable;
	
	/** The vector keeping track of the force of this walking */
	private V walkingForce;
	
	/** The force of jumping on this */
	private V jumpingForce;
	
	/** The force applied to this thing to modify its movement based on flying */
	private V flyingForce;
	
	/** The amount of time, in seconds, this has built up their jump height */
	private double jumpTimeBuilt;
	
	/** true if this is currently building up a jump, false otherwise */
	private boolean buildingJump;
	
	/** true if this is currently stopping its jump, false otherwise */
	private boolean stoppingJump;
	
	/** true if this has hit the ground since it last jumped */
	private boolean groundedSinceLastJump;
	
	/** The {@link EntityThing} using this walk object */
	private final E entity;
	
	// TODO does it even make sense to use an enum here?
	/** The current state describing the type of this thing's movement */
	private WalkType type;
	
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
		this.groundedSinceLastJump = false;
		
		this.walkingForce = entity.setForce(FORCE_NAME_WALKING, this.entity.zeroVector());
		this.flyingForce = entity.setForce(FORCE_NAME_FLYING, this.entity.zeroVector());
		this.jumpingForce = entity.setForce(FORCE_NAME_JUMPING, this.entity.zeroVector());
		
		this.setType(WalkType.WALKING);
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
	
	/** @return See {@link #groundedSinceLastJump} */
	public boolean isGroundedSinceLastJump(){
		return this.groundedSinceLastJump;
	}
	
	/** @param groundedSinceLastJump See {@link #groundedSinceLastJump} */
	public void setGroundedSinceLastJump(boolean groundedSinceLastJump){
		this.groundedSinceLastJump = groundedSinceLastJump;
	}
	
	/** @return See {@link #walkingForce} */
	public V getWalkingForce(){
		return this.walkingForce;
	}
	
	/** @param walkingForce See {@link #walkingForce} */
	public void setWalkingForce(V walkingForce){
		if(this.getType() != WalkType.WALKING){
			this.walkingForce = walkingForce;
			return;
		}
		
		this.walkingForce = this.getEntity().setForce(FORCE_NAME_WALKING, walkingForce);
	}
	
	/** @return See {@link #flyingForce} */
	public V getFlyingForce(){
		return this.flyingForce;
	}
	
	/** @param flyingForce See {@link #flyingForce} */
	public void setFlyingForce(V flyingForce){
		if(this.getType() != WalkType.FLYING){
			this.flyingForce = flyingForce;
			return;
		}
		
		this.flyingForce = this.getEntity().setForce(FORCE_NAME_FLYING, flyingForce);
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
	
	// TODO should these be the params
	/** @param force The amount of force moving during flying */
	public abstract void updateFlyingForce(double force, boolean applyFacing);
	
	/** @return See {@link #type} */
	public WalkType getType(){
		return this.type;
	}
	
	/** @param type See {@link #type} */
	public void setType(WalkType type){
		// TODO make a better way of doing this kind of thing that scales as new states are added
		this.type = type;
		var thing = this.getEntity();
		switch(this.type){
			case FLYING -> {
				thing.removeForce(FORCE_NAME_WALKING);
				thing.removeForce(FORCE_NAME_JUMPING);
				
				thing.setForce(FORCE_NAME_FLYING, this.flyingForce);
				thing.setGravityLevel(0);
			}
			case WALKING -> {
				thing.removeForce(FORCE_NAME_FLYING);
				
				thing.setForce(FORCE_NAME_WALKING, this.walkingForce);
				this.jumpingForce = thing.zeroVector();
				thing.setForce(FORCE_NAME_JUMPING, this.jumpingForce);
				thing.setGravityLevel(1);
			}
		}
	}
}
