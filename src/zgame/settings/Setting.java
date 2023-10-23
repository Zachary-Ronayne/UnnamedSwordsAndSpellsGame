package zgame.settings;

/** A single setting holding one object */
public class Setting{
	
	/** The current value of this setting */
	private Object value;
	
	// TODO maybe have different Setting object types, one for each type of setting
	/**
	 * Create a new setting from the given type
	 * @param type The type holding the default value for this setting
	 */
	public Setting(SettingType<?, ?> type){
		this.value = type.getDefault();
	}
	
	public void set(Object value){
		this.value = value;
	}
	
	/** Get the value of this setting */
	public Object get(){
		return this.value;
	}
}
