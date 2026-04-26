package zgame.things.type;

import zgame.core.Game;
import zgame.core.file.Saveable;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.things.Tag;

import java.util.Arrays;
import java.util.HashSet;

/**
 * An object which exists in the game
 * @param <State> The type of object holding state for this thing
 */
// TODO make sure all GameThing references use a type parameter
public abstract class GameThing<State> implements Comparable<GameThing<State>>, Saveable, Destroyable{
	
	/** Any arbitrary fields associated with this {@link GameThing} */
	private final HashSet<Tag> tags;
	
	// TODO Incrementally move all fields to make their getters and setters modify next and read from current
	/** The object holding all values representing the current state of this thing. This object should be treated as read only */
	private State current;
	/** The state which will become {@link #current} after a tick finishes running */
	private State next;
	
	/** Create an empty {@link GameThing} */
	public GameThing(){
		this.tags = new HashSet<>();
		
		this.next = this.initState();
		this.current = this.initState();
	}
	
	// TODO consider if this should be implemented in the GameTickable class
	// TODO implement this as abstract and force implementations of GameThing to handle this
	public State initState(){
		return null;
	}
	
	// TODO force this to be implemented per thing
	// TODO should copying be handled here? Or every field must be explicitly overwritten
	// TODO for now this will just have to be implemented per thing, need to find a real way to handle this
	public State copyState(State current, State next){
		return current;
	}
	
	/** Move the {@link #next} state on to the {@link #current} state */
	public void updateState(){
		var temp = this.current;
		this.current = this.next;
		this.next = this.copyState(temp, this.current);
		// TODO need to either have a tick fully overwrite all state on current, or make this method copy the values of current onto next at this point
	}
	
	/** @return See {@link #current} */
	public State getCurrent(){
		return this.current;
	}
	
	/** @return See {@link #next} */
	public State getNext(){
		return this.next;
	}
	
	/** Override this method if this {@link GameThing} uses any resources that must be freed when it is no longer in use */
	@Override
	public void destroy(){
	}
	
	/**
	 * Draw this {@link GameThing} to the given {@link Renderer}
	 *
	 * @param r The {@link Renderer} to draw this {@link GameThing} on
	 */
	protected abstract void render(Renderer r);
	
	/**
	 * Determine if this {@link GameThing} should be rendered
	 *
	 * @param r The {@link Renderer} which will be used to draw this {@link GameThing}
	 * @return Always true by default, can override to provide custom behavior. Generally should return false if this object will not appear on the screen
	 */
	public boolean shouldRender(Renderer r){
		return true;
	}
	
	/**
	 * Draw this {@link GameThing} to the given {@link Renderer}, only if {@link #shouldRender(Renderer)} returns true
	 *
	 * @param r The {@link Renderer} to draw this {@link GameThing} on
	 * @return true if the rendering took place, false otherwise
	 */
	public final boolean renderWithCheck(Renderer r){
		if(!shouldRender(r)) return false;
		this.render(r);
		return true;
	}
	
	/**
	 * Called any time this game thing is added to a room, does nothing by default, override to provide custom behavior
	 */
	public void onRoomAdd(){}
	
	/**
	 * Called any time this game thing is removed from a room, does nothing by default, override to provide custom behavior
	 */
	public void onRoomRemove(){}
	
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
	 * Remove this thing from the current game
	 */
	public void removeFrom(){
		Game.get().getCurrentRoom().removeThing(this);
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
