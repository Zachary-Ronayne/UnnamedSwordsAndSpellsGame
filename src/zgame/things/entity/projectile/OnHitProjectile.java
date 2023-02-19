package zgame.things.entity.projectile;

import zgame.core.Game;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.type.HitBox;

/** A {@link Projectile} which destroys itself when it hits anything */
public abstract class OnHitProjectile extends Projectile{
	
	/** true if the projectile should be removed in the next tick, false otherwise */
	private boolean willRemove;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param launchVelocity The initial velocity of the projectile
	 */
	public OnHitProjectile(double x, double y, ZVector launchVelocity){
		super(x, y, launchVelocity);
		this.willRemove = false;
	}
	
	@Override
	public void tick(Game game, double dt){
		if(this.willRemove) this.removeFrom(game);
		super.tick(game, dt);
	}
	
	@Override
	public void touchFloor(Material touched){
		this.willRemove = true;
	}
	
	@Override
	public void touchCeiling(Material touched){
		this.willRemove = true;
	}
	
	@Override
	public void touchWall(Material touched){
		this.willRemove = true;
	}
	
	@Override
	public void collide(CollisionResponse r){
		// OnHit projectiles are removed on collision
		if(r.isCollided()) this.willRemove = true;
	}
	
	@Override
	public void hit(Game game, HitBox thing){
		this.removeFrom(game);
	}
	
}
