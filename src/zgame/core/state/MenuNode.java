package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.menu.Menu;

// This class is hilarious lmfao

/**
 * A helper object used by {@link MenuState} to keep track of if that state should perform things like updates, rendering, etc
 * 
 * @param <D> The type of data of the associated game
 */
public class MenuNode<D>{
	
	/** The {@link Menu} which this node uses */
	private Menu<D> menu;
	
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
	
	/** true if this menu should render it's background while not in focus, false otherwise */
	private boolean isRenderBackground;
	
	/** true if this menu should render it's foreground while not in focus, false otherwise */
	private boolean isRender;
	
	/** true if this menu should render it's hud while not in focus, false otherwise */
	private boolean isRenderHud;
	
	/**
	 * Create a node with the default settings. If this menu is not on top, it will only render, not tick or receive input
	 * @param menu See {@link #menu}
	 */
	public MenuNode(Menu<D> menu){
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
	public MenuNode(Menu<D> menu, boolean tick, boolean input, boolean render){
		this(menu, tick, input, input, input, input, render, render, render);
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
	 * @param isRenderBackground See {@link #isRenderBackground}
	 * @param isRender See {@link #isRender}
	 * @param isRenderHud See {@link #isRenderHud}
	 */
	public MenuNode(Menu<D> menu, boolean isTick, boolean isKeyAction, boolean isMouseAction, boolean isMouseMove, boolean isMouseWheelMove, boolean isRenderBackground, boolean isRender, boolean isRenderHud){
		this.menu = menu;
		this.isTick = isTick;
		this.isKeyAction = isKeyAction;
		this.isMouseAction = isMouseAction;
		this.isMouseMove = isMouseMove;
		this.isMouseWheelMove = isMouseWheelMove;
		this.isRenderBackground = isRenderBackground;
		this.isRender = isRender;
		this.isRenderHud = isRenderHud;
	}
	
	/**
	 * Called each time a game tick occurs. A tick is a game update, i.e. some amount of time passing.
	 * Does nothing if {@link #isTick()} returns false
	 * 
	 * @param game The {@link Game} which called this method
	 * @param dt The amount of time, in seconds, which passed in this tick
	 */
	public void tick(Game<D> game, double dt){
		if(this.isTick()) this.getMenu().tick(game, dt);
	}
	
	/**
	 * Called when a keyboard key is pressed on the game
	 * Does nothing if {@link #isKeyAction()} returns false
	 * 
	 * @param game The {@link Game} which called this method
	 * @param key The id of the key
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public void keyAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.isKeyAction()) this.getMenu().keyAction(game, button, press, shift, alt, ctrl);
	}
	
	/**
	 * Called when a mouse button is pressed on the game
	 * Does nothing if {@link #isMouseAction()} returns false
	 * 
	 * @param game The {@link Game} which called this method
	 * @param button The ID of the mouse button
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.isMouseAction()) this.getMenu().mouseAction(game, button, press, shift, alt, ctrl);
	}
	
	/**
	 * Called when the mouse is moved on the game
	 * Does nothing if {@link #isMouseMove()} returns false
	 * 
	 * @param game The {@link Game} which called this method
	 * @param x The x coordinate in screen coordinates
	 * @param y The y coordinate in screen coordinates
	 */
	public void mouseMove(Game<D> game, double x, double y){
		if(this.isMouseMove()) this.getMenu().mouseMove(game, x, y);
	}
	
	/**
	 * Called when the mouse wheel is moved on the game
	 * Does nothing if {@link #isMouseWheelMove()} returns false
	 * 
	 * @param game The {@link Game} which called this method
	 * @param amount The amount the scroll wheel was moved
	 */
	public void mouseWheelMove(Game<D> game, double amount){
		if(this.isMouseWheelMove()) this.getMenu().mouseWheelMove(game, amount);
	}
	
	/**
	 * Called once each time a frame of the game is drawn, before the main render. Use this method to define what is drawn as a background, i.e. unaffected by the camera
	 * Does nothing if {@link #isRenderBackground()} returns false
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public void renderBackground(Game<D> game, Renderer r){
		if(this.isRenderBackground()) this.getMenu().renderBackground(game, r);
	}
	
	/**
	 * Called once each time a frame is of the game is drawn. Use this method to define what is drawn in the game each frame.
	 * Does nothing if {@link #isRender()} returns false
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public void render(Game<D> game, Renderer r){
		if(this.isRender()) this.getMenu().render(game, r);
	}
	
	/**
	 * Called once each time a frame of the game is drawn, after the main render. Use this method to define what is drawn on top of the screen, i.e. a hud, extra menu, etc
	 * Does nothing if {@link #isRenderHud()} returns false
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public void renderHud(Game<D> game, Renderer r){
		if(this.isRenderHud()) this.getMenu().renderHud(game, r);
	}
	
	/** @return See {@link #menu} */
	public Menu<D> getMenu(){
		return this.menu;
	}
	
	/** @param menu See {@link #menu} */
	public void setMenu(Menu<D> menu){
		this.menu = menu;
	}
	
	/** @return See {@link #shouldTick} */
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
	
	/** @return See {@link #isRenderBackground} */
	public boolean isRenderBackground(){
		return this.isRenderBackground;
	}
	
	/** @param isRenderBackground See {@link #isRenderBackground} */
	public void setRenderBackground(boolean isRenderBackground){
		this.isRenderBackground = isRenderBackground;
	}
	
	/** @return See {@link #isRender} */
	public boolean isRender(){
		return this.isRender;
	}
	
	/** @param isRender See {@link #isRender} */
	public void setRender(boolean isRender){
		this.isRender = isRender;
	}
	
	/** @return See {@link #isRenderHud} */
	public boolean isRenderHud(){
		return this.isRenderHud;
	}
	
	/** @param isRenderHud See {@link #isRenderHud} */
	public void setRenderHud(boolean isRenderHud){
		this.isRenderHud = isRenderHud;
	}
	
}
