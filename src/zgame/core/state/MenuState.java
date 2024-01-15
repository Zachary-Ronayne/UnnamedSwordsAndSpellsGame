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
	 * @param game The game using this state
	 * @param menu The menu to use
	 */
	public MenuState(Game game, Menu menu){
		this(game, new MenuNode(menu));
	}
	
	/**
	 * Create a new {@link MenuState} with the given {@link MenuNode}
	 *
	 * @param game The game using this state
	 * @param menu The node to use
	 */
	public MenuState(Game game, MenuNode menu){
		super(false);
		this.setMenu(game, menu.getMenu());
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
