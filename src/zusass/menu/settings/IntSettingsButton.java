package zusass.menu.settings;

import org.lwjgl.glfw.GLFW;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuThing;
import zgame.menu.format.PercentFormatter;
import zgame.menu.scroller.HorizontalScroller;
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
		
		// TODO maybe update the scroller position when the typed value changes, and when the thing loads?
		var scroller = new HorizontalScroller(0, 0, 1, 1, this.getWidth(), zgame){
			@Override
			public void scroll(double amount){
				super.scroll(amount);
				setCurrentText(String.valueOf((int)(min + (max - min) * getPercent())));
			}
		};
		scroller.setFormatter(new PercentFormatter(1.0, 1.0, 0.5, 0.5));
		
		// TODO make sure only one of these settings buttons can be selected at a time, maybe add a gain and lose focus system to all clickable things?
		
		// TODO Maybe make another scroller implementation for this purpose?
		scroller.removeBorder();
		scroller.invisible();
		scroller.getButton().setFill(new ZColor(.5, .5, .5, .3));
		scroller.getButton().setFormatter(new PercentFormatter(0.2, 1.0, null, null));
		
		scroller.setAmount(1);
		scroller.setScrollWheelAsPercent(false);
		scroller.setDraggableButton(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		
		var moveThing = new MenuThing();
		moveThing.invisible();
		moveThing.setWidth(this.getWidth());
		moveThing.setHeight(scroller.getHeight());
		scroller.setMovingThing(moveThing);
		this.addThing(scroller);
		scroller.format();
		
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
