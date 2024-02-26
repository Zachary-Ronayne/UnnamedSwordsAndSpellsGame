package zusass.game.things;

import zgame.core.Game;
import zgame.things.still.Door;
import zgame.things.type.PositionedHitboxThing2D;
import zgame.world.Room;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.game.LevelRoom;
import zusass.game.ZusassRoom;

/** A {@link Door} used by the infinitely generating levels */
public class LevelDoor extends ZusassDoor{
	
	/** The level of the room that this door will lead to */
	private final int level;
	
	/** The room which contains this {@link LevelDoor} */
	private final ZusassRoom room;
	
	/**
	 * Create a new LevelDoor at the default location
	 *
	 * @param level See {@link #level}
	 * @param room The room which contains this {@link LevelDoor}
	 */
	public LevelDoor(int level, ZusassRoom room){
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
	public LevelDoor(double x, double y, int level, ZusassRoom room){
		super(x, y);
		this.level = level;
		this.room = room;
	}
	
	@Override
	public boolean enterRoom(Room r, PositionedHitboxThing2D thing, Game game){
		ZusassGame zgame = (ZusassGame)game;
		
		// Generate the new room, then enter it
		var levelRoom = new LevelRoom(this.getLevel());
		this.setLeadRoom(levelRoom, 0, 0);
		levelRoom.initRandom();
		this.setRoomY(this.getLeadRoom().maxY() - thing.getHeight());
		boolean success = super.enterRoom(r, thing, game);
		
		// Update the highest level room the player has been in
		if(success){
			ZusassData d = zgame.getData();
			d.updatedHighestRoomLevel(this.getLevel());
			d.checkAutoSave(zgame);
		}
		return success;
	}
	
	@Override
	public boolean canEnter(PositionedHitboxThing2D thing){
		return thing.hasTag(ZusassTags.CAN_ENTER_LEVEL_DOOR);
	}
	
	/** @return See {@link #level} */
	public int getLevel(){
		return this.level;
	}
	
	/** @return  See {@link #room} */
	public ZusassRoom getRoom(){
		return this.room;
	}
	
	@Override
	public int getRenderPriority(){
		return -100;
	}
	
}
