package zgame.core;

import zgame.core.graphics.Renderer;

/** An interface which defines methods used by objects which interact with a {@link Game} using input and rendering */
public interface GameIntractable extends GameTickable{
	
	/**
	 * Called when a keyboard key is pressed on the game
	 * Override to perform an action when a key is pressed
	 * 
	 * @param game The {@link Game} which called this method
	 * @param key The id of the key
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl);
	
	/**
	 * Called when a mouse button is pressed on the game
	 * Override to perform an action when a mouse button is pressed
	 * 
	 * @param game The {@link Game} which called this method
	 * @param button The ID of the mouse button
	 * @param press true if the key was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl);
	
	/**
	 * Called when the mouse is moved on the game
	 * Override to perform an action when the mouse is moved
	 * 
	 * @param game The {@link Game} which called this method
	 * @param x The x coordinate in screen coordinates
	 * @param y The y coordinate in screen coordinates
	 */
	public void mouseMove(Game game, double x, double y);
	
	/**
	 * Called when the mouse wheel is moved on the game
	 * Override to perform an action when the mouse wheel is moved
	 * 
	 * @param game The {@link Game} which called this method
	 * @param amount The amount the scroll wheel was moved
	 */
	public void mouseWheelMove(Game game, double amount);
	
	/**
	 * Called once each time a frame of the game is drawn, before the main render. Use this method to define what is drawn as a background, i.e. unaffected by the camera
	 * Do not manually call this method
	 * Override to define how the background of this object is drawn
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public void renderBackground(Game game, Renderer r);
	
	/**
	 * Called once each time a frame is of the game is drawn. Use this method to define what is drawn in the game each frame.
	 * Do not manually call this method.
	 * Override to define how this object is drawn
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public void render(Game game, Renderer r);
	
	/**
	 * Called once each time a frame of the game is drawn, after the main render. Use this method to define what is drawn on top of the screen, i.e. a hud, extra menu, etc
	 * Do not manually call this method
	 * Override to define how the hud of this object is drawn
	 * 
	 * @param game The {@link Game} which called this method
	 * @param r The Renderer to use for drawing
	 */
	public void renderHud(Game game, Renderer r);
}
