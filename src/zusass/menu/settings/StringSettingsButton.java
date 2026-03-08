package zusass.menu.settings;

import zgame.core.Game;
import zgame.settings.SettingType;
import zgame.settings.StringTypeSetting;
import zusass.ZusassGame;

/** A button used for typing in string settings */
public class StringSettingsButton extends SettingsButtonTextBox<SettingType<String>, String> implements ValueSettingsButton{
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param setting The setting which this button modifies
	 */
	public StringSettingsButton(double x, double y, StringTypeSetting setting, String name, BaseSettingsMenu menu){
		super(x, y, 600, 45, setting, menu);
		
		this.setHint(name + "...");
		this.setLabel(name + ": ");
		this.setCurrentText(ZusassGame.get().get(setting));
	}
	
	@Override
	public void setCurrentText(String currentText){
		super.setCurrentText(currentText);
		this.changeDisplayedSetting(this.getMenu());
	}
	
	@Override
	public String getSettingTextInputValue(){
		return this.getCurrentText();
	}

	@Override
	public void updateSetting(){
		Game.get().set((StringTypeSetting)this.getSetting(), this.getSettingTextInputValue());
	}
	
}
