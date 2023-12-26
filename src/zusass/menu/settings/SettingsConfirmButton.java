package zusass.menu.settings;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.format.PixelFormatter;
import zgame.settings.SettingType;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

import java.util.HashMap;
import java.util.Map;

/** A button pre positioned and with functionality for confirming changes to settings */
public class SettingsConfirmButton extends ZusassButton {
	
	/** A mapping of a setting to its button for modifying it */
	private final Map<SettingType<?>, ValueSettingsButton> buttons;
	
	/**
	 * Create a {@link ZusassButton} with the appropriate parameters
	 *
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public SettingsConfirmButton(ZusassGame zgame){
		super(0, 0, 100, 30, "Confirm", zgame);
		this.buttons = new HashMap<>();
		
		this.setFill(new ZColor(.8, .8, 1));
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(null, 130.0, null, 20.0));
		this.centerText();
		
		this.updateDisabled();
	}
	
	/** @param b A button to add to keep track of for updating when confirm is pressed */
	public void addSettingButton(ValueSettingsButton b){
		this.buttons.put(b.getSetting(), b);
		this.updateDisabled();
	}
	
	/** @param b A button to no longer keep track of for updating when confirm is pressed */
	public void removeSettingButton(ValueSettingsButton b){
		this.buttons.remove(b.getSetting());
		this.updateDisabled();
	}
	
	/** Determine if this button should be disabled or not */
	private void updateDisabled(){
		this.setDisabled(this.buttons.isEmpty());
	}
	
	@Override
	public void click(Game game){
		super.click(game);
		
		for(var button : this.buttons.values()) button.updateSetting(game);
		game.saveGlobalSettings();
		
		this.buttons.clear();
		this.updateDisabled();
	}
}
