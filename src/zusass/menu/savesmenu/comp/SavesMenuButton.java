package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;
import zusass.menu.savesmenu.SavesMenu;

/** A {@link ZusassButton} for navigating the {@link SavesMenu} */
public class SavesMenuButton extends ZusassButton{
	
	/** The width of every {@link SavesMenuButton} */
	public static final double WIDTH = 160;
	/** The height of every {@link SavesMenuButton} */
	public static final double HEIGHT = 40;
	
	// The {@link SavesMenu} associated with this button
	private SavesMenu menu;
	
	/**
	 * Create a main menu button with the appropriate parameters
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param text The text to display
	 * @param zgame The {@link Game} that uses this button
	 */
	public SavesMenuButton(double x, double y, String text, SavesMenu menu, ZusassGame zgame){
		super(x, y, WIDTH, HEIGHT, text, zgame);
		this.menu = menu;
		this.setFontSize(30);
		this.setTextY(35);
		this.setFill(new ZColor(.6));
		this.setBorder(new ZColor(0.8));
		this.setBorderWidth(1);
	}
	
	/** @return See {@link #menu} */
	public SavesMenu getMenu(){
		return this.menu;
	}
	
}
