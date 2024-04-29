package zgame.things.entity.movement;

import zgame.core.Game;
import zgame.physics.material.Material;
import zgame.things.entity.*;

/** A 3D entity which uses movement capabilities */
public abstract class MovementEntityThing3D extends EntityThing3D implements Movement3D{
	
	/** The {@link Walk} object used by this object's implementation of {@link Movement3D} */
	private final Walk3D walk;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass See {@link #mass}
	 */
	public MovementEntityThing3D(double mass){
		this(0, 0, 0, mass);
	}
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param mass See {@link #mass}
	 */
	public MovementEntityThing3D(double x, double y, double z, double mass){
		super(x, y, z, mass);
		
		this.walk = new Walk3D(this, 0);
	}
	
	@Override
	public void tick(Game game, double dt){
		this.movementTick(game, dt);
		super.tick(game, dt);
	}
	
	@Override
	public Walk3D getWalk(){
		return this.walk;
	}
	
	@Override
	public void touchFloor(Material touched){
		super.touchFloor(touched);
		this.movementTouchFloor(touched);
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
