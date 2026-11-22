package zgame.things.entity.state;

import zgame.physics.V3D;
import zgame.things.core.EntityThing3D;

// TODO what should actually be here?
public class EntityState3D extends EntityState<V3D>{
	
	private final EntityThing3D entity;
	
	public EntityState3D(EntityThing3D entity, V3D zeroVector, double gravityAcceleration, double clampVelocity){
		super(zeroVector, gravityAcceleration, clampVelocity);
		this.entity = entity;
	}
}
