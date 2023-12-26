package zusass.menu.settings;

import zgame.core.Game;
import zgame.menu.scroller.HorizontalSelectionScroller;
import zgame.settings.IntTypeSetting;
import zusass.ZusassGame;
import zusass.menu.mainmenu.comp.newgamemenu.ZusassTextBox;

/** A button for selecting an integer setting */
public class IntSettingsButton extends ZusassTextBox implements ValueSettingsButton{
	
	/** The menu holding this button */
	private final BaseSettingsMenu menu;
	
	/** The setting which this button uses */
	private final IntTypeSetting setting;
	
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
	public IntSettingsButton(double x, double y, IntTypeSetting setting, String name, int min, int max, BaseSettingsMenu menu, ZusassGame zgame){
		super(x, y, 300, 45, zgame);
		this.menu = menu;
		this.setting = setting;
		this.zgame = zgame;
		this.setHint(name + "...");
		this.setLabel(name + ": ");
		this.setMode(min < 0 || max < 0 ? Mode.INT : Mode.INT_POS);
		var currentValue = zgame.get(this.setting);
		this.setCurrentText(String.valueOf(currentValue));
		
		this.scroller = new HorizontalSelectionScroller(min, max, this, zgame){
			@Override
			public void onScrollValueChange(double perc){
				super.onScrollValueChange(perc);
				setCurrentText(String.valueOf((int)perc));
			}
		};
		this.addThing(this.scroller);
		this.scroller.setScrolledValue(currentValue);
	}
	
	@Override
	public void setCurrentText(String currentText){
		super.setCurrentText(currentText);
		if(scroller == null) return;
		
		var newValue = this.getSettingInputValue();
		if(newValue == null) newValue = (int)this.scroller.getMin();
		this.scroller.setScrolledValue(newValue);
		this.changeDisplayedSetting(this.zgame, this.menu);
	}
	
	/** @return See {@link #setting} */
	@Override
	public IntTypeSetting getSetting(){
		return this.setting;
	}
	
	@Override
	public Integer getSettingInputValue(){
		return this.getTextAsInt();
	}
	
	@Override
	public void updateSetting(Game game){
		var newValue = this.getSettingInputValue();
		if(newValue != null) game.set(this.setting, newValue, false);
	}
}
