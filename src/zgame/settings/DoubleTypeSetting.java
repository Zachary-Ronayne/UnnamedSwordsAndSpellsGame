package zgame.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import zgame.core.Game;
import zgame.core.sound.SoundManager;

import java.util.function.BiConsumer;

/** A {@link Setting} holding a double. See {@link SettingType} */
public class DoubleTypeSetting extends SettingType<Double>{
	
	public static final DoubleTypeSetting FOV = new DoubleTypeSetting("FOV", 1, false, (oldD, newD) -> Game.get().setFov(newD));
	
	public static final DoubleTypeSetting CAMERA_LOOK_SPEED_X = new DoubleTypeSetting("CAMERA_LOOK_SPEED_X", 0.0009, false);
	public static final DoubleTypeSetting CAMERA_LOOK_SPEED_Y = new DoubleTypeSetting("CAMERA_LOOK_SPEED_Y", 0.0009, false);
	
	/** The volume the music player should be set to, 0 for muted, 100 for full volume */
	public static final DoubleTypeSetting MUSIC_VOLUME = new DoubleTypeSetting("MUSIC_VOLUME", 100.0, true, (oldD, newD) -> {
		var music = SoundManager.get().getMusicPlayer();
		music.setMuted(newD <= 0);
		music.setPaused(newD <= 0);
		music.setVolume(newD / 100.0);
	});
	/** The volume the effects player should be set to, 0 for muted, 100 for full volume */
	public static final DoubleTypeSetting EFFECTS_VOLUME = new DoubleTypeSetting("EFFECTS_VOLUME", 100.0, true, (oldD, newD) -> {
		var effects = SoundManager.get().getEffectsPlayer();
		effects.setMuted(newD <= 0);
		effects.setVolume(newD / 100.0);
	});
	
	/**
	 * Initialize a new double setting.
	 *
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 * @param exclusiveGlobal See {@link #exclusiveGlobal}
	 */
	protected DoubleTypeSetting(String name, double defaultVal, boolean exclusiveGlobal){
		super(name, defaultVal, exclusiveGlobal);
	}
	
	/**
	 * Initialize a new double setting.
	 *
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 * @param exclusiveGlobal See {@link #exclusiveGlobal}
	 * @param onChange See {@link #onChange}
	 */
	protected DoubleTypeSetting(String name, double defaultVal, boolean exclusiveGlobal, BiConsumer<Double, Double> onChange){
		super(name, defaultVal, exclusiveGlobal, onChange);
	}
	
	@Override
	public JsonElement toJson(Setting<Double> setting){
		return new JsonPrimitive(setting.get());
	}
	
	@Override
	public Double fromJson(JsonElement e){
		try{
			return e.getAsDouble();
		}catch(Exception ex){
			return this.getDefault();
		}
	}
	
	/** A dummy method to allow this class to be called on start up, so that its static members are initialized */
	public static void init(){
	}
}
