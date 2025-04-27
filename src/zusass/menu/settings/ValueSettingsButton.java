package zusass.menu.settings;

import zgame.settings.SettingType;
import zusass.ZusassGame;

import java.util.Objects;

/** An interface for input menu things which can modify a setting */
public interface ValueSettingsButton{
	/**
	 * Based on the state of this menu thing, update the current setting value
	 */
	void updateSetting();
	
	/** @return The setting type that this button uses */
	SettingType<?> getSetting();
	
	/** @return The current value of the setting being input */
	Object getSettingInputValue();
	
	/**
	 * Should be called any time the input value for a setting is modified
	 *
	 * @param menu The menu holding the button
	 */
	default void changeDisplayedSetting(BaseSettingsMenu menu){
		if(menu == null) return;
		var confirmButton = menu.getConfirmButton();
		if(confirmButton == null) return;
		var setting = this.getSetting();
		var currentValue = this.getSettingInputValue();
		if(Objects.equals(ZusassGame.get().getAny(setting), currentValue)) confirmButton.removeSettingButton(this);
		else confirmButton.addSettingButton(this);
	}
	
}
