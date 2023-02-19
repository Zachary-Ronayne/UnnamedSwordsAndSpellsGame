package zusass.game.things.entities.projectile;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZStringUtils;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResponse;
import zgame.things.entity.EntityThing;
import zgame.things.entity.projectile.UsedProjectile;
import zgame.things.type.HitBox;
import zgame.things.type.RectangleHitBox;
import zgame.world.Room;

/** A {@link UsedProjectile} which applies a magic effect when it hits something other than its caster */
// TODO make this projectile a circular hitbox
public class MagicProjectile extends UsedProjectile implements RectangleHitBox{
	
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
		setForce(EntityThing.FORCE_NAME_GRAVITY, new ZVector());
		// TODO add a spell object as a parameter
	}
	
	// TODO allow the amount of gravity to change
	
	
	@Override
	public void hit(Game game, HitBox thing){
		super.hit(game, thing);
		ZStringUtils.prints("Magic hit:", thing); // TODO Remove
	}
	
	@Override
	protected void render(Game game, Renderer r){
		r.setColor(.6, .6, 1, .8);
		r.drawRectangle(this.getBounds());
	}
	
	@Override
	public double getFrictionConstant(){
		return 0;
	}
	
	@Override
	public double getWidth(){
		return 20;
	}
	
	@Override
	public double getHeight(){
		return 20;
	}
	
	@Override
	public int getRenderPriority(){
		return 200;
	}
}
