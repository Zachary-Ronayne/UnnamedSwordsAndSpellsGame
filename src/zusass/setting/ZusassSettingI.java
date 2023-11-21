package zusass.setting;

import zgame.settings.IntTypeSetting;

public class ZusassSettingI extends IntTypeSetting{
	
	public static final ZusassSettingI Z_TEST = new ZusassSettingI("Z_TEST", 88);
	
	protected ZusassSettingI(String name, int defaultVal){
		super(name, defaultVal);
	}
	
	/** A dummy method to allow this class to be called on start up, so that its static members are initialized */
	public static void init(){
	}
	
}
