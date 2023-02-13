package zgame.stat;

/**
 * An empty interface for defining enums that can be used in {@link Stats} as a mapping
 * When implementing this class, ensure that every enum is accessed so that every enum gets an ordinal assigned.
 * Otherwise, {@link Stat} and {@link Stats} will be unable to use those enums
 */
public interface StatType{
	/** @return The unique integer ordinal to assign to this {@link StatType}. Should obtain this value by calling {@link StatOrdinal#nextOrdinal()} */
	int getOrdinal();
	
	/**
	 * Get the type from the ordinal. Primarily should be used for debugging, or when performance doesn't matter
	 * @param ordinal The ordinal of the stat type to get
	 * @return The type, or null if none exists from the origin of this call
	 */
	StatType getFromOrdinal(int ordinal);
	
}
