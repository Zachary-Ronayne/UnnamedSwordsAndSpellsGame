package zusass.menu.settings;

import zgame.settings.BooleanTypeSetting;
import zgame.settings.IntTypeSetting;
import zusass.ZusassGame;

/** The menu used for displaying specific settings related to video options */
public class VideoSettingsMenu extends BaseSettingsMenu{
	
	/**
	 * Init the new menu
	 * @param zgame The game using the menu
	 */
	public VideoSettingsMenu(ZusassGame zgame, SettingsMenu settingsMenu){
		super("Video Settings", zgame, settingsMenu);
		this.getTitleThing().setFontSize(60);
		
		this.addThing(new BoolSettingsButton(10, 150, BooleanTypeSetting.V_SYNC, "Vsync Enabled", "Vsync Disabled", zgame));
		this.addThing(new BoolSettingsButton(10, 200, BooleanTypeSetting.FULLSCREEN, "Fullscreen", "Windowed", zgame));
		this.addThing(new IntSettingsButton(10, 250, IntTypeSetting.FPS_LIMIT, "Max FPS", 0, 300, zgame));
		
		// TODO add a confirm system, so the settings don't apply or save until hitting confirm? At least for video settings
	}
}
