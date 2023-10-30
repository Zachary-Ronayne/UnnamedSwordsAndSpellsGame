package zgame.settings;

import zgame.core.Game;
import zgame.core.utils.ZStringUtils;

import java.util.function.BiConsumer;

/** A {@link Setting} holding an integer */
public class DoubleTypeSetting extends SettingType<Double>{
	
	// TODO find a way to do this without defining it in 2 places?
	public static final DoubleTypeSetting TEST_D = new DoubleTypeSetting("TEST_D", 1.2, (game, n) -> ZStringUtils.prints("New value", n, game));
	
	/** An array holding all core double settings */
	public static final DoubleTypeSetting[] VALUES = new DoubleTypeSetting[]{
			TEST_D
	};
	
	/**
	 * Initialize a new double setting.
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 */
	protected DoubleTypeSetting(String name, double defaultVal){
		super(name, defaultVal);
	}
	/**
	 * Initialize a new double setting.
	 * @param name See {@link #name}
	 * @param defaultVal See {@link #defaultVal}
	 * @param onChange See {@link #onChange}
	 */
	protected DoubleTypeSetting(String name, double defaultVal, BiConsumer<Game, Double> onChange){
		super(name, defaultVal, onChange);
	}
	
	@Override
	public SettingType<Double>[] getValues(){
		return VALUES;
	}
}
