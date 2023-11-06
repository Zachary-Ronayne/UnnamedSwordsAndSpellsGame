package zgame.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import zgame.core.Game;
import zgame.core.file.Saveable;

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
	 * Sets a value without checking that the types are the same
	 * @param setting The value of the setting to set
	 * @param value The new value
	 */
	private <T> void setValue(SettingType<T> setting, T value){
		this.values[setting.id()].setRaw(value);
		var onChange = setting.getOnChange();
		if(onChange != null) onChange.accept(this.getGame(), value);
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
	 */
	public void set(IntTypeSetting setting, int value){
		this.setValue(setting, value);
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
	 */
	public void set(DoubleTypeSetting setting, double value){
		this.setValue(setting, value);
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
			
			var index = SettingType.get(name).id();
			var setting = this.values[index];
			setting.setRaw(setting.getType().fromJson(value));
		}
		
		return true;
	}
}
