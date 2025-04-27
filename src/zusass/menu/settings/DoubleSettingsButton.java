package zusass.menu.settings;

import zgame.settings.DoubleTypeSetting;
import zusass.menu.comp.ZusassTextBox;

/** A button for selecting a double setting */
public class DoubleSettingsButton extends NumberSettingsButton<Double>{
	
	/**
	 * Create a new {@link ZusassTextBox} with the given values
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param setting See {@link #setting}
	 * @param name The display text of the setting
	 * @param min The minimum value this setting can be scrolled to
	 * @param max The maximum value this setting can be scrolled to
	 */
	public DoubleSettingsButton(double x, double y, DoubleTypeSetting setting, String name, Integer min, Integer max, BaseSettingsMenu menu){
		super(x, y, setting, name, min, max, true, menu);
	}
	
	@Override
	public Double getSettingInputValue(){
		return this.getTextAsDouble();
	}
}
