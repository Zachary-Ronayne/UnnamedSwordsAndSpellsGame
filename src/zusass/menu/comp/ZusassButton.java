package zusass.menu.comp;

import zgame.menu.MenuButton;

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
	 */
	public ZusassButton(double x, double y, double w, double h, String text){
		super(x, y, w, h, text);
		
		// issue#30 why does using a buffer here make some buttons not display?
//		this.setDefaultUseBuffer(true);
		
		ZusassStyle.applyStyleText(this);
		this.setFontSize(40);
		this.centerText();
	}
	
}