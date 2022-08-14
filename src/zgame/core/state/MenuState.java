package zgame.core.state;

import zgame.core.Game;
import zgame.menu.Menu;

/**
 * A generic class which handles a {@link GameState} designed around interacting with a menu
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public abstract class MenuState<D>extends GameState<D>{
	
	/**
	 * Create a new {@link MenuState} with the given {@link Menu}
	 * 
	 * @param menu The menu to use
	 */
	
	public MenuState(Menu<D> menu){
		this(new MenuNode<>(menu));
	}
	
	/**
	 * Create a new {@link MenuState} with the given {@link MenuNode}
	 * 
	 * @param menu The node to use
	 */
	public MenuState(MenuNode<D> menu){
		super(false);
		this.setMenu(menu.getMenu());
	}
	
}
