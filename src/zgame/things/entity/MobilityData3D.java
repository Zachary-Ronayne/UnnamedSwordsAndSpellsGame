package zgame.things.entity;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector3D;
import zgame.things.entity.mobility.Mobility3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** A type of {@link MobilityData} that exists in 3D space */
public class MobilityData3D extends MobilityData<HitBox3D, EntityThing3D, ZVector3D, Room3D>{
	
	/** The angle, in radians, on the x z plane that {@link #entity} is attempting to move in, i.e. trying to move on the horizontal axis */
	private double movingHorizontalAngle;
	
	/** The angle on the y axis where {@link #entity} is trying to move */
	private double movingVerticalAngle;
	
	/** The angle, in radians, on the x z plane that {@link #entity} is facing in, i.e. facing on the horizontal axis */
	private double facingHorizontalAngle;
	
	/** The angle on the y axis where {@link #entity} is facing */
	private double facingVerticalAngle;
	
	/** true if {@link #entity} wants to walk, false otherwise */
	private boolean tryingToMove;
	
	/**
	 * Create a new walk object for use in {@link Mobility3D}
	 *
	 * @param entity The entity which this walk object will hold data for
	 */
	public MobilityData3D(EntityThing3D entity, double movingHorizontalAngle){
		super(entity);
		
		this.movingHorizontalAngle = movingHorizontalAngle;
		this.movingVerticalAngle = 0;
		this.facingHorizontalAngle = this.movingHorizontalAngle;
		this.facingVerticalAngle = this.movingVerticalAngle;
		this.tryingToMove = false;
	}
	
	/** @return See {@link #movingHorizontalAngle} */
	public double getMovingHorizontalAngle(){
		return this.movingHorizontalAngle;
	}
	
	/** @param movingHorizontalAngle See {@link #movingHorizontalAngle} */
	public void setMovingHorizontalAngle(double movingHorizontalAngle){
		this.movingHorizontalAngle = movingHorizontalAngle;
	}
	
	/** @return See {@link #movingVerticalAngle} */
	public double getMovingVerticalAngle(){
		return this.movingVerticalAngle;
	}
	
	/** @param movingVerticalAngle See {@link #movingVerticalAngle} */
	public void setMovingVerticalAngle(double movingVerticalAngle){
		this.movingVerticalAngle = movingVerticalAngle;
	}
	
	/** @return See {@link #facingHorizontalAngle} */
	public double getFacingHorizontalAngle(){
		return this.facingHorizontalAngle;
	}
	
	/** @param facingHorizontalAngle See {@link #facingHorizontalAngle} */
	public void setFacingHorizontalAngle(double facingHorizontalAngle){
		this.facingHorizontalAngle = facingHorizontalAngle;
	}
	
	/** @return See {@link #facingVerticalAngle} */
	public double getFacingVerticalAngle(){
		return this.facingVerticalAngle;
	}
	
	/** @param facingVerticalAngle See {@link #facingVerticalAngle} */
	public void setFacingVerticalAngle(double facingVerticalAngle){
		this.facingVerticalAngle = facingVerticalAngle;
	}
	
	/** @return See {@link #tryingToMove} */
	public boolean isTryingToMove(){
		return this.tryingToMove;
	}
	
	/** @param tryingToMove See {@link #tryingToMove} */
	public void setTryingToMove(boolean tryingToMove){
		this.tryingToMove = tryingToMove;
	}
	
	@Override
	public void updateWalkingForce(double force){
		this.setWalkingForce(this.getEntity().setForce(FORCE_NAME_WALKING, new ZVector3D(this.movingHorizontalAngle, 0, this.tryingToMove ? force : 0, false)));
	}
	
	@Override
	public void updateFlyingForce(double force, boolean applyFacing){
		double angleH;
		double angleV;
		if(applyFacing){
			angleH = this.movingHorizontalAngle;
			angleV = this.movingVerticalAngle;
		}
		else{
			var currentVel = this.getEntity().getVelocity();
			// TODO why does this need to be modified by pi/2?
			angleH = currentVel.getAngleH() + ZMath.PI_BY_2;
			angleV = currentVel.getAngleV();
		}
		
		this.setFlyingForce(this.getEntity().setForce(FORCE_NAME_FLYING, new ZVector3D(angleH, angleV, force, false)));
	}
}
