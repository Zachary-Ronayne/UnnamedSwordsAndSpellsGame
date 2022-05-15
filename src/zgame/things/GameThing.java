package zgame.things;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** An object which exists in the game */
public abstract class GameThing implements Comparable<GameThing>{
	
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
	
	/**
	 * @return The number which determines how soon this object should render.
	 *         Lower numbers are rendered first, higher numbers are rendered last
	 *         Override to make a custom value
	 *         Defaults to 0.
	 */
	public int getRenderPriority(){
		return 0;
	}

	@Override
	public int compareTo(GameThing gt){
		int r = this.getRenderPriority();
		int t = gt.getRenderPriority();
		if(r < t) return -1;
		if(r > t) return 1;
		else return 0;
	}
	
}
