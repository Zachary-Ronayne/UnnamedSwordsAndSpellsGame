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
 * 
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
		this.menuStack = new ArrayList<MenuNode>();
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
		return this.menuStack.isEmpty() ? null : this.menuStack.get(0).getMenu();
	}
	
	/** @return The menu on top of all other menus, or null if there are no menus */
	public Menu getTopMenu(){
		if(this.menuStack == null) return null;
		return this.menuStack.isEmpty() ? null : this.menuStack.get(this.menuStack.size() - 1).getMenu();
	}
	
	/** @param menu The new root menu of this {@link MenuState}, i.e. the menu on the bottom before popups */
	public void setMenu(Menu menu){
		if(this.menuStack == null || !this.menuStack.isEmpty()) this.menuStack = new ArrayList<MenuNode>();
		this.menuStack.add(0, new MenuNode(menu));
	}
	
	/** @return The number of menus currently displayed on this {@link GameState} */
	public int getStackSize(){
		return this.menuStack == null ? 0 : this.menuStack.size();
	}
	
	/**
	 * Add the given {@link Menu} on top of the existing menus on this state
	 * 
	 * @param menu The menu to add
	 */
	public void popupMenu(Menu menu){
		this.popupMenu(new MenuNode(menu));
	}
	
	/**
	 * Add the given {@link Menu} on top of the existing menus on this state
	 * 
	 * @param menu The node to add
	 */
	public void popupMenu(MenuNode menu){
		this.menuStack.add(menu);
	}
	
	/**
	 * Remove and destroy the menu on the top of this menu state.
	 * 
	 * @return The removed menu, or null if only the base menu exists
	 */
	public Menu removeTopMenu(){
		return this.removeTopMenu(true);
	}
	
	/**
	 * Remove the menu on the top of this menu state.
	 * 
	 * @param destroy true to destroy the menu after it's removed, false otherwise
	 *        If destroy is false, then does not destroy the removed menu or any of its allocated resources.
	 *        It is the responsibility of the caller of this method to destroy the returned menu
	 * @return The removed menu, or null if only the base menu exists
	 */
	public Menu removeTopMenu(boolean destroy){
		if(this.getStackSize() <= getMinMenuStack()) return null;
		Menu removed = this.menuStack.remove(this.menuStack.size() - 1).getMenu();
		if(destroy) removed.destroy();
		return removed;
	}
	
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
	 * A method called when a {@link Game} sets its current state to this {@link GameState}.
	 * Override this method to do something when it happens. Does nothing by default
	 * 
	 * @param game The {@link Game} which set its current state
	 */
	public void onSet(Game game){
	}
	
	@Override
	public void tick(Game game, double dt){
		for(int i = 0; i < this.menuStack.size() - 1; i++){
			MenuNode m = this.menuStack.get(i);
			m.tick(game, dt);
		}
		Menu m = this.getTopMenu();
		if(m != null) m.tick(game, dt);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(int i = 0; i < this.getStackSize() - 1; i++){
			MenuNode m = this.menuStack.get(i);
			m.keyAction(game, button, press, shift, alt, ctrl);
		}
		Menu m = this.getTopMenu();
		if(m != null) m.keyAction(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(int i = 0; i < this.getStackSize() - 1; i++){
			MenuNode m = this.menuStack.get(i);
			m.mouseAction(game, button, press, shift, alt, ctrl);
		}
		Menu m = this.getTopMenu();
		if(m != null) m.mouseAction(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public void mouseMove(Game game, double x, double y){
		for(int i = 0; i < this.getStackSize() - 1; i++){
			MenuNode m = this.menuStack.get(i);
			m.mouseMove(game, x, y);
		}
		Menu m = this.getTopMenu();
		if(m != null) m.mouseMove(game, x, y);
	}
	
	@Override
	public void mouseWheelMove(Game game, double amount){
		for(int i = 0; i < this.getStackSize() - 1; i++){
			MenuNode m = this.menuStack.get(i);
			m.mouseWheelMove(game, amount);
		}
		Menu m = this.getTopMenu();
		if(m != null) m.mouseWheelMove(game, amount);
	}
	
	@Override
	public void renderBackground(Game game, Renderer r){
	}
	
	@Override
	public void render(Game game, Renderer r){
	}
	
	@Override
	public void renderHud(Game game, Renderer r){
		for(int i = 0; i < this.getStackSize() - 1; i++) this.menuStack.get(i).render(game, r);
		Menu m = this.getTopMenu();
		if(m != null) m.renderHud(game, r);
	}

	/**
	 * @return This object, as a {@link PlayState}, or null if it cannot be a {@link PlayState}
	 *         The return value of this method should equal this object, not another version or reference, i.e. (this == this.asPlay()) should evaluate to true
	 */
	public PlayState asPlay(){
		return null;
	}
	
}
