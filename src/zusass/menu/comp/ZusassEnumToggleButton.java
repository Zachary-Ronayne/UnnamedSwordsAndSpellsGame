package zusass.menu.comp;

import zgame.menu.togglebutton.EnumToggleButton;
import zgame.menu.togglebutton.ToggleButton;
import zgame.menu.togglebutton.ToggleButtonValue;

/** A {@link ToggleButton} in the Zusass style */
public class ZusassEnumToggleButton<E extends Enum<E> & ToggleButtonValue> extends EnumToggleButton<E>{
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param defaultSelected The default enum to select for this button
	 * @param values The valid enums to use, usually just MyEnum.values()
	 */
	public ZusassEnumToggleButton(double x, double y, double w, double h, E defaultSelected, E[] values){
		super(x, y, w, h, defaultSelected, values);
		
		ZusassStyle.applyStyleText(this);
	}
	
}
