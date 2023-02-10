package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.material.Material;
import zgame.stat.StatType;
import zgame.stat.ValueStat;
import zgame.things.entity.EntityThing;
import zgame.things.entity.Walk;
import zgame.things.type.RectangleHitBox;
import zusass.ZusassGame;
import zgame.stat.Stats;
import zusass.game.stat.AttackDamage;
import zusass.game.stat.HealthCurrent;
import zusass.game.stat.HealthMax;

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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The stats used by this mob */
	private final Stats stats;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The {@link Walk} which this mob uses for movement */
	private final Walk walk;
	
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
		this.walk = new Walk(this);
		
		this.width = width;
		this.height = height;
		
		this.attackTime = -1;
		this.attackDirection = 0;
		
		this.stats = new Stats();
		this.stats.add(new HealthMax(this.stats));
		this.stats.add(new HealthCurrent(this.stats));
		this.stats.add(new ValueStat(100, this.stats, ATTACK_RANGE));
		this.stats.add(new ValueStat(.5, this.stats, ATTACK_SPEED));
		this.stats.add(new AttackDamage(this.stats));
		this.stats.add(new ValueStat(1, this.stats, STRENGTH));
		
		this.healToMaxHealth();
	}
	
	@Override
	public void tick(Game game, double dt){
		// Update the state of the stats
		this.updateStats(game);
		var zgame = (ZusassGame)game;
		
		// Update the attack timer
		if(this.attackTime > 0){
			this.attackTime -= dt;
			if(this.attackTime <= 0) this.attackNearest(zgame);
		}
		
		var walk = this.getWalk();
		
		walk.updatePosition(game, dt);
		walk.tick(game, dt);
		
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
	 * @param game The game to update the stats on
	 */
	public void updateStats(Game game){
		// If this thing has 0 or less health, kill it
		if(this.getCurrentHealth() <= 0) this.die(game);
	}
	
	/**
	 * Called when this stat thing dies
	 *
	 * @param game The game it was in when it died
	 */
	public void die(Game game){
		// On death, by default, remove the thing from the game
		game.getCurrentRoom().removeThing(this);
	}
	
	/** @return See {@link #attackTime} */
	public double getAttackTime(){
		return this.attackTime;
	}
	
	/**
	 * Cause this mob to begin performing an attack
	 *
	 * @param direction The direction to attack in
	 */
	public void beginAttack(double direction){
		this.attackDirection = direction;
		this.attackTime = this.stat(ATTACK_SPEED);
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
		this.stats.get(HEALTH_CURRENT).addValue(-amount);
	}
	
	/** @return See {@link #stats} */
	public Stats getStats(){
		return this.stats;
	}
	
	/** @return The value of the given stat */
	public double stat(StatType type){
		var stat = this.stats.get(type);
		if(stat == null) return 0;
		return stat.get();
	}
	
	public void setStat(StatType type, double value){
		this.stats.get(type).setValue(value);
	}
	
	/** @return The current amount of heath this {@link ZusassMob} has */
	public double getCurrentHealth(){
		return this.stat(HEALTH_CURRENT);
	}
	
	/** Set this thing's current health to its maximum health */
	public void healToMaxHealth(){
		this.stats.get(HEALTH_CURRENT).setValue(this.stat(HEALTH_MAX));
	}
	
	/** @return The percentage of health this mob has remaining, in the range [0, 1] */
	public double currentHealthPerc(){
		double perc = this.getCurrentHealth() / this.stat(HEALTH_MAX);
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
	 * The return value of this method should equal this object, not another version or reference, i.e. (this == this.asNpc()) should evaluate to true
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
