package zgame.stat;

import zgame.core.file.Saveable;

import java.util.HashMap;
import java.util.Map;

/**
 * An empty interface for defining enums that can be used in {@link Stats} as a mapping
 * When implementing this class, ensure that every enum is accessed so that every enum gets an id assigned.
 * Otherwise, {@link Stat} and {@link Stats} will be unable to use those enums.
 *
 * @param <T> The enum type of this stat
 */
public interface StatType<T extends Enum<T>> extends Saveable{
	/** @return The unique integer id to assign to this {@link StatType}. Should obtain this value by calling {@link StatId#next()} */
	int getId();
	
	/**
	 * Get the type from the id. Primarily should be used for debugging, or when performance doesn't matter
	 * @param id The id of the stat type to get
	 * @return The type, or null if none exists from the origin of this call
	 */
	StatType<T> getFromId(int id);
	
	/** @return The unique name of this stat which can be saved to a file */
	String name();
	
	/** Mapping a stat type string to the stat enum */
	Map<String, StatType<?>> typeMap = new HashMap<>();
	/** Mapping a stat id to the stat enum */
	Map<Integer, StatType<?>> intMap = new HashMap<>();
	
	/**
	 * Add the given types to the static mapping of all stats. Used for loading
	 *
 	 * @param types The types to add
	 */
	static void add(StatType<?>[] types){
		for(var t : types) {
			typeMap.put(t.name(), t);
			intMap.put(t.getId(), t);
		}
	}
	
	/**
	 * Get the stat of the given name
	 * @param name The name of the type to load
	 * @return The stat type, or null if none exists
	 */
	static StatType<?> get(String name){
		return typeMap.get(name);
	}
	
}
