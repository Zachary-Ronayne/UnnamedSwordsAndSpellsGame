package zusass.menu.settings;

import zgame.core.Game;
import zgame.settings.StringTypeSetting;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassTextBox;

/** A toggle button used for modifying boolean settings */
public class StringSettingsButton extends ZusassTextBox implements ValueSettingsButton{
	
	/** The menu holding this button */
	private final BaseSettingsMenu menu;
	
	/** The setting used by this button */
	private final StringTypeSetting setting;
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param setting The setting which this button modifies
	 */
	public StringSettingsButton(double x, double y, StringTypeSetting setting, String name, BaseSettingsMenu menu){
		super(x, y, 600, 45);
		this.menu = menu;
		this.setting = setting;
		
		this.setHint(name + "...");
		this.setLabel(name + ": ");
		this.setCurrentText(ZusassGame.get().get(setting));
	}
	
	@Override
	public void setCurrentText(String currentText){
		super.setCurrentText(currentText);
		this.changeDisplayedSetting(this.menu);
	}
	
	/** @return See {@link #setting} */
	@Override
	public StringTypeSetting getSetting(){
		return this.setting;
	}
	
	@Override
	public String getSettingInputValue(){
		return this.getCurrentText();
	}
	
	@Override
	public void updateSetting(){
		Game.get().set(this.setting, this.getSettingInputValue(), false);
	}
}
