package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.material.Material;
import zgame.stat.Stat;
import zgame.stat.StatType;
import zgame.stat.ValueStat;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;
import zgame.stat.status.StatusEffect;
import zgame.stat.status.StatusEffects;
import zgame.things.entity.EntityThing;
import zgame.things.entity.Walk;
import zgame.things.entity.projectile.Projectile;
import zgame.things.type.RectangleHitBox;
import zusass.ZusassGame;
import zgame.stat.Stats;
import zusass.game.stat.*;
import zusass.game.stat.attributes.Endurance;
import zusass.game.stat.attributes.Intelligence;
import zusass.game.stat.attributes.Strength;
import zusass.game.stat.resources.Health;
import zusass.game.stat.resources.Mana;
import zusass.game.stat.resources.Stamina;
import zusass.game.status.StatEffect;
import zusass.game.things.MobWalk;

import static zusass.game.stat.ZusassStat.*;

import java.util.List;

/** A generic mob in the Zusass game */
public abstract class ZusassMob extends EntityThing implements RectangleHitBox{
	
	/** The width of this mob */
	private double width;
	/** The height of this mob */
	private double height;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The amount of time, in seconds, until this mob will perform an attack, or a negative value if this mob is not preparing for an attack */
	private double attackTime;
	
	/** The direction, an angle in radians, where the mob will attack */
	private double attackDirection;
	
	/** true if this {@link ZusassMob} is in spell casting mode, false for weapon mode */
	private boolean casting;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The stats used by this mob */
	private final Stats stats;
	
	/** The status effects applied to this mob */
	private final StatusEffects effects;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The {@link MobWalk} which this mob uses for movement */
	private final MobWalk walk;
	
	/**
	 * Create a new mob with the given bounds
	 *
	 * @param x The upper left hand x coordinate
	 * @param y The upper left hand y coordinate
	 * @param width The mob's width
	 * @param height The mob's height
	 */
	public ZusassMob(double x, double y, double width, double height){
		super(x, y);
		this.walk = new MobWalk(this);
		
		this.width = width;
		this.height = height;
		
		this.attackTime = -1;
		this.attackDirection = 0;
		this.casting = false;
		
		// Create stats
		this.stats = new Stats();
		
		// Create status effects
		this.effects = new StatusEffects();
		
		// Add attributes
		this.stats.add(new Strength(this.stats));
		this.stats.add(new Endurance(this.stats));
		this.stats.add(new Intelligence(this.stats));
		
		// Add resources
		this.stats.add(new Health(this.stats));
		this.stats.add(new Stamina(this.stats));
		this.stats.add(new Mana(this.stats));
		
		// Add misc stats
		this.stats.add(new ValueStat(100, this.stats, ATTACK_RANGE));
		this.stats.add(new ValueStat(.5, this.stats, ATTACK_SPEED));
		this.stats.add(new AttackDamage(this.stats));
		
		this.stats.add(new MoveSpeed(Walk.DEFAULT_WALK_SPEED_MAX, this.stats, this));
		
		// Ensure this thing stats at full resources
		this.setResourcesMax();
		
		// Set initial attribute values
		this.setStat(STRENGTH, 1);
		this.setStat(ENDURANCE, 5);
		this.setStat(INTELLIGENCE, 5);
	}
	
	@Override
	public void tick(Game game, double dt){
		var zgame = (ZusassGame)game;
		
		// Update the state of the status effects
		this.effects.tick(zgame, dt);
		
		// Update the state of the stats
		this.updateStats(zgame, dt);
		
		// Update the attack timer
		if(this.attackTime > 0){
			this.attackTime -= dt;
			if(this.attackTime <= 0) this.attackNearest(zgame);
		}
		
		var walk = this.getWalk();
		
		walk.updatePosition(game, dt);
		walk.tick(game, dt);
		
		// If walking, need to reduce stamina
		if(!this.getWalk().isWalking() && this.getWalk().isTryingToMove()) this.getStat(STAMINA).addValue(-35 * dt);
		
		// Do the normal game update
		super.tick(game, dt);
	}
	
	/**
	 * Minimal method for drawing a basic attack timer for melee attacks
	 *
	 * @param game The game where the attack is performed
	 * @param r The renderer to draw the attack with
	 */
	public void renderAttackTimer(Game game, Renderer r){
		// issue#23 potentially need some way of ensuring this also gets rendered with the should render thing, or maybe this is just a temporary placeholder
		if(this.getAttackTime() <= 0) return;
		double directionX = Math.cos(this.attackDirection);
		double time = this.getAttackTime();
		double speed = this.stat(ATTACK_SPEED);
		double attackSize = this.stat(ATTACK_RANGE) * (1 - time / speed);
		
		if(directionX < 0) r.drawRectangle(this.centerX() - attackSize, this.centerY(), attackSize, 20);
		else r.drawRectangle(this.centerX(), this.centerY(), attackSize, 20);
	}
	
	/**
	 * Perform any necessary updates for the mob based on its current stats
	 *
	 * @param zgame The game to update the stats on
	 * @param dt The number of seconds which passed in this update
	 */
	public void updateStats(ZusassGame zgame, double dt){
		this.stats.tick(dt);
		
		// If this thing has 0 or less health, kill it
		if(this.getCurrentHealth() <= 0) this.die(zgame);
	}
	
	/**
	 * Called when this stat thing dies
	 *
	 * @param zgame The game it was in when it died
	 */
	public void die(ZusassGame zgame){
		// On death, by default, remove the thing from the game
		zgame.getCurrentRoom().removeThing(this);
	}
	
	@Override
	public void hitBy(Projectile p){
		p.hit(ZusassMob.class, this);
	}
	
	/** @return See {@link #attackTime} */
	public double getAttackTime(){
		return this.attackTime;
	}
	
	/** @return See {@link #attackDirection} */
	public double getAttackDirection(){
		return this.attackDirection;
	}
	
	/** @return See {@link #casting} */
	public boolean isCasting(){
		return this.casting;
	}
	
	/** @param casting See {@link #casting} */
	public void setCasting(boolean casting){
		this.casting = casting;
	}
	
	/** Toggle the state of {@link #casting} */
	public void toggleCasting(){
		this.setCasting(!this.isCasting());
	}
	
	/**
	 * Cause this mob to begin performing an attack or casting a spell depending on which mode is selected
	 *
	 * @param zgame The game where the attack or spell took place
	 * @param direction The direction to attack or cast in
	 */
	public void beginAttackOrSpell(ZusassGame zgame, double direction){
		this.attackDirection = direction;
		if(casting){
			this.castSpell(zgame);
		}
		else{
			this.attackTime = this.stat(ATTACK_SPEED);
			
			// Also drain stamina from the thing
			this.getStat(STAMINA).addValue(-20);
		}
	}
	
	/**
	 * Cause this mob to deal damage to the given mob
	 *
	 * @param mob The mob to attack
	 */
	public void attack(ZusassMob mob){
		mob.damage(this.stat(ATTACK_DAMAGE));
	}
	
	/**
	 * Attack the nearest mob, in {@link #attackDirection}, in the game which is not this mob
	 *
	 * @param game The game where the attack should happen
	 */
	public void attackNearest(ZusassGame game){
		List<ZusassMob> mobs = game.getCurrentRoom().getMobs();
		for(var m : mobs){
			// Skip the current mob if it is this mob or the mob is out of the attack range
			if(m == this || this.center().distance(m.center()) >= this.stat(ATTACK_RANGE)) continue;
			// Also skip the current mob if it is not in the attack direction
			double directionX = Math.cos(this.attackDirection);
			if(this.centerX() < m.centerX() == directionX < 0) continue;
			// Perform the attack
			this.attack(m);
			return;
		}
	}
	
	/**
	 * Cause this mob to be effected by something that deals damage to it's health
	 *
	 * @param amount The amount of damage done. Does nothing if this value is less than or equal to 0
	 */
	public void damage(double amount){
		if(amount <= 0) return;
		this.stats.get(HEALTH).addValue(-amount);
	}
	
	/**
	 * Attempt to cast the currently selected spell
	 *
	 * @param zgame The {@link ZusassGame} where the spell was cast
	 */
	public void castSpell(ZusassGame zgame){
		var cost = 20;
		if(this.stat(MANA) < cost) return;
		this.getStat(MANA).addValue(-cost);
		
		// Casting a spell to move faster as a temporary test
		this.addStatEffect(this.getUuid(), 5, 2, ModifierType.MULT_MULT, MOVE_SPEED);
	}
	
	/** @return See {@link #effects} */
	public StatusEffects getEffects(){
		return this.effects;
	}
	
	/**
	 * Add and apply a status effect to this mob
	 *
	 * @param effect The effect to add
	 */
	public void addEffect(StatusEffect effect){
		this.effects.addEffect(effect);
	}
	
	/**
	 * Add and apply a {@link StatEffect} to this mob
	 *
	 * @param sourceId The id of whatever caused the effect to be applied to this mob
	 * @param duration The duration of the effect
	 * @param value The power of the effect
	 * @param modifierType The way the modifier applies its value
	 * @param statType The {@link Stat} to effect
	 */
	public void addStatEffect(String sourceId, double duration, double value, ModifierType modifierType, StatType statType){
		this.addEffect(new StatEffect(this.getStats(), duration, new StatModifier(sourceId, value, modifierType), statType));
	}
	
	/** @return See {@link #stats} */
	public Stats getStats(){
		return this.stats;
	}
	
	/**
	 * Get a stat from this {@link ZusassMob}
	 *
	 * @param type The type of stat to get
	 * @return The stat
	 */
	public Stat getStat(StatType type){
		return this.stats.get(type);
	}
	
	/** @return The value of the given stat */
	public double stat(StatType type){
		var stat = this.stats.get(type);
		if(stat == null) return 0;
		return stat.get();
	}
	
	/**
	 * Set the current value of a stat
	 *
	 * @param type The type of stat to set
	 * @param value The new value
	 */
	public void setStat(StatType type, double value){
		this.stats.get(type).setValue(value);
	}
	
	/** @return The current amount of heath this {@link ZusassMob} has */
	public double getCurrentHealth(){
		return this.stat(HEALTH);
	}
	
	/** Set every resource, i.e. health, stamina, mana, to their max values */
	public void setResourcesMax(){
		this.setToMaxHealth();
		this.setToMaxStamina();
		this.setToMaxMana();
	}
	
	/** Set this thing's current health to its maximum health */
	public void setToMaxHealth(){
		this.setStat(HEALTH, this.stat(HEALTH_MAX));
	}
	
	/** Set this thing's current stamina to its maximum stamina */
	public void setToMaxStamina(){
		this.setStat(STAMINA, this.stat(STAMINA_MAX));
	}
	
	/** Set this thing's current mana to its maximum stamina */
	public void setToMaxMana(){
		this.setStat(MANA, this.stat(MANA_MAX));
	}
	
	/** @return The percentage of health this mob has remaining, in the range [0, 1] */
	public double currentHealthPerc(){
		double perc = this.getCurrentHealth() / this.stat(HEALTH_MAX);
		return Math.min(1, Math.max(0, perc));
	}
	
	/** @return The percentage of stamina this mob has remaining, in the range [0, 1] */
	public double currentStaminaPerc(){
		double perc = this.stat(STAMINA) / this.stat(STAMINA_MAX);
		return Math.min(1, Math.max(0, perc));
	}
	
	/** @return The percentage of mana this mob has remaining, in the range [0, 1] */
	public double currentManaPerc(){
		double perc = this.stat(MANA) / this.stat(MANA_MAX);
		return Math.min(1, Math.max(0, perc));
	}
	
	/** @return See {@link #width} */
	@Override
	public double getWidth(){
		return this.width;
	}
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	/** @return See {@link #height} */
	@Override
	public double getHeight(){
		return this.height;
	}
	
	/** @param height See {@link #height} */
	public void setHeight(double height){
		this.height = height;
	}
	
	/**
	 * @return This object, as an {@link Npc}, or null if it cannot be an {@link Npc}
	 * 		The return value of this method should equal this object, not another version or reference, i.e. (this == this.asNpc()) should evaluate to true
	 */
	public Npc asNpc(){
		return null;
	}
	
	/** @return See {@link #walk} */
	public Walk getWalk(){
		return this.walk;
	}
	
	@Override
	public double getFrictionConstant(){
		return this.getWalk().getFrictionConstant();
	}
	
	@Override
	public void leaveFloor(){
		super.leaveFloor();
		this.getWalk().leaveFloor();
	}
	
	@Override
	public void leaveWall(){
		super.leaveWall();
		this.getWalk().leaveWall();
	}
	
	@Override
	public void touchFloor(Material touched){
		super.touchFloor(touched);
		this.getWalk().touchFloor(touched);
	}
}
