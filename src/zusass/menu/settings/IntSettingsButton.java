package zusass.menu.settings;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZRect;
import zgame.menu.scroller.HorizontalSelectionScroller;
import zgame.settings.IntTypeSetting;
import zusass.ZusassGame;
import zusass.menu.mainmenu.comp.newgamemenu.ZusassTextBox;

/** A button for selecting an integer setting */
public class IntSettingsButton extends ZusassTextBox{
	
	/** The setting which this button uses */
	private final IntTypeSetting setting;
	
	/** The game using this button */
	private final ZusassGame zgame;
	
	/** The display text of the setting */
	private final String name;
	
	/**
	 * Create a new {@link ZusassTextBox} with the given values
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param setting See {@link #setting}
	 * @param name See {@link #name}
	 * @param min The minimum value this setting can be scrolled to
	 * @param max The maximum value this setting can be scrolled to
	 * @param zgame The game using this button
	 */
	public IntSettingsButton(double x, double y, IntTypeSetting setting, String name, int min, int max, ZusassGame zgame){
		super(x, y, 300, 45, zgame);
		this.setting = setting;
		this.zgame = zgame;
		this.name = name;
		this.setHint(name + "...");
		this.setHintColor(this.getTextColor());
		this.setMode(Mode.INT_POS);
		this.setCurrentText(String.valueOf(this.zgame.get(this.setting)));
		
		// TODO make sure only one of these settings buttons can be selected at a time, maybe add a gain and lose focus system to all clickable things?
		// TODO maybe update the scroller position when the typed value changes, and when the thing loads?
		var scroller = new HorizontalSelectionScroller(min, max, this, zgame){
			@Override
			public void onScrollValueChange(double perc){
				super.onScrollValueChange(perc);
				setCurrentText(String.valueOf((int)perc));
			}
		};
		this.addThing(scroller);
		// TODO add some kind of validation for this, like don't let the setting be confirmed if none is entered
	}
	
	@Override
	public void setCurrentText(String currentText){
		super.setCurrentText(currentText);
		this.updateSetting(this.getTextAsInt());
	}
	
	/**
	 * @param newVal The new value for the setting, does nothing if the value is null
	 */
	private void updateSetting(Integer newVal){
		if(this.zgame != null){
			if(newVal != null) this.zgame.set(this.setting, newVal, false);
		}
	}
	
	@Override
	public String getDisplayText(){
		return this.name + ": " + super.getDisplayText();
	}
	
	@Override
	public double getCursorX(){
		// TODO make the base text box thing account for the offset that this text could have, instead of stupid magic numbers
		return super.getCursorX() + 145;
	}
}
