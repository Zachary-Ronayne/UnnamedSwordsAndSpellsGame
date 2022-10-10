package zgame.menu;

/**
 * A simple {@link MenuThing} that uses an empty thing with no dimensions to group menu things together
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public class MenuHolder<D>extends MenuThing<D>{
	
	/**
	 * Create a {@link MenuHolder} positioned relative to its parent
	 */
	public MenuHolder(){
		this(0, 0);
	}
	/**
	 * Create a {@link MenuHolder} at the given position. Everything added to this holder will be relative to these coordinates
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public MenuHolder(double x, double y){
		super(x, y);
		this.setDrawThingsToBuffer(false);
	}
	
}
