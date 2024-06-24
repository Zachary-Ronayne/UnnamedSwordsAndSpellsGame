package zgame.things.entity;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector3D;
import zgame.things.entity.movement.Movement3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** A type of {@link Walk} that exists in 3D space */
public class Walk3D extends Walk<HitBox3D, EntityThing3D, ZVector3D, Room3D>{
	
	/** The angle, in radians, on the x z plane that {@link #entity} is walking in */
	private double walkingAngle;
	
	/** The angle on the y axis where {@link #entity} is looking */
	private double verticalAngle;
	
	/** true if {@link #entity} wants to walk, false otherwise */
	private boolean tryingToMove;
	
	/**
	 * Create a new walk object for use in {@link Movement3D}
	 *
	 * @param entity The entity which this walk object will hold data for
	 */
	public Walk3D(EntityThing3D entity, double walkingAngle){
		super(entity);
		
		this.walkingAngle = walkingAngle;
		this.verticalAngle = 0;
		this.tryingToMove = false;
	}
	
	/** @return See {@link #walkingAngle} */
	public double getWalkingAngle(){
		return this.walkingAngle;
	}
	
	/** @param walkingAngle See {@link #walkingAngle} */
	public void setWalkingAngle(double walkingAngle){
		this.walkingAngle = walkingAngle;
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
		this.setWalkingForce(this.getEntity().setForce(FORCE_NAME_WALKING, new ZVector3D(this.walkingAngle, 0, this.tryingToMove ? force : 0, false)));
	}
	
	@Override
	public void updateFlyingForce(double force, boolean applyFacing){
		double angleH;
		double angleV;
		if(applyFacing){
			angleH = this.walkingAngle;
			angleV = this.verticalAngle;
		}
		else{
			var currentVel = this.getEntity().getVelocity();
			angleH = currentVel.getAngleH() + ZMath.PI_BY_2;
			angleV = currentVel.getAngleV();
		}
		
		this.setFlyingForce(this.getEntity().setForce(FORCE_NAME_FLYING, new ZVector3D(angleH, angleV, force, false)));
	}
}
