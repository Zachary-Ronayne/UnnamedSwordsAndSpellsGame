package zgame.things.entity;

import zgame.physics.ZVector2D;
import zgame.things.entity.movement.Movement2D;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room2D;

/** A type of {@link Walk} that exists in 2D space */
public class Walk2D extends Walk<HitBox2D, EntityThing2D, ZVector2D, Room2D>{
	
	/** The direction this is walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	private int walkingDirection;
	
	/**
	 * Create a new walk object for use in {@link Movement2D}
	 *
	 * @param entity See {@link #entity}
	 */
	public Walk2D(EntityThing2D entity){
		super(entity);
		
		this.setWalkingDirection(0);
	}
	
	/** @return See {@link #walkingDirection} */
	public int getWalkingDirection(){
		return this.walkingDirection;
	}
	
	/** @param direction See {@link #walkingDirection} */
	public void setWalkingDirection(int direction){
		this.walkingDirection = direction;
	}
	
	/** @param movement The amount of force applied to the x axis when this mob is walking */
	public void setWalkingForce(double movement){
		this.updateWalkingForce(movement);
	}
	
	@Override
	public void updateWalkingForce(double force){
		this.setWalkingForce(this.getEntity().setHorizontalForce(FORCE_NAME_WALKING, force));
	}
	
	@Override
	public void updateFlyingForce(double force, boolean applyFacing){
		// TODO implement with an angle
		this.setWalkingForce(this.getEntity().setForce(FORCE_NAME_FLYING, new ZVector2D()));
	}
}
