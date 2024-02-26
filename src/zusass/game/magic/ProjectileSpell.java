package zusass.game.magic;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.core.utils.NotNullList;
import zgame.physics.ZVector2D;
import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.game.things.entities.projectile.MagicProjectile;

/** A {@link Spell} that creates a projectile when cast */
public class ProjectileSpell extends Spell{
	
	/** The json key storing {@link #radius} */
	private static final String RADIUS_KEY = "radius";
	/** The json key storing {@link #range} */
	private static final String RANGE_KEY = "range";
	/** The json key storing {@link #speed} */
	private static final String SPEED_KEY = "speed";
	
	/** See {@link MagicProjectile#radius} */
	private double radius;
	/** See {@link MagicProjectile#range} */
	private double range;
	/** The initial velocity of this spell when cast */
	private double speed;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public ProjectileSpell(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		super(e);
	}
	
	/**
	 * Create a new spell that casts a projectile
	 *
	 * @param effect A single effect for see effect {@link #effects}
	 */
	public ProjectileSpell(SpellEffect effect){
		this(new NotNullList<>(effect));
	}
	
	/**
	 * Create a new spell that casts a projectile
	 *
	 * @param effect A single effect for see {@link #effects}
	 * @param radius See {@link MagicProjectile#radius}
	 * @param range See {@link MagicProjectile#range}
	 */
	public ProjectileSpell(SpellEffect effect, double radius, double range, double speed){
		this(new NotNullList<>(effect), radius, range, speed);
	}
	
	/**
	 * Create a new spell that casts a projectile
	 *
	 * @param effects See {@link #effects}
	 */
	public ProjectileSpell(NotNullList<SpellEffect> effects){
		this(effects, 10, -1, 400);
	}
	
	/**
	 * Create a new spell that casts a projectile
	 *
	 * @param effects See {@link #effects}
	 * @param radius See {@link MagicProjectile#radius}
	 * @param range See {@link MagicProjectile#range}
	 */
	public ProjectileSpell(NotNullList<SpellEffect> effects, double radius, double range, double speed){
		super(effects);
		this.radius = radius;
		this.range = range;
		this.speed = speed;
	}
	
	@Override
	protected void cast(ZusassGame zgame, ZusassMob caster){
		var r = zgame.getCurrentRoom();
		var vel = new ZVector2D(caster.getAttackDirection(), this.getSpeed(), false);
		var p = new MagicProjectile(caster.centerX(), caster.centerY(), caster.getUuid(), vel, this.getEffects());
		p.setRange(this.range);
		p.setRadius(this.radius);
		p.addX(-p.getWidth() * .5);
		p.addY(-p.getHeight() * .5);
		r.addThing(p);
	}
	
	/** @return See {@link #range} */
	public double getRange(){
		return this.range;
	}
	
	/** @return See {@link #radius} */
	public double getRadius(){
		return this.radius;
	}
	
	/** @return See {@link #speed} */
	public double getSpeed(){
		return this.speed;
	}
	
	@Override
	public double getCost(){
		// This is a very arbitrary calculation for now, basically, the more powerful the spell, the higher the cost
		var range = this.getRange();
		return super.getCost() + ((range < 0 ? 1000 : range) * 0.02) * (this.getSpeed() * 0.1) + this.getRadius() * 0.1;
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.addProperty(RADIUS_KEY, this.getRadius());
		obj.addProperty(RANGE_KEY, this.getRange());
		obj.addProperty(SPEED_KEY, this.getSpeed());
		return super.save(e);
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.radius = Saveable.d(RADIUS_KEY, e, 10);
		this.range = Saveable.d(RANGE_KEY, e, -1);
		this.speed = Saveable.d(SPEED_KEY, e, 400);
		return super.load(e);
	}
}
