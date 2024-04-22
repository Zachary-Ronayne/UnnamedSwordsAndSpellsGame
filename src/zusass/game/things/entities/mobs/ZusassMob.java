package zusass.game.things.entities.mobs;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.file.Saveable;
import zgame.core.graphics.Renderer;
import zgame.stat.Stat;
import zgame.stat.ValueStat;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModTracker;
import zgame.stat.modifier.StatModifier;
import zgame.stat.status.StatusEffect;
import zgame.stat.status.StatusEffects;
import zgame.things.entity.*;
import zgame.things.entity.movement.Movement2D;
import zgame.things.entity.movement.MovementEntityThing2D;
import zgame.things.entity.projectile.Projectile;
import zgame.things.type.bounds.RectangleHitBox;
import zusass.ZusassGame;
import zgame.stat.Stats;
import zusass.game.magic.*;
import zusass.game.stat.*;
import zusass.game.stat.attributes.Agility;
import zusass.game.stat.attributes.Endurance;
import zusass.game.stat.attributes.Intelligence;
import zusass.game.stat.attributes.Strength;
import zusass.game.stat.resources.Health;
import zusass.game.stat.resources.Mana;
import zusass.game.stat.resources.Stamina;
import zusass.game.status.StatEffect;

import static zusass.game.stat.ZusassStat.*;

import java.util.List;

/** A generic mob in the Zusass game */
public abstract class ZusassMob extends MovementEntityThing2D implements RectangleHitBox{
	
	/** The json key used to store the spellbook which this mob has */
	public final static String SPELLBOOK_KEY = "spellbook";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The default value of {@link #canStopJump} */
	public static final boolean DEFAULT_CAN_STOP_JUMP = true;
	/** The default value of {@link #jumpBuildTime} */
	public static final double DEFAULT_JUMP_BUILD_TIME = 0;
	/** The default value of {@link #jumpAfterBuildUp} */
	public static final boolean DEFAULT_JUMP_AFTER_BUILD_UP = true;
	/** The default value of {@link #walkFriction} */
	public static final double DEFAULT_WALK_FRICTION = 1;
	/** The default value of {@link #canWallJump} */
	public static final boolean DEFAULT_CAN_WALL_JUMP = true;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	
	/** The spells known to this mob */
	private Spellbook spells;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The stats used by this mob */
	private final Stats stats;
	
	/** The status effects applied to this mob */
	private final StatusEffects effects;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** A modifier used to drain stamina while running */
	private final StatModTracker staminaRunDrain;
	
	/** The sourceId of the modifier which drains stamina */
	private static final String ID_STAMINA_DRAIN = "staminaDrain";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The {@link Walk} object used by this object's implementation of {@link Movement2D} */
	private final Walk2D walk;
	
	/** See {@link Movement2D#isCanStopJump()} */
	private boolean canStopJump;
	
	/** See {@link Movement2D#getJumpBuildTime()} */
	private double jumpBuildTime;
	
	/** See {@link Movement2D#isJumpAfterBuildUp()} */
	private boolean jumpAfterBuildUp;
	
	/** See {@link Movement2D#getWalkFriction()} */
	private double walkFriction;
	
	/** See {@link Movement2D#isCanWallJump()} */
	private boolean canWallJump;
	
	/** See {@link Movement2D#isWalking()} */
	private boolean walking;
	
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
		
		this.canStopJump = DEFAULT_CAN_STOP_JUMP;
		this.jumpBuildTime = DEFAULT_JUMP_BUILD_TIME;
		this.jumpAfterBuildUp = DEFAULT_JUMP_AFTER_BUILD_UP;
		this.walkFriction = DEFAULT_WALK_FRICTION;
		this.canWallJump = DEFAULT_CAN_WALL_JUMP;
		this.walking = false;
		this.walk = new Walk2D(this);
		
		this.stopWalking();
		
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
		this.stats.add(new Agility(this.stats));
		
		// Add resources
		this.stats.add(new Health(this.stats));
		this.stats.add(new Stamina(this.stats));
		this.stats.add(new Mana(this.stats));
		
		// Add misc stats
		this.stats.add(new ValueStat(100, this.stats, ATTACK_RANGE));
		this.stats.add(new ValueStat(.5, this.stats, ATTACK_SPEED));
		this.stats.add(new AttackDamage(this.stats));
		
		this.stats.add(new MoveSpeed(this.stats));
		
		// Add other modifiers
		this.staminaRunDrain = new StatModTracker(0, ModifierType.ADD, this.getStat(STAMINA_REGEN), ID_STAMINA_DRAIN);
		
		// Ensure this thing stats at full resources
		this.setResourcesMax();
		
		// Set initial attribute values
		this.setStat(STRENGTH, 1);
		this.setStat(ENDURANCE, 5);
		this.setStat(INTELLIGENCE, 5);
		this.setStat(AGILITY, 5);
		
		// Init the spellbook to empty
		this.spells = new Spellbook();
	}
	
	@Override
	public void tick(Game game, double dt){
		var zgame = (ZusassGame)game;
		
		// Update the state of the status effects
		this.effects.tick(zgame, dt, this);
		
		// Update the state of the stats
		this.updateStats(zgame, dt);
		
		// Update the attack timer
		if(this.attackTime >= 0){
			this.attackTime -= dt;
			if(this.attackTime < 0) this.attackNearest(zgame);
		}
		
		// If running and moving, need to drain stamina
		this.staminaRunDrain.setValue(!this.isWalking() && this.isTryingToMove() ? -35 : 0);
		
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
		double speed = getAttacksPerSecond();
		double attackSize = this.stat(ATTACK_RANGE) * (1 - time * speed);
		
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
	
	/** @return The number of attacks per second this mob can perform */
	public final double getAttacksPerSecond(){
		return this.stat(ATTACK_SPEED);
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
			this.attackTime = 1.0 / this.getAttacksPerSecond();
			
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
	
	/** @return See {@link Spellbook#selectedSpell} */
	public Spell getSelectedSpell(){
		return this.spells.getSelectedSpell();
	}
	
	/** @return See {@link #spells} */
	public Spellbook getSpells(){
		return this.spells;
	}
	
	/**
	 * Attempt to cast the currently selected spell
	 *
	 * @param zgame The {@link ZusassGame} where the spell was cast
	 * @return true if the spell could be cast, false otherwise i.e. the caster doesn't have enough mana
	 */
	public boolean castSpell(ZusassGame zgame){
		return this.getSelectedSpell().castAttempt(zgame, this);
	}
	
	/** @return See {@link #effects} */
	public StatusEffects getEffects(){
		return this.effects;
	}
	
	/**
	 * Add and apply a status effect to this mob
	 *
	 * @param effect The effect to add
	 * @param sourceId The id representing whatever originally applied the effect
	 */
	public void addEffect(String sourceId, StatusEffect effect){
		this.effects.addEffect(effect, sourceId, this);
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
	public void addStatEffect(String sourceId, double duration, double value, ModifierType modifierType, ZusassStat statType){
		this.addEffect(sourceId, new StatEffect(duration, new StatModifier(value, modifierType), statType));
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
	public Stat getStat(ZusassStat type){
		return this.stats.get(type);
	}
	
	/** @return The value of the given stat */
	public double stat(ZusassStat type){
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
	public void setStat(ZusassStat type, double value){
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
	
	/** Bring every stat to its base value based on its {@link Stat#reset()} method */
	public void resetStats(){
		for(var m : this.stats.getArr()) m.reset();
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
	
	@Override
	public boolean jump(double dt){
		var jumped = super.jump(dt);
		if(jumped) this.getStat(STAMINA).addValue(-6);
		return jumped;
	}
	
	@Override
	public Walk2D getWalk(){
		return this.walk;
	}
	
	@Override
	public double getCurrentWalkingSpeed(){
		return this.getVX();
	}
	
	@Override
	public double getWalkSpeedMax(){
		// For now just making this a hard coded number based on the move speed stat
		return this.stat(MOVE_SPEED);
	}
	
	@Override
	public double getWalkAcceleration(){
		// For now just making this a hard coded number based on the move speed stat
		return this.stat(MOVE_SPEED) * 7;
	}
	
	@Override
	public double getWalkStopFriction(){
		// For now just making this a hard coded number based on the move speed stat
		return this.stat(MOVE_SPEED) / 30;
	}

	@Override
	public double getFrictionConstant(){
			return this.getWalkFrictionConstant();
	}
	
	@Override
	public double getJumpPower(){
		return Math.pow(this.stat(AGILITY), 0.3) * 35000;
	}
	
	@Override
	public double getJumpStopPower(){
		return Math.pow(this.stat(AGILITY), 0.3) * 3000;
	}
	
	/** @return See {@link #canStopJump} */
	@Override
	public boolean isCanStopJump(){
		return this.canStopJump;
	}
	
	/** @param canStopJump See {@link #canStopJump} */
	public void setCanStopJump(boolean canStopJump){
		this.canStopJump = canStopJump;
	}
	
	/** @return See {@link #jumpBuildTime} */
	@Override
	public double getJumpBuildTime(){
		return this.jumpBuildTime;
	}
	
	/** @param jumpBuildTime See {@link #jumpBuildTime} */
	public void setJumpBuildTime(double jumpBuildTime){
		this.jumpBuildTime = jumpBuildTime;
	}
	
	/** @return See {@link #jumpAfterBuildUp} */
	@Override
	public boolean isJumpAfterBuildUp(){
		return this.jumpAfterBuildUp;
	}
	
	/** @param jumpAfterBuildUp See {@link #jumpAfterBuildUp} */
	public void setJumpAfterBuildUp(boolean jumpAfterBuildUp){
		this.jumpAfterBuildUp = jumpAfterBuildUp;
	}
	
	@Override
	public double getWalkAirControl(){
		return 1.0 - 10.0 / (this.stat(AGILITY) + 10);
	}
	
	/** @return See {@link #walkFriction} */
	@Override
	public double getWalkFriction(){
		return this.walkFriction;
	}
	
	/** @param walkFriction See {@link #walkFriction} */
	public void setWalkFriction(double walkFriction){
		this.walkFriction = walkFriction;
	}
	
	@Override
	public double getWalkingRatio(){
		return 1 - 9.0 / (this.stat(AGILITY) + 10);
	}
	
	/** @return See {@link #canWallJump} */
	@Override
	public boolean isCanWallJump(){
		return this.canWallJump;
	}
	
	/** @param canWallJump See {@link #canWallJump} */
	public void setCanWallJump(boolean canWallJump){
		this.canWallJump = canWallJump;
	}
	
	@Override
	public double getNormalJumpTime(){
		return 0.1 + .75 * (1.0 - (50.0 / (this.stat(AGILITY) + 50)));
	}
	
	@Override
	public double getWallJumpTime(){
		return 0.15 + .75 * (1.0 - (50.0 / (this.stat(AGILITY) + 50)));
	}
	
	@Override
	public boolean isWalking(){
		return this.walking;
	}
	
	/** @param walking See {@link #walking} */
	public void setWalking(boolean walking){
		this.walking = walking;
	}
	
	/** toggle the state of {@link #walking} */
	public void toggleWalking(){
		this.setWalking(!this.isWalking());
	}
	
	@Override
	public boolean save(JsonElement e){
		this.spells.save(Saveable.newObj(SPELLBOOK_KEY, e));
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.spells = Saveable.obj(SPELLBOOK_KEY, e, Spellbook.class);
		return true;
	}
}
