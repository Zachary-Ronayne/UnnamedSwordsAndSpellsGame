package zgame.stat;

/**
 * An object keeping track of all the information, i.e. health, skills, etc., about a mob
 * This object is less of a state, and more of information about the mob, i.e., it should store maximum health, not current health
 */
public class Stats{
	
	/**
	 * Mapping which stats must be recalculated when their key stat is updated.
	 * The outer array is indexed by {@link StatType} ordinals
	 * The inner array is in an arbitrary order, and is just the ordinals which depend on the stat of the outer array
	 */
	public static int[][] dependents;
	
	/**
	 * Must be called after all implementations of {@link StatType} have been initialized
	 */
	public static void init(){
		dependents = new int[StatOrdinal.numOrdinals()][0];
	}
	
	/** The {@link Stat}s which this {@link Stats} uses. Index: the {@link StatType} ordinal, value: the stat */
	private final Stat[] arr;
	
	/** Initialize a new stats object with nothing set */
	public Stats(){
		this.arr = new Stat[StatOrdinal.numOrdinals()];
	}
	
	/** Get the array holding all the stats used by this {@link Stats} */
	public Stat[] getArr(){
		return this.arr;
	}
	
	/** @return See {@link #dependents} */
	public int[][] getDependents(){
		return dependents;
	}
	
	/**
	 * Get the stat types which depend on the given stat type
	 *
	 * @param type The type to look for
	 * @return The dependent ordinals of the given type
	 */
	public int[] getDependents(StatType type){
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
		var ordinal = s.getType().getOrdinal();
		// Go through all the types, which the length will be the number of ordinals
		for(int i = 0; i < ds.length; i++){
			// Find the array to update
			var dependentArr = dependents[ds[i]];
			
			// See if the ordinal is already in that array
			var found = false;
			for(int j = 0; j < dependentArr.length && !found; j++){
				found = dependentArr[j] == ordinal;
			}
			
			// If the ordinal was found, skip it
			if(found) continue;
			// Otherwise resize the array and add the new ordinal
			dependents[ds[i]] = new int[dependentArr.length + 1];
			// Resizing the array
			System.arraycopy(dependentArr, 0, dependents[ds[i]], 0, dependentArr.length);
			// Setting the new ordinal
			dependents[ds[i]][dependentArr.length] = ordinal;
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
		var arr = this.getArr();
		var sb = new StringBuilder("----------------------------------------------------------------------------------------\n");
		sb.append("\t");
		// Go through all the stats to get their names
		for(int i = 0; i < arr.length; i++){
			sb.append(arr[i].getClass().getSimpleName()).append(" (").append(i).append(")\t");
		}
		sb.append("\n");
		// Go through all the stats to show their dependencies
		for(int i = 0; i < arr.length; i++){
			sb.append(arr[i].getClass().getSimpleName()).append(" (").append(i).append(")\t");
			var d = dependents[i];
			// Go through all the stats
			for(int j = 0; j < arr.length; j++){
				var found = false;
				// Check each stat to see if we found a dependency
				for(int k = 0; k < d.length; k++){
					if(d[k] == j){
						found = true;
						break;
					}
				}
				// Add the correct symbol
				sb.append(found ? "Y" : "-").append("\t");
			}
			sb.append("\n");
		}
		sb.append("----------------------------------------------------------------------------------------\n");
		for(int i = 0; i < dependents.length; i++){
			sb.append(arr[i].getClass().getSimpleName()).append(" (").append(i).append(")\t");
			for(int j = 0; j < dependents[i].length; j++){
				sb.append(dependents[i][j]).append("\t");
			}
			sb.append("\n");
		}
		sb.append("----------------------------------------------------------------------------------------");
		System.out.println(sb);
	}
	
}
