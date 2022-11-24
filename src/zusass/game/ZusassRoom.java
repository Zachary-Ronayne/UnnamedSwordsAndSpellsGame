package zusass.game;

import java.util.ArrayList;

import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.game.things.entities.mobs.StatThing;

/** A {@link zgame.world.Room} used by the Zusass game */
public class ZusassRoom extends Room{

	/** All the things in this room which use stats */
	private ArrayList<StatThing> statThings;

	/** Create a new room with nothing in it */
	public ZusassRoom(){
		this(0, 0);
	}

	/**
	 * Create a new room of the given size
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 */
	public ZusassRoom(int xTiles, int yTiles){
		super(xTiles, yTiles);
		this.statThings = new ArrayList<StatThing>();
	}

	@Override
	public void addThing(GameThing thing){
		super.addThing(thing);
		if(thing instanceof StatThing) this.statThings.add((StatThing)thing);
	}

	@Override
	public void tickRemoveThing(GameThing thing){
		super.tickRemoveThing(thing);
		if(thing instanceof StatThing) this.statThings.remove((StatThing)thing);
	}

	/** @return See {@link #statThings} */
	public ArrayList<StatThing> getStatThings(){
		return this.statThings;
	}

}
