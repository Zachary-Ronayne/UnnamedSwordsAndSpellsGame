package zgame.core.state;

import java.util.LinkedList;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.menu.Menu;

/**
 * A generic class which handles a {@link GameState} designed around interacting with a menu
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public abstract class MenuState<D>extends GameState<D>{
	
	/** The {@link Menu}s which this {@link MenuState} uses. The top of the stack ticks and takes input by default, the rest only render */
	private LinkedList<Menu<D>> menuStack;
	
	// TODO add options to turn on or off tick, render, and input methods individually
	// Do this with nodes? Like, each node decides which actions happen
	
	/**
	 * Create a new {@link MenuState} with the given {@link Menu}
	 * 
	 * @param menu The menu to use
	 */
	public MenuState(Menu<D> menu){
		super(false);
		this.menuStack = new LinkedList<Menu<D>>();
		this.menuStack.addFirst(menu);
	}
	
	/** @return The root menu of this {@link MenuState}, i.e. the menu on the bottom before popups */
	public Menu<D> getMenu(){
		return this.menuStack.getFirst();
	}

	public Menu<D> getTopMenu(){
		return this.menuStack.getLast();
	}
	
	/** @param menu The new root menu of this {@link MenuState}, i.e. the menu on the bottom before popups */
	public void setMenu(Menu<D> menu){
		this.menuStack.removeFirst();
		this.menuStack.addFirst(menu);
	}
	
	/**
	 * Add the given {@link Menu} on top of the existing menus on this state
	 * @param menu The menu to add
	 */
	public void popupMenu(Menu<D> menu){
		this.menuStack.addLast(menu);
	}
	
	/**
	 * Remove the menu on the top of this menu state
	 * 
	 * @return The removed menu, or null if only the base menu exists
	 */
	public Menu<D> removeTopMenu(){
		if(this.menuStack.size() == 1) return null;
		return this.menuStack.removeLast();
	}
	
	@Override
	public void tick(Game<D> game, double dt){
		this.getTopMenu().tick(game, dt);
		// this.menuStack.forEach(m -> m.tick(game, dt));
	}
	
	@Override
	public void keyAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.getTopMenu().keyAction(game, button, press, shift, alt, ctrl);
		// this.menuStack.forEach(m -> m.keyAction(game, button, press, shift, alt, ctrl));
	}
	
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.getTopMenu().mouseAction(game, button, press, shift, alt, ctrl);
		// this.menuStack.forEach(m -> m.mouseAction(game, button, press, shift, alt, ctrl));
	}
	
	@Override
	public void mouseMove(Game<D> game, double x, double y){
		this.getTopMenu().mouseMove(game, x, y);
		// this.menuStack.forEach(m -> m.mouseMove(game, x, y));
	}
	
	@Override
	public void mouseWheelMove(Game<D> game, double amount){
		this.getTopMenu().mouseWheelMove(game, amount);
		// this.menuStack.forEach(m -> m.mouseWheelMove(game, amount));
	}
	
	/** Does nothing for MenuState */
	@Override
	public final void renderBackground(Game<D> game, Renderer r){
	}
	
	@Override
	public void render(Game<D> game, Renderer r){
		this.getMenu().render(game, r);
		this.menuStack.forEach(m ->{
			m.renderBackground(game, r);
			m.render(game, r);
			m.renderHud(game, r);
		});
	}
	
	/** Does nothing for MenuState */
	@Override
	public final void renderHud(Game<D> game, Renderer r){
	}
	
}
