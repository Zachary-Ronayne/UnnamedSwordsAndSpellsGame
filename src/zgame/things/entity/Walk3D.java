package zgame.things.entity;

import zgame.physics.ZVector;

/** A type of {@link Walk} that exists in 3D space */
public class Walk3D extends Walk{
	
	// TODO make 2D and 3D vectors
	/** The vector keeping track of the force of this walking */
	private ZVector walkingForce;
	
	// TODO define what thing is in docs
	/**
	 * Create a new walk object for use in {@link Movement2D}
	 */
	// TODO make a hitbox 3D type and use it
	public Walk3D(EntityThing<?, ?> thing){
		super(thing);
	}
	
	@Override
	public void updateWalkingForce(double force){
		// TODO implement
	}
}
