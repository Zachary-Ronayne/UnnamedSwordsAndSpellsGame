package zgame.things.entity.mobility;

import zgame.core.Game;
import zgame.physics.material.Material;
import zgame.things.entity.EntityThing;
import zgame.things.entity.EntityThing2D;
import zgame.things.entity.MobilityData;
import zgame.things.entity.MobilityData2D;

/** A 2D entity which uses mobility capabilities */
public abstract class MobilityEntityThing2D extends EntityThing2D implements Mobility2D{
	
	/** The {@link MobilityData} object used by this object's implementation of {@link Mobility2D} */
	private final MobilityData2D mobilityData;
	
	/**
	 * Create a new empty entity at (0, 0) with a mass of 100
	 */
	public MobilityEntityThing2D(){
		this(0, 0);
	}
	
	/**
	 * Create a new empty entity with a mass of 100
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 */
	public MobilityEntityThing2D(double x, double y){
		this(x, y, 100);
	}
	
	/**
	 * Create a new empty entity
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 * @param mass See {@link EntityThing#mass}
	 */
	public MobilityEntityThing2D(double x, double y, double mass){
		super(x, y, mass);
		
		this.mobilityData = new MobilityData2D(this);
	}
	
	@Override
	public void tick(Game game, double dt){
		this.mobilityTick(game, dt);
		super.tick(game, dt);
	}
	
	@Override
	public MobilityData2D getMobilityData(){
		return this.mobilityData;
	}
	
	@Override
	public void touchFloor(Material touched){
		super.touchFloor(touched);
		this.mobilityTouchFloor(touched);
		this.getMobilityData().setGroundedSinceLastJump(true);
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
