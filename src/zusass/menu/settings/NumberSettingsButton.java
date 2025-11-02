package zusass.menu.settings;

import zgame.core.Game;
import zgame.menu.scroller.HorizontalSelectionScroller;
import zgame.settings.SettingType;
import zusass.menu.comp.ZusassTextBox;

import java.util.Objects;

/**
 * A button for selecting a number setting
 *
 * @param <N> The number type of this setting
 */
public abstract class NumberSettingsButton<N extends Number> extends SettingsButtonTextBox<SettingType<N>, N> implements ValueSettingsButton{
	
	/** The scroller used to change this setting */
	private final HorizontalSelectionScroller scroller;
	
	/** The current value that this setting is expected to be across all inputs */
	private double settingValue;
	
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
	public NumberSettingsButton(double x, double y, SettingType<N> setting, String name, N min, N max, boolean allowDecimal, BaseSettingsMenu menu){
		super(x, y, 300, 45, setting, menu);
		this.setHint(name + "...");
		this.setLabel(name + ": ");
		if(min != null && max != null){
			if(allowDecimal) this.setMode(min.doubleValue() < 0 || max.doubleValue() < 0 ? Mode.FLOAT : Mode.FLOAT_POS);
			else this.setMode(min.doubleValue() < 0 || max.doubleValue() < 0 ? Mode.INT : Mode.INT_POS);
		}
		else this.setMode(allowDecimal ? Mode.FLOAT : Mode.INT);
		
		if(min != null && max != null){
			this.scroller = new HorizontalSelectionScroller(min.doubleValue(), max.doubleValue(), this){
				@Override
				public void onScrollValueChange(double oldValue, double newValue){
					super.onScrollValueChange(oldValue, newValue);
					setSettingValue(newValue);
					onSettingScrollerChange(oldValue, newValue);
				}
			};
			this.addThing(this.scroller);
		}
		else this.scroller = null;
		
		this.setSettingValue(Game.get().getAny(setting).doubleValue());
	}
	
	/** @return See {@link #settingValue} */
	public double getSettingValue(){
		return this.settingValue;
	}
	
	/** @param settingValue See {@link #settingValue} */
	public void setSettingValue(double settingValue){
		this.settingValue = settingValue;
		this.updateFromSettingValue(this.settingValue);
	}
	
	/** Update the ui things related to this button to account for the current value of {@link #settingValue} */
	public void updateFromSettingValue(double value){
		// Update the displayed value for the text
		this.setCurrentTextWithoutUpdate(value);
		
		// Update the position of the scroller
		if(this.scroller != null) this.setScrolledValueWithoutUpdate(value);
		
		// The setting was modified, do base operations for the settings button changing
		this.changeDisplayedSetting(this.getMenu());
	}
	
	@Override
	public void onWidthChange(){
		super.onWidthChange();
		if(this.scroller != null) this.scroller.setWidth(this.getWidth());
	}
	
	/**
	 * Get a string representing the given scroller value
	 *
	 * @param value The value of the scroller at its current position
	 * @return The string representing the value
	 */
	public abstract String scrollPercentToText(double value);
	
	/** @param value The new value for this button's current text. Does not update the internal settings value, only the value internal to the ui things */
	public void setCurrentTextWithoutUpdate(double value){
		// If the current text's double value is the same as the new value, don't do anything
		if(Objects.equals(value, this.getTextAsDouble())) return;
		
		super.setCurrentText(scrollPercentToText(value));
	}
	
	/** @param value The new value for the scroller's setting value. Does not update the internal settings value, only the value internal to the scroller */
	public void setScrolledValueWithoutUpdate(double value){
		if(this.scroller != null) this.scroller.setValueWithoutUpdate(value);
	}
	
	/**
	 * Called when the scroller moves, not necessarily when the value changes. Does nothing by default, override for custom behavior
	 * @param oldValue The old value before the scroll
	 * @param newValue The new value after the scroll
	 */
	public void onSettingScrollerChange(double oldValue, double newValue){}
	
	@Override
	public void setCurrentText(String currentText){
		super.setCurrentText(currentText);
		
		// Update the stored value to the one typed in
		var textValue = this.getSettingTextInputValue();
		if(textValue != null) this.setSettingValue(textValue.doubleValue());
	}
	
	@Override
	public abstract N getSettingTextInputValue();
	
	@Override
	public void updateSetting(){
		var newValue = this.getSettingTextInputValue();
		if(newValue != null) Game.get().setAny(this.getSetting(), newValue, false);
	}
	
}
