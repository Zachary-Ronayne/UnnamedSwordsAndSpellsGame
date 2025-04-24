package zusass.menu.comp;

import zgame.menu.togglebutton.ToggleButton;
import zgame.menu.togglebutton.ToggleButtonValue;

import java.util.List;

/** A {@link ToggleButton} in the Zusass style */
public class ZusassToggleButton<T extends ToggleButtonValue> extends ToggleButton<T>{
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param defaultIndex The index of the initially selected value in values, can be null to default to 0
	 * @param values See {@link #values}
	 */
	public ZusassToggleButton(double x, double y, double w, double h, Integer defaultIndex, List<T> values){
		super(x, y, w, h, defaultIndex, values);
		
		ZusassStyle.applyStyleText(this);
	}
}
