package zgame.settings;

import zgame.core.Game;
import zgame.core.utils.ZStringUtils;

import java.util.function.BiConsumer;

/** A {@link Setting} holding an integer */
public enum DoubleTypeSetting implements SettingType<DoubleTypeSetting, Double>{
	
	// TODO find a way to abstract out most of this that's shared with the int version
	
	TEST_D(1.2, (game, n) -> ZStringUtils.prints("New value", n, game));
	
	/** The id representing this setting */
	private final int id;
	/** The default value of the setting if it hasn't been overridden */
	private final double defaultVal;
	/** See {@link #getOnChange()} */
	private final BiConsumer<Game, Double> onChange;
	
	DoubleTypeSetting(double defaultVal){
		this(defaultVal, null);
	}
	
	DoubleTypeSetting(double defaultVal, BiConsumer<Game, Double> onChange){
		this.id = SettingId.next();
		this.defaultVal = defaultVal;
		this.onChange = onChange;
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Override
	public Double getDefault(){
		return this.defaultVal;
	}
	
	@Override
	public BiConsumer<Game, Double> getOnChange(){
		return this.onChange;
	}
	
	@Override
	public SettingType<DoubleTypeSetting, Double> getFromId(int id){
		for(var v : values()){
			if(id == v.id) return v;
		}
		return null;
	}
	
	/** Must call this before the game is initialized to ensure settings work */
	public static void init(){
		for(var v : values()) v.getId();
		SettingType.add(DoubleTypeSetting.values());
	}
	
}
