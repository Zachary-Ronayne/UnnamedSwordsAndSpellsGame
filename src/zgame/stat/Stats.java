package zgame.stat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * An object keeping track of all the information, i.e. health, skills, etc., about a mob
 * This object is less of a state, and more of information about the mob, i.e., it should store maximum health, not current health
 */
public class Stats{
	
	/** The {@link Stat}s which this {@link Stats} uses. Key: stat name, value: the stat */
	private final Map<StatType, Stat> map;
	
	/** Mapping which stats must be recalculated when their key stat is updated */
	private final Map<StatType, HashSet<StatType>> dependents;
	
	/** Initialize a new stats object with nothing set */
	public Stats(){
		this.map = new HashMap<>();
		this.dependents = new HashMap<>();
	}
	
	/** @return See {@link #dependents} */
	public Map<StatType, HashSet<StatType>> getDependents(){
		return this.dependents;
	}
	
	public void add(Stat s){
		this.map.put(s.getType(), s);
		
		/*
		 When adding a stat to this object, keep track of the reverse of that stats dependents,
		  this way, it's easy to find which stats need to be recalculated when a stat updates
		 */
		for(var d : s.getDependents()){
			if(!this.dependents.containsKey(d)) this.dependents.put(d, new HashSet<>());
			this.dependents.get(d).add(s.getType());
		}
	}
	
	/** @return A stat that this {@link Stats} uses, or null if no stat exists for the given stat enum */
	public Stat get(StatType s){
		return this.map.get(s);
	}
	
	/**
	 * Apply any operations that must happen to {@link #map} over time
	 * @param dt The number of sections that have passed
	 */
	public void tick(double dt){
		for(var s : this.map.values()) s.tick(dt);
	}
	
}
