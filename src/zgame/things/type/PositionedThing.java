package zgame.things.type;

import zgame.core.Game;
import zgame.world.Room;

/** A {@link GameThing} which has a position */
public abstract class PositionedThing extends GameThing {
	
	/** Create a new {@link PositionedThing}  */
	public PositionedThing(){
		super();
	}
	
	/**
	 * Take this {@link PositionedThing} from the given room, and place it in the other given room
	 *
	 * @param from The room to move the thing from, i.e. the thing was in this room. Can be null if the thing didn't come from a room
	 * @param to The room to move the thing to, i.e. the thing is now in this room. Can be null if the thing isn't going to a room
	 * @param game The {@link Game} where this thing entered the room
	 */
	public void enterRoom(Room from, Room to, Game game){
		if(from != null) from.removeThing(this);
		if(to != null) to.addThing(this);
	}
	
}
