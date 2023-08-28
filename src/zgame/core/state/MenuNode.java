package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.menu.Menu;

// This class is hilarious lmfao

/**
 * A helper object used by {@link MenuState} to keep track of if that state should perform things like updates, rendering, etc
 */
public class MenuNode{
	
	/** The {@link Menu} which this node uses */
	private Menu menu;
	
	/** true if this menu should receive tick updates while not in focus, false otherwise */
	private boolean isTick;
	
	/** true if this menu should receive key action updates while not in focus, false otherwise */
	private boolean isKeyAction;
	
	/** true if this menu should receive mouse action updates while not in focus, false otherwise */
	private boolean isMouseAction;
	
	/** true if this menu should receive mouse movement updates while not in focus, false otherwise */
	private boolean isMouseMove;
	
	/** true if this menu should receive mouse wheel movement updates while not in focus, false otherwise */
	private boolean isMouseWheelMove;
	
	/** true if this menu should render while not in focus, false otherwise */
	private boolean isRender;
	
	/**
	 * Create a node with the default settings. If this menu is not on top, it will only render, not tick or receive input
	 *
	 * @param menu See {@link #menu}
	 */
	public MenuNode(Menu menu){
		this(menu, false, false, true);
	}
	
	/**
	 * Create a node with the given settings
	 *
	 * @param menu See {@link #menu}
	 * @param tick See {@link #isTick}
	 * @param input true if this menu should receive input when it is not on top
	 * @param render true if this menu should render when it is not on top
	 */
	public MenuNode(Menu menu, boolean tick, boolean input, boolean render){
		this(menu, tick, input, input, input, input, render);
	}
	
	/**
	 * Create a new node with all fields
	 *
	 * @param menu See {@link #menu}
	 * @param isTick See {@link #isTick}
	 * @param isKeyAction See {@link #isKeyAction}
	 * @param isMouseAction See {@link #isMouseAction}
	 * @param isMouseMove See {@link #isMouseMove}
	 * @param isMouseWheelMove See {@link #isMouseWheelMove}
	 * @param isRender See {@link #isRender}
	 */
	public MenuNode(Menu menu, boolean isTick, boolean isKeyAction, boolean isMouseAction, boolean isMouseMove, boolean isMouseWheelMove, boolean isRender){
		this.menu = menu;
		this.menu.setNode(this);
		this.isTick = isTick;
		this.isKeyAction = isKeyAction;
		this.isMouseAction = isMouseAction;
		this.isMouseMove = isMouseMove;
		this.isMouseWheelMove = isMouseWheelMove;
		this.isRender = isRender;
	}
	
	/**
	 * @param menu See {@link #menu}
	 * @return A {@link MenuNode} with all input, rendering, and ticking enabled
	 */
	public static MenuNode withAll(Menu menu){
		return new MenuNode(menu, true, true, true, true, true, true);
	}
	
	/**
	 * Called each time a game tick occurs. A tick is a game update, i.e. some amount of time passing. Does nothing if {@link #isTick()} returns false
	 *
	 * @param game The {@link Game} which called this method
	 * @param dt The amount of time, in seconds, which passed in this tick
	 */
	public void tick(Game game, double dt){
		if(this.isTick()) this.getMenu().tick(game, dt);
	}
	
	/**
	 * Called when a keyboard key is pressed on the game Does nothing if {@link #isKeyAction()} returns false
	 *
	 * @param game The {@link Game} which called this method
	 * @param button The id of the key
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.isKeyAction()) this.getMenu().keyAction(game, button, press, shift, alt, ctrl);
	}
	
	/**
	 * Called when a mouse button is pressed on the game Does nothing if {@link #isMouseAction()} returns false
	 *
	 * @param game The {@link Game} which called this method
	 * @param button The ID of the mouse button
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 * @return true if sub objects of this object should be blocked from further input, false otherwise
	 */
	public boolean mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.isMouseAction()) return this.getMenu().mouseAction(game, button, press, shift, alt, ctrl);
		return false;
	}
	
	/**
	 * Called when the mouse is moved on the game Does nothing if {@link #isMouseMove()} returns false
	 *
	 * @param game The {@link Game} which called this method
	 * @param x The x coordinate in screen coordinates
	 * @param y The y coordinate in screen coordinates
	 * @return true if sub objects of this object should be blocked from further input, false otherwise
	 */
	public boolean mouseMove(Game game, double x, double y){
		if(this.isMouseMove()) return this.getMenu().mouseMove(game, x, y);
		return false;
	}
	
	/**
	 * Called when the mouse wheel is moved on the game Does nothing if {@link #isMouseWheelMove()} returns false
	 *
	 * @param game The {@link Game} which called this method
	 * @param amount The amount the scroll wheel was moved
	 * @return true if sub objects of this object should be blocked from further input, false otherwise
	 */
	public boolean mouseWheelMove(Game game, double amount){
		if(this.isMouseWheelMove()) return this.getMenu().mouseWheelMove(game, amount);
		return false;
	}
	
	/**
	 * Called once each time a frame is of the game is drawn. Use this method to define what is drawn in the game each frame. Does nothing if {@link #isRender()} returns
	 * false
	 *
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public void render(Game game, Renderer r){
		// Rendering hud because menus should always appear on top
		if(this.isRender()) this.getMenu().renderHud(game, r);
	}
	
	/** @return See {@link #menu} */
	public Menu getMenu(){
		return this.menu;
	}
	
	/** @param menu See {@link #menu} */
	public void setMenu(Menu menu){
		if(this.menu != null) this.menu.setNode(null);
		this.menu = menu;
		this.menu.setNode(this);
	}
	
	/** @return See {@link #isTick} */
	public boolean isTick(){
		return this.isTick;
	}
	
	/** @param isTick See {@link #isTick} */
	public void setTick(boolean isTick){
		this.isTick = isTick;
	}
	
	/** @return See {@link #isKeyAction} */
	public boolean isKeyAction(){
		return this.isKeyAction;
	}
	
	/** @param isKeyAction See {@link #isKeyAction} */
	public void setKeyAction(boolean isKeyAction){
		this.isKeyAction = isKeyAction;
	}
	
	/** @return See {@link #isMouseAction} */
	public boolean isMouseAction(){
		return this.isMouseAction;
	}
	
	/** @param isMouseAction See {@link #isMouseAction} */
	public void setMouseAction(boolean isMouseAction){
		this.isMouseAction = isMouseAction;
	}
	
	/** @return See {@link #isMouseMove} */
	public boolean isMouseMove(){
		return this.isMouseMove;
	}
	
	/** @param isMouseMove See {@link #isMouseMove} */
	public void setMouseMove(boolean isMouseMove){
		this.isMouseMove = isMouseMove;
	}
	
	/** @return See {@link #isMouseWheelMove} */
	public boolean isMouseWheelMove(){
		return this.isMouseWheelMove;
	}
	
	/** @param isMouseWheelMove See {@link #isMouseWheelMove} */
	public void setMouseWheelMove(boolean isMouseWheelMove){
		this.isMouseWheelMove = isMouseWheelMove;
	}
	
	/** @return See {@link #isRender} */
	public boolean isRender(){
		return this.isRender;
	}
	
	/** @param isRender See {@link #isRender} */
	public void setRender(boolean isRender){
		this.isRender = isRender;
	}
	
	/** @param b The value for all of: {@link #isKeyAction}, {@link #isMouseAction}, {@link #isMouseMove}, {@link #isMouseWheelMove}, {@link #isRender} */
	public void setAll(boolean b){
		this.setKeyAction(b);
		this.setMouseAction(b);
		this.setMouseMove(b);
		this.setMouseWheelMove(b);
		this.setRender(b);
	}
	
}
