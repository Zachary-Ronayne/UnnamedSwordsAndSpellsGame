package zgame.menu.format;

import zgame.menu.MenuThing;

import java.util.ArrayList;
import java.util.Arrays;

/** A {@link MenuFormatter} which applies multiple formatters at once */
public class MultiFormatter implements MenuFormatter{
	
	/** The formatters to apply, in the order they should be applied */
	private final ArrayList<MenuFormatter> formatters;
	
	/** @param formatters The initial formatters */
	public MultiFormatter(MenuFormatter... formatters){
		this.formatters = new ArrayList<>(Arrays.asList(formatters));
	}
	
	/** @return See {@link #formatters} */
	public ArrayList<MenuFormatter> getFormatters(){
		return this.formatters;
	}
	
	@Override
	public void onWidthChange(MenuThing thing, double width){
		for(var f : this.formatters) f.onWidthChange(thing, width);
	}
	
	@Override
	public void onHeightChange(MenuThing thing, double height){
		for(var f : this.formatters) f.onHeightChange(thing, height);
	}
}
