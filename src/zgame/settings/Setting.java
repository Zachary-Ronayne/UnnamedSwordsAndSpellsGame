package zgame.settings;

/**
 * A single setting holding one object
 */
public class Setting{
	
	/** The current value of this setting */
	private Object value;
	
	/**
	 * Create a new setting from the given type
	 *
	 * @param type The type holding the default value for this setting
	 */
	public Setting(SettingType<?> type){
		this.value = type.getDefault();
	}
	
	/** @param value The new value of the setting */
	public void set(Object value){
		this.value = value;
	}
	
	/** @return The value of this setting */
	public Object get(){
		return this.value;
	}
}
