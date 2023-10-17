package zgame.stat;

public class StatId{
	/** The current id used for {@link StatType} */
	private static int currentId = 0;
	
	/** @return The next id to use for a stat */
	public synchronized static int next(){
		var current = currentId;
		currentId += 1;
		return current;
	}
	
	/** @return The number of ids currently existing for {@link StatType} */
	public static int numIds(){
		return currentId;
	}
}
