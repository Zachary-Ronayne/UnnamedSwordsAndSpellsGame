package zgame.settings;

import zgame.core.Game;

import java.util.function.BiConsumer;

/** A {@link Setting} holding an integer See {@link SettingType} */
public class IntTypeSetting extends SettingType<Integer>{
	
	public static final IntTypeSetting TEST = new IntTypeSetting("TEST", 0);
	
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
	
	/** A dummy method to allow this class to be called on start up, so that its static members are initialized */
	public static void init(){
	}
}
