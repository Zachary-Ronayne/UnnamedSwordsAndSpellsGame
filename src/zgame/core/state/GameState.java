package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/**
 * A class which handles a single state that a game can be in. A state could be things like playing the game, or being in the main menu.
 * 
 * A state is essentially a separate place where you can easily define what happens for input and rendering
 */
public abstract class GameState{
	
	/** true if this state should use the camera for drawing the main graphics, false otherwise */
	private boolean useCamera;
	
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
	 * Called each time a game tick occurs. A tick is a game update, i.e. some amount of time passing
	 * 
	 * @param game The {@link Game} which called this method
	 * @param dt The amount of time, in seconds, which passed in this tick
	 */
	public abstract void tick(Game game, double dt);
	
	/**
	 * Called when a keyboard key is pressed on the game
	 * 
	 * @param game The {@link Game} which called this method
	 * @param key The id of the key
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public abstract void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl);
	
	/**
	 * Called when a mouse button is pressed on the game
	 * 
	 * @param game The {@link Game} which called this method
	 * @param button The ID of the mouse button
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public abstract void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl);
	
	/**
	 * Called when the mouse is moved on the game
	 * 
	 * @param game The {@link Game} which called this method
	 * @param x The x coordinate in screen coordinates
	 * @param y The y coordinate in screen coordinates
	 */
	public abstract void mouseMove(Game game, double x, double y);
	
	/**
	 * Called when the mouse wheel is moved on the game
	 * 
	 * @param game The {@link Game} which called this method
	 * @param amount The amount the scroll wheel was moved
	 */
	public abstract void mouseWheelMove(Game game, double amount);
	
	/**
	 * Called once each time a frame of the game is drawn, before the main render. Use this method to define what is drawn as a background, i.e. unaffected by the camera
	 * Do not manually call this method
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public abstract void renderBackground(Game game, Renderer r);
	
	/**
	 * Called once each time a frame is of the game is drawn. Use this method to define what is drawn in the game each frame.
	 * Do not manually call this method.
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public abstract void render(Game game, Renderer r);
	
	/**
	 * Called once each time a frame of the game is drawn, after the main render. Use this method to define what is drawn on top of the screen, i.e. a hud, extra menu, etc
	 * Do not manually call this method
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public abstract void renderHud(Game game, Renderer r);
	
}
