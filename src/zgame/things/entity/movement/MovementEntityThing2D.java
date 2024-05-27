package zgame.things.entity.movement;

import zgame.core.Game;
import zgame.physics.material.Material;
import zgame.things.entity.EntityThing;
import zgame.things.entity.EntityThing2D;
import zgame.things.entity.Walk;
import zgame.things.entity.Walk2D;

/** A 2D entity which uses movement capabilities */
public abstract class MovementEntityThing2D extends EntityThing2D implements Movement2D{
	
	/** The {@link Walk} object used by this object's implementation of {@link Movement2D} */
	private final Walk2D walk;
	
	/**
	 * Create a new empty entity at (0, 0) with a mass of 100
	 */
	public MovementEntityThing2D(){
		this(0, 0);
	}
	
	/**
	 * Create a new empty entity with a mass of 100
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 */
	public MovementEntityThing2D(double x, double y){
		this(x, y, 100);
	}
	
	/**
	 * Create a new empty entity
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 * @param mass See {@link EntityThing#mass}
	 */
	public MovementEntityThing2D(double x, double y, double mass){
		super(x, y, mass);
		
		this.walk = new Walk2D(this);
	}
	
	@Override
	public void tick(Game game, double dt){
		this.movementTick(game, dt);
		super.tick(game, dt);
	}
	
	@Override
	public Walk2D getWalk(){
		return this.walk;
	}
	
	@Override
	public void touchFloor(Material touched){
		super.touchFloor(touched);
		this.movementTouchFloor(touched);
		this.getWalk().setGroundedSinceLastJump(true);
	}
	
	@Override
	public void leaveFloor(){
		super.leaveFloor();
		this.movementLeaveFloor();
	}
	
	@Override
	public void leaveWall(){
		super.leaveWall();
		this.movementLeaveWall();
	}
}
