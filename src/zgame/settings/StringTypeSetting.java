package zgame.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Consumer;

/** A {@link Setting} holding a string. See {@link SettingType} */
public class StringTypeSetting extends SettingType<String>{
	
	public static final StringTypeSetting STRING_TEST = new StringTypeSetting("STRING_TEST", "test value", (n) -> {});
	
	/**
	 * Initialize a new boolean setting.
	 *
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 */
	protected StringTypeSetting(String name, String defaultVal){
		super(name, defaultVal);
	}
	
	/**
	 * Initialize a new string setting.
	 *
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 * @param onChange See {@link #onChange}
	 */
	protected StringTypeSetting(String name, String defaultVal, Consumer<String> onChange){
		super(name, defaultVal, onChange);
	}
	
	@Override
	public JsonElement toJson(Setting<String> setting){
		return new JsonPrimitive(setting.get());
	}
	
	@Override
	public String fromJson(JsonElement e){
		try{
			return e.getAsString();
		} catch(Exception ex){
			return this.getDefault();
		}
	}
	
	/** A dummy method to allow this class to be called on start up, so that its static members are initialized */
	public static void init(){
	}
}
