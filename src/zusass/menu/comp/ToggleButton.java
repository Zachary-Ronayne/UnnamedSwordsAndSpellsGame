package zusass.menu.comp;

import zgame.core.Game;
import zusass.ZusassGame;

import java.util.List;

/** A button which allows clicking to go between a list of values */
public class ToggleButton extends ZusassButton{

	/** The values which will be toggled through */
	private List<String> values;

	/** The currently selected index in {@link #values} */
	private int selectedIndex;
	
	/**
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param values See {@link #values}
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public ToggleButton(double x, double y, double w, double h, List<String> values, ZusassGame zgame){
		super(x, y, w, h, "", zgame);
		this.values = values;
		this.setSelectedIndex(0);
	}
	
	public String getSelectedValue(){
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
		this.selectedIndex = selectedIndex;
		if(values == null) return;
		this.setText(this.values.get(this.selectedIndex));
	}
	
	/** @param values See {@link #values} */
	public void setValues(List<String> values){
		this.values = values;
	}
	
	@Override
	public void click(Game game){
		super.click(game);
		if(game.getKeyInput().shift()) this.prevIndex();
		else this.nextIndex();
	}
}
