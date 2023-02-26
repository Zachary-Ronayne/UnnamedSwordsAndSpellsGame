package zusass.game.things.entities.projectile;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.ZVector;
import zgame.things.entity.EntityThing;
import zgame.things.entity.projectile.UsedProjectile;
import zgame.things.type.CircleHitBox;
import zgame.things.type.HitBox;
import zusass.game.things.entities.mobs.ZusassMob;

/** A {@link UsedProjectile} which applies a magic effect when it hits something other than its caster */
public class MagicProjectile extends UsedProjectile implements CircleHitBox{
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param ignoreUuid See {@link #ignoreUuid}, i.e. the uuid of the caster of this magic projectile
	 * @param launchVelocity The initial velocity of the projectile
	 */
	public MagicProjectile(double x, double y, String ignoreUuid, ZVector launchVelocity){
		super(x, y, ignoreUuid, launchVelocity);
		// Turn off gravity
		// TODO allow the amount of gravity to change
		setForce(EntityThing.FORCE_NAME_GRAVITY, new ZVector());
		// TODO add a spell object as a parameter
		
		// Add a function to effect a hit mob with magic
		this.addHitFunc(ZusassMob.class, m -> m.damage(10));
	}
	
	@Override
	public void hit(Game game, HitBox thing){
		super.hit(game, thing);
		thing.hitBy(this);
	}
	
	@Override
	protected void render(Game game, Renderer r){
		r.setColor(.6, .6, 1, .8);
		r.drawEllipse(this.getBounds());
	}
	
	@Override
	public double getFrictionConstant(){
		return 0;
	}
	
	@Override
	public double getRadius(){
		return 100;
	}
	
	@Override
	public int getRenderPriority(){
		return 200;
	}
}
