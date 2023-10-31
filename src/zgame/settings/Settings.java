package zgame.settings;

import zgame.core.Game;

/** An object keeping tracking of values used by the game */
public class Settings{
	
	/** The {@link Game} using this {@link Settings} */
	private final Game game;
	
	/** The settings used by this object */
	private final Setting[] values;
	
	/**
	 * Create a new settings object for the given game
	 * @param game See {@link #game}
	 */
	public Settings(Game game){
		this.game = game;
		
		this.values = new Setting[SettingId.numIds()];
		for(var e : SettingType.idMap.entrySet()){
			var index = e.getKey();
			var type = e.getValue();
			this.values[index] = new Setting(type);
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
	private <T> Object getValue(SettingType<T> setting){
		return this.values[setting.id()].get();
	}
	
	/**
	 * Sets a value without checking that the types are the same
	 * @param setting The value of the setting to set
	 * @param value The new value
	 */
	private <T> void setValue(SettingType<T> setting, T value){
		this.values[setting.id()].set(value);
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
	
}
