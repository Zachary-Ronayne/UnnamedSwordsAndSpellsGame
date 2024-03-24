package zgame.things.entity.projectile;

import zgame.physics.ZVector2D;
import zgame.things.type.bounds.HitBox;
import zgame.things.type.bounds.HitBox2D;

/** A {@link Projectile} that is used by a particular object, where this projectile can hit anything except for the given {@link HitBox}'s uuid */
public abstract class UsedProjectile extends Projectile{
	
	/** The uuid of the object to ignore, i.e. the source of this projectile */
	private final String sourceId;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param sourceId See {@link #sourceId}
	 * @param launchVelocity The initial velocity of the projectile
	 */
	public UsedProjectile(double x, double y, String sourceId, ZVector2D launchVelocity){
		super(x, y, launchVelocity);
		this.sourceId = sourceId;
	}
	
	@Override
	public boolean willHit(HitBox2D thing){
		return super.willHit(thing) && !this.sourceId.equals(thing.getUuid());
	}
	
	/** @return See {@link #sourceId} */
	public String getSourceId(){
		return this.sourceId;
	}
}
