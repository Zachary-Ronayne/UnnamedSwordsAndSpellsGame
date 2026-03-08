package zusass.setting;

import zgame.settings.IntTypeSetting;

public class ZusassSettingI extends IntTypeSetting{
	
	public static final ZusassSettingI Z_TEST = new ZusassSettingI("Z_TEST", 88, false);
	public static final ZusassSettingI Z_TEST_2 = new ZusassSettingI("Z_TEST_2", 4, false);
	
	protected ZusassSettingI(String name, int defaultVal, boolean exclusiveGlobal){
		super(name, defaultVal, exclusiveGlobal);
	}
	
	/** A dummy method to allow this class to be called on start up, so that its static members are initialized */
	public static void init(){
	}
	
}
