package zgame.things;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** An object which exists in the game */
public abstract class GameThing{
	
	/** Create an empty {@link GameThing} */
	public GameThing(){
	}

	/**
	 * Draw this {@link GameThing} to the given {@link Renderer}
	 * 
	 * @param game The {@link Game} to draw this {@link GameThing} relative to
	 * @param r The {@link Renderer} to draw this {@link GameThing} on
	 */
	public abstract void render(Game game, Renderer r);
	
}
