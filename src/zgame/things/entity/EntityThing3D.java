package zgame.things.entity;

import zgame.core.GameTickable;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.HitBox3D;

/**
 * An {@link EntityThing} in 3D
 */
public abstract class EntityThing3D extends EntityThing<HitBox3D, EntityThing3D> implements GameTickable, HitBox3D, Bounds3D{
	
	// TODO implement
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass See {@link #mass}
	 */
	public EntityThing3D(double mass){
		super(mass);
	}
}
