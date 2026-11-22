package zgame.core.type;

import zgame.core.graphics.Renderer;

/** An object representing what kind of game this is, usually 2D or 3D */
public interface RenderStyle{
	
	/** A common instance for use in basic 2D graphics */
	Style2D S_2D = new Style2D();
	/** A common instance for use in basic 3D graphics */
	Style3D S_3D = new Style3D();
	
	/**
	 * Called when a frame needs to be rendered for this style of rendering
	 *
	 * @param r The renderer to update
	 */
	void setupFrame(Renderer r);
	
	/**
	 * Called when the render style is initially loaded in, doing any expensive operations
	 *
	 * @param r The renderer to update
	 */
	void setupCore(Renderer r);
	
	/**
	 * Called when the play state of a game is active, and a menu is opened with no menus previously opened
	 */
	default void onMenuOpened(){}
	
	/**
	 * Called when the play state of a game is active, a menu was open, and all menus have been closed
	 */
	default void onAllMenusClosed(){}
	
	/**
	 * Called when the mouse is moved on the game
	 *
	 * @param x The x coordinate in screen coordinates
	 * @param y The y coordinate in screen coordinates
	 */
	default void mouseMove(double x, double y){}
	
}
