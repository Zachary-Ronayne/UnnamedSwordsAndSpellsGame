package zusass.menu.pause.comp;

import zusass.menu.comp.ZusassButton;
import zusass.menu.pause.PauseMenu;

/** A {@link ZusassButton} used by the pause menu */
public class PauseMenuButton extends ZusassButton{
	
	/** The menu using this button */
	private final PauseMenu menu;
	
	/**
	 * Create a {@link PauseMenuButton} with the given values
	 *
	 * @param x The x coordinate of the button
	 * @param y The y coordinate of the button
	 * @param text The text for the button
	 */
	public PauseMenuButton(double x, double y, String text, PauseMenu menu){
		super(x, y, 250, 50, text);
		this.menu = menu;
	}
	
	/** @return See {@link #menu} */
	public PauseMenu getMenu(){
		return this.menu;
	}
	
}
