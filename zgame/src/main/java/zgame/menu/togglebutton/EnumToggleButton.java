package zgame.menu.togglebutton;

import java.util.ArrayList;
import java.util.Arrays;

/** A {@link ToggleButton} with one value for each in an enum */
public class EnumToggleButton<E extends Enum<E> & ToggleButtonValue> extends ToggleButton<E>{
	
	/** All possible enum values for this button */
	private final E[] values;
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param defaultSelected The default enum to select for this button
	 * @param values The valid enums to use, usually just MyEnum.values()
	 */
	public EnumToggleButton(double x, double y, double w, double h, E defaultSelected, E[] values){
		super(x, y, w, h, Arrays.stream(values).toList().indexOf(defaultSelected), new ArrayList<>(Arrays.stream(values).toList()));
		this.values = values;
	}
	
	/**
	 * Get the index in this button's toggleable values which contains the given enum
	 * @param val The enum
	 * @return The index, or -1 if it doesn't exist
	 */
	public int findIndex(E val){
		return Arrays.stream(this.values).toList().indexOf(val);
	}
	
	/**
	 * Set the enum selected by this button
	 * Note that calls to this method will also call {@link #setSelectedIndex(int)} and update any appropriate fields
	 *
	 * @param val The enum value to set to
	 */
	public void set(E val){
		this.setSelectedIndex(this.findIndex(val));
	}
	
}
