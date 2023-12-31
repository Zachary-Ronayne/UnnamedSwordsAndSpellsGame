package zgame.core.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** An object representing what kind of game this is, usually 2D or 3D */
public interface GameType{
	
	/**
	 * Called when a frame of the foreground of a game needs to be drawn and the type of game needs to update the renderer
	 * @param game The game being updated
	 * @param r The renderer to update
	 */
	void setupRender(Game game, Renderer r);
	
	/**
	 * Called when the type of game is set
	 * @param game The game to update
	 */
	void onTypeSet(Game game);
	
}
