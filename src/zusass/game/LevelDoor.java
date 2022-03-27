package zusass.game;

import zgame.core.Game;
import zgame.things.Door;
import zgame.things.PositionedThing;
import zgame.things.Room;

/** A {@link Door} used by the infinitely generating levels  */
public class LevelDoor extends Door{

	/** The level of the room that this door will lead to */
	private int level;

	/** Create a new LevelDoor */
	public LevelDoor(int level){
		super(600, 350);
		this.level = level;
	}

	@Override
	public void enterRoom(Room r, PositionedThing thing, Game game){
		// Generate the new room, then enter it
		this.setLeadRoom(new LevelRoom(this.getLevel()), 0, 350);
		super.enterRoom(r, thing, game);
	}

	/** @return See {@link #level} */
	public int getLevel(){
		return this.level;
	}
	
}
