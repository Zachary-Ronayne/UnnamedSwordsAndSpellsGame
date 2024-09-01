package zgame.things.entity;

import zgame.physics.ZVector2D;
import zgame.things.entity.mobility.Mobility2D;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room2D;

/** A type of {@link MobilityData} that exists in 2D space */
public class MobilityData2D extends MobilityData<HitBox2D, EntityThing2D, ZVector2D, Room2D>{
	
	/** The direction {@link #entity} walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	private int walkingDirection;
	
	/** The angle, in radians that {@link #entity} is trying to fly at */
	private double flyingAngle;
	
	/**
	 * Create a new walk object for use in {@link Mobility2D}
	 *
	 * @param entity See {@link #entity}
	 */
	public MobilityData2D(EntityThing2D entity){
		super(entity);
		
		this.setWalkingDirection(0);
		this.setFlyingAngle(0);
	}
	
	/** @return See {@link #walkingDirection} */
	public int getWalkingDirection(){
		return this.walkingDirection;
	}
	
	/** @param direction See {@link #walkingDirection} */
	public void setWalkingDirection(int direction){
		this.walkingDirection = direction;
	}
	
	/** @param force The amount of force applied to the x axis when this mob is walking */
	public void setWalkingForce(double force){
		this.updateWalkingForce(force);
	}
	
	@Override
	public void updateWalkingForce(double force){
		this.setWalkingForce(this.getEntity().setHorizontalForce(FORCE_NAME_WALKING, force));
	}
	
	/** @return See {@link #flyingAngle} */
	public double getFlyingAngle(){
		return this.flyingAngle;
	}
	
	/** @param flyingAngle See {@link #flyingAngle} */
	public void setFlyingAngle(double flyingAngle){
		this.flyingAngle = flyingAngle;
	}
	
	@Override
	public void updateFlyingForce(double force, boolean applyFacing){
		// For 2D, apply facing is irrelevant
		this.setFlyingForce(new ZVector2D(this.getFlyingAngle(), force, false));
	}
}
