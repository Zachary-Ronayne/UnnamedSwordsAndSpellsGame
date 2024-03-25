package zgame.things.entity;

import zgame.physics.ZVector3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** A type of {@link Walk} that exists in 3D space */
public class Walk3D extends Walk<HitBox3D, EntityThing3D, ZVector3D, Room3D>{
	
	/** The vector keeping track of the force of this walking */
	private ZVector3D walkingForce;
	
	/**
	 * Create a new walk object for use in {@link Movement3D}
	 *
	 * @param thing The thing which this walk object will hold data for
	 */
	public Walk3D(EntityThing3D thing){
		super(thing);
	}
	
	@Override
	public void updateWalkingForce(double force){
		// TODO implement
	}
}
