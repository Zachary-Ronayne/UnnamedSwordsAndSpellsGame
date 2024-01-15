package zgame.core.state;

import java.util.ArrayList;

import zgame.core.Game;
import zgame.core.GameInteractable;
import zgame.core.file.Saveable;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.menu.Menu;

/**
 * A class which handles a single state that a game can be in. A state could be things like playing the game, or being in the main menu.
 * <p>
 * A state is essentially a separate place where you can easily define what happens for input and rendering
 */
public abstract class GameState implements GameInteractable, Saveable, Destroyable{
	
	/** The {@link MenuNode}s containing {@link Menu}s which this {@link GameState} uses. The top of the stack ticks and takes input by default, the rest only render */
	private ArrayList<MenuNode> menuStack;
	
	/** true if this state should use the camera for drawing the main graphics, false otherwise */
	private boolean useCamera;
	
	/** The minimum number of menus to exist in {@link #menuStack}. 0 by default, use 1 to make sure there's always at least one menu */
	private int minMenuStack;
	
	/** Create a new {@link GameState} which uses the camera for rendering */
	public GameState(){
		this(true);
	}
	
	/**
	 * Create a new {@link GameState} with the given parameters
	 *
	 * @param useCamera See {@link #useCamera}
	 */
	public GameState(boolean useCamera){
		this.setUseCamera(useCamera);
		this.menuStack = new ArrayList<>();
		this.minMenuStack = 0;
	}
	
	@Override
	public void destroy(){
		if(menuStack == null) return;
		for(int i = 0; i < this.menuStack.size(); i++) this.menuStack.get(i).getMenu().destroy();
	}
	
	/** @return The root menu of this {@link MenuState}, i.e. the menu on the bottom before popups, or null if there are no menus */
	public Menu getMenu(){
		if(this.menuStack == null) return null;
		return this.hasMenu() ? this.menuStack.get(0).getMenu() : null;
	}
	
	/** @return The menu on top of all other menus, or null if there are no menus */
	public MenuNode getTopMenuNode(){
		if(this.menuStack == null) return null;
		return this.hasMenu() ? this.menuStack.get(this.menuStack.size() - 1) : null;
	}
	
	/** @return The menu on top of all other menus, or null if there are no menus */
	public Menu getTopMenu(){
		var node = this.getTopMenuNode();
		if(node == null) return null;
		return node.getMenu();
	}
	
	/**
	 * @param game The game using this game state
	 * @param menu The new root menu of this {@link GameState}, i.e. the menu on the bottom before popups
	 */
	public void setMenu(Game game, Menu menu){
		if(this.menuStack == null || this.hasMenu()) this.menuStack = new ArrayList<>();
		var node = new MenuNode(menu);
		this.menuStack.add(0, node);
		this.onMenuChange(game, true);
	}
	
	/** @return The number of menus currently displayed on this {@link GameState} */
	public int getStackSize(){
		return this.menuStack == null ? 0 : this.menuStack.size();
	}
	
	/**
	 * Add the given {@link Menu} on top of the existing menus on this state
	 *
	 * @param game The game where this call happened
	 * @param menu The menu to add
	 */
	public void popupMenu(Game game, Menu menu){
		var node = menu.getNode();
		if(node == null) node = new MenuNode(menu);
		else node = node.copySettings(menu);
		this.popupMenu(game, node);
	}
	
	/**
	 * Put given {@link Menu} on top of the existing menus on this state
	 *
	 * @param game The game where this call happened
	 * @param node The node to add
	 */
	public void popupMenu(Game game, MenuNode node){
		var menu = node.getMenu();
		var foundIndex = this.findIndex(menu);
		
		// If the new menu is already in this state, swap it with the one on top
		if(foundIndex != -1){
			var end = this.menuStack.size() - 1;
			var old = this.menuStack.get(foundIndex);
			this.menuStack.set(foundIndex, this.menuStack.get(end));
			this.menuStack.set(end, old);
		}
		// Otherwise, just add the menu to the top
		else this.menuStack.add(node);
		menu.onRemove(game);
		this.onMenuChange(game, true);
	}
	
	/**
	 * Determine if this state is displaying the given menu
	 *
	 * @param menu The menu to check for
	 * @return true if it is displaying the menu, false otherwise
	 */
	public boolean showingMenu(Menu menu){
		for(var n : this.menuStack){
			if(n.getMenu() == menu) return true;
		}
		return false;
	}
	
	/**
	 * Find the index of the given menu in the menu stack
	 *
	 * @param menu The menu to look for
	 * @return The index in the stack, or -1 if it is not in the stack
	 */
	public int findIndex(Menu menu){
		for(int i = 0; i < this.menuStack.size(); i++){
			var m = this.menuStack.get(i).getMenu();
			if(m == menu) return i;
		}
		return -1;
	}
	
	/**
	 * Remove the given menu from the menu stack
	 *
	 * @param game The game where this call happened
	 * @param menu The menu to remove. This should be the actual object reference to remove, not based on any kind of identifier
	 * @return The removed menu, or null if the menu is not a part of the stack. The returned menu will be destroyed if {@link Menu#isDefaultDestroyRemove()} returns true
	 */
	public Menu removeMenu(Game game, Menu menu){
		return removeMenu(game, menu, false);
	}
	
	/**
	 * Remove the given menu from the menu stack
	 *
	 * @param game The game where this call happened
	 * @param menu The menu to remove. This should be the actual object reference to remove, not based on any kind of identifier
	 * @param preventDestroy true if {@link Menu#isDefaultDestroyRemove()} will be ignored, and the menu will not be destroyed, false otherwise
	 * @return The removed menu, or null if the menu is not a part of the stack. The returned menu will be destroyed if {@link Menu#isDefaultDestroyRemove()} returns true
	 */
	public Menu removeMenu(Game game, Menu menu, boolean preventDestroy){
		var i = findIndex(menu);
		if(i == -1) return null;
		var n = this.menuStack.remove(i);
		if(n == null) return null;
		var m = n.getMenu();
		m.onRemove(game);
		if(m.isDefaultDestroyRemove() && !preventDestroy) m.destroy();
		this.onMenuChange(game, false);
		return m;
	}
	
	/**
	 * Remove and potentially destroy the menu on the top of this menu state.
	 *
	 * @param game The game where this call happened
	 * @return The removed menu, or null if only the base menu exists
	 */
	public Menu removeTopMenu(Game game){
		if(this.getStackSize() <= getMinMenuStack()) return null;
		Menu removed = this.menuStack.remove(this.menuStack.size() - 1).getMenu();
		removed.onRemove(game);
		if(removed.isDefaultDestroyRemove()) removed.destroy();
		this.onMenuChange(game, false);
		return removed;
	}
	
	/**
	 * Remove the menu on the top of this menu state.
	 *
	 * @param game The game where this call happened
	 * @param destroy true to destroy the menu after it's removed, false otherwise If destroy is false, then does not destroy the removed menu or any of its allocated
	 * 		resources. It is the responsibility of the caller of this method to destroy the returned menu if the menu is not destroyed by default
	 * @return The removed menu, or null if only the base menu exists
	 */
	public Menu removeTopMenu(Game game, boolean destroy){
		var removed = this.removeTopMenu(game);
		if(removed == null) return null;
		// If we are destroying the menu by default, don't try to destroy it again
		if(removed.isDefaultDestroyRemove()) return removed;
		// Otherwise, destroy it
		if(destroy) removed.destroy();
		return removed;
	}
	
	/** @return true if any menus are open, false otherwise */
	public boolean hasMenu(){
		return !this.menuStack.isEmpty();
	}
	
	/**
	 * Called when a menu is added or removed from this state. Does nothing by default, override to provide custom behavior
	 *
	 * @param game The game where the change happened
	 * @param added true if a menu was added, false if it was removed
	 */
	public void onMenuChange(Game game, boolean added){}
	
	/** @return See {@link #minMenuStack} */
	public int getMinMenuStack(){
		return this.minMenuStack;
	}
	
	/** @param minMenuStack See {@link #minMenuStack} */
	public void setMinMenuStack(int minMenuStack){
		this.minMenuStack = minMenuStack;
	}
	
	/** @return See {@link #useCamera} */
	public boolean isUseCamera(){
		return this.useCamera;
	}
	
	/** @param useCamera See {@link #useCamera} */
	public void setUseCamera(boolean useCamera){
		this.useCamera = useCamera;
	}
	
	/**
	 * A method called when a {@link Game} sets its current state to this {@link GameState}. Override this method to do something when it happens. Does nothing by default
	 *
	 * @param game The {@link Game} which set its current state
	 */
	public void onSet(Game game){
	}
	
	@Override
	public void tick(Game game, double dt){
		Menu menu = this.getTopMenu();
		if(menu != null && menu.isPropagateTick()){
			for(int i = 0; i < this.menuStack.size() - 1; i++){
				MenuNode m = this.menuStack.get(i);
				m.tick(game, dt);
			}
		}
		if(menu != null) menu.tick(game, dt);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		Menu menu = this.getTopMenu();
		if(menu != null) menu.keyAction(game, button, press, shift, alt, ctrl);
		if(menu != null && !menu.isPropagateKeyAction()) return;
		for(int i = this.getStackSize() - 2; i >= 0; i--){
			MenuNode m = this.menuStack.get(i);
			m.keyAction(game, button, press, shift, alt, ctrl);
		}
	}
	
	@Override
	public boolean mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		Menu menu = this.getTopMenu();
		if(menu != null && menu.mouseAction(game, button, press, shift, alt, ctrl)) return true;
		if(menu != null && !menu.isPropagateMouseAction()) return false;
		for(int i = this.getStackSize() - 2; i >= 0; i--){
			MenuNode m = this.menuStack.get(i);
			if(m.mouseAction(game, button, press, shift, alt, ctrl)) return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseMove(Game game, double x, double y){
		Menu menu = this.getTopMenu();
		if(menu != null){
			// Check for the mouse entering or leaving the menu and all other menus
			var onChild = menu.updateMouseOn(game, x, y, false);
			
			if(menu.isPropagateMouseMove()){
				for(int i = this.getStackSize() - 2; i >= 0; i--){
					MenuNode m = this.menuStack.get(i);
					onChild = m.getMenu().updateMouseOn(game, x, y, onChild);
				}
			}
			
			// Account for mouse movement on the top menu
			if(menu.mouseMove(game, x, y)) return true;
		}
		// Account for mouse movement on every other menu
		if(menu != null && menu.isPropagateMouseMove()){
			for(int i = this.getStackSize() - 2; i >= 0; i--){
				MenuNode m = this.menuStack.get(i);
				if(m.mouseMove(game, x, y)) return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean mouseWheelMove(Game game, double amount){
		Menu menu = this.getTopMenu();
		if(menu != null && menu.mouseWheelMove(game, amount)) return true;
		if(menu != null && !menu.isPropagateMouseWheelMove()) return false;
		for(int i = this.getStackSize() - 2; i >= 0; i--){
			MenuNode m = this.menuStack.get(i);
			if(m.mouseWheelMove(game, amount)) return true;
		}
		return false;
	}
	
	@Override
	public void renderBackground(Game game, Renderer r){
	}
	
	@Override
	public void render(Game game, Renderer r){
	}
	
	@Override
	public void renderHud(Game game, Renderer r){
		Menu m = this.getTopMenu();
		if(m != null && m.isPropagateRender()){
			for(int i = 0; i < this.getStackSize() - 1; i++) this.menuStack.get(i).render(game, r);
		}
		if(m != null) m.renderHud(game, r);
	}
	
	/**
	 * @return This object, as a {@link PlayState}, or null if it cannot be a {@link PlayState} The return value of this method should equal this object, not another version
	 * 		or reference, i.e. (this == this.asPlay()) should evaluate to true
	 */
	public PlayState asPlay(){
		return null;
	}
}
