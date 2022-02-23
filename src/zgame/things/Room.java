package zgame.things;

import java.util.ArrayList;
import java.util.Collection;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;

/** An object which represents a location in a game, i.e. something that holds the player, NPCs, the tiles, etc. */
public class Room implements GameTickable{
	
	/** All of the {@link GameTickable} objects which exist in in the game */
	private Collection<GameTickable> tickableThings;
	/** All of the {@link GameThing} objects which exist in in the game */
	private Collection<GameThing> things;
	
	/** Create a new empty {@link Room} */
	public Room(){
		things = new ArrayList<GameThing>();
		tickableThings = new ArrayList<GameTickable>();
	}
	
	/**
	 * Add a {@link GameThing} to this {@link Room}
	 * 
	 * @param thing The {@link GameThing} to add
	 */
	public void addThing(GameThing thing){
		this.things.add(thing);
		if(thing instanceof GameTickable) this.tickableThings.add((GameTickable)thing);
	}
	
	/**
	 * Remove a {@link GameTickable} from this {@link Room}
	 * 
	 * @param thing The {@link GameTickable} to remove
	 */
	public void removeThing(GameThing thing){
		this.things.remove(thing);
		if(thing instanceof GameTickable) this.tickableThings.remove((GameTickable)thing);
	}
	
	@Override
	/**
	 * Update this {@link Room}
	 * 
	 * @param game The {@link Game} which this {@link Room} should update relative to
	 * @param dt The amount of time passed in this update
	 */
	public void tick(Game game, double dt){
		for(GameTickable t : this.tickableThings) t.tick(game, dt);
	}
	
	/**
	 * Draw this {@link Room} to the given {@link Renderer}
	 * 
	 * @param game The {@link Game} to draw this {@link Room} relative to
	 * @param r The {@link Renderer} to draw this {@link Room} on
	 */
	public void render(Game game, Renderer r){
		for(GameThing t : this.things) t.render(game, r);
	}
}
