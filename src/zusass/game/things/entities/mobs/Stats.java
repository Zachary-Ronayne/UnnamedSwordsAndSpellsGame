package zusass.game.things.entities.mobs;

/** An object keeping track of all the information, i.e. health, skills, etc., about a thing */
public class Stats{
	
	/** The amount of hit points this object has remaining */
	private double health;

	/** Initialize a new stats object with nothing set */
	public Stats(){
		this.health = 10;
	}

	/** @return See {@link #health} */
	public double getHealth(){
		return this.health;
	}

	/** @param health See {@link #health} */
	public void setHealth(double health){
		this.health = health;
	}
	
}
