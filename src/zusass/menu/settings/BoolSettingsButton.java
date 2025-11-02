package zusass.menu.settings;

import zgame.core.Game;
import zgame.menu.togglebutton.BoolToggleButtonValue;
import zgame.settings.BooleanTypeSetting;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassBoolToggleButton;

/** A toggle button used for modifying boolean settings */
public class BoolSettingsButton extends ZusassBoolToggleButton implements ValueSettingsButton{
	
	/** The menu holding this button */
	private final BaseSettingsMenu menu;
	
	/** The setting used by this button */
	private final BooleanTypeSetting setting;
	
	/** The value of this setting before any changes */
	private Object initialValue;
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param setting The setting which this button modifies
	 * @param trueValue The value to display when this button is toggle to true
	 * @param falseValue The value to display when this button is toggle to false
	 */
	public BoolSettingsButton(double x, double y, BooleanTypeSetting setting, String trueValue, String falseValue, BaseSettingsMenu menu){
		super(x, y, 300, 45, ZusassGame.get().get(setting), trueValue, falseValue);
		this.menu = menu;
		this.setting = setting;
		this.initialValue = Game.get().getAny(setting);
		this.centerText();
	}
	
	@Override
	public void onValueChange(BoolToggleButtonValue value){
		super.onValueChange(value);
		this.changeDisplayedSetting(this.menu);
		this.centerText();
	}
	
	/** @return See {@link #setting} */
	@Override
	public BooleanTypeSetting getSetting(){
		return this.setting;
	}
	
	@Override
	public Boolean getSettingTextInputValue(){
		return this.getSelectedValue().isTrue();
	}
	
	@Override
	public Object getInitialValue(){
		return this.initialValue;
	}
	
	@Override
	public void updateInitialValue(){
		this.initialValue = Game.get().get(this.setting);
	}
	
	@Override
	public void updateSetting(){
		Game.get().set(this.setting, this.getSelectedValue().isTrue(), false);
	}
}
