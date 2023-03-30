package zusass.game.things.entities.projectile;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.ZVector;
import zgame.things.entity.projectile.UsedProjectile;
import zgame.things.type.CircleHitBox;
import zgame.things.type.HitBox;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** A {@link UsedProjectile} which applies a magic effect when it hits something other than its caster */
public class MagicProjectile extends UsedProjectile implements CircleHitBox{
	
	/** The radius of the projectile */
	private double radius;
	
	/** The spell to apply when this projectile hits a mob */
	private final SpellEffect effect;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param sourceId See {@link #sourceId}, i.e. the uuid of the caster of this magic projectile
	 * @param launchVelocity The initial velocity of the projectile
	 * @param effect See {@link #effect}
	 */
	public MagicProjectile(double x, double y, String sourceId, ZVector launchVelocity, SpellEffect effect){
		this(x, y, 10, sourceId, launchVelocity, effect);
	}
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param radius See {@link #radius}
	 * @param sourceId See {@link #sourceId}, i.e. the uuid of the caster of this magic projectile
	 * @param launchVelocity The initial velocity of the projectile
	 * @param effect See {@link #effect}
	 */
	public MagicProjectile(double x, double y, double radius, String sourceId, ZVector launchVelocity, SpellEffect effect){
		super(x, y, sourceId, launchVelocity);
		this.setRadius(radius);
		this.effect = effect;
		
		// Turn off gravity
		this.setGravityLevel(0);
		
		// Add a function to effect a hit mob with magic
		this.addHitFunc(ZusassMob.class, m -> this.getEffect().apply(sourceId, m));
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
	
	/** @return See {@link #effect} */
	public SpellEffect getEffect(){
		return this.effect;
	}
	
	@Override
	public double getFrictionConstant(){
		return 0;
	}
	
	@Override
	public double getRadius(){
		return this.radius;
	}
	
	/** @param radius See {@link #radius} */
	public void setRadius(double radius){
		this.radius = radius;
	}
	
	@Override
	public int getRenderPriority(){
		return 200;
	}
}
