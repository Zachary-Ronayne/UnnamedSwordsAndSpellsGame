package zgame.menu;

import zgame.core.Game;

/**
 * An object which represents a single {@link Menu}. A menu is a simple object used to group together other {@link MenuThing} objects with a background.
 */
public class Menu extends MenuThing{
	
	/** true if this menu should be destroyed by default when it is removed, false otherwise. This value is true by default */
	private boolean defaultDestroyRemove;
	
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
		this.setDefaultDestroyRemove(true);
	}
	
	/** @return See {@link #defaultDestroyRemove} */
	public boolean isDefaultDestroyRemove(){
		return this.defaultDestroyRemove;
	}
	
	/** @param defaultDestroyRemove See {@link #defaultDestroyRemove} */
	public void setDefaultDestroyRemove(boolean defaultDestroyRemove){
		this.defaultDestroyRemove = defaultDestroyRemove;
	}
	
	@Override
	public boolean useMouseInput(Game game){
		return true;
	}
}
