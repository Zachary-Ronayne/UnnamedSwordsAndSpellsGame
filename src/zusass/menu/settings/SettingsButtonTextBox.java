package zusass.menu.settings;

import zgame.core.Game;
import zgame.settings.SettingType;
import zusass.menu.comp.ZusassTextBox;

/**
 * A button and text box used for allowing values to be typed in
 * @param <S> The type of setting this button text box will use
 * @param <D> The actual data type the setting uses
 */
public abstract class SettingsButtonTextBox<S extends SettingType<D>, D> extends ZusassTextBox implements ValueSettingsButton{
	
	/** The menu holding this button */
	private final BaseSettingsMenu menu;
	
	/** The setting used by this button */
	private final S setting;
	
	/** The value of this setting before any changes */
	private Object initialValue;
	
	/**
	 * Create a new {@link ZusassTextBox} with the given values
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public SettingsButtonTextBox(double x, double y, double w, double h, S setting, BaseSettingsMenu menu){
		super(x, y, w, h);
		this.menu = menu;
		this.setting = setting;
		this.initialValue = Game.get().getAny(setting);
		
	}
	
	/** @return See {@link #menu} */
	public BaseSettingsMenu getMenu(){
		return this.menu;
	}
	
	@Override
	public S getSetting(){
		return this.setting;
	}
	
	@Override
	public Object getInitialValue(){
		return this.initialValue;
	}
	
	@Override
	public void updateInitialValue(){
		this.initialValue = Game.get().getAny(this.getSetting());
	}
	
}
