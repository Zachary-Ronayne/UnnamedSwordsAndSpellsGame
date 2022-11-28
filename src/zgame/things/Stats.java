package zgame.things;

/**
 * An object keeping track of all the information, i.e. health, skills, etc., about a mob
 * This object is less of a state, and more of information about the mob, i.e., it should store maximum health, not current health
 */
public class Stats{
	
	/** The amount of hit points this object can have */
	private double maxHealth;

	/** How string this object is */
	private double strength;
	
	/** Initialize a new stats object with nothing set */
	public Stats(){
		this.maxHealth = 10;
		this.strength = 1;
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
	
}
