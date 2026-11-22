package zusass.menu.settings;

import zgame.settings.SettingType;

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
	Object getSettingTextInputValue();
	
	/** @return The value of this setting before modifications to it were last saved */
	Object getInitialValue();
	
	/** Update the value returned by {@link #getInitialValue()} so that it reflects the current setting */
	void updateInitialValue();
	
	/**
	 * Should be called any time the input value for a setting is modified
	 *
	 * @param menu The menu holding the button
	 */
	default void changeDisplayedSetting(BaseSettingsMenu menu){
		if(menu == null) return;
		var confirmButton = menu.getConfirmButton();
		if(confirmButton == null) return;
		var currentValue = this.getSettingTextInputValue();
		if(Objects.equals(this.getInitialValue(), currentValue)) confirmButton.removeSettingButton(this);
		else confirmButton.addSettingButton(this);
	}
	
}
