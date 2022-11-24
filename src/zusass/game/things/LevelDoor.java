package zusass.game.things;

import zgame.core.Game;
import zgame.things.still.Door;
import zgame.things.type.PositionedHitboxThing;
import zgame.world.Room;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.game.LevelRoom;
import zusass.game.things.entities.mobs.ZusassPlayer;

/** A {@link Door} used by the infinitely generating levels */
public class LevelDoor extends ZusassDoor{
	
	/** The level of the room that this door will lead to */
	private int level;

	/** The room which contains this {@link LevelDoor} */
	private Room room;
	
	/**
	 * Create a new LevelDoor at the default location
	 * 
	 * @param level See {@link #level}
	 * @param room T
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
		this.room = room;
	}
	
	@Override
	public boolean enterRoom(Room r, PositionedHitboxThing thing, Game game){
		ZusassGame zgame = (ZusassGame)game;
		
		// Generate the new room, then enter it
		this.setLeadRoom(new LevelRoom(this.getLevel()), 0, 0);
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
	
	// TODO work on this logic, and make it not so spaghetti 
	// Only players can enter LevelDoors
	@Override
	public boolean canEnter(PositionedHitboxThing thing){
		if(this.room instanceof LevelRoom){
			LevelRoom lr = (LevelRoom)this.room;
			if(!lr.isRoomCleared()) return false;
		}
		return thing instanceof ZusassPlayer;
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
