package zgame.menu;

/**
 * An object which represents a single {@link Menu}. A menu is a simple object used to group together other {@link MenuThing} objects
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public class Menu<D> extends MenuThing<D>{
	
	/** Create an empty menu at (0, 0) */
	public Menu(){
		this(0, 0);
	}
	
	/**
	 * Create an empty menu at the given coordinates
	 * 
	 * @param x The x coordinate of the menu
	 * @param y The y coordinate of the menu
	 */
	public Menu(double x, double y){
		super(x, y);
		this.setFill(this.getFill().solid());
	}
	
}
