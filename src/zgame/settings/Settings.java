package zgame.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import zgame.core.Game;
import zgame.core.file.Saveable;
import zgame.core.utils.ZConfig;

/** An object keeping tracking of values used by the game */
public class Settings implements Saveable{
	
	/** The json key used to store the settings used by this object */
	public static final String SETTINGS_ARR_KEY = "settings";
	
	/** The {@link Game} using this {@link Settings} */
	private final Game game;
	
	/** The settings used by this object */
	private Setting<?>[] values;
	
	/**
	 * Create a new settings object for the given game
	 * @param game See {@link #game}
	 */
	public Settings(Game game){
		this.game = game;
		this.initValues();
	}
	
	/** Set up the array of settings to have one index per setting, and for all of them to have the default values */
	private void initValues(){
		this.values = new Setting[SettingId.numIds()];
		for(var e : SettingType.idMap.entrySet()){
			var index = e.getKey();
			var type = e.getValue();
			this.values[index] = new Setting<>(type);
		}
	}
	
	/** Load the default value for every setting */
	public void setDefaults(){
		for(var v : values) v.setRaw(v.getType().getDefault());
	}
	
	/** @return See {@link #game} */
	public Game getGame(){
		return this.game;
	}
	
	/**
	 * Gets a setting without checking its type
	 * @param setting The value of the setting to get
	 * @return The value
	 */
	public <T> Object getValue(SettingType<T> setting){
		return this.values[setting.id()].get();
	}
	
	/**
	 * Sets a value without checking that the types are the same. Generally should avoid using when not needed
	 * @param setting The value of the setting to set
	 * @param value The new value
	 * @param shouldChange true if updating this setting should call the {@link SettingType#onChange} method
	 */
	@SuppressWarnings("unchecked")
	public <T> void setValue(SettingType<T> setting, Object value, boolean shouldChange){
		this.values[setting.id()].setRaw(value);
		if(!shouldChange) return;
		
		var onChange = setting.getOnChange();
		if(onChange != null) onChange.accept(this.getGame(), (T)value);
	}
	
	/**
	 * Get a boolean value of a setting
	 * @param setting The name of the setting to get the value of
	 * @return The setting's value
	 */
	public Boolean get(BooleanTypeSetting setting){
		return (Boolean)this.getValue(setting);
	}
	
	/**
	 * Set the value of a boolean setting
	 * @param setting The name of the setting to set the value of
	 * @param value The new value
	 * @param shouldChange true if updating this setting should call the {@link SettingType#onChange} method
	 */
	public void set(BooleanTypeSetting setting, boolean value, boolean shouldChange){
		this.setValue(setting, value, shouldChange);
	}
	
	/**
	 * Get an integer value of a setting
	 * @param setting The name of the setting to get the value of
	 * @return The setting's value
	 */
	public Integer get(IntTypeSetting setting){
		return (Integer)this.getValue(setting);
	}
	
	/**
	 * Set the value of an integer setting
	 * @param setting The name of the setting to set the value of
	 * @param value The new value
	 * @param shouldChange true if updating this setting should call the {@link SettingType#onChange} method
	 */
	public void set(IntTypeSetting setting, int value, boolean shouldChange){
		this.setValue(setting, value, shouldChange);
	}
	
	/**
	 * Get a double value of a setting
	 * @param setting The name of the setting to get the value of
	 * @return The setting's value
	 */
	public Double get(DoubleTypeSetting setting){
		return (Double)this.getValue(setting);
	}
	
	/**
	 * Set the value of a double setting
	 * @param setting The name of the setting to set the value of
	 * @param value The new value
	 * @param shouldChange true if updating this setting should call the {@link SettingType#onChange} method
	 */
	public void set(DoubleTypeSetting setting, double value, boolean shouldChange){
		this.setValue(setting, value, shouldChange);
	}
	
	/**
	 * Set this object's settings values to the ones in the given settings object which are not the default settings
	 * @param settings The settings to place into this settings
	 * @param shouldChange true if updating this setting should call the {@link SettingType#onChange} method
	 */
	public void setNonDefault(Settings settings, boolean shouldChange){
		for(var s : settings.values){
			if(s.getType().isDefault(s.get())) continue;
			this.setValue(s.getType(), s.get(), shouldChange);
		}
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		var settingsMap = new JsonObject();
		for(var v : this.values){
			// Save only settings which are not equal to their defaults
			if(!v.getType().isDefault(v.get())) v.save(settingsMap);
		}
		
		obj.add(SETTINGS_ARR_KEY, settingsMap);
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.initValues();
		var settingsMapElement = e.getAsJsonObject().get(SETTINGS_ARR_KEY);
		if(settingsMapElement == null) return true;
		var settingsMap = settingsMapElement.getAsJsonObject();
		for(var entry : settingsMap.entrySet()){
			var name = entry.getKey();
			var value = entry.getValue();
			
			// If the setting is unknown, skip it
			var s = SettingType.get(name);
			if(s == null) {
				ZConfig.error("When loading settings, could not modify setting with name: ", name);
				continue;
			}
			
			var index = s.id();
			var setting = this.values[index];
			setting.setRaw(setting.getType().fromJson(value));
		}
		
		return true;
	}
	
}
