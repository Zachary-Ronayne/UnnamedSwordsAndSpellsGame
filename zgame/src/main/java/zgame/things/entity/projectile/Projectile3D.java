package zgame.things.entity.projectile;

import zgame.core.utils.Uuidable;
import zgame.physics.V3D;
import zgame.things.core.EntityThing3D;

/** A 3D implementation of a projectile */
public class Projectile3D extends Projectile<V3D>{
	
	/**
	 * @param entity See {@link #entity}
	 * @param source See {@link #source}
	 */
	public Projectile3D(EntityThing3D entity, Uuidable source){
		super(entity, source);
	}
	
}
