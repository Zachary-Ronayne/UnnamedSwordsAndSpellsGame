package zgame.things.entity;

import zgame.physics.ZVector3D;
import zgame.things.entity.movement.Movement3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** A type of {@link Walk} that exists in 3D space */
public class Walk3D extends Walk<HitBox3D, EntityThing3D, ZVector3D, Room3D>{
	
	/** The angle, in radians, on the x z plane that {@link #entity} is walking in */
	private double walkingAngle;
	
	/** true if {@link #entity} wants to walk, false otherwise */
	private boolean tryingToMove;
	
	/**
	 * Create a new walk object for use in {@link Movement3D}
	 *
	 * @param entity The entity which this walk object will hold data for
	 */
	public Walk3D(EntityThing3D entity, double walkingAngle){
		super(entity, entity.setForce(FORCE_NAME_WALKING, new ZVector3D()));
		
		this.walkingAngle = walkingAngle;
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
}
