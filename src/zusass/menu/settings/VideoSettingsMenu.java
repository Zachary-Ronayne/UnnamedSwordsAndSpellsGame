package zusass.menu.settings;

import zgame.settings.BooleanTypeSetting;
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
		
		// TODO make an abstract object for modifying a numerical setting
	}
}
