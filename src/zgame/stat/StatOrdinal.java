package zgame.stat;

public class StatOrdinal{
	/** The current ordinal used for {@link StatType} */
	private static int currentOrdinal = 0;
	
	/** @return The next ordinal to use for a stat */
	public synchronized static int nextOrdinal(){
		var current = currentOrdinal;
		currentOrdinal += 1;
		return current;
	}
	
	/** @return The number of ordinals currently existing for {@link StatType} */
	public static int numOrdinals(){
		return currentOrdinal;
	}
	
	/**
	 * Mapping which stats must be recalculated when their key stat is updated.
	 * The outer and inner arrays are both indexed by {@link StatType} ordinal
	 * i.e. dependents[s][type that must be recalculated when s changes]
	 * true means there is a dependent relationship, false otherwise.
	 */
	public static boolean[][] dependents;
	
	/**
	 * Must be called after all implementations of {@link StatType} have been initialized
	 */
	public static void init(){
		dependents = new boolean[StatOrdinal.numOrdinals()][StatOrdinal.numOrdinals()];
	}
	
}
