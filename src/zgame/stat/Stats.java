package zgame.stat;

import zgame.core.utils.ZStringUtils;

/**
 * An object keeping track of all the information, i.e. health, skills, etc., about a mob
 * This object is less of a state, and more of information about the mob, i.e., it should store maximum health, not current health
 */
public class Stats{
	
	/** The {@link Stat}s which this {@link Stats} uses. Index: the {@link StatType} ordinal, value: the stat */
	private final Stat[] arr;
	
	// TODO try making this array static
	/**
	 * Mapping which stats must be recalculated when their key stat is updated.
	 * The outer and inner arrays are both indexed by {@link StatType} ordinal
	 * i.e. dependents[s][type that must be recalculated when s changes]
	 * true means there is a dependent relationship, false otherwise
	 */
	private final boolean[][] dependents;
	
	/** Initialize a new stats object with nothing set */
	public Stats(){
		this.arr = new Stat[StatOrdinal.numOrdinals()];
		this.dependents = new boolean[StatOrdinal.numOrdinals()][StatOrdinal.numOrdinals()];
	}
	
	/** Get the array holding all the stats used by this {@link Stats} */
	public Stat[] getArr(){
		return this.arr;
	}
	
	/** @return See {@link #dependents} */
	public boolean[][] getDependents(){
		return this.dependents;
	}
	
	/**
	 * Get the stat types which depend on the given stat type
	 *
	 * @param type The type to look for
	 * @return Which types are dependents, as an array indexed by stat ordinal type
	 */
	public boolean[] getDependents(StatType type){
		return this.getDependents()[type.getOrdinal()];
	}
	
	/**
	 * Add the given stat to this {@link Stats}
	 *
	 * @param s The stat to add
	 */
	public void add(Stat s){
		this.arr[s.getType().getOrdinal()] = s;
		
		/*
		 When adding a stat to this object, keep track of the reverse of that stats dependents,
		  this way, it's easy to find which stats need to be recalculated when a stat updates
		 */
		var ds = s.getDependents();
		// Go through all the types, which the length will be the number of ordinals
		for(int i = 0; i < ds.length; i++){
			// Set the current dependent as the given stat's type
			this.dependents[ds[i]][s.getType().getOrdinal()] = true;
		}
	}
	
	/**
	 * @param s The type of stat to get
	 * @return A stat that this {@link Stats} uses, or null if no stat exists for the given stat enum
	 */
	public Stat get(StatType s){
		return this.get(s.getOrdinal());
	}
	
	/**
	 * @param ordinal The ordinal of the stat type to get
	 * @return A stat that this {@link Stats} uses, or null if no stat exists for the given stat ordinal
	 */
	public Stat get(int ordinal){
		return this.arr[ordinal];
	}
	
	/**
	 * Apply any operations that must happen to {@link #arr} over time
	 *
	 * @param dt The number of sections that have passed
	 */
	public void tick(double dt){
		for(int i = 0; i < this.arr.length; i++) this.arr[i].tick(dt);
	}
	
	/**
	 * A debugging tool. Prints an array of all the stats separated by tabs, copy to something like Excel to make it look normal.
	 * Each row is a stat type.
	 * The columns within a row, say Y if that row's stat type is used when calculating the column's stat type, and a dash otherwise
	 */
	public void printStats(){
		var d = this.getDependents();
		var arr = this.getArr();
		System.out.print("\t");
		for(int i = 0; i < arr.length; i++){
			System.out.print(arr[i].getClass().getSimpleName() + "\t");
		}
		System.out.println();
		for(int i = 0; i < arr.length; i++){
			System.out.print(arr[i].getClass().getSimpleName() + "\t");
			for(var y : d[i]){
				System.out.print((y ? "Y" : "-") + "\t");
			}
			System.out.println();
		}
	}
	
}
