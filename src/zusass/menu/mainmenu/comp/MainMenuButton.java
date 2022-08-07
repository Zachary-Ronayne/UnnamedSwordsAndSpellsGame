package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZUSASSData;

/** A class used to define similarities between buttons for the main menu */
public abstract class MainMenuButton extends MenuButton<ZUSASSData>{

	/**
	 * Create a main menu button with the appropriate parameters
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param text The text to display
	 */
	public MainMenuButton(double x, double y, String text, Game<ZUSASSData> game){
		super(x, y, 500, 120, text);
		this.setFont(game.getFont("zfont"));
		this.setFontColor(new ZColor(.2));
		this.setFontSize(50);
		this.setBorderWidth(2);
		this.setBorder(new ZColor(.6));
		this.setTextX(10);
		this.setTextY(80);
	}
	
}
