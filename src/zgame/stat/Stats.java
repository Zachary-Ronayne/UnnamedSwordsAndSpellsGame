package zgame.stat;

import java.util.HashMap;
import java.util.Map;

/**
 * An object keeping track of all the information, i.e. health, skills, etc., about a mob
 * This object is less of a state, and more of information about the mob, i.e., it should store maximum health, not current health
 */
public class Stats{
	
	/** The {@link Stat}s which this {@link Stats} uses. Key: stat name, value: the stat */
	private final Map<StatType, Stat> map;
	
	/** Initialize a new stats object with nothing set */
	public Stats(){
		this.map = new HashMap<>();
	}
	
	public void add(Stat s){
		this.map.put(s.getType(), s);
	}
	
	/** @return A stat that this {@link Stats} uses, or null if no stat exists for the given stat enum */
	public Stat get(StatType s){
		return this.map.get(s);
	}
}
