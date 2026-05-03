package zgame.things.entity.mobility;

import zgame.core.graphics.camera.GameCamera3D;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResult3D;
import zgame.things.entity.*;
import zgame.things.entity.state.EntityState;

/** A 3D entity which uses mobility capabilities */
public abstract class MobilityEntity3D extends EntityThing3D implements Mobility3D{
	
	/** An amount of distance this entity's vision begins from in front of its normal vision position */
	private double visionForwardDistance;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass The initial mass of the entity
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
	 * @param mass The initial mass of the entity
	 */
	public MobilityEntity3D(double x, double y, double z, double mass){
		super(x, y, z, mass);
		this.visionForwardDistance = 0;
	}
	
	@Override
	protected EntityState<ZVector3D> initEntityState(ZVector3D zeroVector, double gravityAcceleration, double clampVelocity){
		return new MobilityState3D(gravityAcceleration, clampVelocity);
	}
	
	/** @return See {@link #visionForwardDistance} */
	public double getVisionForwardDistance(){
		return this.visionForwardDistance;
	}
	
	/** @param visionForwardDistance See {@link #visionForwardDistance} */
	public void setVisionForwardDistance(double visionForwardDistance){
		this.visionForwardDistance = visionForwardDistance;
	}
	
	@Override
	public void tick(double dt){
		this.mobilityTick();
		super.tick(dt);
	}
	
	// TODO should the state be obtained this way? Probably replace this with using next or current where applicable
	@Override
	public MobilityState3D getMobilityState(){
		// TODO probably avoid having to cast this
		return (MobilityState3D)this.getCurrent();
	}
	
	@Override
	public void touchFloor(CollisionResult3D collision){
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
	
	@Override
	public void updateCameraPos(GameCamera3D camera){
		super.updateCameraPos(camera);
		camera.setPositionOffset(this.getVisionForwardDistance());
		
		/*
		 issue#64
		 It is a little weird that roll is updated here but pitch and yaw are updated directly by the look method.
		 The look method in the Game class maybe shouldn't directly affect the camera, but call a method the game can use to update some
		 */
		camera.setRoll(this.getMobilityState().getFacingRoll());
	}
}
