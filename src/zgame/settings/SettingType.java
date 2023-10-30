package zgame.settings;

import zgame.core.Game;
import zgame.core.file.Saveable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An interface to be used for generating settings, should be implemented by an enum
 * @param <E> The type of enum using this interface
 * @param <T> The type of data used by the setting
 */
public interface SettingType<E extends Enum<E>, T> extends Saveable{
	/** @return The unique integer id to assign to this {@link SettingType}. Should obtain this value by calling {@link SettingId#next()} */
	int getId();
	
	/** @return The default value used by this setting */
	T getDefault();
	
	/** @return A function that runs each time the setting changes, or null to do nothing on change. The game is the game where the setting changed, the T is the new value */
	BiConsumer<Game, T> getOnChange();
	
	/**
	 * Get the type from the id. Primarily should be used for debugging, or when performance doesn't matter
	 * @param id The id of the setting to get
	 * @return The type, or null if none exists from the origin of this call
	 */
	SettingType<E, T> getFromId(int id);
	
	/** @return The unique name of this setting which can be saved to a file */
	String name();
	
	/** Mapping a setting type string to the setting enum */
	Map<String, SettingType<?, ?>> typeMap = new HashMap<>();
	/** Mapping a setting id to the setting enum */
	Map<Integer, SettingType<?, ?>> intMap = new HashMap<>();
	
	/**
	 * Add the given types to the static mapping of all settings. Used for loading
	 *
	 * @param types The types to add
	 */
	static void add(SettingType<?, ?>[] types){
		for(var t : types) {
			typeMap.put(t.name(), t);
			intMap.put(t.getId(), t);
		}
	}
	
	// TODO use this to allow settings to be saved
	/**
	 * Get the setting of the given name
	 * @param name The name of the type to load
	 * @return The setting type, or null if none exists
	 */
	static SettingType<?, ?> get(String name){
		return typeMap.get(name);
	}
	
	/** Initialize all settings core to the main engine */
	static void initCore(){
		IntTypeSetting.init();
		DoubleTypeSetting.init();
	}
	
}
