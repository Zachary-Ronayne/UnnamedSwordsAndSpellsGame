package zusass.game.stat;

/**
 * An object keeping track of all the information, i.e. health, skills, etc., about a mob
 * This object is less of a state, and more of information about the mob, i.e., it should store maximum health, not current health
 */
public class Stats{
	
	/** The amount of hit points this object can have */
	private double maxHealth;
	
	/** How string this object is */
	private double strength;
	
	/** The amount of units this object can attack from */
	private double attackRange;
	
	/** The amount of time, in seconds it takes for this object to perform an attack */
	private double attackSpeed;
	
	/** Initialize a new stats object with nothing set */
	public Stats(){
		this.maxHealth = 10;
		this.strength = 1;
		this.attackRange = 100;
		this.attackSpeed = 0.5;
	}
	
	/** @return See {@link #maxHealth} */
	public double getMaxHealth(){
		return this.maxHealth;
	}
	
	/** @param maxHealth See {@link #maxHealth} */
	public void setMaxHealth(double maxHealth){
		this.maxHealth = maxHealth;
	}
	
	/** @return See {@link #strength} */
	public double getStrength(){
		return this.strength;
	}
	
	/** @param strength See {@link #strength} */
	public void setStrength(double strength){
		this.strength = strength;
	}
	
	/** @return See {@link #attackRange} */
	public double getAttackRange(){
		return attackRange;
	}
	
	/** @param attackRange See {@link #attackRange} */
	public void setAttackRange(double attackRange){
		this.attackRange = attackRange;
	}
	
	/** @return See {@link #attackSpeed} */
	public double getAttackSpeed(){
		return attackSpeed;
	}
	
	/** @param attackSpeed See {@link #attackSpeed} */
	public void setAttackSpeed(double attackSpeed){
		this.attackSpeed = attackSpeed;
	}
	
}
