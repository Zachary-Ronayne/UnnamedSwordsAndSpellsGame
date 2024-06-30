package zgame.things.entity;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector3D;
import zgame.things.entity.mobility.Mobility3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** A type of {@link MobilityData} that exists in 3D space */
public class MobilityData3D extends MobilityData<HitBox3D, EntityThing3D, ZVector3D, Room3D>{
	
	/** The angle, in radians, on the x z plane that {@link #entity} is walking in, i.e. on facing on the horizontal axis */
	private double horizontalAngle;
	
	/** The angle on the y axis where {@link #entity} is looking */
	private double verticalAngle;
	
	/** true if {@link #entity} wants to walk, false otherwise */
	private boolean tryingToMove;
	
	/**
	 * Create a new walk object for use in {@link Mobility3D}
	 *
	 * @param entity The entity which this walk object will hold data for
	 */
	public MobilityData3D(EntityThing3D entity, double horizontalAngle){
		super(entity);
		
		this.horizontalAngle = horizontalAngle;
		this.verticalAngle = 0;
		this.tryingToMove = false;
	}
	
	/** @return See {@link #horizontalAngle} */
	public double getHorizontalAngle(){
		return this.horizontalAngle;
	}
	
	/** @param horizontalAngle See {@link #horizontalAngle} */
	public void setHorizontalAngle(double horizontalAngle){
		this.horizontalAngle = horizontalAngle;
	}
	
	/** @return See {@link #verticalAngle} */
	public double getVerticalAngle(){
		return this.verticalAngle;
	}
	
	/** @param verticalAngle See {@link #verticalAngle} */
	public void setVerticalAngle(double verticalAngle){
		this.verticalAngle = verticalAngle;
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
		this.setWalkingForce(this.getEntity().setForce(FORCE_NAME_WALKING, new ZVector3D(this.horizontalAngle, 0, this.tryingToMove ? force : 0, false)));
	}
	
	@Override
	public void updateFlyingForce(double force, boolean applyFacing){
		double angleH;
		double angleV;
		if(applyFacing){
			angleH = this.horizontalAngle;
			angleV = this.verticalAngle;
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
