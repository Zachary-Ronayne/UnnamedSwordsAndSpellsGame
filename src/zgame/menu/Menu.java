package zgame.menu;

/**
 * An object which represents a single {@link Menu}. A menu is a simple object used to group together other {@link MenuThing} objects with a background.
 */
public class Menu extends MenuThing{
	
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
		this(x, y, 0, 0, false);
	}
	
	/**
	 * Create a {@link Menu} of the given position and size
	 *
	 * @param x See {@link #relX}
	 * @param y See {@link #relY}
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 * @param useBuffer true to use {@link #buffer}, false otherwise
	 */
	public Menu(double x, double y, double width, double height, boolean useBuffer){
		super(x, y, width, height, useBuffer);
		this.setFill(this.getFill().solid());
	}
	
}
