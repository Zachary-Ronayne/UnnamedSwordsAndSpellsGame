package zgame.things.entity.projectile;

import zgame.core.utils.Uuidable;
import zgame.physics.V2D;
import zgame.things.core.EntityThing2D;

/** A 2D implementation of a projectile */
public class Projectile2D extends Projectile<V2D>{
	
	/**
	 * @param entity See {@link #entity}
	 * @param source See {@link #source}
	 */
	public Projectile2D(EntityThing2D entity, Uuidable source){
		super(entity, source);
	}
	
}
