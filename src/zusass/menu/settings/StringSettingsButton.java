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
	
	/** The game using this button */
	private final ZusassGame zgame;
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param setting The setting which this button modifies
	 * @param zgame The game that uses this button
	 */
	public StringSettingsButton(double x, double y, StringTypeSetting setting, String name, BaseSettingsMenu menu, ZusassGame zgame){
		super(x, y, 600, 45, zgame);
		this.menu = menu;
		this.setting = setting;
		this.zgame = zgame;
		
		this.setHint(name + "...");
		this.setLabel(name + ": ");
		this.setCurrentText(zgame.get(setting));
	}
	
	@Override
	public void setCurrentText(String currentText){
		super.setCurrentText(currentText);
		this.changeDisplayedSetting(this.zgame, this.menu);
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
	public void updateSetting(Game game){
		game.set(this.setting, this.getSettingInputValue(), false);
	}
}
