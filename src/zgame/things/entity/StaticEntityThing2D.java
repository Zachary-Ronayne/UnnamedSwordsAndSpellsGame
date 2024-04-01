package zgame.things.entity;

import zgame.core.Game;
import zgame.physics.ZVector2D;
import zgame.physics.collision.CollisionResponse;
import zgame.things.BaseTags;

/** A 2D entity which does not move */
public abstract class StaticEntityThing2D extends EntityThingRect2D{
	
	// TODO make this not coded as an entity? Just another game thing?
	
	// TODO avoid doing any force calculations for this entity
	
	/**
	 * Create a new entity with the given values
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 */
	public StaticEntityThing2D(double x, double y, double w, double h){
		super(x, y, w, h, 0);
		this.addTags(BaseTags.PROJECTILE_NOT_COLLIDE);
		this.setGravityLevel(0);
	}
	
	@Override
	public double getFrictionConstant(){
		return 0;
	}
	
	// TODO should these overrides be here? Should this be implemented differently?
	@Override
	public void collide(CollisionResponse r){
		// Do nothing when colliding with a static entity by default
	}
	
	@Override
	public void checkEntityCollision(Game game, EntityThing2D entity, double dt){
		// Do nothing when colliding with a static entity by default
	}
	
	@Override
	public void checkEntityCollisions(Game game, double dt){
		// Do nothing when colliding with a static entity by default
	}
	
	@Override
	public void moveEntity(ZVector2D acceleration, double dt){
		// Do nothing when moving with a static entity by default
	}
	
	@Override
	public boolean canEnterRooms(){
		return false;
	}
}