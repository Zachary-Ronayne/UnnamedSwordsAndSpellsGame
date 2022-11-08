package zgame.core.state;

import zgame.menu.Menu;

/**
 * A generic class which handles a {@link GameState} designed around interacting with a menu
 */
public abstract class MenuState extends GameState{
	
	/**
	 * Create a new {@link MenuState} with the given {@link Menu}
	 * 
	 * @param menu The menu to use
	 */
	
	public MenuState(Menu menu){
		this(new MenuNode(menu));
	}
	
	/**
	 * Create a new {@link MenuState} with the given {@link MenuNode}
	 * 
	 * @param menu The node to use
	 */
	public MenuState(MenuNode menu){
		super(false);
		this.setMenu(menu.getMenu());
	}
	
}
