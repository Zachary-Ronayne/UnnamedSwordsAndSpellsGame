package zgame.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import zgame.core.Game;

import java.util.function.BiConsumer;

/** A {@link Setting} holding a double. See {@link SettingType} */
public class DoubleTypeSetting extends SettingType<Double>{
	
	public static final DoubleTypeSetting FOV = new DoubleTypeSetting("FOV", 1, Game::setFov);
	
	public static final DoubleTypeSetting CAMERA_LOOK_SPEED_X = new DoubleTypeSetting("CAMERA_LOOK_SPEED_X", 0.0007);
	public static final DoubleTypeSetting CAMERA_LOOK_SPEED_Y = new DoubleTypeSetting("CAMERA_LOOK_SPEED_Y", 0.0007);
	
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
