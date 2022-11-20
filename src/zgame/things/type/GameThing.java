package zgame.things.type;

import zgame.core.Game;
import zgame.core.file.Saveable;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;

/** An object which exists in the game */
public abstract class GameThing implements Comparable<GameThing>, Saveable, Destroyable{
	
	/** Create an empty {@link GameThing} */
	public GameThing(){
	}
	
	/** Override this method if this {@link GameThing} uses any resources that must be freed when it is no longer in use */
	@Override
	public void destroy(){
	}
	
	/**
	 * Draw this {@link GameThing} to the given {@link Renderer}
	 * 
	 * @param game The {@link Game} to draw this {@link GameThing} relative to
	 * @param r The {@link Renderer} to draw this {@link GameThing} on
	 */
	protected abstract void render(Game game, Renderer r);
	
	/**
	 * Determine if this {@link GameThing} should be rendered
	 * 
	 * @param game The {@link Game} which will be used to draw this {@link GameThing}
	 * @param r The {@link Renderer} which will be used to draw this {@link GameThing}
	 * @return Always true by default, can override to provide custom behavior. Generally should return false if this object will not appear on the screen
	 */
	public boolean shouldRender(Game game, Renderer r){
		return true;
	}
	
	/**
	 * Draw this {@link GameThing} to the given {@link Renderer}, only if {@link #shouldRender()} returns true
	 * 
	 * @param game The {@link Game} to draw this {@link GameThing} relative to
	 * @param r The {@link Renderer} to draw this {@link GameThing} on
	 * @return true if the rendering took place, false otherwise
	 */
	public final boolean renderWithCheck(Game game, Renderer r){
		if(!shouldRender(game, r)) return false;
		this.render(game, r);
		return true;
	}
	
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
