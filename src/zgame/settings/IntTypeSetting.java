package zgame.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import zgame.core.Game;

import java.util.function.BiConsumer;

/** A {@link Setting} holding an integer See {@link SettingType} */
public class IntTypeSetting extends SettingType<Integer>{
	
	public static final IntTypeSetting FPS_LIMIT = new IntTypeSetting("FPS_LIMIT", 100, Game::setMaxFps);
	
	/**
	 * Initialize a new int setting.
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 */
	protected IntTypeSetting(String name, int defaultVal){
		super(name, defaultVal);
	}
	
	/**
	 * Initialize a new int setting.
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 * @param onChange See {@link #onChange}
	 */
	protected IntTypeSetting(String name, int defaultVal, BiConsumer<Game, Integer> onChange){
		super(name, defaultVal, onChange);
	}
	
	@Override
	public JsonElement toJson(Setting<Integer> setting){
		return new JsonPrimitive(setting.get());
	}
	
	@Override
	public Integer fromJson(JsonElement e){
		try{
			return e.getAsInt();
		} catch(Exception ex){
			return this.getDefault();
		}
	}
	
	/** A dummy method to allow this class to be called on start up, so that its static members are initialized */
	public static void init(){
	}
	
}
