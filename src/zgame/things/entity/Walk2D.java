package zgame.things.entity;

import zgame.physics.ZVector;

/** A type of {@link Walk} that exists in 2D space */
public class Walk2D extends Walk{
	
	/** The direction this is walking. -1 for walking to the left, 0 for not walking, 1 for walking to the right */
	private int walkingDirection;
	
	/** The vector keeping track of the force of this walking */
	private ZVector walkingForce;
	
	/**
	 * Create a new walk object for use in {@link Movement2D}
	 *
	 * @param thing See {@link #thing}
	 */
	public Walk2D(EntityThing thing){
		super(thing);
		
		this.setWalkingDirection(0);
		this.walkingForce = thing.setForce(FORCE_NAME_WALKING, new ZVector());
	}
	
	/** @return See {@link #walkingDirection} */
	public int getWalkingDirection(){
		return this.walkingDirection;
	}
	
	/** @param direction See {@link #walkingDirection} */
	public void setWalkingDirection(int direction){
		this.walkingDirection = direction;
	}
	
	/** @return See {@link #walkingForce} */
	public ZVector getWalkingForce(){
		return this.walkingForce;
	}
	
	/** @param movement The amount of force applied to the x axis when this mob is walking */
	public void setWalkingForce(double movement){
		this.updateWalkingForce(movement);
	}
	
	@Override
	public void updateWalkingForce(double force){
		this.walkingForce = this.getThing().setForce(FORCE_NAME_WALKING, force, 0);
	}
}
