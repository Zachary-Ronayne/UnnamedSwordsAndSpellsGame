package zusass.menu.settings;

import zgame.settings.BooleanTypeSetting;
import zgame.settings.IntTypeSetting;
import zusass.setting.ZusassSettingI;

/** The menu used for displaying specific settings related to video options */
public class VideoSettingsMenu extends BaseSettingsMenu{
	
	/**
	 * Init the new menu
	 *
	 * @param settingsMenu The main menu using this menu
	 */
	public VideoSettingsMenu(SettingsMenu settingsMenu){
		super("Video Settings", settingsMenu, true);
		this.getTitleThing().setFontSize(60);
		
		this.addThing(new BoolSettingsButton(10, 150, BooleanTypeSetting.V_SYNC, "Vsync Enabled", "Vsync Disabled", this));
		this.addThing(new BoolSettingsButton(10, 200, BooleanTypeSetting.FULLSCREEN, "Fullscreen", "Windowed", this));
		this.addThing(new IntSettingsButton(10, 250, IntTypeSetting.FPS_LIMIT, "Max FPS", 0, 300, this));
		this.addThing(new IntSettingsButton(10, 300, ZusassSettingI.Z_TEST, "Test", -100, 230, this));
	}
}
