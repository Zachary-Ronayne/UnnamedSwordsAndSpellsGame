package zusass.game.things.entities.mobs;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.file.Saveable;
import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.sound.SoundSource;
import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint3D;
import zgame.physics.ZVector3D;
import zgame.stat.Stat;
import zgame.stat.ValueStat;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModTracker;
import zgame.stat.modifier.StatModifier;
import zgame.stat.status.StatusEffect;
import zgame.stat.status.StatusEffects;
import zgame.things.entity.*;
import zgame.things.entity.mobility.Mobility3D;
import zgame.things.entity.mobility.MobilityEntity3D;
import zgame.things.entity.projectile.Projectile3D;
import zgame.things.type.bounds.ClickerBounds;
import zgame.things.type.bounds.CylinderClickable;
import zgame.things.type.bounds.CylinderHitbox;
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
import zusass.game.things.ZThingClickDetector;

import static zusass.game.stat.ZusassStat.*;

/** A generic mob in the Zusass game. All mobs have a cylinder hitbox */
public abstract class ZusassMob extends MobilityEntity3D implements CylinderHitbox, ClickerBounds, ZThingClickDetector, CylinderClickable{
	
	/** The json key used to store the spellbook which this mob has */
	public final static String SPELLBOOK_KEY = "spellbook";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The default value of {@link #jumpBuildTime} */
	public static final double DEFAULT_JUMP_BUILD_TIME = 0;
	/** The default value of {@link #jumpAfterBuildUp} */
	public static final boolean DEFAULT_JUMP_AFTER_BUILD_UP = true;
	/** The default value of {@link #walkFriction} */
	public static final double DEFAULT_WALK_FRICTION = 1;
	/** The default value of {@link #canWallJump} */
	public static final boolean DEFAULT_CAN_WALL_JUMP = true;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The radius of the center of this mob */
	private double radius;
	/** The height of this mob */
	private double height;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The amount of time, in seconds, until this mob will perform an attack, or a negative value if this mob is not preparing for an attack */
	private double attackTime;
	
	/** The spells known to this mob */
	private Spellbook spells;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The stats used by this mob */
	private final Stats stats;
	
	/** The status effects applied to this mob */
	private final StatusEffects effects;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** A modifier used to drain stamina while running */
	private StatModTracker staminaRunDrain;
	
	/** The sourceId of the modifier which drains stamina */
	private static final String ID_STAMINA_DRAIN = "staminaDrain";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** The source of the sound for this mob casting a spell */
	private SoundSource castSoundSource;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The {@link MobilityData} object used by this object's implementation of {@link Mobility3D} */
	private final MobilityData3D mobilityData;
	
	/** See {@link Mobility3D#getJumpBuildTime()} */
	private double jumpBuildTime;
	
	/** See {@link Mobility3D#isJumpAfterBuildUp()} */
	private boolean jumpAfterBuildUp;
	
	/** See {@link Mobility3D#getWalkFriction()} */
	private double walkFriction;
	
	/** See {@link Mobility3D#isCanWallJump()} */
	private boolean canWallJump;
	
	/** See {@link Mobility3D#isSprinting()} */
	private boolean sprinting;
	
	/**
	 * Create a new mob with the given bounds
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param z See {@link #getZ()}
	 * @param radius See {@link #radius}
	 * @param height See {@link #height}
	 */
	public ZusassMob(double x, double y, double z, double radius, double height){
		super(x, y, z, 1);
		
		this.jumpBuildTime = DEFAULT_JUMP_BUILD_TIME;
		this.jumpAfterBuildUp = DEFAULT_JUMP_AFTER_BUILD_UP;
		this.walkFriction = DEFAULT_WALK_FRICTION;
		this.canWallJump = DEFAULT_CAN_WALL_JUMP;
		this.sprinting = false;
		this.mobilityData = new MobilityData3D(this);
		
		this.stopWalking();
		
		this.radius = radius;
		this.height = height;
		
		this.attackTime = -1;
		
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
		this.stats.add(new ValueStat(1, this.stats, ATTACK_RANGE));
		this.stats.add(new ValueStat(.5, this.stats, ATTACK_SPEED));
		this.stats.add(new AttackDamage(this.stats));
		
		this.stats.add(new MoveSpeed(this.stats));
		
		// Add other modifiers
		this.initMobStatModifiers();
		
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
	
	/** Reinitialize the state of all base stat modifiers used by mobs */
	public void initMobStatModifiers(){
		// This could use a better way of doing this, basically, remove the modifier if it's already there, or do nothing if it's not there, then recreate it
		if(this.staminaRunDrain != null) this.getStat(STAMINA_REGEN).removeModifier(ID_STAMINA_DRAIN, this.staminaRunDrain.getMod());
		this.staminaRunDrain = new StatModTracker(0, ModifierType.ADD, this.getStat(STAMINA_REGEN), ID_STAMINA_DRAIN);
	}
	
	// issue#62, also make sure to destroy all sound resources once they are not needed anymore
	/**
	 * Initialize this mob for creating sounds, otherwise sounds will not play
	 * @param zgame The game the sound will be played in
	 */
	public void initSounds(ZusassGame zgame){
		if(this.castSoundSource == null) this.castSoundSource = zgame.getSounds().createSource(this.getX(), this.getY(), this.getZ());
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
		this.staminaRunDrain.setValue(this.isSprinting() && this.isTryingToMove() ? -35 : 0);
		
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
		// Do nothing if not attacking
		if(this.getAttackTime() < 0) return;
		
		double time = this.getAttackTime();
		double speed = this.getAttacksPerSecond();
		double attackPercent = 1 - time * speed;
		// Scale the time until attacking to make the arm move slowly at first, then quick at the end
		double anglePerc = Math.pow(1 - time * speed, 7);
		double attackSize = this.stat(ATTACK_RANGE) * 0.5 * attackPercent + 0.5;
		double attackYaw = this.getMobilityData().getFacingYaw();
		
		// Find the position where the arm will start
		var attackDirectionVec = new ZVector3D(attackYaw, 0, attackSize, false);
		var armBaseVec = new ZVector3D(this.getMobilityData().getFacingYaw() + ZMath.PI_BY_2, 0, this.getWidth() * 0.5, false);
		
		// Find the position where the arm will attack to
		var basePoint = this.center();
		basePoint.setX(basePoint.getX() + armBaseVec.getX());
		basePoint.setZ(basePoint.getZ() + armBaseVec.getZ());
		var attackPoint = basePoint.copy();
		attackPoint.setX(attackPoint.getX() + attackDirectionVec.getX());
		attackPoint.setZ(attackPoint.getZ() + attackDirectionVec.getZ());
		
		// Find the correct pitch and yaw to rotate the arm from the base to the attack point
		double armSize = this.getWidth() * 0.1;
		double dx = attackPoint.getX() - basePoint.getX();
		double dy = attackPoint.getY() - basePoint.getY();
		double dz = attackPoint.getZ() - basePoint.getZ();
		double yaw = ZMath.atan2Normalized(dx, dz);
		double pitch = ZMath.atan2Normalized(dy, Math.sqrt(dx * dx + dz * dz)) + ZMath.atan2Normalized(armSize, 0) * anglePerc;
		
		// Draw the final rotated rect
		var rect = new RectRender3D(basePoint.getX(), basePoint.getY(), basePoint.getZ(), armSize, attackSize, armSize);
		rect.setCoordinateRotation(false);
		rect.setYaw(yaw);
		rect.setPitch(pitch);
		rect.setRoll(0);
		var c = r.getColor();
		r.drawRectPrism(rect, c, c, c, c, c, c);
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
	public void hitBy(Projectile3D p){
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
	
	/**
	 * Cause this mob to begin performing an attack in the direction it is facing
	 *
	 * @param zgame The game where the attack took place
	 */
	public void beginAttack(ZusassGame zgame){
		// Do not allow attacking if an attack is taking place
		if(this.attackTime > 0) return;
		
		this.attackTime = 1.0 / this.getAttacksPerSecond();
		
		// Also drain stamina from the thing
		this.getStat(STAMINA).addValue(-20);
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
	 * Attack the nearest mob, in the direction the mob is facing, in the game which is not this mob
	 *
	 * @param game The game where the attack should happen
	 */
	public void attackNearest(ZusassGame game){
		var mobs = game.getCurrentRoom().getMobs();
		for(var m : mobs){
			// Skip the current mob if it is this mob or the mob is out of the attack range
			if(m == this) continue;
			double mobDist = this.distance(m);
			double attackRange = this.stat(ATTACK_RANGE);
			if(mobDist >= attackRange) continue;
			// Also skip the current mob if it cannot be "clicked" on
			if(!this.canClick(attackRange, this.findClickDistance(m))) continue;
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
		var success = this.getSelectedSpell().castAttempt(zgame, this);
		if(this.castSoundSource != null) {
			var sm = zgame.getSounds();
			sm.updateSourcePos(this.castSoundSource, this.getX(), this.getY(), this.getZ());
			sm.updateSourceDirection(this.castSoundSource, 0, 0, 0);
			this.castSoundSource.setBaseVolume(0.2);
			zgame.playEffect(this.castSoundSource, "win");
		}
		return success;
	}
	
	/** @return The point where this mob should cast a spell at when a spell is positional, defaults the middle top of the hitbox */
	public ZPoint3D getSpellCastPont(){
		return new ZPoint3D(this.centerX(), this.centerY() + this.getHeight() * 0.5, this.centerZ());
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
	
	/** @return See {@link #radius} */
	@Override
	public double getRadius(){
		return this.radius;
	}
	
	/** @param radius See {@link #radius} */
	public void setRadius(double radius){
		this.radius = radius;
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
	public double getClickX(){
		return this.getX();
	}
	
	@Override
	public double getClickY(){
		return this.getY() + this.getHeight();
	}
	
	@Override
	public double getClickZ(){
		return this.getZ();
	}
	
	@Override
	public double getClickYaw(){
		return this.getMobilityData().getFacingYaw();
	}
	
	@Override
	public double getClickPitch(){
		return this.getMobilityData().getFacingPitch();
	}
	
	@Override
	public double getClickRange(){
		return this.stat(ATTACK_RANGE) * 0.8;
	}
	
	@Override
	public boolean jump(double dt){
		var jumped = super.jump(dt);
		if(jumped) this.getStat(STAMINA).addValue(-6);
		return jumped;
	}
	
	@Override
	public MobilityData3D getMobilityData(){
		return this.mobilityData;
	}
	
	@Override
	public double getWalkSpeedMax(){
		// For now just making this a hard coded number based on the move speed stat
		return this.stat(MOVE_SPEED);
	}
	
	@Override
	public double getWalkPower(){
		double agilityModifier = 1 + this.stat(AGILITY);
		if(agilityModifier < 0) agilityModifier = 0;
		
		// Both movement speed and agility make acceleration faster
		return (1 + this.stat(MOVE_SPEED)) * 0.003 * agilityModifier;
	}
	
	@Override
	public double getWalkStopFriction(){
		// For now just making this a hard coded number based on the move speed stat
		double agilityModifier = this.stat(AGILITY) / 6.0;
		if(agilityModifier < 0) agilityModifier = 0;
		
		return this.stat(MOVE_SPEED) * 2 + agilityModifier;
	}

	@Override
	public double getFrictionConstant(){
		return this.getWalkFrictionConstant();
	}
	
	@Override
	public double getJumpPower(){
		double agility = this.stat(AGILITY);
		if(agility <= 0) return -1;
		
		return Math.pow(agility, 0.3) * 1.5;
	}
	
	@Override
	public double getJumpStopPower(){
		double agility = this.stat(AGILITY);
		if(agility <= 0) return -1;
		
		return Math.pow(agility, 0.3) * 0.1;
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
		double agility = this.stat(AGILITY);
		if(agility <= -10) return 0;
		return 1.0 - 10.0 / (agility + 10);
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
	public double getSprintingRatio(){
		double agility = this.stat(AGILITY);
		double divisor = -10 - agility * 0.5;
		// If agility is too low, no sprinting bonus
		if(divisor >= 0) return 1;
		
		// Approach a sprinting bonus of double
		return 2 + 10 / divisor;
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
		double agility = this.stat(AGILITY);
		if(agility <= -50) return -1;
		
		return 0.1 + .75 * (1.0 - (50.0 / (agility + 50)));
	}
	
	@Override
	public double getWallJumpTime(){
		double agility = this.stat(AGILITY);
		if(agility <= -50) return -1;
		
		return 0.15 + .75 * (1.0 - (50.0 / (agility + 50)));
	}
	
	@Override
	public boolean isSprinting(){
		return this.sprinting;
	}
	
	/** @param sprinting See {@link #sprinting} */
	public void setSprinting(boolean sprinting){
		this.sprinting = sprinting;
	}
	
	/** toggle the state of {@link #sprinting} */
	public void toggleSprinting(){
		this.setSprinting(!this.isSprinting());
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
