package zgame.things.entity.state.vector;

import zgame.physics.V2D;
import zgame.things.entity.EntityThing2D;
import zgame.things.entity.state.EntityState;
import zgame.things.type.bounds.HitBox2D;

// TODO is the the correct pattern to follow?
public class EntityState2D extends EntityState<V2D> implements HitBox2D{
	
	private final EntityThing2D entity;
	
	public EntityState2D(EntityThing2D entity, V2D zeroVector, double gravityAcceleration, double clampVelocity){
		super(zeroVector, gravityAcceleration, clampVelocity);
		this.entity = entity;
	}
	
	// TODO make a proper implementation after EntityState3D
	
}
