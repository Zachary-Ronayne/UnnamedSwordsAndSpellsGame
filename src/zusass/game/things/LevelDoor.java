package zusass.game.things;

import zgame.core.Game;
import zgame.things.still.Door;
import zgame.things.type.PositionedHitboxThing;
import zgame.world.Room;
import zusass.game.LevelRoom;

/** A {@link Door} used by the infinitely generating levels */
public class LevelDoor extends Door{
	
	/** The level of the room that this door will lead to */
	private int level;
	
	/**
	 * Create a new LevelDoor at the default location
	 * 
	 * @param level See {@link #level}
	 * @param room The room which contains this {@link LevelDoor}
	 */
	public LevelDoor(int level, Room room){
		this(600, 0, level, room);
		this.setY(room.maxY() - this.getHeight());
	}

	/**
	 * Create a new LevelDoor at the given location
	 * 
	 * @param x The x coordinate of the door
	 * @param y The y coordinate of the door
	 * @param level See {@link #level}
	 * @param room The room which contains this {@link LevelDoor}
	 */
	public LevelDoor(double x, double y, int level, Room room){
		super(x, y);
		this.level = level;
	}
	
	@Override
	public void enterRoom(Room r, PositionedHitboxThing thing, Game game){
		// Generate the new room, then enter it
		this.setLeadRoom(new LevelRoom(this.getLevel()), 0, 0);
		this.setRoomY(this.getLeadRoom().maxY() - thing.getHeight());
		super.enterRoom(r, thing, game);
	}
	
	/** @return See {@link #level} */
	public int getLevel(){
		return this.level;
	}
	
	@Override
	public int getRenderPriority(){
		return -100;
	}

}
