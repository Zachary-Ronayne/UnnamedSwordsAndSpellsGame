package zgame.menu.togglebutton;

import zgame.core.Game;
import zgame.menu.MenuButton;

import java.util.List;

/**
 * A button which allows clicking to go between a list of values
 *
 * @param <T> The type of data in each toggleable state
 */
public class ToggleButton<T extends ToggleButtonValue> extends MenuButton{
	
	/** The values which will be toggled through */
	private List<T> values;
	
	/** The currently selected index in {@link #values} */
	private int selectedIndex;
	
	/**
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param defaultIndex The index of the initially selected value in values, can be null to default to 0
	 * @param values See {@link #values}
	 */
	public ToggleButton(double x, double y, double w, double h, Integer defaultIndex, List<T> values){
		super(x, y, w, h, "");
		this.values = values;
		this.setSelectedIndex(defaultIndex == null ? 0 : defaultIndex);
	}
	
	/** @return The currently selected value of this button */
	public T getSelectedValue(){
		if(values == null) return null;
		return this.values.get(this.selectedIndex);
	}
	
	/** Toggle to the next value in {@link #values} */
	public void nextIndex(){
		if(values == null) return;
		var i = this.getSelectedIndex() + 1;
		if(i >= this.values.size()) i = 0;
		this.setSelectedIndex(i);
	}
	
	/** Go to the previous value in {@link #values} */
	public void prevIndex(){
		if(values == null) return;
		var i = this.getSelectedIndex() - 1;
		if(i < 0) i = this.values.size() - 1;
		this.setSelectedIndex(i);
	}
	
	/** @return See {@link #selectedIndex} */
	public int getSelectedIndex(){
		return this.selectedIndex;
	}
	
	/** @param selectedIndex See {@link #selectedIndex} */
	public void setSelectedIndex(int selectedIndex){
		if(selectedIndex < 0) return;
		this.selectedIndex = selectedIndex;
		if(values == null) return;
		var v = this.values.get(this.selectedIndex);
		this.setText(v.getText());
		this.onValueChange(v);
	}
	
	/** @param values See {@link #values} */
	public void setValues(List<T> values){
		this.values = values;
	}
	
	@Override
	public void click(){
		super.click();
		if(Game.get().getKeyInput().shift()) this.prevIndex();
		else this.nextIndex();
	}
	
	/**
	 * Called whenever the value on this toggle button changes, does nothing by default
	 *
	 * @param value The new value
	 */
	public void onValueChange(T value){}
	
}
