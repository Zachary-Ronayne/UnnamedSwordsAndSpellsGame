package zusass.menu.comp;

import zgame.menu.togglebutton.BoolToggleButton;
import zgame.menu.togglebutton.ToggleButton;
import zusass.ZusassGame;

/** A {@link ToggleButton} in the Zusass style */
public class ZusassBoolToggleButton extends BoolToggleButton{
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param trueSelected true if true is selected by default, false otherwise
	 * @param trueValue The value to display when this button is toggle to true
	 * @param falseValue The value to display when this button is toggle to false
	 * @param zgame The game that uses this button
	 */
	public ZusassBoolToggleButton(double x, double y, double w, double h, boolean trueSelected, String trueValue, String falseValue, ZusassGame zgame){
		super(x, y, w, h, trueSelected, trueValue, falseValue, zgame);
		
		ZusassStyle.applyStyleText(zgame, this);
	}
	
}
