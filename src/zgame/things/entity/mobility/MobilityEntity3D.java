package zgame.things.entity.mobility;

import zgame.core.graphics.camera.GameCamera3D;
import zgame.physics.V3D;
import zgame.physics.collision.Collision;
import zgame.things.core.state.StateHolder;
import zgame.things.entity.*;
import zgame.things.core.EntityThing3D;

/** A 3D entity which uses mobility capabilities */
public abstract class MobilityEntity3D extends EntityThing3D implements Mobility3D{
	
	// TODO make proper methods in mobility entity 2D and 3D to access and set state appropriately for mobility
	/** State holding the mobility data for this entity */
	private final StateHolder<MobilityState3D> mobilityState;
	
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
	
	// TODO udpate docs
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass The initial mass of the entity
	 */
	public MobilityEntity3D(double x, double y, double z, double mass){
		super(x, y, z, mass);
		this.visionForwardDistance = 0;
		
		this.mobilityState = this.registerState(this::initMobilityState);
	}
	
	// TODO make proper docs explaining the stages of updating state in each section
	private MobilityState3D initMobilityState(){
		return new MobilityState3D(this.getGravityAcceleration(), this.getClampVelocity());
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
	
	// TODO should all of these things to call mobility touch floor methods be here?
	@Override
	public void touchFloor(Collision<V3D> collision){
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
