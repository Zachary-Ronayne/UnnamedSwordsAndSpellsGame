package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.menu.Menu;

/**
 * A generic class which handles a {@link GameState} designed around interacting with a menu
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public abstract class MenuState<D> extends GameState<D>{
	
	/** The {@link Menu} which this {@link MenuState} uses */
	private Menu<D> menu;
	
	/**
	 * Create a new {@link MenuState} with the given {@link Menu}
	 * 
	 * @param menu The menu to use
	 */
	public MenuState(Menu<D> menu){
		super(false);
		this.menu = menu;
	}
	
	/** @return See {@link #menu} */
	public Menu<D> getMenu(){
		return this.menu;
	}
	
	/** @param See {@link #menu} */
	public void setMenu(Menu<D> menu){
		this.menu = menu;
	}
	
	@Override
	public void tick(Game<D> game, double dt){
		this.getMenu().tick(game, dt);
	}
	
	@Override
	public void keyAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.getMenu().keyAction(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.getMenu().mouseAction(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public void mouseMove(Game<D> game, double x, double y){
		this.getMenu().mouseMove(game, x, y);
	}
	
	@Override
	public void mouseWheelMove(Game<D> game, double amount){
		this.getMenu().mouseWheelMove(game, amount);
	}
	
	@Override
	public void renderBackground(Game<D> game, Renderer r){
		this.getMenu().renderBackground(game, r);
	}
	
	@Override
	public void render(Game<D> game, Renderer r){
		this.getMenu().render(game, r);
	}
	
	@Override
	public void renderHud(Game<D> game, Renderer r){
		this.getMenu().renderHud(game, r);
	}
	
}
