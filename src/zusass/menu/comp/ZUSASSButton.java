package zusass.menu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZUSASSData;

/** A {@link MenuButton} which is used by the ZUSASSGame */
public abstract class ZUSASSButton extends MenuButton<ZUSASSData>{

	/**
	 * Create a {@link ZUSASSButton} with the appropriate parameters
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 * @param game The {@link Game} that uses this button
	 */
	public ZUSASSButton(double x, double y, double w, double h, String text, Game<ZUSASSData> game){
		super(x, y, w, h, text);
		this.setFont(game.getFont("zfont"));
		this.setFontColor(new ZColor(.2));
		this.setFontSize(50);
		this.setBorderWidth(2);
		this.setBorder(new ZColor(.6));
		this.setTextX(10);
		this.setTextY(80);
	}

}