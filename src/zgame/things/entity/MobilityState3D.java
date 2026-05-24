package zgame.things.entity;

import zgame.physics.V3D;
import zgame.things.entity.mobility.Mobility3D;
import zgame.things.entity.mobility.MobilityType;

/** A type of {@link MobilityState} that exists in 3D space */
public class MobilityState3D extends MobilityState<V3D>{
	
	/** The angle, in radians, on the x z plane attempting to move in, i.e. trying to move on the horizontal axis */
	private double movingYaw;
	
	/** The angle on the y axis trying to move */
	private double movingPitch;
	
	/** The angle, in radians, on the x z plane is facing in, i.e. facing on the horizontal axis */
	private double facingYaw;
	
	/** The angle on the y axis facing */
	private double facingPitch;
	
	/** The angle rolled at with its perspective */
	private double facingRoll;
	
	/** true if wanting to walk, false otherwise */
	private boolean tryingToMove;
	
	/**
	 * Create a new walk object for use in {@link Mobility3D}
	 */
	public MobilityState3D(double gravityAcceleration, double clampVelocity){
		super(new V3D(), gravityAcceleration, clampVelocity);
		
		this.movingYaw = 0;
		this.movingPitch = 0;
		this.facingYaw = 0;
		this.facingPitch = 0;
		this.facingRoll = 0;
		this.tryingToMove = false;
	}
	
	/** @return See {@link #movingYaw} */
	public double getMovingYaw(){
		return this.movingYaw;
	}
	
	/** @param movingYaw See {@link #movingYaw} */
	public void setMovingYaw(double movingYaw){
		this.movingYaw = movingYaw;
	}
	
	/** @return See {@link #movingPitch} */
	public double getMovingPitch(){
		return this.movingPitch;
	}
	
	/** @param movingPitch See {@link #movingPitch} */
	public void setMovingPitch(double movingPitch){
		this.movingPitch = movingPitch;
	}
	
	/** @return See {@link #facingYaw} */
	public double getFacingYaw(){
		return this.facingYaw;
	}
	
	/** @param facingYaw See {@link #facingYaw} */
	public void setFacingYaw(double facingYaw){
		this.facingYaw = facingYaw;
	}
	
	/** @return See {@link #facingPitch} */
	public double getFacingPitch(){
		return this.facingPitch;
	}
	
	/** @param facingPitch See {@link #facingPitch} */
	public void setFacingPitch(double facingPitch){
		this.facingPitch = facingPitch;
	}
	
	/** @return See {@link #facingRoll} */
	public double getFacingRoll(){
		return this.facingRoll;
	}
	
	/** @param facingRoll See {@link #facingRoll} */
	public void setFacingRoll(double facingRoll){
		this.facingRoll = facingRoll;
	}
	
	/** @param facingRoll The value to add to {@link #facingRoll} */
	public void addFacingRoll(double facingRoll){
		this.setFacingRoll(this.getFacingRoll() + facingRoll);
	}
	
	/** @return See {@link #tryingToMove} */
	public boolean isTryingToMove(){
		return this.tryingToMove;
	}
	
	/** @return true if trying to move vertically, false otherwise */
	public boolean isTryingToMoveVertical(){
		return this.tryingToMove && this.getType() != MobilityType.WALKING;
	}
	
	/** @param tryingToMove See {@link #tryingToMove} */
	public void setTryingToMove(boolean tryingToMove){
		this.tryingToMove = tryingToMove;
	}
	
	@Override
	public void updateWalkingForce(double force){
		this.attemptSetForce(FORCE_WALKING, new V3D(this.movingYaw, 0, this.tryingToMove ? force : 0, false));
	}
	
	@Override
	public void updateFlyingForce(double force, boolean applyFacing){
		double yaw;
		double pitch;
		// When trying to move, go in the direction that movement is trying to happen in
		if(applyFacing){
			yaw = this.movingYaw;
			pitch = this.movingPitch;
		}
		// When not trying to move, go based on the direction that movement is happening in
		else{
			var currentVel = this.getVelocity();
			yaw = currentVel.getYaw();
			pitch = currentVel.getPitch();
		}
		
		this.attemptSetForce(FORCE_FLYING, new V3D(yaw, pitch, force, false));
	}
}
