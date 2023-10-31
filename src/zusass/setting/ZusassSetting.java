package zusass.setting;

/** A simple class holding all initialization for all settings used by Zusass */
public final class ZusassSetting{
	
	/** Init all Zusass related settings */
	public static void init(){
		ZusassSettingI.init();
	}
	
	/** Cannot instantiate {@link ZusassSetting} */
	private ZusassSetting(){}
}
