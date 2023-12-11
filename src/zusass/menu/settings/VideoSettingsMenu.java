package zusass.menu.settings;

import zgame.menu.togglebutton.BoolToggleButtonValue;
import zgame.settings.BooleanTypeSetting;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassBoolToggleButton;

/** The menu used for displaying specific settings related to video options */
public class VideoSettingsMenu extends BaseSettingsMenu{
	
	/**
	 * Init the new menu
	 * @param zgame The game using the menu
	 */
	public VideoSettingsMenu(ZusassGame zgame, SettingsMenu settingsMenu){
		super("Video Settings", zgame, settingsMenu);
		this.getTitleThing().setFontSize(60);
		
		// TODO somehow abstract this out, like a boolean settings button
		var vsyncButton = new ZusassBoolToggleButton(10, 50, 350, 100, zgame.get(BooleanTypeSetting.V_SYNC), "Vsync Enabled", "Vsync Disabled", zgame){
			@Override
			public void onValueChange(BoolToggleButtonValue value){
				super.onValueChange(value);
				zgame.set(BooleanTypeSetting.V_SYNC, value.isTrue(), false);
				centerText();
			}
		};
		vsyncButton.centerText();
		this.addThing(vsyncButton);
		
		// TODO make an abstract object for modifying a numerical setting
	}
}
