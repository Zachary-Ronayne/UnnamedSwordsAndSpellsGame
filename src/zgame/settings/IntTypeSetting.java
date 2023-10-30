package zgame.settings;

import zgame.core.Game;

import java.util.function.BiConsumer;

/** A {@link Setting} holding an integer */
public enum IntTypeSetting implements SettingType<IntTypeSetting, Integer>{
	
	// TODO should this be defined as another interface, and then a default int settings enum for core settings?
	
	TEST(0);
	
	/** The id representing this setting */
	private final int id;
	/** The default value of the setting if it hasn't been overridden */
	private final int defaultVal;
	/** See {@link #getOnChange()} */
	private final BiConsumer<Game, Integer> onChange;
	
	IntTypeSetting(int defaultVal){
		this(defaultVal, null);
	}
	
	IntTypeSetting(int defaultVal, BiConsumer<Game, Integer> onChange){
		this.id = SettingId.next();
		this.defaultVal = defaultVal;
		this.onChange = onChange;
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Override
	public Integer getDefault(){
		return this.defaultVal;
	}
	
	@Override
	public BiConsumer<Game, Integer> getOnChange(){
		return this.onChange;
	}
	
	@Override
	public SettingType<IntTypeSetting, Integer> getFromId(int id){
		for(var v : values()){
			if(id == v.id) return v;
		}
		return null;
	}
	
	/** Must call this before the game is initialized to ensure settings work */
	public static void init(){
		for(var v : values()) v.getId();
		SettingType.add(IntTypeSetting.values());
	}
	
}
