package zusass.menu.settings;

import zgame.core.Game;
import zgame.menu.scroller.HorizontalSelectionScroller;
import zgame.settings.SettingType;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassTextBox;

/**
 * A button for selecting a number setting
 *
 * @param <N> The number type of this setting
 */
public abstract class NumberSettingsButton<N extends Number> extends ZusassTextBox implements ValueSettingsButton{
	
	/** The menu holding this button */
	private final BaseSettingsMenu menu;
	
	/** The setting which this button uses */
	private final SettingType<N> setting;
	
	/** The scroller used to change this setting */
	private final HorizontalSelectionScroller scroller;
	
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
		super(x, y, 300, 45);
		this.menu = menu;
		this.setting = setting;
		this.setHint(name + "...");
		this.setLabel(name + ": ");
		if(min != null && max != null){
			if(allowDecimal) this.setMode(min.doubleValue() < 0 || max.doubleValue() < 0 ? Mode.FLOAT : Mode.FLOAT_POS);
			else this.setMode(min.doubleValue() < 0 || max.doubleValue() < 0 ? Mode.INT : Mode.INT_POS);
		}
		else this.setMode(allowDecimal ? Mode.FLOAT : Mode.INT);
		
		var currentValue = ZusassGame.get().getAny(this.setting);
		this.setCurrentText(String.valueOf(currentValue));
		
		if(min != null && max != null){
			this.scroller = new HorizontalSelectionScroller(min.doubleValue(), max.doubleValue(), this){
				@Override
				public void onScrollValueChange(double amount){
					super.onScrollValueChange(amount);
					setCurrentText(scrollPercentToText(amount));
				}
			};
			this.addThing(this.scroller);
			this.scroller.setScrolledValue(currentValue.doubleValue());
		}
		else this.scroller = null;
	}
	
	@Override
	public void onWidthChange(){
		super.onWidthChange();
		if(this.scroller != null) this.scroller.setWidth(this.getWidth());
	}
	
	/**
	 * Get a string representing the given scroller value
	 *
	 * @param amount The value of the scroller at its current position
	 * @return The string representing the value
	 */
	public abstract String scrollPercentToText(double amount);
	
	@Override
	public void setCurrentText(String currentText){
		super.setCurrentText(currentText);
		
		// Move the scroller position to the appropriate place
		Number newValue = this.getSettingInputValue();
		if(this.scroller != null){
			if(newValue == null) newValue = this.scroller.getMin();
			// TODO is a delta comparison the correct approach for a base case to avoid infinite recursion?
			if(Math.abs(newValue.doubleValue() - this.scroller.getScrolledValue()) > 1E-8) this.scroller.setScrolledValue(newValue.doubleValue());
		}
		
		// TODO fix the confirm button being disabled when going to 0 for the setting
		
		this.changeDisplayedSetting(this.menu);
	}
	
	/** @return See {@link #setting} */
	@Override
	public SettingType<N> getSetting(){
		return this.setting;
	}
	
	@Override
	public abstract N getSettingInputValue();
	
	@Override
	public void updateSetting(){
		var newValue = this.getSettingInputValue();
		if(newValue != null) Game.get().setAny(this.setting, newValue, false);
	}
	
}
