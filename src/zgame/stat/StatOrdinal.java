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
}
