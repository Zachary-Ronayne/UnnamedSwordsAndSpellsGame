package zgame.settings;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;

/**
 * A single setting holding one object
 * @param <T> The type of data in this setting
 */
public class Setting<T> implements Saveable{
	
	/** The current value of this setting */
	private T value;
	
	/** The type of setting used by this setting */
	private final SettingType<T> type;
	
	/**
	 * Create a new setting from the given type
	 *
	 * @param type The type holding the default value for this setting
	 */
	public Setting(SettingType<T> type){
		this.value = type.getDefault();
		this.type = type;
	}
	
	/**
	 * Should generally use {@link #set(Object)} of this method whenever possible
	 * @param value The new value of the setting. It is assumed that this will be of the expected type for this setting
	 */
	@SuppressWarnings("unchecked")
	public void setRaw(Object value){
		this.value = (T)value;
	}
	
	/** @param value The new value of the setting */
	public void set(T value){
		this.value = value;
	}
	
	/** @return The value of this setting */
	public T get(){
		return this.value;
	}
	
	/** @return See {@link #type} */
	public SettingType<T> getType(){
		return this.type;
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.add(this.getType().name(), this.getType().toJson(this));
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var val = this.getType().fromJson(e);
		this.set(val);
		return true;
	}
}
