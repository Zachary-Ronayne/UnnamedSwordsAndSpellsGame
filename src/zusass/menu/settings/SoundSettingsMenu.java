package zusass.menu.settings;

import zgame.core.Game;
import zgame.settings.DoubleTypeSetting;

/** The menu used for displaying specific settings related to video options */
public class SoundSettingsMenu extends BaseSettingsMenu{
	
	/**
	 * Init the new menu
	 *
	 * @param settingsMenu The main menu using this menu
	 */
	public SoundSettingsMenu(SettingsMenu settingsMenu){
		super("Sound Settings", settingsMenu, true);
		this.getTitleThing().setFontSize(60);
		
		this.addThing(new SoundButton(0, DoubleTypeSetting.MUSIC_VOLUME, "Music Volume", this));
		this.addThing(new SoundButton(1, DoubleTypeSetting.EFFECTS_VOLUME, "Effects Volume", this));
	}
	
	// TODO fix the sound value not always using the updated value from the setting
	
	// TODO make releasing the effects button play an effect at the updated volume
	
	/** A button used to change sound volume levels */
	private static class SoundButton extends DoubleSettingsButton{
		
		/** The double type setting which this button changes */
		private final DoubleTypeSetting setting;
		
		/**
		 * Create a new sound button with the given values
		 *
		 * @param index The index position of the button
		 * @param setting See {@link #setting}
		 * @param name The display text of the setting
		 * @param menu The menu containing this button
		 */
		public SoundButton(int index, DoubleTypeSetting setting, String name, SoundSettingsMenu menu){
			// TODO fix the flickering back and forth slider button
			super(10, 150 + 50 * index, setting, name, 0.0, 100.0, menu);
			this.setWidth(500);
			this.setting = setting;
		}
		
		@Override
		public void changeDisplayedSetting(BaseSettingsMenu menu){
			super.changeDisplayedSetting(menu);
			var inputValue = this.getSettingInputValue();
			if(inputValue != null && this.setting != null) Game.get().set(this.setting, inputValue, true);
		}
		
	}
	
	@Override
	public void goBack(){
		super.goBack();
		this.getConfirmButton().handleConfirm();
	}
}
