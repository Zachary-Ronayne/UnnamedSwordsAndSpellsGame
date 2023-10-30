package zgame.settings;

import zgame.core.Game;
import zgame.core.file.Saveable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An interface to be used for generating settings, should be implemented by an enum
 * @param <T> The type of data used by the setting
 */
public abstract class SettingType<T> implements Saveable{
	
	/** Mapping a setting type string to the setting */
	public static final Map<String, SettingType<?>> typeMap = new HashMap<>();
	/** Mapping a setting id to the setting */
	public static final Map<Integer, SettingType<?>> intMap = new HashMap<>();
	
	/** The name representing this setting, should be unique from every other setting */
	private final String name;
	/** The id representing this setting, auto generated */
	private final int id;
	/** The default value of the setting if it hasn't been overridden */
	private final T defaultVal;
	/** A function that runs each time the setting changes, or null to do nothing on change. The game is the game where the setting changed, the T is the new value */
	private final BiConsumer<Game, T> onChange;
	
	/**
	 * Initialize a new setting.
	 * Only direct implementations of this class are permitted to create settings, and all settings must be initialized before any instances of {@link Settings} are created
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 */
	protected SettingType(String name, T defaultVal){
		this(name, defaultVal, null);
	}
	
	/**
	 * Initialize a new setting.
	 * Only direct implementations of this class are permitted to create settings, and all settings must be initialized before any instances of {@link Settings} are created
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 * @param onChange See {@link #onChange}
	 */
	protected SettingType(String name, T defaultVal, BiConsumer<Game, T> onChange){
		this.name = name;
		this.id = SettingId.next();
		this.defaultVal = defaultVal;
		this.onChange = onChange;
		
		add(this);
	}
	
	/** @return See {@link #name} */
	public String name(){
		return this.name;
	}
	
	/** @return The unique integer id to assign to this {@link SettingType}. Is obtained on init by calling {@link SettingId#next()} */
	public int id(){
		return this.id;
	}
	
	/** @return The default value used by this setting */
	public T getDefault(){
		return this.defaultVal;
	}
	
	/** See {@link #onChange} */
	public BiConsumer<Game, T> getOnChange(){
		return this.onChange;
	}
	
	/** @return All of the settings used in this settings type */
	public abstract SettingType<T>[] getValues();
	
	/**
	 * Get the type from the id. Primarily should be used for debugging, or when performance doesn't matter
	 * @param id The id of the setting to get
	 * @return The type, or null if none exists from the origin of this call
	 */
	public SettingType<T> getFromId(int id){
		for(var v : getValues()){
			if(id == v.id()) return v;
		}
		return null;
	}
	
	/**
	 * Add the given types to the static mapping of all settings.
	 *
	 * @param t The type to add
	 */
	public void add(SettingType<T> t){
		typeMap.put(t.name(), t);
		intMap.put(t.id(), t);
	}
	
	// TODO use this to allow settings to be saved
	/**
	 * Get the setting of the given name
	 * @param name The name of the type to load
	 * @return The setting type, or null if none exists
	 */
	public static SettingType<?> get(String name){
		return typeMap.get(name);
	}
	
}
