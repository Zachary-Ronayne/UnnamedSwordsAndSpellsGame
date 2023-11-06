package zgame.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import zgame.core.Game;
import zgame.core.utils.ZStringUtils;

import java.util.function.BiConsumer;

// TODO populate with actual settings
/** A {@link Setting} holding a double. See {@link SettingType} */
public class DoubleTypeSetting extends SettingType<Double>{
	
	public static final DoubleTypeSetting TEST_D = new DoubleTypeSetting("TEST_D", 1.2, (game, n) -> ZStringUtils.prints("New value", n, game));
	
	/**
	 * Initialize a new double setting.
	 *
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 */
	protected DoubleTypeSetting(String name, double defaultVal){
		super(name, defaultVal);
	}
	
	/**
	 * Initialize a new double setting.
	 *
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 * @param onChange See {@link #onChange}
	 */
	protected DoubleTypeSetting(String name, double defaultVal, BiConsumer<Game, Double> onChange){
		super(name, defaultVal, onChange);
	}
	
	@Override
	public JsonElement toJson(Setting<Double> setting){
		return new JsonPrimitive(setting.get());
	}
	
	@Override
	public Double fromJson(JsonElement e){
		try{
			return e.getAsDouble();
		} catch(Exception ex){
			return this.getDefault();
		}
	}
	
	/** A dummy method to allow this class to be called on start up, so that its static members are initialized */
	public static void init(){
	}
}
