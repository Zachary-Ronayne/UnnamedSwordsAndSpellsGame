package zusass.menu.comp;

import zgame.menu.MenuButton;
import zusass.ZusassGame;

/** A {@link MenuButton} which is used by the ZusassGame */
public abstract class ZusassButton extends MenuButton{
	
	/**
	 * Create a {@link ZusassButton} with the appropriate parameters
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public ZusassButton(double x, double y, double w, double h, String text, ZusassGame zgame){
		super(x, y, w, h, text, zgame);
		ZusassStyle.applyStyleText(zgame, this);
		this.setFontSize(40);
		this.centerText();
	}
	
}