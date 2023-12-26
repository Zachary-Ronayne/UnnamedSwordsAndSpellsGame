package zgame.menu.togglebutton;

import zgame.core.Game;

import java.util.ArrayList;
import java.util.List;

/** A {@link ToggleButton} with two values, displaying strings */
public class BoolToggleButton extends ToggleButton<BoolToggleButtonValue>{
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param trueSelected true if true is selected by default, false otherwise
	 * @param trueValue The value to display when this button is toggle to true
	 * @param falseValue The value to display when this button is toggle to false
	 * @param game The game that uses this button
	 */
	public BoolToggleButton(double x, double y, double w, double h, boolean trueSelected, String trueValue, String falseValue, Game game){
		super(x, y, w, h, trueSelected ? 0 : 1, new ArrayList<>(List.of(new BoolToggleButtonValue(true, trueValue), new BoolToggleButtonValue(false, falseValue))), game);
	}
	
	/**
	 * Set if this button is selected as true or false.
	 * Note that calls to this method will also call {@link #setSelectedIndex(int)} and update any appropriate fields
	 *
	 * @param isTrue true if the button should be set to true, false otherwise
	 */
	public void set(boolean isTrue){
		this.setSelectedIndex(isTrue ? 0 : 1);
	}
	
}
