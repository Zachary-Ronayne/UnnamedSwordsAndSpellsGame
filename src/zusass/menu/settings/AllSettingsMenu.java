package zusass.menu.settings;

import zgame.settings.BooleanTypeSetting;
import zgame.settings.IntTypeSetting;
import zgame.settings.SettingType;
import zusass.ZusassGame;

/** A menu for changing any setting */
public class AllSettingsMenu extends BaseSettingsMenu{
	
	/**
	 * Init the new menu
	 * @param zgame The game using the menu
	 * @param settingsMenu The main menu using this menu
	 */
	public AllSettingsMenu(ZusassGame zgame, SettingsMenu settingsMenu){
		super("All Settings", zgame, settingsMenu, true);
		
		var y = 250;
		for(var setting : SettingType.idMap.values()){
			// I don't like this use of instance of, but it's good enough for now
			if(setting.getDefault() instanceof Integer){
				this.addThing(new IntSettingsButton(10, y, (IntTypeSetting)setting, setting.name(), null, null, this, zgame));
			}
			else if(setting.getDefault() instanceof Boolean){
				this.addThing(new BoolSettingsButton(10, y, (BooleanTypeSetting)setting, setting.name() + ": true", setting.name() + ": false", this, zgame));
			}
			else continue;
			
			y += 50;
		}
	}
}
