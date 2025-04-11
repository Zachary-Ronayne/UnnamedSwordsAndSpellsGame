package zgame.things.type;

import zgame.core.Game;
import zgame.core.file.Saveable;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.things.Tag;

import java.util.Arrays;
import java.util.HashSet;

/** An object which exists in the game */
public abstract class GameThing implements Comparable<GameThing>, Saveable, Destroyable{
	
	/** Any arbitrary fields associated with this {@link GameThing} */
	private final HashSet<Tag> tags;
	
	/** Create an empty {@link GameThing} */
	public GameThing(){
		this.tags = new HashSet<>();
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
	 * Draw this {@link GameThing} to the given {@link Renderer}, only if {@link #shouldRender(Game, Renderer)} returns true
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
	 * Called any time this game thing is removed from a room, does nothing by default, override to provide custom behavior
	 * @param game The game where the removal took place, the room will be the current room of the game's play state
	 */
	public void onRoomRemove(Game game){}
	
	/**
	 * Determines the order that game things are stored in lists rooms. Primarily used in 2D when determining which thing should be rendered first.
	 * Can also adjust how the elements are sorted by overriding {@link #compareTo(GameThing)}
	 *
	 * @return The number which determines how this thing is sorted relative to other game things.
	 * 		Lower numbers are first, higher numbers are last
	 * 		Override to make a custom value
	 * 		Defaults to 0.
	 */
	public int getSortPriority(){
		return 0;
	}
	
	@Override
	public int compareTo(GameThing gt){
		return Integer.compare(this.getSortPriority(), gt.getSortPriority());
	}
	
	/**
	 * Remove this thing from the given game
	 *
	 * @param game The game to remove it from
	 */
	public void removeFrom(Game game){
		game.getCurrentRoom().removeThing(this);
	}
	
	/**
	 * Determine if this object has the given tag
	 *
	 * @param tag The tag to check for
	 * @return true if it has the tag, false otherwise
	 */
	public boolean hasTag(Tag tag){
		return this.tags.contains(tag);
	}
	
	/**
	 * Give a list of tags to this {@link GameThing}
	 *
	 * @param tags The tags
	 */
	public void addTags(Tag... tags){
		this.tags.addAll(Arrays.asList(tags));
	}
	
	/**
	 * Remove tags from this {@link GameThing}
	 *
	 * @param tags The tags
	 */
	public void removeTags(Tag... tags){
		for(var tag : tags) this.tags.remove(tag);
	}
	
}
