package zgame.things.entity.mobility;

import zgame.physics.V2D;
import zgame.physics.collision.Collision;
import zgame.physics.collision.Collision2D;
import zgame.things.core.EntityThing2D;
import zgame.things.core.StateHolder;
import zgame.things.entity.MobilityState;
import zgame.things.entity.MobilityState2D;
import zgame.things.entity.state.EntityState;

/** A 2D entity which uses mobility capabilities */
public abstract class MobilityEntity2D extends EntityThing2D{
	
	/** State holding the mobility data for this entity */
	private final StateHolder<MobilityState2D> mobilityState;
	
	/**
	 * Create a new empty entity at (0, 0) with a mass of 100
	 */
	public MobilityEntity2D(){
		this(0, 0);
	}
	
	/**
	 * Create a new empty entity with a mass of 100
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 */
	public MobilityEntity2D(double x, double y){
		this(x, y, 100);
	}
	
	/**
	 * Create a new empty entity
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 * @param mass The initial mass of the entity
	 */
	public MobilityEntity2D(double x, double y, double mass){
		super(x, y, mass);
		this.mobilityState = new StateHolder<>(this::initState);
	}
	
	// TODO make proper docs explaining the stages of updating state in each section
	private MobilityState2D initState(){
		return new MobilityState2D(this.getGravityAcceleration(), this.getClampVelocity());
	}
	
	// TODO where should these be called?
	@Override
	public void tick(double dt){
		this.mobilityTick();
		super.tick(dt);
	}
	
	@Override
	public void touchFloor(Collision<V2D> collision){
		super.touchFloor(collision);
		this.mobilityTouchFloor();
		this.getMobilityState().setGroundedSinceLastJump(true);
	}
	
	@Override
	public void leaveFloor(){
		super.leaveFloor();
		this.mobilityLeaveFloor();
	}
	
	@Override
	public void leaveWall(){
		super.leaveWall();
		this.mobilityLeaveWall();
	}
}
