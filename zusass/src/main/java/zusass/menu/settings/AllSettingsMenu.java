package zusass.menu.settings;

import zgame.settings.*;

/** A menu for changing any setting */
public class AllSettingsMenu extends BaseSettingsMenu{
	
	/**
	 * Init the new menu
	 *
	 * @param settingsMenu The main menu using this menu
	 */
	public AllSettingsMenu(SettingsMenu settingsMenu){
		super("All Settings", settingsMenu, true);
		
		var y = 250;
		for(var setting : SettingType.idMap.values()){
			// I don't like this use of instance of, but it's good enough for now
			if(setting.getDefault() instanceof Integer){
				this.addThing(new IntSettingsButton(10, y, (IntTypeSetting)setting, setting.name(), null, null, this));
			}
			else if(setting.getDefault() instanceof Double){
				this.addThing(new DoubleSettingsButton(10, y, (DoubleTypeSetting)setting, setting.name(), null, null, this));
			}
			else if(setting.getDefault() instanceof Boolean){
				this.addThing(new BoolSettingsButton(10, y, (BooleanTypeSetting)setting, setting.name() + ": true", setting.name() + ": false", this));
			}
			else if(setting.getDefault() instanceof String){
				this.addThing(new StringSettingsButton(10, y, (StringTypeSetting)setting, setting.name(), this));
			}
			else continue;
			
			y += 50;
		}
	}
}
