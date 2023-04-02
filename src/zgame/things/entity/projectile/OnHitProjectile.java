package zgame.things.entity.projectile;

import zgame.core.Game;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.BaseTags;
import zgame.things.type.HitBox;

/** A {@link Projectile} which destroys itself when it hits anything */
public abstract class OnHitProjectile extends Projectile{
	
	// TODO implement these projectile sub classes as parameters instead of nested classes
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param launchVelocity The initial velocity of the projectile
	 */
	public OnHitProjectile(double x, double y, ZVector launchVelocity){
		super(x, y, launchVelocity);
		this.addTags(BaseTags.PROJECTILE_NOT_COLLIDE);
	}
	
	@Override
	public void touchFloor(Material touched){
		this.removeNext();
	}
	
	@Override
	public void touchCeiling(Material touched){
		this.removeNext();
	}
	
	@Override
	public void touchWall(Material touched){
		this.removeNext();
	}
	
	@Override
	public void collide(CollisionResponse r){
		// OnHit projectiles are removed on collision
		if(r.isCollided()) this.removeNext();
	}
	
	@Override
	public void hit(Game game, HitBox thing){
		this.removeFrom(game);
	}
	
}
