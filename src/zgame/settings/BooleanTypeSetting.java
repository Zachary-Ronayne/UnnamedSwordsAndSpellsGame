package zgame.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import zgame.core.Game;

import java.util.function.BiConsumer;

/** A {@link Setting} holding a boolean. See {@link SettingType} */
public class BooleanTypeSetting extends SettingType<Boolean>{
	
	public static final BooleanTypeSetting V_SYNC = new BooleanTypeSetting("V_SYNC", true, (game, n) -> game.getWindow().setUseVsync(n));
	public static final BooleanTypeSetting FULLSCREEN = new BooleanTypeSetting("FULLSCREEN", false, (game, n) -> game.getWindow().setFullscreen(n));
	public static final BooleanTypeSetting PRINT_FPS = new BooleanTypeSetting("PRINT_FPS", true, (game, n) -> game.getRenderLooper().setPrintRate(n));
	public static final BooleanTypeSetting PRINT_TPS = new BooleanTypeSetting("PRINT_TPS", true, (game, n) -> game.getTickLooper().setPrintRate(n));
	
	public static final BooleanTypeSetting CAMERA_LOOK_INVERT_X = new BooleanTypeSetting("CAMERA_INVERT_X", false);
	public static final BooleanTypeSetting CAMERA_LOOK_INVERT_Y = new BooleanTypeSetting("CAMERA_INVERT_Y", false);
	
	/**
	 * Initialize a new boolean setting.
	 *
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 */
	protected BooleanTypeSetting(String name, boolean defaultVal){
		super(name, defaultVal);
	}
	
	/**
	 * Initialize a new boolean setting.
	 *
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 * @param onChange See {@link #onChange}
	 */
	protected BooleanTypeSetting(String name, boolean defaultVal, BiConsumer<Game, Boolean> onChange){
		super(name, defaultVal, onChange);
	}
	
	@Override
	public JsonElement toJson(Setting<Boolean> setting){
		return new JsonPrimitive(setting.get());
	}
	
	@Override
	public Boolean fromJson(JsonElement e){
		try{
			return e.getAsBoolean();
		} catch(Exception ex){
			return this.getDefault();
		}
	}
	
	/** A dummy method to allow this class to be called on start up, so that its static members are initialized */
	public static void init(){
	}
}
