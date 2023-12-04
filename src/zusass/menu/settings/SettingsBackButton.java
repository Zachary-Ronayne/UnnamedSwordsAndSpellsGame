package zusass.menu.settings;

import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** A button pre positioned for moving back to the previous settings menu */
public class SettingsBackButton extends ZusassButton{
	
	/**
	 * Create a {@link ZusassButton} with the appropriate parameters
	 *
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public SettingsBackButton(ZusassGame zgame){
		super(0, 0, 100, 30, "Back", zgame);
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(null, 20.0, null, 20.0));
		this.centerText();
	}
}
