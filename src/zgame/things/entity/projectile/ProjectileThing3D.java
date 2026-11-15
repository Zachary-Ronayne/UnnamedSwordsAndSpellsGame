package zgame.things.entity.projectile;

import zgame.physics.V3D;
import zgame.things.core.EntityThing3D;
import zgame.things.core.state.StateHolder;
import zgame.things.entity.state.collision.CollisionUpdate;

import java.util.List;

/** An entity representing a projectile in 3D space */
public abstract class ProjectileThing3D extends EntityThing3D{
	
	/** The object representing this thing's projectile */
	private final StateHolder<Projectile3D> projectile;
	
	// TODO docs
	public ProjectileThing3D(V3D pos){
		super(pos.getX(), pos.getY(), pos.getZ(), 1);
		this.projectile = this.registerState(() -> new Projectile3D(this, this));
	}
	
	/** @return See {@link #projectile} */
	public Projectile3D getProjectile(){
		return this.projectile.getCurrent();
	}
	
	@Override
	public void collide(List<CollisionUpdate<V3D>> c){
		super.collide(c);
		this.getProjectile().collided(c);
	}
	
	@Override
	public void tick(double dt){
		super.tick(dt);
		this.getProjectile().tick(dt);
	}
	
	@Override
	public void checkEntityCollision(EntityThing3D entity, double dt){
		super.checkEntityCollision(entity, dt);
		this.getProjectile().checkHitEntity(entity);
	}
}
