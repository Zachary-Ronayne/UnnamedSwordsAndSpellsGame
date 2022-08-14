package zgame.core.state;

import java.util.ArrayList;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.menu.Menu;

/**
 * A generic class which handles a {@link GameState} designed around interacting with a menu
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public abstract class MenuState<D>extends GameState<D>{
	
	/** The {@link MenuNode}s containing {@link Menu}s which this {@link MenuState} uses. The top of the stack ticks and takes input by default, the rest only render */
	private ArrayList<MenuNode<D>> menuStack;
	
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
		this.menuStack = new ArrayList<MenuNode<D>>();
		this.menuStack.add(menu);
	}
	
	/** @return The root menu of this {@link MenuState}, i.e. the menu on the bottom before popups */
	public Menu<D> getMenu(){
		return this.menuStack.get(0).getMenu();
	}

	/** @return The menu on top of all other menus */
	public Menu<D> getTopMenu(){
		return this.menuStack.get(this.menuStack.size() - 1).getMenu();
	}
	
	/** @param menu The new root menu of this {@link MenuState}, i.e. the menu on the bottom before popups */
	public void setMenu(Menu<D> menu){
		this.menuStack.remove(0);
		this.menuStack.add(0, new MenuNode<>(menu));
	}
	
	/**
	 * Add the given {@link Menu} on top of the existing menus on this state
	 * @param menu The menu to add
	 */
	public void popupMenu(Menu<D> menu){
		this.popupMenu(new MenuNode<D>(menu));
	}

	/**
	 * Add the given {@link Menu} on top of the existing menus on this state
	 * @param menu The node to add
	 */
	public void popupMenu(MenuNode<D> menu){
		this.menuStack.add(menu);
	}
	
	/**
	 * Remove the menu on the top of this menu state
	 * 
	 * @return The removed menu, or null if only the base menu exists
	 */
	public Menu<D> removeTopMenu(){
		if(this.menuStack.size() == 1) return null;
		return this.menuStack.remove(this.menuStack.size() - 1).getMenu();
	}
	
	@Override
	public void tick(Game<D> game, double dt){
		for(int i = 0; i < this.menuStack.size() - 1; i++){
			MenuNode<D> m = this.menuStack.get(i);
			m.tick(game, dt);
		}
		this.getTopMenu().tick(game, dt);
	}
	
	@Override
	public void keyAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(int i = 0; i < this.menuStack.size() - 1; i++){
			MenuNode<D> m = this.menuStack.get(i);
			m.keyAction(game, button, press, shift, alt, ctrl);
		}
		this.getTopMenu().keyAction(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(int i = 0; i < this.menuStack.size() - 1; i++){
			MenuNode<D> m = this.menuStack.get(i);
			m.mouseAction(game, button, press, shift, alt, ctrl);
		}
		this.getTopMenu().mouseAction(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public void mouseMove(Game<D> game, double x, double y){
		for(int i = 0; i < this.menuStack.size() - 1; i++){
			MenuNode<D> m = this.menuStack.get(i);
			m.mouseMove(game, x, y);
		}
		this.getTopMenu().mouseMove(game, x, y);
	}
	
	@Override
	public void mouseWheelMove(Game<D> game, double amount){
		for(int i = 0; i < this.menuStack.size() - 1; i++){
			MenuNode<D> m = this.menuStack.get(i);
			m.mouseWheelMove(game, amount);
		}
		this.getTopMenu().mouseWheelMove(game, amount);
	}
	
	/** Does nothing for MenuState */
	@Override
	public final void renderBackground(Game<D> game, Renderer r){
	}
	
	@Override
	public void render(Game<D> game, Renderer r){
		Menu<D> menu = this.getTopMenu();
		menu.renderBackground(game, r);
		menu.render(game, r);
		menu.renderHud(game, r);
		for(int i = 0; i < this.menuStack.size() - 1; i++){
			MenuNode<D> m = this.menuStack.get(i);
			m.renderBackground(game, r);
			m.render(game, r);
			m.renderHud(game, r);
		}
	}
	
	/** Does nothing for MenuState */
	@Override
	public final void renderHud(Game<D> game, Renderer r){
	}
	
}
