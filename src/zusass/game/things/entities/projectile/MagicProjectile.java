package zusass.game.things.entities.projectile;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.NotNullList;
import zgame.physics.ZVector3D;
import zgame.things.BaseTags;
import zgame.things.entity.projectile.Projectile2D;
import zgame.things.entity.projectile.Projectile3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.things.type.bounds.SphereHitBox;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** A {@link Projectile2D} which applies a magic effect when it hits something other than its caster */
public class MagicProjectile extends Projectile3D implements SphereHitBox{
	
	/** The radius of the projectile */
	private double radius;
	
	/** The effects to apply when this projectile hits a mob */
	private final NotNullList<SpellEffect> effects;
	
	/** The base color to use for this projectile, for now it's random */
	private final ZColor color;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param z The initial z position of the projectile
	 * @param sourceId See {@link #sourceId}, i.e. the uuid of the caster of this magic projectile
	 * @param launchVelocity The initial velocity of the projectile
	 * @param effects See {@link #effects}
	 */
	public MagicProjectile(double x, double y, double z, String sourceId, ZVector3D launchVelocity, NotNullList<SpellEffect> effects){
		this(x, y, z, 0.2, sourceId, launchVelocity, effects);
	}
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param z The initial z position of the projectile
	 * @param radius See {@link #radius}
	 * @param sourceId See {@link #sourceId}, i.e. the uuid of the caster of this magic projectile
	 * @param launchVelocity The initial velocity of the projectile
	 * @param effects See {@link #effects}
	 */
	public MagicProjectile(double x, double y, double z, double radius, String sourceId, ZVector3D launchVelocity, NotNullList<SpellEffect> effects){
		this(x, y, z, radius, -1, sourceId, launchVelocity, effects);
	}
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param z The initial z position of the projectile
	 * @param radius See {@link #radius}
	 * @param range See {@link #range}
	 * @param sourceId See {@link #sourceId}, i.e. the uuid of the caster of this magic projectile
	 * @param launchVelocity The initial velocity of the projectile
	 * @param effects See {@link #effects}
	 */
	public MagicProjectile(double x, double y, double z, double radius, double range, String sourceId, ZVector3D launchVelocity, NotNullList<SpellEffect> effects){
		super(x, y, z, launchVelocity);
		this.color = new ZColor(Math.random(), Math.random(), Math.random(), 0.4 * Math.random() + 0.4);
		
		this.setSourceId(sourceId);
		this.setRadius(radius);
		this.setRange(range);
		this.effects = effects;
		this.setOnHit(true);
		this.addTags(BaseTags.PROJECTILE_NOT_COLLIDE);
		
		// Turn off gravity
		this.setGravityLevel(0);
		
		// Add a function to effect a hit mob with magic
		this.addHitFunc(ZusassMob.class, m -> {
			for(var ef : this.effects) ef.apply(sourceId, m);
		});
	}
	
	@Override
	public void hit(Game game, HitBox3D thing){
		thing.hitBy(this);
	}
	
	@Override
	protected void render(Game game, Renderer r){
		r.setColor(this.color);
		r.drawSphere(this.getX(), this.getY(), this.getZ(), this.getRadius());
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
	public int getSortPriority(){
		return 200;
	}
	
}
