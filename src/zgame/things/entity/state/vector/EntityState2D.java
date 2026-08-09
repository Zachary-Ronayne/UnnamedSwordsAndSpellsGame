package zgame.things.entity.state.vector;

import zgame.core.utils.ZRect2D;
import zgame.physics.V2D;
import zgame.things.core.EntityThing2D;
import zgame.things.entity.state.EntityState;

public class EntityState2D extends EntityState<V2D>{
	
	private final EntityThing2D entity;
	
	public EntityState2D(EntityThing2D entity, V2D zeroVector, double gravityAcceleration, double clampVelocity){
		super(zeroVector, gravityAcceleration, clampVelocity);
		this.entity = entity;
	}
	
	// TODO make a proper implementation after EntityState3D
	
}
