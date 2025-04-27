package zgame.things.entity.mobility;

import zgame.core.graphics.camera.GameCamera3D;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResult3D;
import zgame.things.entity.*;

/** A 3D entity which uses mobility capabilities */
public abstract class MobilityEntity3D extends EntityThing3D implements Mobility3D{
	
	/** The {@link MobilityData} object used by this object's implementation of {@link Mobility3D} */
	private final MobilityData3D mobilityData;
	
	/** An amount of distance this entity's vision begins from in front of its normal vision position */
	private double visionForwardDistance;
	
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
		this.visionForwardDistance = 0;
		
		this.mobilityData = new MobilityData3D(this);
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
		this.mobilityTick(dt);
		super.tick(dt);
	}
	
	@Override
	public MobilityData3D getMobilityData(){
		return this.mobilityData;
	}
	
	@Override
	public void touchFloor(CollisionResult3D collision){
		super.touchFloor(collision);
		this.mobilityTouchFloor(collision);
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
	
	@Override
	public void updateCameraPos(GameCamera3D camera){
		super.updateCameraPos(camera);
		
		var mobilityData = this.getMobilityData();
		var facingVec = new ZVector3D(mobilityData.getFacingYaw(), mobilityData.getFacingPitch(), this.getVisionForwardDistance(), false);
		camera.addX(facingVec.getX());
		camera.addZ(facingVec.getZ());
		
		/*
		 issue#64
		 It is a little weird that roll is updated here but pitch and yaw are updated directly by the look method.
		 The look method in the Game class maybe shouldn't directly affect the camera, but call a method the game can use to update some
		 */
		camera.setRoll(this.getMobilityData().getFacingRoll());
	}
}
