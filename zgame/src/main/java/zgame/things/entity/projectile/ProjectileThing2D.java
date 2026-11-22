package zgame.things.entity.projectile;

import zgame.physics.V2D;
import zgame.things.core.EntityThing2D;
import zgame.things.core.state.StateHolder;
import zgame.things.entity.state.collision.CollisionUpdate;

import java.util.List;

/** An entity representing a projectile in 2D space */
public abstract class ProjectileThing2D extends EntityThing2D{
	
	// TODO does the data from this projectile need to be moved to state?
	/** The object representing this thing's projectile */
	private final StateHolder<Projectile2D> projectile;
	
	// TODO docs
	public ProjectileThing2D(V2D pos){
		super(pos.getX(), pos.getY(), 1);
		this.projectile = this.registerState(() -> new Projectile2D(this, this));
	}
	
	/** @return See {@link #projectile} */
	public Projectile2D getProjectile(){
		return this.projectile.getCurrent();
	}
	
	@Override
	public void collide(List<CollisionUpdate<V2D>> c){
		super.collide(c);
		this.getProjectile().collided(c);
	}
	
	@Override
	public void tick(double dt){
		super.tick(dt);
		this.getProjectile().tick(dt);
	}
	
	@Override
	public void checkEntityCollision(EntityThing2D entity, double dt){
		super.checkEntityCollision(entity, dt);
		this.getProjectile().checkHitEntity(entity);
	}
}
