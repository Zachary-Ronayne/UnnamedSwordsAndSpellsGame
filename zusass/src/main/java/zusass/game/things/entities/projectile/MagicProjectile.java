package zusass.game.things.entities.projectile;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.sound.ManagedSoundSource;
import zgame.core.utils.NotNullList;
import zgame.core.utils.Uuidable;
import zgame.physics.V3D;
import zgame.things.BaseTags;
import zgame.things.entity.projectile.Projectile3D;
import zgame.things.entity.projectile.ProjectileThing3D;
import zgame.things.entity.state.EntityState;
import zgame.things.type.bounds.threeDee.bounds.shape.SphereBounds;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.utils.ZusassSounds;

/** A {@link Projectile3D} which applies a magic effect when it hits something other than its caster */
public class MagicProjectile extends ProjectileThing3D implements SphereBounds{
	
	/** The radius of the projectile */
	private double radius;
	
	/** The effects to apply when this projectile hits a mob */
	private final NotNullList<SpellEffect> effects;
	
	/** The base color to use for this projectile, for now it's random */
	private final ZColor color;
	
	/** The source of the sound for this projectile being removed from the game */
	private final ManagedSoundSource removedSoundSource;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param pos The initial position of the projectile
	 * @param launchVelocity The initial velocity of the projectile
	 * @param radius See {@link #radius}
	 * @param range The maximum range the projectile can move
	 * @param source See the caster of this magic projectile
	 * @param effects See {@link #effects}
	 */
	public MagicProjectile(V3D pos, V3D launchVelocity, double radius, double range, Uuidable source, NotNullList<SpellEffect> effects){
		super(pos);
		this.color = new ZColor(Math.random(), Math.random(), Math.random(), 0.4 * Math.random() + 0.4);
		
		this.setRadius(radius);
		this.getProjectile().setRange(range);
		this.getProjectile().setOnHit(true);
		this.effects = effects;
		this.addTags(BaseTags.PROJECTILE_NOT_COLLIDE);
		
		// Turn off gravity
		this.attemptSetForce(EntityState.FORCE_GRAVITY, V3D.EMPTY);
		
		// Add a function to effect a hit mob with magic
		this.getProjectile().addHitFunc(ZusassMob.class, m -> {
			for(var ef : this.effects) ef.apply(source.getUuid(), m);
		});
		
		this.removedSoundSource = new ManagedSoundSource(ZusassSounds.MAGIC_DAMAGE, this, 0.95, 1.05, 1);
	}
	
	// TODO should this be inherent to entity thing?
	@Override
	public V3D getMinPosition(){
		return this.getStatePosition();
	}
	
	@Override
	public void onRoomRemove(){
		super.onRoomRemove();
		if(this.removedSoundSource != null){
			this.removedSoundSource.playSound();
		}
	}
	
	@Override
	protected void render(Renderer r){
		r.setColor(this.color);
		var pos = this.getCenterPosition();
		r.drawSphere(pos.getX(), pos.getY(), pos.getZ(), this.getRadius());
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
