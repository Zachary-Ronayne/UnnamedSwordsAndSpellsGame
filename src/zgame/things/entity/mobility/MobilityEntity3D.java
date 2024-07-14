package zgame.things.entity.mobility;

import zgame.core.Game;
import zgame.physics.material.Material;
import zgame.things.entity.*;

/** A 3D entity which uses mobility capabilities */
public abstract class MobilityEntity3D extends EntityThing3D implements Mobility3D{
	
	/** The {@link MobilityData} object used by this object's implementation of {@link Mobility3D} */
	private final MobilityData3D mobilityData;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass See {@link #mass}
	 */
	public MobilityEntity3D(double mass){
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
	public MobilityEntity3D(double x, double y, double z, double mass){
		super(x, y, z, mass);
		
		this.mobilityData = new MobilityData3D(this, 0);
	}
	
	@Override
	public void tick(Game game, double dt){
		this.mobilityTick(dt);
		super.tick(game, dt);
	}
	
	@Override
	public MobilityData3D getMobilityData(){
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
