package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
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
		this.setMinMenuStack(1);
	}
	
	/** Do not call directly, use {@link #renderHud(Game, Renderer)} to draw the state of this {@link Menu} and override its rendering behavior */
	@Override
	public void renderBackground(Game game, Renderer r){
	}
	
	/** Do not call directly, use {@link #renderHud(Game, Renderer)} to draw the state of this {@link Menu} and override its rendering behavior */
	@Override
	public final void render(Game game, Renderer r){
	}

	@Override
	public void renderHud(Game game, Renderer r){
		super.renderHud(game, r);
	}
	
}
