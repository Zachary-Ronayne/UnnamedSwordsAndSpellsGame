package zgame.settings;

import zgame.core.Game;

import java.util.function.BiConsumer;

/** A {@link Setting} holding an integer */
public class IntTypeSetting extends SettingType<Integer>{
	
	// TODO find a way to do this without defining it in 2 places?
	public static final IntTypeSetting TEST = new IntTypeSetting("TEST", 0);
	
	// TODO should this be an array list?
	/** An array holding all core int settings */
	public static final IntTypeSetting[] VALUES = new IntTypeSetting[]{
			TEST
	};
	
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
	public SettingType<Integer>[] getValues(){
		return VALUES;
	}
}
