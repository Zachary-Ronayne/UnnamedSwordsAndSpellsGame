package zgame.things.entity.state.vector;

import zgame.physics.V2D;
import zgame.things.core.EntityThing2D;
import zgame.things.entity.state.EntityState;
import zgame.things.type.bounds.HitBox2D;

public abstract class EntityState2D <ES extends EntityState2D<ES>> extends EntityState<V2D> implements HitBox2D{
	
	private final EntityThing2D<ES> entity;
	
	public EntityState2D(EntityThing2D<ES> entity, V2D zeroVector, double gravityAcceleration, double clampVelocity){
		super(zeroVector, gravityAcceleration, clampVelocity);
		this.entity = entity;
	}
	
	@Override
	public V2D getCenterPosition(){
		return HitBox2D.super.getCenterPosition();
	}
	
	// TODO make a proper implementation after EntityState3D
	
}
