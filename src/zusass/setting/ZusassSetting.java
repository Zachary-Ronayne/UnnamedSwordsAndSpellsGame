package zusass.setting;

import zgame.settings.DoubleTypeSetting;
import zusass.ZusassGame;

/** A simple class holding all initialization for all settings used by Zusass */
public final class ZusassSetting{
	
	/** Init all Zusass related settings */
	public static void init(){
		ZusassSettingI.init();
		
		// Make the music restart after it goes from no volume to some volume
		DoubleTypeSetting.MUSIC_VOLUME.registerOnChange((oldD, newD) -> {
			if(oldD <= 0 && newD > 0) ZusassGame.get().startMusicLoop();
		});
	}
	
	/** Cannot instantiate {@link ZusassSetting} */
	private ZusassSetting(){}
}
