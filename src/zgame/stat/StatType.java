package zgame.stat;

/**
 * An empty interface for defining enums that can be used in {@link Stats} as a mapping
 * When implementing this class, ensure that every enum is accessed so that every enum gets an id assigned.
 * Otherwise, {@link Stat} and {@link Stats} will be unable to use those enums
 */
public interface StatType{
	/** @return The unique integer id to assign to this {@link StatType}. Should obtain this value by calling {@link StatId#next()} */
	int getId();
	
	/**
	 * Get the type from the id. Primarily should be used for debugging, or when performance doesn't matter
	 * @param id The id of the stat type to get
	 * @return The type, or null if none exists from the origin of this call
	 */
	StatType getFromId(int id);
	
}
