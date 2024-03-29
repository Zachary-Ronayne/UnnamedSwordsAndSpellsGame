package zusass.menu.settings;

import zgame.core.Game;
import zgame.menu.scroller.HorizontalSelectionScroller;
import zgame.settings.SettingType;
import zusass.ZusassGame;
import zusass.menu.mainmenu.comp.newgamemenu.ZusassTextBox;

/**
 * A button for selecting a number setting
 * @param <N> The number type of this setting
 */
public abstract class NumberSettingsButton<N extends Number> extends ZusassTextBox implements ValueSettingsButton{
	
	/** The menu holding this button */
	private final BaseSettingsMenu menu;
	
	/** The setting which this button uses */
	private final SettingType<N> setting;
	
	/** The game using this button */
	private final ZusassGame zgame;
	
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
	 * @param zgame The game using this button
	 */
	public NumberSettingsButton(double x, double y, SettingType<N> setting, String name, Integer min, Integer max, boolean allowDecimal, BaseSettingsMenu menu, ZusassGame zgame){
		super(x, y, 300, 45, zgame);
		this.menu = menu;
		this.setting = setting;
		this.zgame = zgame;
		this.setHint(name + "...");
		this.setLabel(name + ": ");
		if(min != null && max != null) {
			if(allowDecimal) this.setMode(min < 0 || max < 0 ? Mode.FLOAT : Mode.FLOAT_POS);
			else this.setMode(min < 0 || max < 0 ? Mode.INT : Mode.INT_POS);
		}
		else this.setMode(allowDecimal ? Mode.FLOAT : Mode.INT);
		
		var currentValue = zgame.getAny(this.setting);
		this.setCurrentText(String.valueOf(currentValue));
		
		if(min != null && max != null){
			this.scroller = new HorizontalSelectionScroller(min, max, this, zgame){
				@Override
				public void onScrollValueChange(double perc){
					super.onScrollValueChange(perc);
					setCurrentText(String.valueOf((int)perc));
				}
			};
			this.addThing(this.scroller);
			this.scroller.setScrolledValue(currentValue.doubleValue());
		}
		else this.scroller = null;
	}
	
	@Override
	public void setCurrentText(String currentText){
		super.setCurrentText(currentText);
		
		Number newValue = this.getSettingInputValue();
		if(scroller != null){
			if(newValue == null) newValue = this.scroller.getMin();
			this.scroller.setScrolledValue(newValue.doubleValue());
		}
		this.changeDisplayedSetting(this.zgame, this.menu);
	}
	
	/** @return See {@link #setting} */
	@Override
	public SettingType<N> getSetting(){
		return this.setting;
	}
	
	@Override
	public abstract N getSettingInputValue();
	
	@Override
	public void updateSetting(Game game){
		var newValue = this.getSettingInputValue();
		if(newValue != null) game.setAny(this.setting, newValue, false);
	}
	
}
