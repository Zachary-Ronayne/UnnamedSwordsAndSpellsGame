package zgame.things.entity.projectile;

import zgame.physics.ZVector;
import zgame.things.type.HitBox;

/** A {@link OnHitProjectile} that is used by a particular object, where this projectile can hit anything except for the given {@link HitBox}'s uuid */
public abstract class UsedProjectile extends OnHitProjectile{
	
	/** The uuid of the object to ignore */
	private final String ignoreUuid;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param ignoreUuid See {@link #ignoreUuid}
	 * @param launchVelocity The initial velocity of the projectile
	 */
	public UsedProjectile(double x, double y, String ignoreUuid, ZVector launchVelocity){
		super(x, y, launchVelocity);
		this.ignoreUuid = ignoreUuid;
	}
	
	@Override
	public boolean willHit(HitBox thing){
		return super.willHit(thing) && !this.ignoreUuid.equals(thing.getUuid());
	}
	
	/** @return See {@link #ignoreUuid} */
	public String getIgnoreUuid(){
		return this.ignoreUuid;
	}
}
