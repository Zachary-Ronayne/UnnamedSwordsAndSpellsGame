package zusass.menu.settings;

import zgame.menu.togglebutton.BoolToggleButtonValue;
import zgame.settings.BooleanTypeSetting;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassBoolToggleButton;

/** A toggle button used for modifying boolean settings */
public class BoolSettingsButton extends ZusassBoolToggleButton{
	
	/** The setting used by this button */
	private final BooleanTypeSetting setting;
	
	/** The game using this button */
	private final ZusassGame zgame;
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param setting The setting which this button modifies
	 * @param trueValue The value to display when this button is toggle to true
	 * @param falseValue The value to display when this button is toggle to false
	 * @param zgame The game that uses this button
	 */
	public BoolSettingsButton(double x, double y, BooleanTypeSetting setting, String trueValue, String falseValue, ZusassGame zgame){
		super(x, y, 300, 45, zgame.get(setting), trueValue, falseValue, zgame);
		this.setting = setting;
		this.zgame = zgame;
		this.centerText();
	}
	
	@Override
	public void onValueChange(BoolToggleButtonValue value){
		super.onValueChange(value);
		if(this.zgame != null) this.zgame.set(this.setting, value.isTrue(), false);
		this.centerText();
	}
}
